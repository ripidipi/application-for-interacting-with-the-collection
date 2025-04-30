package exceptions;

import commands.Exit;

public class ServerDisconnect extends Exception {
    public ServerDisconnect(String message) {
        super(message);
        Exit.exit();
    }

    @Override
    public String getMessage() {
        return "Error happened, server disconnect";
    }
}
