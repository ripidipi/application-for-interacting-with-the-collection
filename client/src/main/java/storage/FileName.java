package storage;

import io.Authentication;

/**
 * Encapsulates the name of a file along with the associated authentication context.
 * <p>
 * This record is used when operations require both the target file path
 * and the credentials of the currently authenticated user.
 * </p>
 *
 * @param fileName the path or name of the file to process
 * @param auth     the {@link Authentication} instance representing the user context
 */
public record FileName(String fileName, Authentication auth) {}
