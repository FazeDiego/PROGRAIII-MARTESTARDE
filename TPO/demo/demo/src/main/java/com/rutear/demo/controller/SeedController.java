package com.rutear.demo.controller;

import com.rutear.demo.repository.CornerRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SeedController {
  private final CornerRepository repo;
  public SeedController(CornerRepository repo){ this.repo = repo; }

  @PostMapping("/seed")
  public String seed(){
    // Inserta un par de aristas de ejemplo (id, name, lat, lng) -> (id, name, lat, lng, distance, traffic, risk, timePenalty)
    repo.upsertRoad("A","A",0.0,0.0,
                    "B","B",0.0,0.0,
                    1.0,0.1,0.01,0.0);
    repo.upsertRoad("B","B",0.0,0.0,
                    "C","C",0.0,0.0,
                    2.0,0.2,0.02,0.0);
    return "seeded";
  }
}
