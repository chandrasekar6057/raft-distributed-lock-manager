package com.example.neuron.config;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "cluster")
public class ClusterConfig {

    private String nodeId;
    private Tcp tcp;
    private Map<String, Peer> peers;

    public static class Tcp {
        private int port;
        public int getPort() { return port; }
        public void setPort(int port) { this.port = port; }
    }

    public static class Peer {
        private String host;
        private int port;

        public String getHost() { return host; }
        public void setHost(String host) { this.host = host; }

        public int getPort() { return port; }
        public void setPort(int port) { this.port = port; }
    }

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }

    public Tcp getTcp() { return tcp; }
    public void setTcp(Tcp tcp) { this.tcp = tcp; }

    public Map<String, Peer> getPeers() { return peers; }
    public void setPeers(Map<String, Peer> peers) { this.peers = peers; }

    @PostConstruct
    public void logCluster() {
        System.out.println("Node: " + nodeId);
        peers.forEach((id, p) ->
                System.out.println("Peer " + id + " -> " + p.getHost() + ":" + p.getPort())
        );
    }

}
