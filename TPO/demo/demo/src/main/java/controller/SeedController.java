package com.rutear.demo.controller;

import com.rutear.demo.repository.CornerRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DbPingController {
  private final CornerRepository repo;
  public DbPingController(CornerRepository repo){ this.repo = repo; }

  @GetMapping("/db/ping")
  public String dbPing(){
    long n = repo.countCorners();
    return "Neo4j OK · corners="+n;
  }
}
