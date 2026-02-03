package com.example.neuron.core;

import com.example.neuron.config.ClusterConfig;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.UUID;
@Data
@Component
public class NodeContext {

   // private final String nodeId = "Node-" + UUID.randomUUID().toString().substring(0, 4);
   private final String nodeId;

    private volatile long currentTerm = 0;
    private volatile NodeState state = NodeState.FOLLOWER;
    private volatile String votedFor = null;
    private volatile String leaderId = null;

    public NodeContext(ClusterConfig config) {
        this.nodeId = config.getNodeId();
    }
    public synchronized void incrementTerm() {
        currentTerm++;
        votedFor = null;
    }

//    public synchronized boolean voteFor(String candidateId, long term) {
//        if (term < currentTerm) return false;
//
//        if (votedFor == null || votedFor.equals(candidateId)) {
//            votedFor = candidateId;
//            currentTerm = term;
//            return true;
//        }
//        return false;
//    }
public synchronized boolean voteFor(String candidateId, long term) {

    if (term < currentTerm) {
        return false;
    }

    if (term > currentTerm) {
        currentTerm = term;
        votedFor = null;
    }

    if (votedFor == null || votedFor.equals(candidateId)) {
        votedFor = candidateId;
        return true;
    }

    return false;
}


    // getters/setters
}


