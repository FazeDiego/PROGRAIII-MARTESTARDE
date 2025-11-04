package com.rutear.demo.service;

import com.rutear.demo.dto.PoiDTO;               // 👈 nuevo import
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

  // =========================================================
  // 🔍 NUEVO: BFS para encontrar POIs cercanos al startId
  // =========================================================
  public List<PoiDTO> bfsNearby(String startId, String typesCsv, int maxDepth, int limit) {
    if (!repo.existsById(startId))
      throw new IllegalArgumentException("Nodo inexistente: " + startId);
    if (maxDepth < 0) maxDepth = 0;
    if (limit <= 0) limit = 10;

    // Tipos a filtrar (puede ser null para "todos")
    List<String> types = GraphDao.parseTypes(typesCsv);

    // Cola con profundidad
    record Item(String id, int depth) {}
    Queue<Item> q = new ArrayDeque<>();
    Set<String> visCorners = new HashSet<>();

    // Usamos LinkedHashMap para evitar duplicados y preservar orden de hallazgo
    Map<String, PoiDTO> found = new LinkedHashMap<>();

    q.add(new Item(startId, 0));
    visCorners.add(startId);

    while (!q.isEmpty() && found.size() < limit) {
      Item cur = q.poll();

      // 1) POIs en la esquina actual
      var poisHere = dao.poisAtCorner(cur.id, types);
      for (var p : poisHere) {
        if (found.containsKey(p.getId())) continue;
        // seteamos profundidad donde se lo encontró
        p.setHops(cur.depth);
        found.put(p.getId(), p);
        if (found.size() >= limit) break;
      }
      if (found.size() >= limit) break;

      // 2) Expandir vecinos si no superamos profundidad
      if (cur.depth < maxDepth) {
        dao.neighbors(cur.id).forEach(nb -> {
          String v = nb.toId();
          if (visCorners.add(v)) {
            q.add(new Item(v, cur.depth + 1));
          }
        });
      }
    }

    return new ArrayList<>(found.values());
  }

  // =========================================================
  // 🔍 NUEVO: BFS para POIs usando GraphDao.poisNear
  // =========================================================
  @Override
  public List<PoiDTO> bfsPois(String startId, int maxDepth, java.util.Set<String> types) {
    if (!repo.existsById(startId))
      throw new IllegalArgumentException("Nodo inexistente: " + startId);
    return dao.poisNear(startId, maxDepth, types);
  }

  // =========================================================
  // 🔍 NUEVO: Buscar POIs cercanos simplificado
  // =========================================================
  @Override
  public List<PoiDTO> findNearbyPois(String startId, int depth, String typesCsv) {
    if (!repo.existsById(startId))
      throw new IllegalArgumentException("Nodo inexistente: " + startId);
    
    // Convertir CSV a array de strings
    String[] typesArray = null;
    if (typesCsv != null && !typesCsv.isBlank()) {
      typesArray = java.util.Arrays.stream(typesCsv.split("[,|]"))
          .map(String::trim)
          .filter(s -> !s.isEmpty())
          .toArray(String[]::new);
    }
    
    // Si no hay tipos, usar un array vacío o valores por defecto
    if (typesArray == null || typesArray.length == 0) {
      typesArray = new String[]{"GAS", "MECH", "ER"};
    }
    
    return dao.findNearbyPois(startId, depth, typesArray);
  }
}
