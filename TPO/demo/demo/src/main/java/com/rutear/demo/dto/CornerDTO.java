package com.rutear.demo.dto;

public class CornerDTO {
  private String id;
  private String name;
  private double lat;
  private double lng;

  public CornerDTO() {}
  public CornerDTO(String id, String name, double lat, double lng) {
    this.id = id; this.name = name; this.lat = lat; this.lng = lng;
  }
  // getters/setters
}
