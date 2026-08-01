package beleg.rouletteservice.client.banking;

import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.Result;

import java.math.BigDecimal;

// Interface für HTTP Kommunikation mit Banking Service,
// ermöglicht Testen der RouletteGameHandler Implementierung mit Mockito ohne echten HTTP aufruf
public interface BankingClient {

    // arbeitet mit Result pattern statt exceptions, schaut ob user existiert..
    Result<BankingUserDto, Failures> getUser(Long userId);

    // Transaktion (Gewinn/Verlust) wird für Nutzer bei banking service gebucht
    Result<Void, Failures> bookTransaction(Long userId, BigDecimal amount);
}