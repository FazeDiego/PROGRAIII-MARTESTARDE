package com.rutear.demo.service;

import com.rutear.demo.dto.PathResponse;
import com.rutear.demo.util.CostMode;

public interface RoutingService {
  PathResponse dijkstra(String from, String to, CostMode mode);
  PathResponse astar(String from, String to, CostMode mode);
}
