package beleg.rouletteservice.result;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.List;

public class ResultsTest {

    private static IResult<Void, Failures> success() {
        return new Success<>(null);
    }
 
    private static IResult<Void, Failures> failure(Failures reason) {
        return new Failure<>(reason);
    }

    @Test
    void returnsOnlyFirstFailure() {
        IResult<Void, Failures> first = failure(Failures.USER_NOT_FOUND);
        IResult<Void, Failures> second = failure(Failures.BANKING_SERVICE_UNAVAILABLE);
 
        IResult<Void, Failures> result = Results.firstFailure(
                List.of(success(), first, second));
 
        assertSame(first, result);
        assertEquals(Failures.USER_NOT_FOUND, result.getMessage());
    }

    @Test
    void returnsFailureAtDifferentPositionThanFirst() {
        IResult<Void, Failures> first = failure(Failures.USER_NOT_FOUND);
        IResult<Void, Failures> result = Results.firstFailure(
                List.of(success(), success(), success(), first, success()));
 
        assertSame(first, result);
        assertFalse(result.isSuccess());
        assertEquals(Failures.USER_NOT_FOUND, result.getMessage());

    }

    @Test
    void returnsSuccessForNoFailures() {
        IResult<Void, Failures> result = Results.firstFailure(
                List.of(success(), success(), success()));

        assertTrue(result.isSuccess());
    }

    @Test
    void returnsSuccessForEmptyList() {
        IResult<Void, Failures> result = Results.firstFailure(List.of());

        assertTrue(result.isSuccess());
    }    
}
