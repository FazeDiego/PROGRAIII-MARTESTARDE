package com.rutear.demo.service;

import com.rutear.demo.dto.AssignmentRequest;
import com.rutear.demo.dto.AssignmentResponse;
import com.rutear.demo.dto.CourierAssignment;
import com.rutear.demo.dto.PathResponse;
import com.rutear.demo.model.Corner;
import com.rutear.demo.repository.CornerRepository;
import com.rutear.demo.util.CostMode;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AssignmentServiceImpl implements AssignmentService {

  private final CornerRepository cornerRepo;
  private final RoutingService routing;

  public AssignmentServiceImpl(CornerRepository cornerRepo, RoutingService routing) {
    this.cornerRepo = cornerRepo;
    this.routing = routing;
  }

  @Override
  public AssignmentResponse assign(AssignmentRequest req) {
    List<String> orders = req.getOrders() == null ? Collections.emptyList() : req.getOrders();
    int numCouriers = Math.max(1, req.getNumCouriers());
    int maxPer = Math.max(1, req.getMaxPerCourier());

    List<Corner> corners = new ArrayList<>();
    cornerRepo.findAll().forEach(corners::add);
    if (corners.isEmpty()) return new AssignmentResponse(Collections.emptyList());

    Random rnd = new Random();
    // pick random distinct courier positions
    List<String> courierPositions = new ArrayList<>();
    Set<Integer> picked = new HashSet<>();
    while (courierPositions.size() < numCouriers) {
      int i = rnd.nextInt(corners.size());
      if (picked.add(i)) courierPositions.add(corners.get(i).getId());
    }

    // initialize assignments
    List<CourierAssignment> couriers = new ArrayList<>();
    for (int i = 0; i < courierPositions.size(); i++) {
      couriers.add(new CourierAssignment("R" + (i+1), courierPositions.get(i), new ArrayList<>()));
    }

    // For each order, find nearest courier with capacity
    for (String orderCorner : orders) {
      double bestDist = Double.MAX_VALUE;
      int bestIdx = -1;
      for (int i = 0; i < couriers.size(); i++) {
        CourierAssignment c = couriers.get(i);
        if (c.getOrders().size() >= maxPer) continue;
        try {
          PathResponse pr = routing.dijkstra(c.getCornerId(), orderCorner, CostMode.FAST);
          double dist = pr != null ? pr.getTotalDistance() : Double.MAX_VALUE;
          if (dist < bestDist) { bestDist = dist; bestIdx = i; }
        } catch (Exception e) {
          // If routing fails, skip this courier for this order
        }
      }
      if (bestIdx >= 0) {
        couriers.get(bestIdx).getOrders().add(orderCorner);
      }
      // if no courier had capacity or reachable, order remains unassigned (ignored here)
    }

    return new AssignmentResponse(couriers);
  }
}
