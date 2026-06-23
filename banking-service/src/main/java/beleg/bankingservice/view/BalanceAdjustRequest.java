package beleg.bankingservice.view;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record BalanceAdjustRequest(
        @NotNull(message = "amount darf nicht null sein")
        BigDecimal amount
) {}
