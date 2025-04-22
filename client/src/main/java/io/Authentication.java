package io;

import commands.Commands;
import commands.Exit;
import exceptions.UnauthorizedUser;
import storage.Logging;
import storage.Request;

import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;


import java.util.Objects;

public class Authentication {

    private static Authentication instance = null;
    private static String username;
    private static String password;

    private Authentication(String name, String passwordH) {
        username = name;
        password = passwordH;
    }

    public static Authentication getInstance() {
        if (instance == null) {
            instance = askAuthentication();
        }
        return instance;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAuthenticated() {
        boolean result = false;
        if (instance == null) {
            System.out.println("Authentication unsuccessfully");
            Exit.exit();
            return false;
        }
        try (DatagramChannel client = DatagramChannel.open()) {
            client.configureBlocking(false);
            client.connect(new InetSocketAddress(Server.getServerHost(), Server.getServerPort()));
            return Objects.requireNonNull(Server.interaction(client, new Request<>(Commands.CHECK_AUTHENTICATION, null))).contains("true");
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
        System.out.println("Authentication" + (result ? " successfully" : " unsuccessfully"));
        if (!result) Exit.exit();
        return true;
    }

    public static Authentication askAuthentication() {
        try {
            if (instance == null) {
                System.out.println("Authentication unsuccessfully");
            }
            System.out.println("Authorization is required");
            String username = PrimitiveDataInput.input("Username", String.class);
            String password = PrimitiveDataInput.input("Password", String.class);
            return new Authentication(username, makeHash(password));
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
        throw new UnauthorizedUser("");
    }

    public static String makeHash(String arg) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(arg.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(encodedHash);
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
