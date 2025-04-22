package commands;

import commands.interfaces.Command;
import io.DistributionOfTheOutputStream;
import storage.Authentication;
import storage.DBManager;

public class CheckAuthentication implements Command<Authentication> {

    @Override
    public void execute(Authentication arg, boolean muteMode) {
        DistributionOfTheOutputStream.println(DBManager.isCorrectUser(arg.name(), arg.password()).toString());
    }


}
