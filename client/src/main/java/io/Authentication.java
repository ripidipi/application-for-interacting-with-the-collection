package io;

import commands.Commands;
import commands.Exit;
import exceptions.IncorrectConstant;
import exceptions.IncorrectValue;
import exceptions.ServerDisconnect;
import exceptions.UnauthorizedUser;
import storage.Logging;
import storage.Request;

import java.io.Console;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

/**
 * Manages user authentication for the client application using a singleton pattern.
 * <p>
 * Provides methods to sign in or log in a user by reading credentials from the console,
 * hashing the password with SHA-256, and verifying or registering the user on the server.
 * </p>
 *
 * @implNote
 * All I/O is performed via {@link System#out} and {@link Console}. If the console
 * is unavailable, operations that require user input will throw a {@link RuntimeException}.
 * Authentication state is stored in a single {@code Authentication} instance.
 */
public class Authentication {

    /** Singleton instance of {@code Authentication}. */
    private static Authentication instance = null;
    /** The authenticated user's username. */
    private static String username;
    /** The SHA-256 hash of the authenticated user's password. */
    private static String password;

    /**
     * Private constructor which sets the authenticated credentials.
     *
     * @param name      the username
     * @param passwordH the SHA-256 password hash
     */
    private Authentication(String name, String passwordH) {
        username = name;
        password = passwordH;
    }

    /**
     * Retrieves the singleton {@code Authentication} instance, prompting
     * the user to log in or sign up if not already authenticated.
     *
     * @return the existing or newly created {@code Authentication} instance
     */
    public static Authentication getInstance() {
        if (instance == null) {
            askAuthentication();
        }
        return instance;
    }

    /**
     * Returns the current authenticated username.
     *
     * @return the username of the authenticated user, or {@code null} if none
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the SHA-256 hashed password of the authenticated user.
     *
     * @return the password hash, or {@code null} if none
     */
    public String getPassword() {
        return password;
    }

    /**
     * Checks authentication status by sending a {@link Commands#CHECK_AUTHENTICATION}
     * request to the server.
     * <p>
     * If no authentication instance exists, prints an error message and exits.
     * On server disconnect, returns {@code false}.
     * Any other exception is logged.
     * </p>
     *
     * @return {@code true} if the server confirms authentication; {@code false} otherwise
     */
    public boolean isAuthenticated() {
        if (instance == null) {
            System.out.println("Authentication unsuccessfully");
            Exit.exit();
            return false;
        }
        try {
            boolean result = Objects.requireNonNull(
                    Server.interaction(new Request<>(Commands.CHECK_AUTHENTICATION, null))
            ).contains("true");
            System.out.println("Authentication" + (result ? " successfully" : " unsuccessfully"));
            if (!result) {
                Exit.exit();
            }
            return result;
        } catch (ServerDisconnect _) {
            return false;
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
            return false;
        }
    }

    /**
     * Prompts the user to choose between signing in (new user) or logging in (existing user).
     * <p>
     * “L” – login; any other input – sign in.
     * </p>
     */
    public static void askAuthentication() {
        try {
            System.out.println("To Sign in: type S \t to Login: type L ");
            Scanner scanner = new Scanner(System.in);
            String decision = scanner.nextLine();
            if ("L".equalsIgnoreCase(decision)) {
                logIn();
            } else {
                signIn();
            }
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
    }

    /**
     * Initiates an existing-user login flow by prompting for credentials.
     *
     * @throws Exception if console input fails
     */
    public static void logIn() throws Exception {
        System.out.println("Login attempt");
        askUser();
    }

    /**
     * Initiates a new-user sign-up flow by prompting for credentials,
     * then sends an {@link Commands#ADD_USER} request to register the user.
     *
     * @throws Exception if console input fails
     */
    public static void signIn() throws Exception {
        System.out.println("Sign In attempt");
        askUser();
        try {
            DistributionOfTheOutputStream.printFromServer(
                    Server.interaction(new Request<>(Commands.ADD_USER, null))
            );
        } catch (ServerDisconnect _) {
            // ignore server disconnect during sign-up
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
    }

    /**
     * Prompts for and reads the username and password from the console,
     * hashing the password and creating the singleton instance.
     *
     * @throws Exception if the console is unavailable or input is invalid
     * @throws IncorrectConstant if username or password is empty
     */
    public static void askUser() throws Exception {
        Console console = System.console();
        if (console == null) {
            throw new RuntimeException("Console not available");
        }
        String user = console.readLine("Username: ");
        char[] pwdChars = console.readPassword("Password: ");
        if (user == null || user.isEmpty()) {
            throw new IncorrectConstant("Username or password cannot be empty");
        }
        String pass = new String(pwdChars);
        Arrays.fill(pwdChars, ' ');
        instance = new Authentication(user, makeHash(pass));
    }

    /**
     * Computes the SHA-256 hash of the given string.
     *
     * @param arg the input string (plain-text password)
     * @return the hexadecimal representation of the SHA-256 digest
     * @throws Exception if the SHA-256 algorithm is unavailable
     */
    public static String makeHash(String arg) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(arg.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(encodedHash);
    }

    /**
     * Converts a byte array into its lowercase hexadecimal string representation.
     *
     * @param hash the byte array to convert
     * @return a hex string of length twice the input array
     */
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
