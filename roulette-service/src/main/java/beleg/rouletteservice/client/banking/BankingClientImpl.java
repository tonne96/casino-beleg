package beleg.rouletteservice.client.banking;

import beleg.rouletteservice.result.Failure;
import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import beleg.rouletteservice.result.Success;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

@Component
public class BankingClientImpl implements IBankingClient {

    private static final String INVOICING_PARTY = "ROULETTE";

    private final RestClient restClient;

    @Autowired
    public BankingClientImpl(@Value("${banking.service.base-url}") String bankingServiceBaseUrl) {
        this(RestClient.builder().baseUrl(bankingServiceBaseUrl).build());
    }

    BankingClientImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public IResult<BankingUserDto, Failures> getUser(Long userId) {
        try {
            ResponseEntity<BankingUserDto> response = restClient.get()
                    .uri("/casino/bank/api/user/{id}", userId)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, res) -> { /* nicht werfen */ })
                    .toEntity(BankingUserDto.class);

            if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                return new Failure<>(Failures.USER_NOT_FOUND);
            }
            if (response.getStatusCode().isError() || response.getBody() == null) {
                return new Failure<>(Failures.BANKING_SERVICE_UNAVAILABLE);
            }
            return new Success<>(response.getBody());
        } catch (RestClientException e) {
            return new Failure<>(Failures.BANKING_SERVICE_UNAVAILABLE);
        }
    }                           

    @Override
    public IResult<Void, Failures> bookTransaction(Long userId, BigDecimal amount) {
        try {
            TransactionRequestDto request = new TransactionRequestDto(INVOICING_PARTY, amount);
            ResponseEntity<Void> response = restClient.post()
                    .uri("/casino/bank/api/transaction/user/{userId}", userId)
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, res) -> { /* nicht werfen */ })
                    .toBodilessEntity();

            if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                return new Failure<>(Failures.USER_NOT_FOUND);
            }
            if (response.getStatusCode().isError()) {
                return new Failure<>(Failures.BANKING_SERVICE_UNAVAILABLE);
            }
            return new Success<>(null);
        } catch (RestClientException e) {
            return new Failure<>(Failures.BANKING_SERVICE_UNAVAILABLE);
        }
    }
}