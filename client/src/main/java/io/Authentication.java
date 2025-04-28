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
import java.util.Scanner;

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
            askAuthentication();
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
        try {
            return Objects.requireNonNull(Server.interaction(new Request<>(Commands.CHECK_AUTHENTICATION, null))).contains("true");
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
        System.out.println("Authentication" + (result ? " successfully" : " unsuccessfully"));
        if (!result) Exit.exit();
        return true;
    }

    public static void askAuthentication() {
        try {
            System.out.println("To Sign in: type S \t to Login: type L ");
            Scanner scanner = new Scanner(System.in);
            String decision = scanner.nextLine();
            if (decision.equalsIgnoreCase("L")) {
                logIn();
            } else {
                signIn();
            }
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
    }

    public static void logIn() throws Exception {
        System.out.println("Login attempt");
        askUser();
    }

    public static void signIn() throws Exception {
        System.out.println("Sign In attempt");
        askUser();
        try {
             DistributionOfTheOutputStream.printFromServer(Server.interaction(new Request<>(Commands.ADD_USER, null)));
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
    }

    public static void askUser() throws Exception {
        String username = PrimitiveDataInput.input("Username", String.class);
        String password = PrimitiveDataInput.input("Password", String.class);
        instance = new Authentication(username, makeHash(password));
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
