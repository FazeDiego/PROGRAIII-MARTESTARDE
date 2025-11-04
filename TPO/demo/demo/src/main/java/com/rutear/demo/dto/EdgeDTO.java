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

  public String getFromId() { return fromId; }
  public void setFromId(String fromId) { this.fromId = fromId; }
  public String getToId() { return toId; }
  public void setToId(String toId) { this.toId = toId; }
  public double getDistance() { return distance; }
  public void setDistance(double distance) { this.distance = distance; }
  public double getTraffic() { return traffic; }
  public void setTraffic(double traffic) { this.traffic = traffic; }
  public double getRisk() { return risk; }
  public void setRisk(double risk) { this.risk = risk; }
  public double getTimePenalty() { return timePenalty; }
  public void setTimePenalty(double timePenalty) { this.timePenalty = timePenalty; }
}
