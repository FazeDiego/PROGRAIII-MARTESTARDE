package com.rutear.demo.controller;

import com.rutear.demo.dto.PathRequest;
import com.rutear.demo.dto.PathResponse;
import com.rutear.demo.service.RoutingService;
import com.rutear.demo.util.CostMode;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@RestController
@RequestMapping("/routes")
public class RouteController {
  private final RoutingService routing;
  public RouteController(RoutingService routing){ this.routing = routing; }

  @PostMapping("/dijkstra")
  public PathResponse dijkstra(@Valid @RequestBody PathRequest req){
    var mode = "SAFE".equalsIgnoreCase(req.getMode()) ? CostMode.SAFE : CostMode.FAST;
    return routing.dijkstra(req.getFrom(), req.getTo(), mode);
  }

// RouteController
// RouteController.java
@GetMapping("/dijkstra")
public ResponseEntity<?> dijkstraQuery(@RequestParam String from,
                                       @RequestParam String to,
                                       @RequestParam(defaultValue = "FAST") String mode) {
  try {
    var m = "SAFE".equalsIgnoreCase(mode)
        ? com.rutear.demo.util.CostMode.SAFE
        : com.rutear.demo.util.CostMode.FAST;
    return ResponseEntity.ok(routing.dijkstra(from, to, m));
  } catch (Exception e) {
    return ResponseEntity.status(500).body(
        Map.of("error", e.getClass().getSimpleName(), "message", e.getMessage())
    );
  }
}

}