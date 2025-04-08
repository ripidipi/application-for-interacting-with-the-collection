package io;

import commands.Exit;
import storage.RequestPair;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;

public class Server {

    private static final String SERVER_HOST = "helios.cs.ifmo.ru";
    private static final int SERVER_PORT = 9911;

    public static String getServerHost() {
        return SERVER_HOST;
    }

    public static int getServerPort() {
        return SERVER_PORT;
    }

    public static String interaction(DatagramChannel client, RequestPair<?> request) throws IOException {
        // to bite
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objectOut = new ObjectOutputStream(byteOut);
        objectOut.writeObject(request);
        objectOut.flush();
        byte[] bytes = byteOut.toByteArray();

        // send
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        client.write(buffer);

        // take response
        ByteBuffer receiveBuffer = ByteBuffer.allocate(4096);
        long startTime = System.currentTimeMillis();


        while (receiveBuffer.position() == 0) {
            if (System.currentTimeMillis() - startTime > 1e4) {
                System.out.println("Server unavailable.");
                Exit.exit();
                return null;
            }
            client.read(receiveBuffer);
        }

        receiveBuffer.flip();
        return new String(receiveBuffer.array(), 0,
                receiveBuffer.limit(), StandardCharsets.UTF_8);
    }
}
