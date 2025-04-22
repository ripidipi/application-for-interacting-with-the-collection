package storage;

import commands.Commands;

import java.io.Serializable;


public record Request<T>(Commands command, T object, String username, String password) implements Serializable {

    public Request(Commands commandC, T objectC) {
        this(commandC, objectC, "", "");
    }

}

