package com.rutear.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

public class PathResponse {
  @JsonIgnore // Evitar serialización que dispara lazy loading
  private List<CornerDTO> nodes;
  private List<String> nodeIds; // IDs de nodos para evitar lazy loading
  private List<EdgeDTO> edges;
  private double totalCost;
  private double totalDistance;
  private double totalTimePenalty;
  private double totalRisk;

  public PathResponse() {}

  public PathResponse(List<CornerDTO> nodes, List<EdgeDTO> edges,
                      double totalCost, double totalDistance,
                      double totalTimePenalty, double totalRisk) {
    this.nodes = nodes;
    this.edges = edges;
    this.totalCost = totalCost;
    this.totalDistance = totalDistance;
    this.totalTimePenalty = totalTimePenalty;
    this.totalRisk = totalRisk;
  }

  public List<CornerDTO> getNodes() { return nodes; }
  public void setNodes(List<CornerDTO> nodes) { this.nodes = nodes; }
  public List<String> getNodeIds() { return nodeIds; }
  public void setNodeIds(List<String> nodeIds) { this.nodeIds = nodeIds; }
  public List<EdgeDTO> getEdges() { return edges; }
  public void setEdges(List<EdgeDTO> edges) { this.edges = edges; }
  public double getTotalCost() { return totalCost; }
  public void setTotalCost(double totalCost) { this.totalCost = totalCost; }
  public double getTotalDistance() { return totalDistance; }
  public void setTotalDistance(double totalDistance) { this.totalDistance = totalDistance; }
  public double getTotalTimePenalty() { return totalTimePenalty; }
  public void setTotalTimePenalty(double totalTimePenalty) { this.totalTimePenalty = totalTimePenalty; }
  public double getTotalRisk() { return totalRisk; }
  public void setTotalRisk(double totalRisk) { this.totalRisk = totalRisk; }
}
