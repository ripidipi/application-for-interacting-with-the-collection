package storage;

import commands.Commands;
import io.Authentication;

import java.io.Serializable;
import java.util.Base64;

/**
 * Represents a request sent from the client to the server.
 * Encapsulates the command, optional payload (as a serialized Base64 string), and credentials.
 */
public record Request<T>(Commands command,
                         T object,
                         String username,
                         String password) implements Serializable {

    public Request(Commands commandC, T objectC) {
        this(commandC,
                objectC,
                Authentication.getInstance().getUsername(),
                Authentication.getInstance().getPassword());
    }

    @Override
    public String toString() {
        String serializedObject = "";
        if (object != null) {
            try {
                java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                try (java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(baos)) {
                    oos.writeObject(object);
                }
                serializedObject = Base64.getEncoder().encodeToString(baos.toByteArray());
            } catch (Exception e) {
                throw new RuntimeException("Failed to serialize object: " + e.getMessage(), e);
            }
        }
        return command.name() + "||" + username + "||" + password + "||" + serializedObject;
    }

    /**
     * Parses a string into a Request object.
     * Only use this if the object is expected and must be deserialized.
     */
    public static Request<?> fromString(String str) throws Exception {
        String[] parts = str.split("\\|\\|", 4);
        if (parts.length < 3) {
            throw new IllegalArgumentException("Malformed request string");
        }

        Commands cmd = Commands.valueOf(parts[0]);
        String user = parts[1];
        String pass = parts[2];
        Object obj = null;

        if (parts.length == 4 && !parts[3].isEmpty()) {
            byte[] data = Base64.getDecoder().decode(parts[3]);
            try (java.io.ObjectInputStream ois = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(data))) {
                obj = ois.readObject();
            }
        }

        return new Request<>(cmd, obj, user, pass);
    }
}