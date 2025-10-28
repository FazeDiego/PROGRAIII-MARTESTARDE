package com.rutear.demo.service;

import com.rutear.demo.dto.TraversalResponse;
import com.rutear.demo.repository.CornerRepository;
import com.rutear.demo.repository.GraphDao;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GraphServiceImpl implements GraphService {

  private final CornerRepository repo;
  private final GraphDao dao;

  public GraphServiceImpl(CornerRepository repo, GraphDao dao) {
    this.repo = repo;
    this.dao = dao;
  }

  @Override
  public TraversalResponse bfs(String startId) {
    if (!repo.existsById(startId))
      throw new IllegalArgumentException("Nodo inexistente: " + startId);

    Queue<String> q = new ArrayDeque<>();
    Set<String> vis = new HashSet<>();
    List<String> order = new ArrayList<>();
    List<List<String>> levels = new ArrayList<>();

    q.add(startId);
    vis.add(startId);

    while (!q.isEmpty()) {
      int sz = q.size();
      List<String> level = new ArrayList<>(sz);
      for (int i = 0; i < sz; i++) {
        String u = q.poll();
        level.add(u);
        order.add(u);
        dao.neighbors(u).forEach(nb -> {
          String v = nb.toId();
          if (vis.add(v)) q.add(v);
        });
      }
      levels.add(level);
    }
    return new TraversalResponse(startId, "BFS", order, levels);
  }

  @Override
  public TraversalResponse dfs(String startId) {
    if (!repo.existsById(startId))
      throw new IllegalArgumentException("Nodo inexistente: " + startId);

    List<String> order = new ArrayList<>();
    Set<String> vis = new HashSet<>();
    Deque<String> st = new ArrayDeque<>();
    st.push(startId);

    while (!st.isEmpty()) {
      String u = st.pop();
      if (!vis.add(u)) continue;
      order.add(u);

      var neighs = new ArrayList<>(dao.neighbors(u)); // Collection -> ArrayList
      Collections.reverse(neighs); // para que se parezca al recursivo
      neighs.forEach(nb -> {
        if (!vis.contains(nb.toId())) st.push(nb.toId());
      });
    }
    return new TraversalResponse(startId, "DFS", order, null);
  }
}
