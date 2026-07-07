package beleg.rouletteservice.result;

public interface Result<T,M> {
    boolean isSuccess();
    T getValue();
    M getMessage();
}