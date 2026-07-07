package beleg.rouletteservice.client.banking;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;


// DTO für transaction, ebenfalls getrennt von anderen DTO's
public record TransactionRequestDto(

        @JsonProperty("invoicing_party")
        String invoicingParty,
        BigDecimal amount
) {
}