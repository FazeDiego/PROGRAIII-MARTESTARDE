package com.rutear.demo.service;

import com.rutear.demo.dto.CornerDTO;
import com.rutear.demo.dto.EdgeDTO;
import com.rutear.demo.dto.NeighborRow;
import com.rutear.demo.dto.PathResponse;
import com.rutear.demo.mapper.GraphMapper;
import com.rutear.demo.model.Corner;
import com.rutear.demo.repository.CornerRepository;
import com.rutear.demo.repository.GraphDao;
import com.rutear.demo.util.CostFunction;
import com.rutear.demo.util.CostMode;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.util.*;

@Service
public class RoutingServiceImpl implements RoutingService {

  private final CornerRepository repo;
  private final GraphDao dao;
  
  // CACHÉ EN MEMORIA: Map<nodoId, List<vecinos>>
  private Map<String, List<NeighborRow>> graphCache;

  public RoutingServiceImpl(CornerRepository repo, GraphDao dao) {
    this.repo = repo;
    this.dao = dao;
  }
  
  @PostConstruct
  public void loadGraphToMemory() {
    System.out.println("🔄 Cargando grafo completo en memoria...");
    long start = System.currentTimeMillis();
    
    List<GraphDao.GraphEdge> allEdges = dao.loadAllEdges();
    graphCache = new HashMap<>();
    
    for (var edge : allEdges) {
      graphCache.computeIfAbsent(edge.fromId(), k -> new ArrayList<>())
          .add(new NeighborRow(edge.toId(), edge.distance(), edge.traffic(), edge.risk(), edge.timePenalty()));
    }
    
    long elapsed = System.currentTimeMillis() - start;
    System.out.println("✅ Grafo cargado: " + allEdges.size() + " aristas, " + 
                       graphCache.size() + " nodos, " + elapsed + " ms");
  }

  static final class State {
    final String id;
    final double cost;
    State(String id, double cost) { this.id = id; this.cost = cost; }
  }

  // Para reconstrucción de aristas en ambas búsquedas
  private static record EdgeData(
      String from, String to, double distance, double traffic, double risk, double timePenalty
  ) {}

  @Override
  public PathResponse dijkstra(String from, String to, CostMode mode) {
    // Validaciones comentadas por rendimiento - si el nodo no existe, no habrá camino
    // if (!repo.existsById(from)) throw new IllegalArgumentException("Nodo origen inexistente: " + from);
    // if (!repo.existsById(to))   throw new IllegalArgumentException("Nodo destino inexistente: " + to);
    if (from.equals(to)) {
      // No cargar el Corner para evitar lazy loading
      PathResponse response = new PathResponse(List.of(), List.of(), 0, 0, 0, 0);
      response.setNodeIds(List.of(from)); // Un solo nodo
      return response;
    }

    PriorityQueue<State> pq = new PriorityQueue<>(Comparator.comparingDouble(s -> s.cost));
    Map<String, Double> dist = new HashMap<>();
    Map<String, String> prev = new HashMap<>();
    Set<String> vis = new HashSet<>();
    Map<String, EdgeData> how = new HashMap<>();

    dist.put(from, 0.0);
    pq.offer(new State(from, 0.0));

    while (!pq.isEmpty()) {
      State cur = pq.poll();
      if (!vis.add(cur.id)) continue;     // ya procesado
      if (cur.id.equals(to)) break;       // llegamos al destino

      // OPTIMIZACIÓN: Usar caché en memoria en lugar de consultar BD
      var neighs = graphCache.getOrDefault(cur.id, List.of());
      for (var nb : neighs) {
        double w = CostFunction.cost(nb.distance(), nb.traffic(), nb.risk(), nb.timePenalty(), mode);
        double alt = dist.get(cur.id) + w;

        if (alt < dist.getOrDefault(nb.toId(), Double.POSITIVE_INFINITY)) {
          dist.put(nb.toId(), alt);
          prev.put(nb.toId(), cur.id);
          how.put(nb.toId(), new EdgeData(
              cur.id, nb.toId(), nb.distance(), nb.traffic(), nb.risk(), nb.timePenalty()
          ));
          pq.offer(new State(nb.toId(), alt));
        }
      }
    }

    if (!dist.containsKey(to)) {
      throw new IllegalArgumentException("No existe camino de " + from + " a " + to);
    }

    // reconstrucción del camino (ids)
    List<String> pathIds = new ArrayList<>();
    for (String at = to; at != null; at = prev.get(at)) pathIds.add(at);
    Collections.reverse(pathIds);

    // NO cargar nodos para evitar lazy loading masivo
    // Los nodos se cargarán solo cuando se necesiten para visualización
    List<CornerDTO> nodes = new ArrayList<>();
    // COMENTADO: for (String id : pathIds) nodes.add(GraphMapper.toDTO(repo.findById(id).orElseThrow()));

    // aristas + totales
    List<EdgeDTO> edges = new ArrayList<>();
    double totalDist = 0, totalTime = 0, totalRisk = 0;
    for (int i = 1; i < pathIds.size(); i++) {
      var e = how.get(pathIds.get(i));
      if (e == null) {
        throw new IllegalStateException("Camino inconsistente al reconstruir " + pathIds.get(i-1) + "->" + pathIds.get(i));
      }
      edges.add(new EdgeDTO(e.from(), e.to(), e.distance(), e.traffic(), e.risk(), e.timePenalty()));
      totalDist += e.distance();
      totalTime += e.timePenalty();
      totalRisk += e.risk();
    }

    PathResponse response = new PathResponse(nodes, edges, dist.get(to), totalDist, totalTime, totalRisk);
    response.setNodeIds(new ArrayList<>(pathIds)); // Guardar IDs para evitar lazy loading
    return response;
  }

