package exceptions;

public class ServerDisconnect extends Exception {
    public ServerDisconnect(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return "Error happened, server disconnect";
    }
}
