package beleg.bankingservice.view;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Request-DTO für das Anlegen und Aktualisieren einer Transaktion.
 */
public record TransactionRequest(
        @NotBlank(message = "invoicing_party darf nicht leer sein")
        String invoicing_party,

        Long user,

        @NotNull(message = "amount darf nicht null sein")
        BigDecimal amount
) {}