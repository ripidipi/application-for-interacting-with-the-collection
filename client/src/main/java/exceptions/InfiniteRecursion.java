package exceptions;

public class InfiniteRecursion extends Exception {
    public InfiniteRecursion(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return "Running " + super.getMessage() + " got into an infinite recursion, fix it and try again.";
    }
}
