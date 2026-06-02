package beleg.slotsservice.client.banking;

import beleg.slotsservice.exception.BankingCommunicationException;
import beleg.slotsservice.exception.BankingUserNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;

/**
 * Client = HTTP-Kommunikation zum Banking-Service.
 *
 * Der Slots-Service kennt dadurch nicht direkt die Banking-Implementierung,
 * sondern nur die Banking-API.
 */
@Service
public class BankingClient {

    private static final String INVOICING_PARTY = "SLOTS";

    private final RestClient restClient;

    public BankingClient(@Value("${banking.service.url}") String bankingServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(bankingServiceUrl)
                .build();
    }

    /**
     * Fragt den User beim Banking-Service ab.
     *
     * Wird genutzt, um zu pruefen:
     *  - existiert der User?
     *  - wie hoch ist sein aktuelles Guthaben?
     */
    public BankingUserView getUser(Long userId) {
        try {
            return restClient.get()
                    .uri("/casino/bank/api/user/{id}", userId)
                    .retrieve()
                    .body(BankingUserView.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new BankingUserNotFoundException(userId);
        } catch (RestClientException e) {
            throw new BankingCommunicationException("Banking-Service konnte User nicht liefern.", e);
        }
    }

    /**
     * Bucht das Ergebnis einer Slot-Runde im Banking-Service.
     *
     * amount ist bereits das fertige Spielergebnis:
     *  - positiv = Gewinn
     *  - negativ = Verlust
     */
    public void createSlotsTransaction(Long userId, BigDecimal amount) {
        BankingTransactionRequest request = new BankingTransactionRequest(INVOICING_PARTY, userId, amount);

        try {
            restClient.post()
                    .uri("/casino/bank/api/transaction/user/{user_id}", userId)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound e) {
            throw new BankingUserNotFoundException(userId);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new IllegalArgumentException("Banking-Service hat die Slots-Transaktion abgelehnt.");
            }
            throw new BankingCommunicationException("Banking-Service hat unerwartet geantwortet.", e);
        } catch (RestClientException e) {
            throw new BankingCommunicationException("Banking-Service konnte Transaktion nicht buchen.", e);
        }
    }
}
