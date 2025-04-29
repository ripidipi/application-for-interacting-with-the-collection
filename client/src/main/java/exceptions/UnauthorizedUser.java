package exceptions;

public class UnauthorizedUser extends RuntimeException {
    public UnauthorizedUser(String message) {
        super(message);
    }

    @Override
    public String getMessage() { return "Unauthorized user unable to work in the system";}
}
