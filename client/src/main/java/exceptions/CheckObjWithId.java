package exceptions;

import commands.Commands;
import io.Server;
import storage.Request;

public class CheckObjWithId {
    public static void checkObjWithId(Integer id) throws IncorrectValue, ServerDisconnect {
        Request<Integer> request = new Request<>(Commands.CHECK_IS_WITH_ID, id);
        if (Server.interaction(request).contains("false")) {
            throw new IncorrectValue("Update failed, object with this ID doesn't exist\n " +
                    "or you couldn't update this object");
        }
    }
}
