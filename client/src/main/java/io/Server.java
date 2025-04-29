package io;

import commands.Exit;
import exceptions.ServerDisconnect;
import storage.Logging;
import storage.Request;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;

public class Server {

    private static final String SERVER_HOST = "127.0.0.1"; // helios.cs.ifmo.ru    127.0.0.1
    private static final int SERVER_PORT = 6611;

    public static String getServerHost() {
        return SERVER_HOST;
    }

    public static int getServerPort() {
        return SERVER_PORT;
    }

    public static String interaction(Request<?> request) throws ServerDisconnect {
        try (DatagramChannel client = DatagramChannel.open()) {
            client.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            client.configureBlocking(false);

            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            try (ObjectOutputStream oout = new ObjectOutputStream(bout)) {
                oout.writeObject(request);
            }
            byte[] bytes = bout.toByteArray();

            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            client.write(buffer);

            ByteBuffer recv = ByteBuffer.allocate(4096);
            long deadline = System.currentTimeMillis() + 3000;
            while (recv.position() == 0) {
                if (System.currentTimeMillis() > deadline) {
                    System.out.println("Server unavailable.");
                    Exit.exit();
                    throw new ServerDisconnect("");
                }
                Thread.sleep(10);
                client.read(recv);
            }
            recv.flip();
            return StandardCharsets.UTF_8.decode(recv).toString();
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
        throw new ServerDisconnect("");
    }

}
