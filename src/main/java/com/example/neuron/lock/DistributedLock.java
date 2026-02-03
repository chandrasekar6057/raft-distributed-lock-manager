package com.example.neuron.lock;



public class DistributedLock {

    private final String resourceId;
    private final String ownerNodeId;
    private final long acquiredAt;
    private final long ttlMillis;

    public DistributedLock(String resourceId, String ownerNodeId, long ttlMillis) {
        this.resourceId = resourceId;
        this.ownerNodeId = ownerNodeId;
        this.ttlMillis = ttlMillis;
        this.acquiredAt = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - acquiredAt > ttlMillis;
    }

    public String getOwnerNodeId() {
        return ownerNodeId;
    }
}
