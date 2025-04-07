package exceptions;

public class EmptyLine extends RuntimeException {
    public EmptyLine(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " field can't be empty.";
    }
}
