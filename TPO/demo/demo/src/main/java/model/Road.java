package com.rutear.demo.model;

import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;

@RelationshipProperties
public class Road {
  @Id @GeneratedValue
  private Long id;

  private double distance;   // en metros
  private double traffic;    // 0..1
  private double risk;       // 0..1
  private double timePenalty;// segundos extra

  @TargetNode
  private Corner to;

  public Road() {}
  public Road(Corner to, double distance, double traffic, double risk, double timePenalty) {
    this.to = to; this.distance = distance; this.traffic = traffic; this.risk = risk; this.timePenalty = timePenalty;
  }
public Long getId() {
    return id;
}

public void setId(Long id) {
    this.id = id;
}

public double getDistance() {
    return distance;
}

public void setDistance(double distance) {
    this.distance = distance;
}

public double getTraffic() {
    return traffic;
}

public void setTraffic(double traffic) {
    this.traffic = traffic;
}

public double getRisk() {
    return risk;
}

public void setRisk(double risk) {
    this.risk = risk;
}

public double getTimePenalty() {
    return timePenalty;
}

public void setTimePenalty(double timePenalty) {
    this.timePenalty = timePenalty;
}

public Corner getTo() {
    return to;
}

public void setTo(Corner to) {
    this.to = to;
}
  // getters/setters
}
