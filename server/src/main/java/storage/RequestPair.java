package storage;

import commands.Commands;

import java.io.Serializable;


public record RequestPair<T>(Commands command, T object, String username, String password) implements Serializable {};

