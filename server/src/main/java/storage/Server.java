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

    public static void sendResponse(DatagramChannel server, SocketAddress address) throws java.io.IOException {
        String response = PreparingOfOutputStream.getOutMessage();
        byte[] data = response.getBytes(StandardCharsets.UTF_8);
        final int CHUNK_SIZE = 1000;
        int offset = 0;
        while (offset < data.length) {
            int len = Math.min(CHUNK_SIZE, data.length - offset);
            ByteBuffer buf = ByteBuffer.wrap(data, offset, len);
            server.send(buf, address);
            offset += len;
        }
    }
}
