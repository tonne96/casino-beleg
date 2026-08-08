package beleg.slotsservice.client;

import beleg.slotsservice.exception.BankingCommunicationException;
import beleg.slotsservice.exception.BankingUserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Unit-Tests für die HTTP-Kommunikation mit dem Banking-Service.
 */
class BankingClientTest {

    private MockRestServiceServer server;
    private BankingClient bankingClient;

    @BeforeEach
    void setUp() {
        RestClient.Builder restClientBuilder = RestClient.builder()
                .baseUrl("http://banking.test");
        server = MockRestServiceServer.bindTo(restClientBuilder).build();
        bankingClient = new BankingClient(restClientBuilder.build());
    }

    @Test
    void publicConstructorAcceptsConfiguredBankingUrl() {
        BankingClient configuredClient = new BankingClient("http://localhost:8080");

        assertNotNull(configuredClient);
    }

    @Test
    void getUserReturnsBankingUserForSuccessfulResponse() {
        server.expect(requestTo("http://banking.test/casino/bank/api/user/7"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(
                        "{\"id\":7,\"first_name\":\"Ada\",\"last_name\":\"Lovelace\",\"balance\":125.50}",
                        MediaType.APPLICATION_JSON
                ));

        BankingUserView user = bankingClient.getUser(7L);

        assertEquals(7L, user.id());
        assertEquals("Ada", user.first_name());
        assertEquals("Lovelace", user.last_name());
        assertEquals(new BigDecimal("125.50"), user.balance());
        server.verify();
    }

    @Test
    void getUserThrowsNotFoundExceptionForUnknownUser() {
        server.expect(requestTo("http://banking.test/casino/bank/api/user/99"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        try {
            bankingClient.getUser(99L);
            fail("Ein unbekannter Banking-User muss als NotFound gemeldet werden.");
        } catch (BankingUserNotFoundException e) {
            assertTrue(e.getMessage().contains("99"));
        }

        server.verify();
    }

    @Test
    void getUserThrowsCommunicationExceptionForServerError() {
        server.expect(requestTo("http://banking.test/casino/bank/api/user/1"))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        try {
            bankingClient.getUser(1L);
            fail("Ein Banking-Serverfehler muss als Kommunikationsfehler gemeldet werden.");
        } catch (BankingCommunicationException e) {
            assertNotNull(e.getCause());
        }

        server.verify();
    }

    @Test
    void createSlotsTransactionSendsExpectedRequest() {
        server.expect(requestTo("http://banking.test/casino/bank/api/transaction/user/7"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"invoicing_party\":\"SLOTS\",\"user\":7,\"amount\":-10.50}"))
                .andRespond(withSuccess());

        bankingClient.createSlotsTransaction(7L, new BigDecimal("-10.50"));

        server.verify();
    }

    @Test
    void createSlotsTransactionThrowsNotFoundForUnknownUser() {
        server.expect(requestTo("http://banking.test/casino/bank/api/transaction/user/99"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        try {
            bankingClient.createSlotsTransaction(99L, BigDecimal.TEN.negate());
            fail("Ein unbekannter User muss als NotFound gemeldet werden.");
        } catch (BankingUserNotFoundException e) {
            assertTrue(e.getMessage().contains("99"));
        }

        server.verify();
    }

    @Test
    void createSlotsTransactionThrowsIllegalArgumentForBadRequest() {
        server.expect(requestTo("http://banking.test/casino/bank/api/transaction/user/1"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        try {
            bankingClient.createSlotsTransaction(1L, BigDecimal.TEN);
            fail("Eine vom Banking abgelehnte Transaktion muss als ungueltig gemeldet werden.");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        server.verify();
    }

    @Test
    void createSlotsTransactionThrowsCommunicationExceptionForServerError() {
        server.expect(requestTo("http://banking.test/casino/bank/api/transaction/user/1"))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        try {
            bankingClient.createSlotsTransaction(1L, BigDecimal.TEN);
            fail("Ein unerwarteter Banking-Fehler muss als Kommunikationsfehler gemeldet werden.");
        } catch (BankingCommunicationException e) {
            assertNotNull(e.getCause());
        }

        server.verify();
    }

    @Test
    void createSlotsTransactionThrowsCommunicationExceptionForUnexpectedClientError() {
        server.expect(requestTo("http://banking.test/casino/bank/api/transaction/user/1"))
                .andRespond(withStatus(HttpStatus.FORBIDDEN));

        try {
            bankingClient.createSlotsTransaction(1L, BigDecimal.TEN);
            fail("Ein unerwarteter Banking-Status muss als Kommunikationsfehler gemeldet werden.");
        } catch (BankingCommunicationException e) {
            assertNotNull(e.getCause());
        }

        server.verify();
    }
}
