package storage;

import java.util.HashSet;
import java.util.Set;

/**
 * Singleton class that manages the set of files currently in use.
 * Provides methods to add, remove, and check files in the set.
 */
public class RunningFiles {

    private Set<String> filesNames = new HashSet<>();
    private static RunningFiles instance;

    private RunningFiles() {}

    /**
     * Returns the singleton instance of the RunningFiles class.
     * If the instance does not exist, it creates one.
     *
     * @return the singleton instance of RunningFiles
     */
    public static RunningFiles getInstance() {
        if (instance == null) {
            instance = new RunningFiles();
        }
        return instance;
    }

    /**
     * Returns the set of file names that are currently being managed.
     *
     * @return a set containing the names of the files
     */
    public Set<String> getFilesNames() {
        return filesNames;
    }

    /**
     * Adds a file name to the set of currently managed files.
     *
     * @param fileName the name of the file to add to the set
     */
    public void addFileName(String fileName) {
        this.filesNames.add(fileName);
    }

    /**
     * Checks if a specific file is currently in the set of running files.
     *
     * @param fileName the name of the file to check
     * @return true if the file is present in the set, false otherwise
     */
    public boolean isThere(String fileName) {
        return filesNames.contains(fileName);
    }

    /**
     * Removes a file name from the set of running files.
     *
     * @param fileName the name of the file to remove from the set
     */
    public void removeFileName(String fileName) {
        this.filesNames.remove(fileName);
    }
}
