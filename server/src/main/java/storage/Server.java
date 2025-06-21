package storage;

import io.ClientRequest;
import io.PreparingOfOutputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Server {

    private static final int SERVER_PORT = 6600;
    private static final int TREADS_QUANTITY = 5;

    public static int getServerPort() {
        return SERVER_PORT;
    }

    public static int getTreadsQuantity() {
        return TREADS_QUANTITY;
    }

    public static ClientRequest readFromClient(DatagramChannel server, ByteBuffer buffer) throws IOException {
        buffer.clear();
        SocketAddress clientAddress = server.receive(buffer);

        if (clientAddress == null) {
            return null;
        }

        buffer.flip();
        ByteArrayInputStream byteIn = new ByteArrayInputStream(buffer.array(), 0, buffer.limit());
        ObjectInputStream objectIn = new ObjectInputStream(byteIn);

        return new ClientRequest(clientAddress, objectIn);
    }

    public static void sendResponse(DatagramChannel server, SocketAddress address) throws IOException {
        String response = PreparingOfOutputStream.getOutMessage();
        byte[] data = response.getBytes(StandardCharsets.UTF_8);

        final int CHUNK_SIZE = 1000;
        int requestId = new Random().nextInt();
        int totalChunks = (data.length + CHUNK_SIZE - 1) / CHUNK_SIZE;

        for (int seq = 0; seq < totalChunks; seq++) {
            int offset = seq * CHUNK_SIZE;
            int len    = Math.min(CHUNK_SIZE, data.length - offset);

            ByteBuffer buf = ByteBuffer.allocate(4 + 4 + 4 + len);
            buf.putInt(requestId);
            buf.putInt(seq);
            buf.putInt(totalChunks);
            buf.put(data, offset, len);
            buf.flip();

            server.send(buf, address);
        }
    }

}
