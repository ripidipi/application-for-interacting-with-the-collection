package io;

import commands.Exit;
import exceptions.ServerDisconnect;
import service.ClientService;
import storage.Logging;
import storage.Request;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;

/**
 * Handles low-level communication with the remote server over UDP.
 * <p>
 * Serializes {@link Request} objects, sends them to the configured server host and port,
 * waits for a response within a timeout, and returns the decoded UTF-8 string.
 * </p>
 * <p>
 * On send or receive errors, logs exceptions and throws {@link ServerDisconnect}.
 * Exits the client on timeout or unrecoverable error.
 * </p>
 */
public class Server {

    /**
     * Default server hostname.
     */
    private static final String SERVER_HOST = "127.0.0.1"; // helios.cs.ifmo.ru    127.0.0.1
    /**
     * Default server port number.
     */
    private static final int SERVER_PORT = 9610;

    /**
     * Returns the configured server hostname.
     *
     * @return the server host name (never null)
     */
    public static String getServerHost() {
        return SERVER_HOST;
    }

    /**
     * Returns the configured server port.
     *
     * @return the server port number
     */
    public static int getServerPort() {
        return SERVER_PORT;
    }

    public static String interaction(Request<?> request) throws ServerDisconnect {
        final int CHUNK_SIZE = 1000;
        try (DatagramChannel client = DatagramChannel.open()) {
            client.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));

            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            try (ObjectOutputStream oout = new ObjectOutputStream(bout)) {
                oout.writeObject(request);
            }
            byte[] bytes = bout.toByteArray();
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            client.write(buffer);

            StringBuilder fullResponse = new StringBuilder();
            long overallDeadline = System.currentTimeMillis() + 3000;

            while (true) {
                ByteBuffer recvBuf = ByteBuffer.allocate(8192);
                long chunkDeadline = System.currentTimeMillis() + 100;
                boolean gotAny = false;

                while (true) {
                    if (System.currentTimeMillis() > overallDeadline) {
                        throw new ServerDisconnect("Response timeout");
                    }
                    int readBytes = client.read(recvBuf);
                    if (readBytes > 0) {
                        gotAny = true;
                        break;
                    }
                    if (System.currentTimeMillis() > chunkDeadline) {
                        break;
                    }
                    Thread.sleep(5);
                }

                if (!gotAny) break;

                recvBuf.flip();
                String part = StandardCharsets.UTF_8.decode(recvBuf).toString();
                fullResponse.append(part);

                if (part.length() < CHUNK_SIZE) break;
            }

            return fullResponse.toString();
        } catch (ServerDisconnect e) {
            System.out.println(e.getMessage());
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
            throw e;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
            throw new ServerDisconnect("Communication failure: " + e.getMessage());
        }
    }


}