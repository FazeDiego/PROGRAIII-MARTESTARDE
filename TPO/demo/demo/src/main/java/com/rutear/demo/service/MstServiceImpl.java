package com.rutear.demo.service;

import com.rutear.demo.dto.MstResponse;
import com.rutear.demo.dto.NeighborRow;
import com.rutear.demo.repository.CornerRepository;
import com.rutear.demo.repository.GraphDao;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MstServiceImpl implements MstService {

  private final CornerRepository repo;
  private final GraphDao dao;

  public MstServiceImpl(CornerRepository repo, GraphDao dao){
    this.repo = repo;
    this.dao = dao;
  }

  static class Candidate {
    final String u, v; final double w;
    Candidate(String u, String v, double w){ this.u=u; this.v=v; this.w=w; }
  }

  @Override
  public MstResponse prim(String startId, int maxNodes) {
    if (!repo.existsById(startId)) throw new IllegalArgumentException("Nodo inexistente: " + startId);

    Set<String> in = new HashSet<>();
    PriorityQueue<Candidate> pq = new PriorityQueue<>(Comparator.comparingDouble(c -> c.w));
    List<MstResponse.MstEdge> picked = new ArrayList<>();
    double total = 0;

    // seed
    in.add(startId);
    pushNeighbors(startId, in, pq);

    while (!pq.isEmpty() && in.size() < maxNodes) {
      var c = pq.poll();
      if (in.contains(c.v)) continue;             // ya conectado
      in.add(c.v);
      picked.add(new MstResponse.MstEdge(c.u, c.v, c.w));
      total += c.w;
      pushNeighbors(c.v, in, pq);
    }

    return new MstResponse(picked, total, in.size());
  }

  private void pushNeighbors(String u, Set<String> in, PriorityQueue<Candidate> pq){
    Collection<NeighborRow> neighs = dao.neighbors(u); // tratamos ROAD como no dirigido
    for (var nb : neighs) {
      if (!in.contains(nb.toId())) {
        // peso del MST: podés elegir distance o una combinación
        double w = nb.distance(); // simple: distancia
        pq.offer(new Candidate(u, nb.toId(), w));
      }
    }
  }
}
