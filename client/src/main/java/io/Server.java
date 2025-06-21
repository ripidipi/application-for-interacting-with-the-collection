package io;

import commands.Exit;
import exceptions.ServerDisconnect;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import storage.Logging;
import storage.Request;

public class Server {
    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 6600;

    public static String interaction(Request<?> request) throws ServerDisconnect {
        final int HEADER = 4 + 4 + 4;          // 3 поля по 4 байта
        final int CHUNK_SIZE = 1000;
        Map<Integer, byte[]> chunks = new ConcurrentHashMap<>();
        int expectedChunks = -1;
        int requestId = Integer.MIN_VALUE;
        long deadline = System.currentTimeMillis() + 5000;

        try (DatagramChannel client = DatagramChannel.open()) {
            client.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));

            // Отправляем запрос (как раньше)...
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            try (ObjectOutputStream oout = new ObjectOutputStream(bout)) {
                oout.writeObject(request);
            }
            client.write(ByteBuffer.wrap(bout.toByteArray()));

            // Принимаем куски
            while (System.currentTimeMillis() < deadline) {
                ByteBuffer recv = ByteBuffer.allocate(HEADER + CHUNK_SIZE);
                client.configureBlocking(false);

                int read = client.read(recv);
                if (read <= 0) {
                    Thread.sleep(5);
                    continue;
                }

                recv.flip();
                int rid    = recv.getInt();
                int seq    = recv.getInt();
                int total  = recv.getInt();
                int payloadLen = recv.remaining();

                if (requestId == Integer.MIN_VALUE) {
                    requestId = rid;
                    expectedChunks = total;
                }
                if (rid != requestId) {
                    // Пакет не от нашей сессии — игнорируем
                    continue;
                }

                byte[] payload = new byte[payloadLen];
                recv.get(payload);
                chunks.put(seq, payload);

                if (chunks.size() == expectedChunks) {
                    byte[] all = new byte[(expectedChunks - 1) * CHUNK_SIZE +
                            chunks.get(expectedChunks - 1).length];
                    for (int i = 0; i < expectedChunks; i++) {
                        byte[] part = chunks.get(i);
                        System.arraycopy(part, 0, all, i * CHUNK_SIZE, part.length);
                    }
                    return new String(all, StandardCharsets.UTF_8);
                }
            }
            throw new ServerDisconnect("Timeout waiting for all response chunks");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ServerDisconnect("Interrupted while reading response");
        } catch (IOException e) {
            throw new ServerDisconnect("IO error: " + e.getMessage());
        }
    }


    public static String getServerHost() {
        return SERVER_HOST;
    }

    public static int getServerPort() {
        return SERVER_PORT;
    }
}