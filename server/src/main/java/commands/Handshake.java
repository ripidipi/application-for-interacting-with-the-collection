package commands;

import commands.interfaces.Command;
import io.DistributionOfTheOutputStream;
import storage.Authentication;

public class Handshake implements Command<Void> {

    @Override
    public void execute(Void arg, boolean muteMode, Authentication auth) {
        DistributionOfTheOutputStream.printlnC("Hi my little friend");
    }

}
