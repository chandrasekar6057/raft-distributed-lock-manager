package com.example.neuron.networking;

import com.example.neuron.config.ClusterConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Service
public class P2PListener {

    private final ClusterConfig clusterConfig;


    private final List<Consumer<RaftMessage>> messageHandlers =
            new CopyOnWriteArrayList<>();

    public P2PListener(
            ClusterConfig clusterConfig
    ) {
        this.clusterConfig = clusterConfig;
    }

    /**
     * Start TCP listener
     */
    @PostConstruct
    public void startListening() {
        int tcpPort = clusterConfig.getTcp().getPort();

        Thread.ofVirtual().name("p2p-acceptor").start(() -> {
            try (ServerSocket serverSocket = new ServerSocket(tcpPort)) {

                System.out.println("P2P listening on port " + tcpPort);

                while (true) {
                    Socket socket = serverSocket.accept();
                    Thread.ofVirtual().start(() -> handleConnection(socket));
                }

            } catch (Exception e) {
                System.err.println("P2P listener failed: " + e.getMessage());
            }
        });
    }


//    public void send(String targetNodeId, RaftMessage msg) {
//        ClusterConfig.Peer peer = clusterConfig.getPeers().get(targetNodeId);
//        if (peer == null) return;
//
//        String host = peer.getHost();
//        int port = peer.getPort();
//
//        Thread.ofVirtual().start(() -> {
//            try (Socket socket = new Socket(host, port);
//                 ObjectOutputStream out =
//                         new ObjectOutputStream(socket.getOutputStream())) {
//
//                out.writeObject(msg);
//                out.flush();
//
//            } catch (Exception e) {
//                System.err.println("Failed to send to " + targetNodeId + ": " + e.getMessage());
//            }
//        });
//    }
//
//
//    public void broadcast(RaftMessage msg) {
//        clusterConfig.getPeers().keySet()
//                .forEach(nodeId -> send(nodeId, msg));
//    }

    public int getClusterSize() {
        return clusterConfig.getPeers().size() + 1;
    }

    public void registerHandler(Consumer<RaftMessage> handler) {
        messageHandlers.add(handler);
    }

    private void handleConnection(Socket socket) {
        try (socket;
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            RaftMessage msg = (RaftMessage) in.readObject();
            messageHandlers.forEach(h -> h.accept(msg));

        } catch (Exception e) {
            System.err.println("Inbound message failed: " + e.getMessage());
        }
    }
}
