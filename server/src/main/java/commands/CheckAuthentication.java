package commands;

import commands.interfaces.Command;
import io.DistributionOfTheOutputStream;
import storage.Authentication;
import storage.DBManager;

public class CheckAuthentication implements Command<Void> {

    @Override
    public void execute(Void arg, boolean muteMode, Authentication auth) {
        DistributionOfTheOutputStream.println(DBManager.isCorrectUser(auth.name(), auth.password()).toString());
    }


}
