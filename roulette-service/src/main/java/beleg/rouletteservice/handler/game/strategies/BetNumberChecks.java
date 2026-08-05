package beleg.rouletteservice.handler.game.strategies;

import beleg.rouletteservice.result.Failure;
import beleg.rouletteservice.result.Failures;
import beleg.rouletteservice.result.IResult;
import beleg.rouletteservice.result.Success;

import java.util.List;

// Wiederverwendbare bausteine fuer IBetStrategy-Implementierungen
final class BetNumberChecks {

    private BetNumberChecks() {}

    static final IResult<Void, Failures> OK = new Success<>(null);
    static final IResult<Void, Failures> OUT_OF_RANGE = new Failure<>(Failures.OUT_OF_RANGE);

    // genau eine Zahl, nur 0 oder 1
    static IResult<Void, Failures> binaryChoice(List<Integer> betNumbers) {
        if (betNumbers.size() != 1) {
            return OUT_OF_RANGE;
        }
        int choice = betNumbers.get(0);
        return (choice == 0 || choice == 1) ? OK : OUT_OF_RANGE;
    }

    // genau eine Zahl, nur 1, 2 oder 3
    static IResult<Void, Failures> oneToThree(List<Integer> betNumbers) {
        if (betNumbers.size() != 1) {
            return OUT_OF_RANGE;
        }
        int choice = betNumbers.get(0);
        return (choice >= 1 && choice <= 3) ? OK : OUT_OF_RANGE;
    }

    // genau eine zahl aus der linken Spalte: (a - 1) % 3 == 0
    static IResult<Void, Failures> rowStart(List<Integer> betNumbers, int max) {
        if (betNumbers.size() != 1) {
            return OUT_OF_RANGE;
        }
        int a = betNumbers.get(0);
        return (a >= 1 && a <= max && (a - 1) % 3 == 0) ? OK : OUT_OF_RANGE;
    }
}