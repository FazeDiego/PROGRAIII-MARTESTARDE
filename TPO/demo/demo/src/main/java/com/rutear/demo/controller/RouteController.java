package com.rutear.demo.controller;

import com.rutear.demo.dto.PathRequest;
import com.rutear.demo.dto.PathResponse;
import com.rutear.demo.service.RoutingService;
import com.rutear.demo.util.CostMode;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

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
}
