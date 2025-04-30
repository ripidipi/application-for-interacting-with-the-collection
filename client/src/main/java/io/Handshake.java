package io;

import commands.Commands;
import exceptions.ServerDisconnect;
import storage.Logging;
import storage.Request;

public class Handshake {

    static public boolean makeHandshake() throws ServerDisconnect {
        try {
            String ans = Server.interaction(new Request<>(Commands.HANDSHAKE, null, "-1", "-1"));
            if (ans.equals("##C#Hi my little friend\n")) {
                return true;
            }
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
        throw new ServerDisconnect("");
    }

}
