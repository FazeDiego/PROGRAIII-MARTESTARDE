package com.rutear.demo.dto;

public class EdgeDTO {
  private String fromId;
  private String toId;
  private double distance;
  private double traffic;
  private double risk;
  private double timePenalty;

  public EdgeDTO() {}
  public EdgeDTO(String fromId, String toId, double distance, double traffic, double risk, double timePenalty) {
    this.fromId = fromId; this.toId = toId;
    this.distance = distance; this.traffic = traffic; this.risk = risk; this.timePenalty = timePenalty;
  }
  // getters/setters
}
