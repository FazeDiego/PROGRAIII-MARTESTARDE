package com.rutear.demo.service;

import com.rutear.demo.dto.PoiDTO;
import com.rutear.demo.dto.TraversalResponse;
import java.util.List;

public interface GraphService {
  TraversalResponse bfs(String startId);
  TraversalResponse dfs(String startId);

  // 🔍 POIs cercanos vía BFS (firma única y final)
  List<PoiDTO> bfsNearby(String startId, String typesCsv, int maxDepth, int limit);
  
  // 🔍 POIs usando BFS con Set de tipos
  List<PoiDTO> bfsPois(String startId, int maxDepth, java.util.Set<String> types);
}
