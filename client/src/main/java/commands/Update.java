package commands;

import collection.fabrics.StudyGroupFabric;
import storage.Logging;
import collection.StudyGroup;
import commands.interfaces.Command;
import commands.interfaces.Helpable;
import exceptions.RemoveOfTheNextSymbol;
import io.DistributionOfTheOutputStream;
import storage.RequestPair;


/**
 * Command that updates a study group in the collection by its ID from console.
 */
public class Update implements Helpable, Command {

    @Override
    public RequestPair<?> execute(String arg, String inputMode) {
        try{
            StudyGroup studyGroup = StudyGroupFabric.parseStudyGroup(arg, inputMode, "Update");
            return new RequestPair<>(Commands.UPDATE, studyGroup);
        }  catch (RemoveOfTheNextSymbol e) {
            DistributionOfTheOutputStream.println(e.getMessage());
            Exit.exit();
        } catch (RuntimeException e) {
            DistributionOfTheOutputStream.println(e.getMessage());
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
        return null;
    }

    @Override
    public String getHelp() {
        return "Updates an existing study group by its ID. You can update study " +
                "groups either through user input or by loading data from a file.";
    }
}
