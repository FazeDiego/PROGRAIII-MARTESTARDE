package com.rutear.demo.controller;

import com.rutear.demo.repository.CornerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import com.rutear.demo.repository.GraphDao;

@RestController
@RequestMapping("/graph")
public class GraphController {

  private final GraphDao dao;

  public GraphController(GraphDao dao) {
    this.dao = dao;
  }

  @GetMapping("/neighbors/{id}")
  public ResponseEntity<?> neighbors(@PathVariable String id) {
    try {
      return ResponseEntity.ok(dao.neighbors(id));
    } catch (Exception e) {
      return ResponseEntity.status(500).body(
          java.util.Map.of("error", e.getClass().getSimpleName(), "message", e.getMessage())
      );
    }
  }
}
