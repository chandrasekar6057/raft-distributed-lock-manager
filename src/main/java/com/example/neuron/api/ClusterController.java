package com.example.neuron.api;

import com.example.neuron.api.dto.ClusterStatusDTO;
import com.example.neuron.core.NodeContext;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cluster")
@CrossOrigin(origins = {"http://localhost:5173", "http://internal-dashboard.local"},
        allowedHeaders = "*",
        allowCredentials = "true")
public class ClusterController {

    private final NodeContext context;

    public ClusterController(NodeContext context) {
        this.context = context;
    }

    @GetMapping("/status")
    public ClusterStatusDTO status() {
        return new ClusterStatusDTO(
                context.getNodeId(),
                context.getState().name(),
                context.getCurrentTerm(),
                context.getLeaderId()
        );
    }
}
