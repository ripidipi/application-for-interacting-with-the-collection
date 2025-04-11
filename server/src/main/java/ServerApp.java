import collection.Collection;
import commands.*;
import io.ClientRequest;
import io.CommandsHandler;
import io.DistributionOfTheOutputStream;
import io.PreparingOfOutputStream;
import storage.FillCollectionFromFile;
import storage.Logging;
import storage.RequestPair;
import storage.Server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class ServerApp {

    public static void main(String[] args) {
        initializeApplication();
        listenLoop();
    }

    private static void listenLoop() {
        try (DatagramChannel server = DatagramChannel.open()) {
            server.configureBlocking(false);
            server.bind(new InetSocketAddress(Server.getServerPort()));
            System.out.println("Server waiting on port - " + Server.getServerPort());

            ByteBuffer buffer = ByteBuffer.allocate(4096);

            while (true) {
                ClientRequest requestWrapper = Server.readFromClient(server, buffer);
                if (requestWrapper == null) {
                    Thread.sleep(3);
                    continue;
                }

                RequestPair<?> request = (RequestPair<?>) requestWrapper.input.readObject();

                PreparingOfOutputStream.clear();
                CommandsHandler.execute(request, false);

                Server.sendResponse(server, requestWrapper.address);
            }

        } catch (Exception e) {
            Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
        } finally {
            System.out.println("Server closed");
        }
    }


    private static void initializeApplication() {
        Collection.getInstance();
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