  // ==========================
  //           A*
  // ==========================
  @Override
  public PathResponse astar(String from, String to, CostMode mode) {
    // Validaciones comentadas por rendimiento - si el nodo no existe, no habrá camino
    // if (!repo.existsById(from)) throw new IllegalArgumentException("Nodo origen inexistente: " + from);
    // if (!repo.existsById(to))   throw new IllegalArgumentException("Nodo destino inexistente: " + to);

    record AState(String id, double g, double f) {}

    PriorityQueue<AState> open = new PriorityQueue<>(Comparator.comparingDouble(s -> s.f()));
    Map<String, Double> gScore = new HashMap<>();
    Map<String, String> prev = new HashMap<>();
    Map<String, EdgeData> how = new HashMap<>();

    // cache de coordenadas para la heurística
    Map<String, double[]> coord = new HashMap<>();
    Corner goal = repo.findById(to).orElseThrow();
    coord.put(goal.getId(), new double[]{goal.getLat(), goal.getLng()});

    // heurística admisible: distancia geodésica en metros
    java.util.function.Function<String, Double> h = (id) -> {
      double[] c1 = coord.computeIfAbsent(id, k -> {
        var c = repo.findById(k).orElseThrow();
        return new double[]{c.getLat(), c.getLng()};
      });
      double[] c2 = coord.get(goal.getId());
      return haversineMeters(c1[0], c1[1], c2[0], c2[1]);
    };

    gScore.put(from, 0.0);
    open.add(new AState(from, 0.0, h.apply(from)));

    // ponderación de la heurística (SAFE usa un poco menos para priorizar costo real)
    final double alpha = (mode == CostMode.SAFE) ? 0.7 : 1.0;

    while (!open.isEmpty()) {
      AState cur = open.poll();
      if (cur.id().equals(to)) break;

      // OPTIMIZACIÓN: Usar caché en memoria en lugar de consultar BD
      var neighs = graphCache.getOrDefault(cur.id(), List.of());
      for (var nb : neighs) {
        double w = CostFunction.cost(nb.distance(), nb.traffic(), nb.risk(), nb.timePenalty(), mode);
        double tentative = gScore.get(cur.id()) + w;

        if (tentative < gScore.getOrDefault(nb.toId(), Double.POSITIVE_INFINITY)) {
          gScore.put(nb.toId(), tentative);
          prev.put(nb.toId(), cur.id());
          how.put(nb.toId(), new EdgeData(
              cur.id(), nb.toId(), nb.distance(), nb.traffic(), nb.risk(), nb.timePenalty()
          ));
          double f = tentative + alpha * h.apply(nb.toId());
          open.add(new AState(nb.toId(), tentative, f));
        }
      }
    }

    if (!gScore.containsKey(to)) {
      throw new IllegalArgumentException("No existe camino de " + from + " a " + to);
    }

    // reconstrucción ids
    List<String> pathIds = new ArrayList<>();
    for (String at = to; at != null; at = prev.get(at)) pathIds.add(at);
    Collections.reverse(pathIds);

    // NO cargar nodos para evitar lazy loading masivo
    List<CornerDTO> nodes = new ArrayList<>();
    // COMENTADO: for (String id : pathIds) nodes.add(GraphMapper.toDTO(repo.findById(id).orElseThrow()));

    // aristas + totales
    List<EdgeDTO> edges = new ArrayList<>();
    double totalDist = 0, totalTime = 0, totalRisk = 0;
    for (int i = 1; i < pathIds.size(); i++) {
      var e = how.get(pathIds.get(i));
      edges.add(new EdgeDTO(e.from(), e.to(), e.distance(), e.traffic(), e.risk(), e.timePenalty()));
      totalDist += e.distance();
      totalTime += e.timePenalty();
      totalRisk += e.risk();
    }

    PathResponse response = new PathResponse(nodes, edges, gScore.get(to), totalDist, totalTime, totalRisk);
    response.setNodeIds(new ArrayList<>(pathIds)); // Guardar IDs para evitar lazy loading
    return response;
  }

  // ---- heurística local (metros) para no depender de otra clase ----
  private static double haversineMeters(double lat1, double lng1, double lat2, double lng2) {
    final double R = 6371000.0; // radio tierra en m
    double dLat = Math.toRadians(lat2 - lat1);
    double dLng = Math.toRadians(lng2 - lng1);
    double a = Math.sin(dLat/2)*Math.sin(dLat/2) +
        Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2)) *
        Math.sin(dLng/2)*Math.sin(dLng/2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    return R * c;
  }
}
