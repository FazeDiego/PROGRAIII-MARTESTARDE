package com.rutear.demo.dto;

import java.util.List;

public class AssignmentRequest {
  private List<String> orders; // corner ids where orders start
  private int numCouriers = 3;
  private int maxPerCourier = 2;

  public AssignmentRequest() {}

  public List<String> getOrders() { return orders; }
  public void setOrders(List<String> orders) { this.orders = orders; }
  public int getNumCouriers() { return numCouriers; }
  public void setNumCouriers(int numCouriers) { this.numCouriers = numCouriers; }
  public int getMaxPerCourier() { return maxPerCourier; }
  public void setMaxPerCourier(int maxPerCourier) { this.maxPerCourier = maxPerCourier; }
}
