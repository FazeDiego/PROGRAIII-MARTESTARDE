package com.rutear.demo.dto;

public class PoiDTO {
  private String id;
  private String name;
  private String type;
  private double lat, lng;
  private int depth; // en cuántos “saltos” de BFS se encontró

  public PoiDTO(String id, String name, String type, double lat, double lng, int depth) {
    this.id = id; this.name = name; this.type = type; this.lat = lat; this.lng = lng; this.depth = depth;
  }
  public PoiDTO() {}
  // getters/setters
  public String getId(){return id;} public void setId(String id){this.id=id;}
  public String getName(){return name;} public void setName(String name){this.name=name;}
  public String getType(){return type;} public void setType(String type){this.type=type;}
  public double getLat(){return lat;} public void setLat(double lat){this.lat=lat;}
  public double getLng(){return lng;} public void setLng(double lng){this.lng=lng;}
  public int getDepth(){return depth;} public void setDepth(int depth){this.depth=depth;}
}
