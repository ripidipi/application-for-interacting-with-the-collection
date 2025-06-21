package io;

import commands.Commands;
import exceptions.ServerDisconnect;
import storage.Logging;
import storage.Request;

public class Authentication {
    private static Authentication instance = new Authentication("", "");
    private final String username;
    private final String passwordHash;

    private Authentication(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public static Authentication getInstance() {
        return instance;
    }

    public static void logout() {
        instance = null;
    }

    public static boolean login(String username, String plainPassword) {
        String hash = makeHash(plainPassword);
        instance = new Authentication(username, hash);
        try {
            String response = Server.interaction(new Request<>(Commands.CHECK_AUTHENTICATION, null));
            boolean ok = response.contains("true");
            if (!ok) {
                instance = null;
            }
            return ok;
        } catch (ServerDisconnect e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
            instance = null;
            return false;
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
            instance = null;
            return false;
        }
    }

    public static boolean register(String username, String plainPassword) {
        String hash = makeHash(plainPassword);
        instance = new Authentication(username, hash);
        try {
            String response = Server.interaction(new Request<>(Commands.ADD_USER, null));
            boolean success = !response.contains("already exists");
            if (!success) {
                instance = null;
            }
            return success;
        } catch (ServerDisconnect e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
            instance = null;
            return false;
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
            instance = null;
            return false;
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return passwordHash;
    }

    public static String makeHash(String arg) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(arg.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}