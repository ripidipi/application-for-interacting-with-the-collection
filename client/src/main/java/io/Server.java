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

    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 9999;

    public static String getServerHost() {
        return SERVER_HOST;
    }

    public static int getServerPort() {
        return SERVER_PORT;
    }

    public static void interaction(DatagramChannel client, RequestPair<?> request) throws IOException {
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
//            if (System.currentTimeMillis() - startTime > 3000) {
//                System.out.println("Server unavailable.");
//                Exit.exit();
//            }
            client.read(receiveBuffer);
        }

        receiveBuffer.flip();
        String response = new String(receiveBuffer.array(), 0,
                receiveBuffer.limit(), StandardCharsets.UTF_8);
        DistributionOfTheOutputStream.printFromServer(response);
    }
}
