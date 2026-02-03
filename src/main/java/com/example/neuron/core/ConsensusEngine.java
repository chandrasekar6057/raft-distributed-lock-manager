package com.example.neuron.core;

import com.example.neuron.networking.P2PListener;
import com.example.neuron.networking.P2PTransport;
import com.example.neuron.networking.RaftMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ConsensusEngine {

    private final NodeContext context;
    private final P2PListener listener;
    private final P2PTransport transport;

    private final AtomicInteger votesReceived = new AtomicInteger(0);

    private volatile long lastHeartbeatTime;
    private volatile long electionTimeout;

    // Leader heartbeat control
    private volatile long lastHeartbeatSent = 0;
    private static final long HEARTBEAT_INTERVAL = 300; // ms

    public ConsensusEngine(NodeContext context,
                           P2PListener listener,
                           P2PTransport transport) {

        this.context = context;
        this.listener = listener;
        this.transport = transport;

        resetElectionTimeout();
        this.lastHeartbeatTime = System.currentTimeMillis();

        this.listener.registerHandler(this::processIncomingMessage);
    }

    /* ---------------- TIMING ---------------- */

    private void resetElectionTimeout() {
        this.electionTimeout = 1500 + new Random().nextInt(1500);
    }

    @Scheduled(fixedRate = 50)
    public void tick() {
        long now = System.currentTimeMillis();

        switch (context.getState()) {

            case FOLLOWER, CANDIDATE -> {
                if (now - lastHeartbeatTime > electionTimeout) {
                    startElection();
                }
            }

            case LEADER -> {
                if (now - lastHeartbeatSent >= HEARTBEAT_INTERVAL) {
                    sendHeartbeats();
                    lastHeartbeatSent = now;
                }
            }
        }
    }

    /* ---------------- NETWORK ---------------- */

    private synchronized void processIncomingMessage(RaftMessage msg) {

        // Step down on higher term
        if (msg.getTerm() > context.getCurrentTerm()) {
            context.setCurrentTerm(msg.getTerm());
            context.setState(NodeState.FOLLOWER);
            context.setLeaderId(null);
            context.setVotedFor(null);
            resetElectionTimeout();
        }

        switch (msg.getType()) {

            case "HEARTBEAT" -> handleHeartbeat(msg);
            case "VOTE_REQUEST" -> handleVoteRequest(msg);
            case "VOTE_RESPONSE" -> handleVoteResponse(msg);
        }
    }

    /* ---------------- ELECTION ---------------- */

    private synchronized void startElection() {
        resetElectionTimeout();

        context.incrementTerm();
        context.setState(NodeState.CANDIDATE);
        context.setVotedFor(context.getNodeId());
        context.setLeaderId(null);

        votesReceived.set(1); // self vote
        lastHeartbeatTime = System.currentTimeMillis();

        System.out.println(
                context.getNodeId() + " started election for term " +
                        context.getCurrentTerm()
        );

        RaftMessage voteReq = new RaftMessage(
                "VOTE_REQUEST",
                context.getNodeId(),
                context.getCurrentTerm(),
                null
        );

        transport.broadcast(voteReq);
    }

    private void handleVoteRequest(RaftMessage msg) {

        boolean granted = context.voteFor(
                msg.getSenderId(),
                msg.getTerm()
        );

        if (granted) {
            lastHeartbeatTime = System.currentTimeMillis();
            resetElectionTimeout();
        }

        RaftMessage response = new RaftMessage(
                "VOTE_RESPONSE",
                context.getNodeId(),
                context.getCurrentTerm(),
                granted ? "YES" : "NO"
        );

        transport.send(msg.getSenderId(), response);
    }

    private void handleVoteResponse(RaftMessage msg) {

        if (context.getState() != NodeState.CANDIDATE) return;
        if (!"YES".equals(msg.getPayload())) return;
        if (msg.getTerm() != context.getCurrentTerm()) return;

        int votes = votesReceived.incrementAndGet();

//        if (votes > listener.getClusterSize() / 2) {
//            becomeLeader();
//        }
        if (votes > transport.clusterSize() / 2) {
            becomeLeader();
        }
        if (msg.getTerm() > context.getCurrentTerm()) {
            context.setCurrentTerm(msg.getTerm());
            context.setState(NodeState.FOLLOWER);
            context.setVotedFor(null);
            return;
        }

    }

    /* ---------------- LEADER ---------------- */

    private synchronized void becomeLeader() {
        context.setState(NodeState.LEADER);
        context.setLeaderId(context.getNodeId());
        lastHeartbeatSent = 0;

        System.out.println(
                context.getNodeId() + " became LEADER for term " +
                        context.getCurrentTerm()
        );

        sendHeartbeats(); // immediate
    }

//    private void handleHeartbeat(RaftMessage msg) {
//
//        if (msg.getTerm() < context.getCurrentTerm()) return;
//
//        lastHeartbeatTime = System.currentTimeMillis();
//        resetElectionTimeout();
//
//        if (context.getState() != NodeState.FOLLOWER) {
//            context.setState(NodeState.FOLLOWER);
//        }
//
//        context.setLeaderId(msg.getSenderId());
//    }

    private synchronized void handleHeartbeat(RaftMessage msg) {

        if (msg.getTerm() < context.getCurrentTerm()) {
            return;
        }

        context.setCurrentTerm(msg.getTerm());
        context.setState(NodeState.FOLLOWER);
        context.setLeaderId(msg.getSenderId());
        context.setVotedFor(null);

        lastHeartbeatTime = System.currentTimeMillis();
        resetElectionTimeout();
    }


    private void sendHeartbeats() {

        RaftMessage hb = new RaftMessage(
                "HEARTBEAT",
                context.getNodeId(),
                context.getCurrentTerm(),
                null
        );

        transport.broadcast(hb);
    }
}
