package itkach.aard2.callbacks;

public interface LookupListener {
    void onLookupStarted(String query);
    void onLookupFinished(String query);
    void onLookupCanceled(String query);
}
