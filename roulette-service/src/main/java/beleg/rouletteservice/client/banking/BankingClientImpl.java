package beleg.rouletteservice.client.banking;

import beleg.rouletteservice.result.Failure;
import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import beleg.rouletteservice.result.Success;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;


// per REST wird mit banking kommuniziert, banking kann roulette service als bekannt zuordnen
// da als invoicing party ROULETTE mitgeschickt wird
@Component
public class BankingClientImpl implements IBankingClient {

    private static final Logger log = LoggerFactory.getLogger(BankingClientImpl.class);
    private static final String INVOICING_PARTY = "ROULETTE";

    private final RestTemplate restTemplate;
    private final String bankingServiceBaseUrl;

    public BankingClientImpl(
            RestTemplate restTemplate,
            @Value("${banking.service.base-url}") String bankingServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.bankingServiceBaseUrl = bankingServiceBaseUrl;
    }

    @Override
    public IResult<BankingUserDto, Failures> getUser(Long userId) {
        try {
            BankingUserDto user = restTemplate.getForObject(
                    bankingServiceBaseUrl + "/casino/bank/api/user/{id}",
                    BankingUserDto.class,
                    userId
            );
            if (user == null) {
                log.error("Banking-Service lieferte fuer GET /user/{} einen leeren Body", userId);
                return new Failure<>(Failures.BANKING_SERVICE_UNAVAILABLE);
            }
            return new Success<>(user);
        } catch (HttpClientErrorException.NotFound e) {
            return new Failure<>(Failures.USER_NOT_FOUND);
        } catch (HttpClientErrorException e) {
            log.error("Banking-Service hat GET /user/{} mit {} abgelehnt: {}",
                    userId, e.getStatusCode(), e.getResponseBodyAsString());
            return new Failure<>(Failures.BANKING_SERVICE_UNAVAILABLE);
        } catch (RestClientException e) {
            log.error("Banking-Service unter GET /user/{} nicht erreichbar", userId, e);
            return new Failure<>(Failures.BANKING_SERVICE_UNAVAILABLE);
        }
    }

    @Override
    public IResult<Void, Failures> bookTransaction(Long userId, BigDecimal amount) {
        try {
            TransactionRequestDto request = new TransactionRequestDto(INVOICING_PARTY, amount);
            restTemplate.postForEntity(
                    bankingServiceBaseUrl + "/casino/bank/api/transaction/user/{userId}",
                    request,
                    Object.class,
                    userId
            );
            return new Success<>(null);
        } catch (HttpClientErrorException.NotFound e) {
            return new Failure<>(Failures.USER_NOT_FOUND);
        } catch (HttpClientErrorException e) {
            log.error("Banking-Service hat POST /transaction/user/{} mit {} abgelehnt: {}",
                    userId, e.getStatusCode(), e.getResponseBodyAsString());
            return new Failure<>(Failures.BANKING_SERVICE_UNAVAILABLE);
        } catch (RestClientException e) {
            log.error("Banking-Service unter POST /transaction/user/{} nicht erreichbar", userId, e);
            return new Failure<>(Failures.BANKING_SERVICE_UNAVAILABLE);
        }
    }
}