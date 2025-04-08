package commands.interfaces;

import storage.RequestPair;

public interface Command {
    /**
     * Method for all commands. Perform main functional
     */
    RequestPair<?> execute(String arg, String inputMode);

}
