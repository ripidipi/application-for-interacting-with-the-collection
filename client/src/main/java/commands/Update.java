package commands;

import collection.fabrics.StudyGroupFabric;
import io.Server;
import storage.Logging;
import collection.StudyGroup;
import commands.interfaces.Command;
import commands.interfaces.Helpable;
import exceptions.RemoveOfTheNextSymbol;
import io.DistributionOfTheOutputStream;
import storage.RequestPair;

import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;


/**
 * Command that updates a study group in the collection by its ID from console.
 */
public class Update implements Helpable, Command {

    @Override
    public RequestPair<?> execute(String arg, String inputMode) {
        try{
            Integer id = StudyGroupFabric.getIdInteger(arg);
            if (!checkIsWithId(id)) {
                throw new RuntimeException("No element to update with this id in collection");
            }
            StudyGroup studyGroup = StudyGroupFabric.parseStudyGroup(arg, inputMode, "Update", true);
            return new RequestPair<>(Commands.UPDATE, studyGroup);
        } catch (RemoveOfTheNextSymbol e) {
            DistributionOfTheOutputStream.println(e.getMessage());
            Exit.exit();
        } catch (RuntimeException e) {
            DistributionOfTheOutputStream.println(e.getMessage());
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
        return null;
    }

    private boolean checkIsWithId(int id) {
        try (DatagramChannel client = DatagramChannel.open()) {
            client.configureBlocking(false);
            client.connect(new InetSocketAddress(Server.getServerHost(), Server.getServerPort()));
            String response = Server.interaction(client, new RequestPair<>(Commands.CHECK_IS_WITH_ID, id));
            if (response == null) {return true;}
            return response.contains("true");
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
        return false;
    }

    @Override
    public String getHelp() {
        return "Updates an existing study group by its ID. You can update study " +
                "groups either through user input or by loading data from a file.";
    }
}
