package com.rutear.demo.controller;

import com.rutear.demo.repository.CornerRepository;
import com.rutear.demo.repository.GraphDao;
import com.rutear.demo.service.GraphService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/graph")
public class GraphController {

  private final GraphDao dao;
  private final CornerRepository repo;
  private final GraphService graphService;

  public GraphController(GraphDao dao, CornerRepository repo, GraphService graphService) {
    this.dao = dao;
    this.repo = repo;
    this.graphService = graphService;
  }

  // --- Vecinos (debug) ---
  @GetMapping(value = "/neighbors/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> neighbors(@PathVariable String id) {
    try {
      return ResponseEntity.ok(dao.neighbors(id));
    } catch (Exception e) {
      return ResponseEntity.status(500).body(
          Map.of("error", e.getClass().getSimpleName(), "message", e.getMessage())
      );
    }
  }

  // --- Listar todos los Corner ---
  @GetMapping(value = "/corners", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> allCorners() {
    try {
      return ResponseEntity.ok(repo.findAll());
    } catch (Exception e) {
      return ResponseEntity.status(500).body(
          Map.of("error", e.getClass().getSimpleName(), "message", e.getMessage())
      );
    }
  }

  // --- BFS ---
  @GetMapping(value = "/bfs", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> bfs(@RequestParam String start) {
    try {
      return ResponseEntity.ok(graphService.bfs(start));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(
          Map.of("error", e.getClass().getSimpleName(), "message", e.getMessage())
      );
    }
  }

  // --- DFS ---
  @GetMapping(value = "/dfs", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> dfs(@RequestParam String start) {
    try {
      return ResponseEntity.ok(graphService.dfs(start));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(
          Map.of("error", e.getClass().getSimpleName(), "message", e.getMessage())
      );
    }
  }
}
