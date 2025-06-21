package io;

import commands.Exit;
import exceptions.ServerDisconnect;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.charset.StandardCharsets;
import storage.Logging;
import storage.Request;

public class Server {
    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 6600;

    public static String interaction(Request<?> request) throws ServerDisconnect {
        Logging.log("Starting interaction with server: " + request.command());
        final int CHUNK_SIZE = 1000;
        try (DatagramChannel client = DatagramChannel.open()) {
            client.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            try (ObjectOutputStream oout = new ObjectOutputStream(bout)) {
                oout.writeObject(request);
            }
            byte[] bytes = bout.toByteArray();
            client.write(ByteBuffer.wrap(bytes));

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
                    if (readBytes < 0) {
                        throw new ServerDisconnect("Channel closed unexpectedly");
                    }
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

            String result = fullResponse.toString();
            Logging.log("Received response: " + result);
            return result;
        } catch (ServerDisconnect e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
            throw e;
        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
            throw new ServerDisconnect("Communication failure: " + e.getMessage());
        } finally {
            Logging.log("Ending interaction with server");
        }
    }

    public static String getServerHost() {
        return SERVER_HOST;
    }

    public static int getServerPort() {
        return SERVER_PORT;
    }
}