package com.example.neuron.core;

public enum NodeState {
    FOLLOWER,   // Passive, listening for heartbeats
    CANDIDATE,  // Lost heartbeat, trying to become leader
    LEADER      // Owns the cluster, grants locks
}