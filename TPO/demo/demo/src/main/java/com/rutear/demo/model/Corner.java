package com.rutear.demo.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import java.util.Set;

@Node
public class Corner {
  @Id
  private String id; // ej: "C1"
  private String name;
  private double lat;
  private double lng;

  @Relationship(type = "ROAD", direction = Relationship.Direction.OUTGOING)
  private Set<Road> roads;

  public Corner() {}
  public Corner(String id, String name, double lat, double lng) {
    this.id = id; this.name = name; this.lat = lat; this.lng = lng;
  }
public String getId() {
    return id;
}

public void setId(String id) {
    this.id = id;
}

public String getName() {
    return name;
}

public void setName(String name) {
    this.name = name;
}

public double getLat() {
    return lat;
}

public void setLat(double lat) {
    this.lat = lat;
}

public double getLng() {
    return lng;
}

public void setLng(double lng) {
    this.lng = lng;
}

public Set<Road> getRoads() {
    return roads;
}

public void setRoads(Set<Road> roads) {
    this.roads = roads;
}
  // getters/setters
}
