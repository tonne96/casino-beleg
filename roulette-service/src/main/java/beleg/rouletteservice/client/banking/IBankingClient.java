package beleg.rouletteservice.client.banking;

import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;

import java.math.BigDecimal;

// Interface für HTTP Kommunikation mit Banking Service,
// ermöglicht Testen der RouletteGameHandler Implementierung mit Mockito ohne echten HTTP aufruf
public interface IBankingClient {

    // arbeitet mit Result pattern statt exceptions, schaut ob user existiert..
    IResult<BankingUserDto, Failures> getUser(Long userId);

    // Transaktion (Gewinn/Verlust) wird für Nutzer bei banking service gebucht
    IResult<Void, Failures> bookTransaction(Long userId, BigDecimal amount);
}