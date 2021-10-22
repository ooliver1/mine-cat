package tootymc;

import org.bukkit.Server;
import java.util.logging.Logger;
import com.mitch528.sockets.Sockets.TCPClient;
import com.mitch528.sockets.Sockets.TCPServer;
import com.mitch528.sockets.Sockets.SocketHandler;
import com.mitch528.sockets.events.MessageReceivedEvent;
import com.mitch528.sockets.events.SocketConnectedEvent;
import com.mitch528.sockets.events.SocketDisconnectedEvent;
import com.mitch528.sockets.events.ServerSocketStartedEvent;
import com.mitch528.sockets.events.ServerSocketAcceptedEvent;
import com.mitch528.sockets.events.MessageReceivedEventListener;
import com.mitch528.sockets.events.SocketConnectedEventListener;
import com.mitch528.sockets.events.SocketDisconnectedEventListener;
import com.mitch528.sockets.events.ServerSocketStartedEventListener;
import com.mitch528.sockets.events.ServerSocketAcceptedEventListener;

public class Socket {
    private final TCPServer tcpServer = new TCPServer(9876);
    private final TCPClient tcpClient = new TCPClient("127.0.0.1", 9876);
    private Server mcServer;
    private Logger logger;

    public Socket(Tooty plugin) {
        this.mcServer = plugin.getServer();
        this.logger = this.mcServer.getLogger();
    }

    public void disable() {
        tcpClient.Disconnect();
        tcpServer.ShutdownAll();
        tcpServer.StopServer();
    }

    public void enable() {
        tcpServer.getSocketAccepted()
            .addServerSocketAcceptedEventListener(
                new ServerSocketAcceptedEventListener() {
                    public void socketAccepted(ServerSocketAcceptedEvent evt) {
                        logger.info("tcpServer - tcpClient has connected (ID: "
                            + evt.getHandler().getId() + ")");

                        final SocketHandler handler = evt.getHandler();
                        handler.getMessage().addMessageReceivedEventListener(
                            new MessageReceivedEventListener() {
                                public void messageReceived(MessageReceivedEvent evt) {
                                    logger.info(
                                        "tcpServer - Received message from tcpClient - "
                                            + evt.getMessage());
                                    logger.info("tcpServer - Sending reply to tcpClient");
                                    handler.SendMessage("Goodbye World!");
                                }
                            }
                        );
                        handler.getDisconnected()
                            .addSocketDisconnectedEventListener(
                                new SocketDisconnectedEventListener() {
                                    public void socketDisconnected(SocketDisconnectedEvent evt) {
                                        logger.info("tcpServer - tcpClient "
                                            + evt.getID() + " disconnected");
                                    }
                                }
                            );
                    }
                }
            );

        tcpServer.getServerSocketStarted()
            .addServerSocketStartedEventListener(
                new ServerSocketStartedEventListener() {
                    public void serverSocketStarted(ServerSocketStartedEvent evt) {
                        tcpClient.Connect();
                    }
                }
            );

        tcpClient.getHandler().getConnected()
            .addSocketConnectedEventListener(
                new SocketConnectedEventListener() {
                    public void socketConnected(SocketConnectedEvent evt) {
                        logger.info("tcpClient - Connected to tcpServer!");
                        logger.info("tcpClient - Sending message to tcpServer.");
                        tcpClient.SendMessage("Hello World!");
                    }
                }
            );
        tcpClient.getHandler().getMessage()
            .addMessageReceivedEventListener(
                new MessageReceivedEventListener() {
                    public void messageReceived(MessageReceivedEvent evt) {
                        logger.info("tcpClient - I got the following message: "
                            + evt.getMessage());

                        tcpClient.Disconnect();
                    }
                }
            );
        tcpClient.getHandler().getDisconnected()
            .addSocketDisconnectedEventListener(
                new SocketDisconnectedEventListener() {
                    public void socketDisconnected(SocketDisconnectedEvent evt) {
                        logger.info("tcpClient - Disconnected");
                    }
                }
            );
        tcpServer.start();
    }
}
