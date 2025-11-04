package com.rutear.demo.dto;

import java.util.List;

public class CourierAssignment {
  private String courierId; // generated id like R1
  private String cornerId; // corner where courier is located
  private List<String> orders; // assigned order corner ids

  public CourierAssignment() {}

  public CourierAssignment(String courierId, String cornerId, List<String> orders) {
    this.courierId = courierId; this.cornerId = cornerId; this.orders = orders;
  }

  public String getCourierId() { return courierId; }
  public void setCourierId(String courierId) { this.courierId = courierId; }
  public String getCornerId() { return cornerId; }
  public void setCornerId(String cornerId) { this.cornerId = cornerId; }
  public List<String> getOrders() { return orders; }
  public void setOrders(List<String> orders) { this.orders = orders; }
}
