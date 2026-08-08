package beleg.rouletteservice.client.banking;

import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;

import org.junit.jupiter.api.AfterEach;
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


class BankingClientImplTest {

    private MockRestServiceServer server;
    private BankingClientImpl bankingClient;

    @BeforeEach
    void setUp() {
        RestClient.Builder restClientBuilder = RestClient.builder()
                .baseUrl("http://banking.test");
        server = MockRestServiceServer.bindTo(restClientBuilder).build();
        bankingClient = new BankingClientImpl(restClientBuilder.build());
    }

    @AfterEach
    void verifyAllExpectationsMet() {
        server.verify();
    }

    @Test
    void publicConstructorAcceptsConfiguredBankingUrl() {
        BankingClientImpl configuredClient = new BankingClientImpl("http://localhost:8080");

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

        IResult<BankingUserDto, Failures> result = bankingClient.getUser(7L);

        assertTrue(result.isSuccess());
        BankingUserDto user = result.getValue();
        assertEquals(7L, user.id());
        assertEquals("Ada", user.firstName());
        assertEquals("Lovelace", user.lastName());
        assertEquals(0, new BigDecimal("125.50").compareTo(user.balance()));
    }

    @Test
    void getUserThrowsNotFoundExceptionForUnknownUser() {
        server.expect(requestTo("http://banking.test/casino/bank/api/user/99"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        IResult<BankingUserDto, Failures> result = bankingClient.getUser(99L);

        assertFalse(result.isSuccess());
        assertEquals(Failures.USER_NOT_FOUND, result.getMessage());
    }

    @Test
    void getUserThrowsCommunicationExceptionForServerError() {
        server.expect(requestTo("http://banking.test/casino/bank/api/user/1"))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        IResult<BankingUserDto, Failures> result = bankingClient.getUser(1L);

        assertFalse(result.isSuccess());
        assertEquals(Failures.BANKING_SERVICE_UNAVAILABLE, result.getMessage());
    }

    @Test
    void boookRouletteTransactionSendsExpectedRequest() {
        server.expect(requestTo("http://banking.test/casino/bank/api/transaction/user/7"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"invoicing_party\":\"ROULETTE\",\"amount\":-10.50}"))
                .andRespond(withSuccess());

        IResult<Void, Failures> result = bankingClient.bookTransaction(7L, new BigDecimal("-10.50"));

        assertTrue(result.isSuccess());
    }

    @Test
    void bookTransactionReturnsUserNotFoundForUnknownUser() {
        server.expect(requestTo("http://banking.test/casino/bank/api/transaction/user/99"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        IResult<Void, Failures> result = bankingClient.bookTransaction(99L, BigDecimal.TEN.negate());

        assertFalse(result.isSuccess());
        assertEquals(Failures.USER_NOT_FOUND, result.getMessage());
    }

    @Test
    void bookTransactionReturnsServiceUnavailableForBadRequest() {
        server.expect(requestTo("http://banking.test/casino/bank/api/transaction/user/1"))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        IResult<Void, Failures> result = bankingClient.bookTransaction(1L, BigDecimal.TEN);

        assertFalse(result.isSuccess());
        assertEquals(Failures.BANKING_SERVICE_UNAVAILABLE, result.getMessage());
    }

    @Test
    void bookTransactionReturnsServiceUnavailableForServerError() {
        server.expect(requestTo("http://banking.test/casino/bank/api/transaction/user/1"))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        IResult<Void, Failures> result = bankingClient.bookTransaction(1L, BigDecimal.TEN);

        assertFalse(result.isSuccess());
        assertEquals(Failures.BANKING_SERVICE_UNAVAILABLE, result.getMessage());
    }

    @Test
    void bookTransactionReturnsServiceUnavailableForUnexpectedClientError() {
        server.expect(requestTo("http://banking.test/casino/bank/api/transaction/user/1"))
                .andRespond(withStatus(HttpStatus.FORBIDDEN));

        IResult<Void, Failures> result = bankingClient.bookTransaction(1L, BigDecimal.TEN);

        assertFalse(result.isSuccess());
        assertEquals(Failures.BANKING_SERVICE_UNAVAILABLE, result.getMessage());
    }
}