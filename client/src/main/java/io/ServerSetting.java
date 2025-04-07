package io;

public class ServerSetting {

    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 9999;

    public static String getServerHost() {
        return SERVER_HOST;
    }

    public static int getServerPort() {
        return SERVER_PORT;
    }
}
