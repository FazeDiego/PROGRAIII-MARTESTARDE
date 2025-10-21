package com.rutear.demo.service;

import com.rutear.demo.dto.CornerDTO;
import com.rutear.demo.dto.EdgeDTO;
import com.rutear.demo.dto.PathResponse;
import com.rutear.demo.mapper.GraphMapper;
import com.rutear.demo.model.Corner;
import com.rutear.demo.repository.CornerRepository;
import com.rutear.demo.util.CostFunction;
import com.rutear.demo.util.CostMode;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RoutingServiceImpl implements RoutingService {

  private final CornerRepository repo;

  public RoutingServiceImpl(CornerRepository repo) {
    this.repo = repo;
  }

  static final class State {
    final String id;
    final double cost;
    State(String id, double cost) { this.id = id; this.cost = cost; }
  }

  @Override
  public PathResponse dijkstra(String from, String to, CostMode mode) {
    // validaciones básicas
    if (!repo.existsById(from)) throw new IllegalArgumentException("Nodo origen inexistente: " + from);
    if (!repo.existsById(to))   throw new IllegalArgumentException("Nodo destino inexistente: " + to);
    if (from.equals(to)) {
      Corner only = repo.findById(from).orElseThrow();
      return new PathResponse(
          List.of(GraphMapper.toDTO(only)),
          List.of(), 0, 0, 0, 0
      );
    }

    // estructuras
    PriorityQueue<State> pq = new PriorityQueue<>(Comparator.comparingDouble(s -> s.cost));
    Map<String, Double> dist = new HashMap<>();
    Map<String, String> prev = new HashMap<>();
    Set<String> vis = new HashSet<>();

    // para reconstruir aristas y métricas
    record EdgeData(String from, String to, double distance, double traffic, double risk, double timePenalty) {}
    Map<String, EdgeData> how = new HashMap<>();

    dist.put(from, 0.0);
    pq.offer(new State(from, 0.0));

    while (!pq.isEmpty()) {
      State cur = pq.poll();
      if (!vis.add(cur.id)) continue;     // ya procesado
      if (cur.id.equals(to)) break;       // llegamos al destino

      // expandir vecinos desde la DB (proyección)
      var neighs = repo.neighbors(cur.id);
      for (var nb : neighs) {
        double w = CostFunction.cost(nb.getDistance(), nb.getTraffic(), nb.getRisk(), nb.getTimePenalty(), mode);
        double alt = dist.get(cur.id) + w;

        if (alt < dist.getOrDefault(nb.getToId(), Double.POSITIVE_INFINITY)) {
          dist.put(nb.getToId(), alt);
          prev.put(nb.getToId(), cur.id);
          how.put(nb.getToId(), new EdgeData(
              cur.id, nb.getToId(), nb.getDistance(), nb.getTraffic(), nb.getRisk(), nb.getTimePenalty()
          ));
          pq.offer(new State(nb.getToId(), alt));
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

    // cargar nodos completos y mapear a DTO
    List<CornerDTO> nodes = new ArrayList<>();
    for (String id : pathIds) {
      Corner c = repo.findById(id).orElseThrow();
      nodes.add(GraphMapper.toDTO(c));
    }

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

    double totalCost = dist.get(to);
    return new PathResponse(nodes, edges, totalCost, totalDist, totalTime, totalRisk);
  }
}
