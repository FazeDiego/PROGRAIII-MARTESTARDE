package com.rutear.demo.controller;

import com.rutear.demo.dto.CornerDTO;
import com.rutear.demo.dto.PoiDTO;
import com.rutear.demo.mapper.GraphMapper;
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

  // --- Obtener 1 corner por id (DTO liviano) ---
  @GetMapping(value = "/corner/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getCorner(@PathVariable String id) {
    try {
      var corner = repo.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("Corner no encontrado: " + id));
      CornerDTO dto = GraphMapper.toDTO(corner);
      return ResponseEntity.ok(dto);
    } catch (Exception e) {
      return ResponseEntity.status(404).body(
          Map.of("error", e.getClass().getSimpleName(), "message", e.getMessage())
      );
    }
  }

  // --- Listar corners (DTOs) con límite (evita JSON recursivo) ---
  @GetMapping(value = "/corners", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> allCorners(@RequestParam(defaultValue = "100") int limit) {
    try {
      return ResponseEntity.ok(dao.allCorners(Math.max(1, limit)));
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

  // --- NUEVO: POIs cercanos vía BFS ---
  @GetMapping(value = "/nearby", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> nearby(
      @RequestParam String start,
      @RequestParam(required = false) String types,
      @RequestParam(name = "maxDepth", defaultValue = "3") int maxDepth,
      @RequestParam(name = "limit", defaultValue = "6") int limit
  ) {
    try {
      return ResponseEntity.ok(graphService.bfsNearby(start, types, maxDepth, limit));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(
          Map.of("error", e.getClass().getSimpleName(), "message", e.getMessage())
      );
    }
  }

  // --- NUEVO: POIs cercanos con regex ---
  @GetMapping(value = "/pois/near", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> poisNear(
      @RequestParam String start,
      @RequestParam(defaultValue = "3") int depth,
      @RequestParam(defaultValue = "GAS|MECH|ER") String types) {
    try {
      var list = graphService.bfsPois(start, depth, types);
      return ResponseEntity.ok(list);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(
          Map.of("error", e.getClass().getSimpleName(), "message", e.getMessage())
      );
    }
  }
}
