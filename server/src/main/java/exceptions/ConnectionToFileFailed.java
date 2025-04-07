package exceptions;

public class ConnectionToFileFailed extends Exception {
  public ConnectionToFileFailed(String message) {
    super(message);
  }

  @Override
  public String getMessage() {
    return super.getMessage() + ". Please try again.";
  }
}
