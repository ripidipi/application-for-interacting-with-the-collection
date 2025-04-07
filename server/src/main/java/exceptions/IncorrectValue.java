package exceptions;

public class IncorrectValue extends RuntimeException {
    public IncorrectValue(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " invalid argument for command";
  }
}
