package storage;

import commands.Commands;
import io.Authentication;

import java.io.Serializable;

/**
 * Represents a request sent from the client to the server.
 * <p>
 * Encapsulates the command to execute, an optional payload object, and
 * the credentials of the authenticated user.
 * </p>
 *
 * @param <T> the type of the payload object sent with the request (may be {@code Void})
 */
public record Request<T>(Commands command,
                         T object,
                         String username,
                         String password) implements Serializable {

    /**
     * Constructs a new Request using the current authentication context.
     * <p>
     * Automatically retrieves the username and password hash from
     * {@link Authentication#getInstance()}.
     * </p>
     *
     * @param commandC the command enum indicating the action to perform
     * @param objectC  the payload object (or {@code null} if none)
     */
    public Request(Commands commandC, T objectC) {
        this(commandC,
                objectC,
                Authentication.getInstance().getUsername(),
                Authentication.getInstance().getPassword());
    }

}
