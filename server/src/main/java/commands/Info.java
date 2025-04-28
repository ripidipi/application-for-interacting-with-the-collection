package commands;

import collection.Collection;
import commands.interfaces.Command;
import commands.interfaces.Helpable;
import io.DistributionOfTheOutputStream;
import storage.Authentication;
import storage.Logging;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Command that provides information about the collection.
 */
public class Info implements Helpable, Command<Void> {

    private static final ReentrantLock lock = new ReentrantLock();
    /**
     * Prints information about the collection (type, initialization date, number of elements).
     */
    public static void info() {
        try {
            lock.lock();
            Collection.getInstance().reload();
            DistributionOfTheOutputStream.println(Collection.getInstance().getInfo());
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void execute(Void arg, boolean muteMode, Authentication auth) {
        info();
    }

    @Override
    public String getHelp() {
        return "Returns information about the collection (type, initialization date, number of elements).";
    }
}
