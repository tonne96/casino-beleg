package beleg.rouletteservice.result;

public interface IResult<T,M> {
    boolean isSuccess();
    T getValue();
    M getMessage();
}