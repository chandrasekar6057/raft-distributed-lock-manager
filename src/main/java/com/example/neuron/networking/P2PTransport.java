package com.example.neuron.networking;

import com.example.neuron.config.ClusterConfig;
import org.springframework.stereotype.Service;

import java.io.ObjectOutputStream;
import java.net.Socket;

@Service
public class P2PTransport {

    private final ClusterConfig config;

    public P2PTransport(ClusterConfig config) {
        this.config = config;
    }

    public void send(String targetNodeId, RaftMessage msg) {

        ClusterConfig.Peer peer = config.getPeers().get(targetNodeId);
        if (peer == null) return;

        try (
                Socket socket = new Socket(peer.getHost(), peer.getPort());
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())
        ) {
            out.writeObject(msg);
            out.flush();
        } catch (Exception e) {
            //System.err.println("❌    Send failed to " + targetNodeId + ": " + e.getMessage());
            System.out.println("ℹ️ Peer " + targetNodeId + " is currently unreachable (Node is down).");
        }

    }

    public void broadcast(RaftMessage msg) {
        config.getPeers().keySet()
                .forEach(peerId -> send(peerId, msg));
    }

    public int clusterSize() {
        return config.getPeers().size() + 1; // peers + self
    }
}
