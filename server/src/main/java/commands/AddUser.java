package commands;

import collection.StudyGroup;
import commands.interfaces.Command;
import io.DistributionOfTheOutputStream;
import storage.Authentication;
import storage.DBManager;
import storage.Logging;

public class AddUser implements Command<Void> {

    @Override
    public void execute(Void arg, boolean muteMode, Authentication auth) {
        if (DBManager.addUser(auth.name(), auth.password())) {
            DistributionOfTheOutputStream.println("User added");
        } else {
            DistributionOfTheOutputStream.println("User not added");
        }
    }

}
