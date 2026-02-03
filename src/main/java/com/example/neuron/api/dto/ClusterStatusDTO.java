package com.example.neuron.api.dto;

public class ClusterStatusDTO {

    public String nodeId;
    public String state;
    public long term;
    public String leaderId;

    public ClusterStatusDTO(String nodeId, String state, long term, String leaderId) {
        this.nodeId = nodeId;
        this.state = state;
        this.term = term;
        this.leaderId = leaderId;
    }
}
