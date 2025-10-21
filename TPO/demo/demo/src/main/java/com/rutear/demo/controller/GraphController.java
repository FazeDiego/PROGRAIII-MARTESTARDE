package com.rutear.demo.controller;

import com.rutear.demo.repository.CornerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/graph")
public class GraphController {

  private final CornerRepository repo;

  public GraphController(CornerRepository repo) {
    this.repo = repo;
  }

  // Endpoint de debug: vecinos salientes de un Corner
  @GetMapping("/neighbors/{id}")
  public ResponseEntity<?> neighbors(@PathVariable String id) {
    try {
      var list = repo.neighbors(id).stream().map(n -> Map.of(
          "toId", n.getToId(),
          "distance", n.getDistance(),
          "traffic", n.getTraffic(),
          "risk", n.getRisk(),
          "timePenalty", n.getTimePenalty()
      )).toList();
      return ResponseEntity.ok(list);
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of(
          "error", e.getClass().getSimpleName(),
          "message", e.getMessage()
      ));
    }
  }
}
