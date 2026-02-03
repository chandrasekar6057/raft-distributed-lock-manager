//package com.example.neuron.networking;
//
//import com.example.neuron.config.ClusterConfig;
//import org.springframework.stereotype.Service;
//
//import java.io.ObjectOutputStream;
//import java.net.Socket;
//import java.util.Map;
//@Service
//public class P2PClient {
//
//    private final ClusterConfig clusterConfig;
//
//    public P2PClient(ClusterConfig clusterConfig) {
//        this.clusterConfig = clusterConfig;
//    }
//
//    public void send(String peerId, RaftMessage message) {
//        ClusterConfig.Peer peer =
//                clusterConfig.getPeers().get(peerId);
//
//        if (peer == null) {
//            System.err.println("Unknown peer: " + peerId);
//            return;
//        }
//
//        Thread.ofVirtual().start(() -> {
//            try (Socket socket =
//                         new Socket(peer.getHost(), peer.getPort());
//                 ObjectOutputStream out =
//                         new ObjectOutputStream(socket.getOutputStream())) {
//
//                out.writeObject(message);
//                out.flush();
//
//            } catch (Exception e) {
//                System.err.println(
//                        "Failed to send to " + peerId + ": " + e.getMessage()
//                );
//            }
//        });
//    }
//
//    public void broadcast(RaftMessage message) {
//        clusterConfig.getPeers().keySet()
//                .forEach(peerId -> send(peerId, message));
//    }
//}
//
