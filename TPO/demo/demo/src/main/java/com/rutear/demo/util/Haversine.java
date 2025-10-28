package com.rutear.demo.util;

public final class Haversine {
  private static final double R = 6371000.0; // radio tierra en metros
  private Haversine() {}
  public static double meters(double lat1, double lng1, double lat2, double lng2) {
    double dLat = Math.toRadians(lat2 - lat1);
    double dLng = Math.toRadians(lng2 - lng1);
    double a = Math.sin(dLat/2)*Math.sin(dLat/2) +
               Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2)) *
               Math.sin(dLng/2)*Math.sin(dLng/2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    return R * c;
  }
}
