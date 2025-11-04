package com.rutear.demo.dto;

import java.util.List;

public class AssignmentResponse {
  private List<CourierAssignment> couriers;

  public AssignmentResponse() {}
  public AssignmentResponse(List<CourierAssignment> couriers) { this.couriers = couriers; }
  public List<CourierAssignment> getCouriers() { return couriers; }
  public void setCouriers(List<CourierAssignment> couriers) { this.couriers = couriers; }
}
