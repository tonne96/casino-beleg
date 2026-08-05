package beleg.rouletteservice.result;

public class Success<T,M> implements IResult<T,M> {
    private final T value;

    public Success(T value){
        this.value = value;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public M getMessage() {
        return null;
    }


    @Override
    public boolean isSuccess() {
        return true;
    }
}
