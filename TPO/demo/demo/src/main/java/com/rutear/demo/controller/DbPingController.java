package com.rutear.demo.controller;

import org.springframework.http.ResponseEntity;
import com.rutear.demo.repository.CornerRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DbPingController {
  private final CornerRepository repo;
  public DbPingController(CornerRepository repo){ this.repo = repo; }

  @GetMapping("/db/ping")
  public ResponseEntity<String> dbPing() {
    try {
      long n = repo.countCorners();
      return ResponseEntity.ok("Neo4j OK · corners=" + n);
    } catch (Exception e) {
      return ResponseEntity.status(500)
          .body("Neo4j ERROR: " + e.getClass().getSimpleName() + " - " + e.getMessage());
    }
  }
}
