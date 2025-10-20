package com.rutear.demo.dto;

import java.util.List;

public class PathResponse {
  private List<CornerDTO> nodes;  // ordenados como recorre el camino
  private List<EdgeDTO> edges;    // aristas del camino
  private double totalCost;
  private double totalDistance;
  private double totalTimePenalty;
  private double totalRisk;

  public PathResponse() {}
  public PathResponse(List<CornerDTO> nodes, List<EdgeDTO> edges,
                      double totalCost, double totalDistance, double totalTimePenalty, double totalRisk) {
    this.nodes = nodes; this.edges = edges;
    this.totalCost = totalCost; this.totalDistance = totalDistance;
    this.totalTimePenalty = totalTimePenalty; this.totalRisk = totalRisk;
  }
  // getters/setters
}
