package com.example.neuron.networking;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class RaftMessage implements Serializable {
    private String type;      // "HEARTBEAT", "VOTE_REQUEST", "VOTE_RESPONSE"
    private String senderId;
    private long term;
    private String payload;   // e.g., the name of the lock being requested
}
