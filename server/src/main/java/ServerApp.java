import collection.Collection;
import commands.*;
import io.ClientRequest;
import io.CommandsHandler;
import io.DistributionOfTheOutputStream;
import io.PreparingOfOutputStream;
import storage.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerApp {

    public static void main(String[] args) throws SQLException {
        Connection connection = DBManager.getConnection();
        System.out.println("Connected: " + !connection.isClosed());
        initializeApplication();
        listenLoop();
    }

    private static void listenLoop() {
        try (DatagramChannel server = DatagramChannel.open()) {
            server.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            server.configureBlocking(false);
            server.bind(new InetSocketAddress(Server.getServerPort()));
            System.out.println("Server waiting on port " + Server.getServerPort());

            ExecutorService connectionPool = Executors.newFixedThreadPool(Server.getTreadsQuantity());
            ByteBuffer buffer = ByteBuffer.allocate(4096);

            while (true) {
                ClientRequest req = Server.readFromClient(server, buffer);
                if (req == null) {
                    Thread.sleep(3);
                    continue;
                }

                connectionPool.submit(() -> {
                    try {
                        RequestPair<?> request = (RequestPair<?>) req.input.readObject();

                        new Thread(() -> {
                            PreparingOfOutputStream.clear();
                            CommandsHandler.execute(request, false);

                            new Thread(() -> {
                                try {
                                    Server.sendResponse(server, req.address);
                                } catch (IOException e) {
                                    Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
                                }
                            }).start();

                        }).start();

                    } catch (Exception e) {
                        Logging.log(Logging.makeMessage(e.getMessage(), e.getStackTrace()));
                    }
                });
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
