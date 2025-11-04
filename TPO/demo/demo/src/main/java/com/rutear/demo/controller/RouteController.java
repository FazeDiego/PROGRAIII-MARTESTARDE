package com.rutear.demo.controller;

import com.rutear.demo.dto.PathRequest;
import com.rutear.demo.dto.PathResponse;
import com.rutear.demo.service.RoutingService;
import com.rutear.demo.util.CostMode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/routes")
public class RouteController {

  private final RoutingService routing;

  public RouteController(RoutingService routing) {
    this.routing = routing;
  }

  // --- DIJKSTRA (GET, para probar rápido desde el navegador) ---
  @GetMapping(value = "/dijkstra", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> dijkstraQuery(@RequestParam String from,
                                         @RequestParam String to,
                                         @RequestParam(defaultValue = "FAST") String mode) {
    try {
      CostMode m = "SAFE".equalsIgnoreCase(mode) ? CostMode.SAFE : CostMode.FAST;
      PathResponse resp = routing.dijkstra(from, to, m);
      return ResponseEntity.ok(resp);
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of(
          "error", e.getClass().getSimpleName(),
          "message", e.getMessage()
      ));
    }
  }

  // --- DIJKSTRA (POST, para front/Thunder) ---
  @PostMapping(value = "/dijkstra",
               consumes = MediaType.APPLICATION_JSON_VALUE,
               produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> dijkstra(@RequestBody PathRequest req) {
    try {
      CostMode m = "SAFE".equalsIgnoreCase(req.getMode()) ? CostMode.SAFE : CostMode.FAST;
      PathResponse resp = routing.dijkstra(req.getFrom(), req.getTo(), m);
      return ResponseEntity.ok(resp);
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of(
          "error", e.getClass().getSimpleName(),
          "message", e.getMessage()
      ));
    }
  }

  // --- A* (GET) ---
  @GetMapping(value = "/astar", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> astar(@RequestParam String from,
                                 @RequestParam String to,
                                 @RequestParam(defaultValue = "FAST") CostMode mode) {
    try {
      return ResponseEntity.ok(routing.astar(from, to, mode)); // <-- acá estaba el typo
    } catch (Exception e) {
      return ResponseEntity.status(400).body(Map.of(
          "error", e.getClass().getSimpleName(),
          "message", e.getMessage()
      ));
    }
  }
}
