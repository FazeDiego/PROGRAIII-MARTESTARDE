package com.rutear.demo.service;

import com.rutear.demo.dto.TraversalResponse;

public interface GraphService {
  TraversalResponse bfs(String startId);
  TraversalResponse dfs(String startId);
}
