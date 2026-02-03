package com.example.neuron.lock;


import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LockManager {

    private final Map<String, DistributedLock> locks = new ConcurrentHashMap<>();

    public synchronized boolean tryAcquire(
            String resourceId,
            String nodeId,
            long ttlMillis
    ) {
        DistributedLock existing = locks.get(resourceId);

        if (existing != null && !existing.isExpired()) {
            return false;
        }

        locks.put(resourceId, new DistributedLock(resourceId, nodeId, ttlMillis));
        return true;
    }

    public Map<String, DistributedLock> getLocks() {
        return locks;
    }
}
