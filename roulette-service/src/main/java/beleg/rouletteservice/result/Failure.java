package beleg.rouletteservice.result;

public class Failure<T,M> implements Result<T,M> {
    private final M message;

    public Failure(M message){
        this.message=message;

    }

    @Override
    public T getValue() {
        return null;
    }

    @Override
    public M getMessage() {
        return message;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }


}
