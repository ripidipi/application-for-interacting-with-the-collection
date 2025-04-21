package storage;

import commands.Commands;
import io.Authentication;

import java.io.Serializable;


public record RequestPair<T>(Commands command, T object, String username, String password) implements Serializable {

    public RequestPair(Commands commandC, T objectC) {
        this(commandC, objectC, Authentication.getInstance().getUsername(), Authentication.getInstance().getPassword());
    }

}




