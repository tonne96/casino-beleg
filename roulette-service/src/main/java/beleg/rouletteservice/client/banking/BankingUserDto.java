package beleg.rouletteservice.client.banking;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;


// DTO hier und nicht bei den anderen, da es sich um einen fremden Service handelt
public record BankingUserDto(

        Long id,
        @JsonProperty("first_name")
        String firstName,
        @JsonProperty("last_name")
        String lastName,
        BigDecimal balance
) {
}