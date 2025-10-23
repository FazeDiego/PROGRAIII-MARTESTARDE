package com.rutear.demo.repository.projection;

public interface NeighborProjection {
  String getToId();
  double getDistance();
  double getTraffic();
  double getRisk();
  double getTimePenalty();
}
