import collection.Collection;
import commands.*;
import io.ClientRequest;
import io.CommandsHandler;
import io.DistributionOfTheOutputStream;
import io.PreparingOfOutputStream;
import storage.FillCollectionFromFile;
import storage.Logging;
import storage.RequestPair;
import storage.RunningFiles;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;

public class ServerApp {
    private static final int PORT = 9999;

    public static void main(String[] args) {
        initializeApplication();
        listenLoop();
    }

    private static ClientRequest readFromClient(DatagramChannel server, ByteBuffer buffer) throws IOException {
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


    private static void sendResponse(DatagramChannel server,SocketAddress address) throws IOException {
        String response = PreparingOfOutputStream.getOutMessage();
        ByteBuffer responseBuffer = ByteBuffer.wrap(response.getBytes(StandardCharsets.UTF_8));
        server.send(responseBuffer, address);
    }

    private static void listenLoop() {
        try (DatagramChannel server = DatagramChannel.open()) {
            server.configureBlocking(false);
            server.bind(new InetSocketAddress(PORT));
            System.out.println("Server waiting on port - " + PORT);

            ByteBuffer buffer = ByteBuffer.allocate(4096);

            while (true) {
                ClientRequest requestWrapper = readFromClient(server, buffer);
                if (requestWrapper == null) {
                    continue;
                }

                RequestPair<?> request = (RequestPair<?>) requestWrapper.input.readObject();

                PreparingOfOutputStream.clear();
                CommandsHandler.execute(request, false);

                sendResponse(server, requestWrapper.address);
            }

        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        } finally {
            System.out.println("Server closed");
        }
    }


    private static void initializeApplication() {
        Collection.getInstance();
        RunningFiles.getInstance();
        Help help = Help.getInstance();
        Logging.initialize();

        help.addCommand(
                help, new Add(), new Info(), new Show(),
                new Update(), new Exit(), new Save(),
                new AddIfMax(), new Clear(), new CountByGroupAdmin(),
                new ExecuteScript(), new GroupCountingById(),
                new RemoveAnyByGroupAdmin(), new RemoveById(),
                new RemoveGreater(), new RemoveLower()
        );

        try {
            FillCollectionFromFile.fillCollectionFromFile();
        } catch (Exception e) {
            DistributionOfTheOutputStream.println("Error loading collection from file: " + e.getMessage());
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        }
    }

}
