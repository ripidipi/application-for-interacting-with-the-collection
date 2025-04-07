package exceptions;

public class RemoveOfTheNextSymbol extends RuntimeException {

    public RemoveOfTheNextSymbol(String message) {
        super(message);
    }

    public RemoveOfTheNextSymbol() {}

    @Override
    public String getMessage() {
        return "CTRL + D detected. Problem unable be solve - stop of work";
    }
}
