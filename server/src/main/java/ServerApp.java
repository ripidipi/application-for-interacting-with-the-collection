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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerApp {

    public static void main(String[] args) {
        initializeApplication();
        listenLoop();
    }

    private static void listenLoop() {
        try (DatagramChannel server = DatagramChannel.open()) {
            ExecutorService connectionPool = Executors.newFixedThreadPool(Server.getTreadsQuantity());
            server.configureBlocking(false);
            server.bind(new InetSocketAddress(Server.getServerPort()));
            System.out.println("Server waiting on port - " + Server.getServerPort());

            connectionPool.submit(() -> {
            while (true) {
                    try {
                        ByteBuffer buffer = ByteBuffer.allocate(4096);
                        ClientRequest requestWrapper = Server.readFromClient(server, buffer);

                        if (requestWrapper == null) {
                            Thread.sleep(3);
                            continue;
                        }
                        ObjectInputStream input = requestWrapper.input;
                        RequestPair<?> request = (RequestPair<?>) input.readObject();

                        new Thread(() -> {
                            PreparingOfOutputStream.clear();
                            CommandsHandler.execute(request, false);

                            new Thread(() -> {
                                try {
                                    Server.sendResponse(server, requestWrapper.address);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }).start();

                        }).start();

                    } catch (Exception e) {
                        Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
                    }

                Thread.sleep(3);
            }
            });

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
