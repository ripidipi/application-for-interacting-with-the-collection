package commands.interfaces;

import commands.Commands;
import exceptions.ServerDisconnect;
import io.Server;
import storage.Logging;
import storage.Request;

import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

public interface Command {
    /**
     * Executes the command with the given argument and input mode.
     *
     * @param arg the argument string passed to the command
     * @param inputMode the mode of input (e.g., "M", "F", or default)
     * @return a Request object representing the command request to be sent to the server
     */
    Request<?> execute(String arg, String inputMode);

    /**
     * Checks if the specified ID is not already in use on the server.
     * Sends a CHECK_IS_WITH_ID request and interprets the response.
     *
     * @param id the integer ID to check for existence
     * @return true if the ID is not in use; false otherwise or on error
     */
    static boolean checkIsNotWithId(int id) {
        try {
            String response = Server.interaction(new Request<>(Commands.CHECK_IS_WITH_ID, id));
            if (response == null) {
                return false;
            }
            return !response.contains("true");
        } catch (ServerDisconnect _) {
            return false;
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
        return true;
    }
}