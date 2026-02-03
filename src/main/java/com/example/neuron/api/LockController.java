package com.example.neuron.api;

import com.example.neuron.core.NodeContext;
import com.example.neuron.core.NodeState;
import com.example.neuron.lock.LockManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/lock")
//public class LockController {
//
//    private final NodeContext context;
//    private final LockManager lockManager;
//
//    public LockController(NodeContext context, LockManager lockManager) {
//        this.context = context;
//        this.lockManager = lockManager;
//    }
//
//    @PostMapping("/acquire")
//    public ResponseEntity<?> acquire(@RequestParam String resource) {
//
//        if (context.getState() != NodeState.LEADER) {
//            return ResponseEntity.status(403)
//                    .body("Not leader. Leader is: " + context.getLeaderId());
//        }
//
//        boolean acquired = lockManager.tryAcquire(
//                resource,
//                context.getNodeId(),
//                10_000 // 10 sec TTL
//        );
//
//        return acquired
//                ? ResponseEntity.ok("LOCK_GRANTED")
//                : ResponseEntity.status(409).body("LOCK_BUSY");
//    }
//}
@RestController
@RequestMapping("/lock")
public class LockController {

    private final NodeContext context;
    private final LockManager lockManager;

    public LockController(NodeContext context, LockManager lockManager) {
        this.context = context;
        this.lockManager = lockManager;
    }

    @PostMapping("/acquire/{resource}")
    public boolean acquire(@PathVariable String resource) {

        if (context.getState() != NodeState.LEADER) {
            throw new IllegalStateException(
                    "Not leader. Leader is " + context.getLeaderId()
            );
        }

        return lockManager.tryAcquire(
                resource,
                context.getNodeId(),
                5000
        );
    }
}