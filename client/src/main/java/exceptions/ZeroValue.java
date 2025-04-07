package exceptions;

public class ZeroValue extends RuntimeException {
    public ZeroValue(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " can't be <= 0.";
    }
}
