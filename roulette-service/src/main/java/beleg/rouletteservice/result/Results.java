package beleg.rouletteservice.result;

import java.util.List;

public final class Results {

    private Results() {}

    public static <M> IResult<Void, M> firstFailure(List<IResult<Void, M>> results) {
        for (IResult<Void, M> result : results) {
            if (!result.isSuccess()) {
                return result;
            }
        }
        return new Success<>(null);
    }
}
