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
     * Method for all commands. Perform main functional
     */
    Request<?> execute(String arg, String inputMode);

    static boolean checkIsNotWithId(int id) {
        try {
            String response = Server.interaction(new Request<>(Commands.CHECK_IS_WITH_ID, id));
            if (response == null) {return false;}
            return !response.contains("true");
        } catch (ServerDisconnect _) {
            return false;
        }
        catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
        return true;
    }

}
