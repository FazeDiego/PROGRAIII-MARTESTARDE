package com.rutear.demo.dto;

import java.util.List;

public class TraversalResponse {
  private String start;
  private String algorithm;
  private List<String> order;
  private List<List<String>> levels; // null en DFS

  public TraversalResponse() {}

  public TraversalResponse(String start, String algorithm, List<String> order, List<List<String>> levels) {
    this.start = start;
    this.algorithm = algorithm;
    this.order = order;
    this.levels = levels;
  }

  public String getStart() { return start; }
  public void setStart(String start) { this.start = start; }
  public String getAlgorithm() { return algorithm; }
  public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
  public List<String> getOrder() { return order; }
  public void setOrder(List<String> order) { this.order = order; }
  public List<List<String>> getLevels() { return levels; }
  public void setLevels(List<List<String>> levels) { this.levels = levels; }
}
