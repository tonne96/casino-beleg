package beleg.rouletteservice.result;

import java.util.List;

public final class Results {

    private Results() {
    }

    public static <M> Result<Void, M> firstFailure(List<Result<Void, M>> results) {
        for (Result<Void, M> result : results) {
            if (!result.isSuccess()) {
                return result;
            }
        }
        return new Success<>(null);
    }
}
