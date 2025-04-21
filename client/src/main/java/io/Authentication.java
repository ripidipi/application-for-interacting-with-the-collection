package io;

import commands.Exit;
import exceptions.RemoveOfTheNextSymbol;
import exceptions.UnauthorizedUser;
import storage.Logging;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;


import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Authentication {

    private static Authentication instance;
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
        return instance != null;
    }

    public static Authentication askAuthentication() {
        try {
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
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
