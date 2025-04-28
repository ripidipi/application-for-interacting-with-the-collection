package io;

import commands.Exit;
import storage.Request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;

public class Server {

    private static final String SERVER_HOST = "127.0.0.1"; // helios.cs.ifmo.ru
    private static final int SERVER_PORT = 9999;

    public static String getServerHost() {
        return SERVER_HOST;
    }

    public static int getServerPort() {
        return SERVER_PORT;
    }

    public static String interaction(DatagramChannel client, Request<?> request) throws IOException, InterruptedException {

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objectOut = new ObjectOutputStream(byteOut);
        objectOut.writeObject(request);
        objectOut.flush();
        byte[] bytes = byteOut.toByteArray();

        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        client.write(buffer);

        ByteBuffer receiveBuffer = ByteBuffer.allocate(4096);
        long startTime = System.currentTimeMillis();


        while (receiveBuffer.position() == 0) {
            if (System.currentTimeMillis() - startTime > 1e4) {
                System.out.println("Server unavailable.");
                Exit.exit();
                return null;
            }
            Thread.sleep(3);
            client.read(receiveBuffer);
        }

        receiveBuffer.flip();
        return new String(receiveBuffer.array(), 0,
                receiveBuffer.limit(), StandardCharsets.UTF_8);
    }
}
