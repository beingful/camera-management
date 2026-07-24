package camera.connection;

public interface ICameraConnectivityController {
    void connect();

    boolean isConnected();

    void disconnect();
}
