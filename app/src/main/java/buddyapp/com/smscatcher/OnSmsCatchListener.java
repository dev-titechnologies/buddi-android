package buddyapp.com.smscatcher;

public interface OnSmsCatchListener<T> {
    void onSmsCatch(String message);
}