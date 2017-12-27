package buddiapp.com.smscatcher;

public interface OnSmsCatchListener<T> {
    void onSmsCatch(String message);
}