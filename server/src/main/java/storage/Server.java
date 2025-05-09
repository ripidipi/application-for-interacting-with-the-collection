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

    public static void sendResponse(DatagramChannel server,SocketAddress address) throws IOException {
        String response = PreparingOfOutputStream.getOutMessage();
        int CHUNK_SIZE = 100000;
        while (!response.isEmpty()) {
            int len = Math.min(CHUNK_SIZE, response.length());
            String sentMessage = response.substring(0, len);
            ByteBuffer buf = ByteBuffer.wrap(sentMessage.getBytes(StandardCharsets.UTF_8));
            server.send(buf, address);
            response = response.substring(len);
        }
    }
}
