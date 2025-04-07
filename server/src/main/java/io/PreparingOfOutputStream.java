package io;

public class PreparingOfOutputStream {

    private static String outMessage;

    public static String getOutMessage() {
        return outMessage;
    }

    public static void addToOutMassage(String message) {
        outMessage = outMessage + message;
    }

    public static void clear() {
        outMessage = "";
    }

}
