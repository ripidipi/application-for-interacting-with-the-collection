package storage;

import java.util.HashSet;
import java.util.Set;

/**
 * Manages a global set of file names that represent scripts currently in execution.
 * <p>
 * Implements the singleton pattern to ensure a single shared registry of active files,
 * preventing recursive or concurrent execution of the same script file.
 * </p>
 */
public class RunningFiles {

    /** The set of file names currently active. */
    private final Set<FileName> fileNames = new HashSet<>();

    /** Singleton instance. */
    private static RunningFiles instance;

    /**
     * Private constructor to enforce singleton pattern.
     */
    private RunningFiles() {}

    /**
     * Retrieves the singleton instance of {@code RunningFiles}, creating it if necessary.
     *
     * @return the shared {@code RunningFiles} instance
     */
    public static synchronized RunningFiles getInstance() {
        if (instance == null) {
            instance = new RunningFiles();
        }
        return instance;
    }

    /**
     * Returns the set of currently registered file names.
     *
     * @return an unmodifiable view of the active file set
     */
    public Set<FileName> getFileNames() {
        return Set.copyOf(fileNames);
    }

    /**
     * Registers a file name as active.
     *
     * @param fileName the {@link FileName} to add
     */
    public void addFileName(FileName fileName) {
        fileNames.add(fileName);
    }

    /**
     * Checks if a file name is already registered as active.
     *
     * @param fileName the {@link FileName} to check
     * @return {@code true} if the file is active; {@code false} otherwise
     */
    public boolean contains(FileName fileName) {
        return fileNames.contains(fileName);
    }

    /**
     * Unregisters a file name, marking it as no longer active.
     *
     * @param fileName the {@link FileName} to remove
     */
    public void removeFileName(FileName fileName) {
        fileNames.remove(fileName);
    }
}