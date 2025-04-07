package exceptions;

public class IncorrectCommand extends RuntimeException {
    public IncorrectCommand(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return "'" + super.getMessage() + "' command doesn't exist.";
    }
}
