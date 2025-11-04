package com.rutear.demo.dto;

import java.util.List;

/**
 * Representa un segmento de ruta (de un punto a otro)
 */
public class RouteSegmentDTO {
    private String from;
    private String to;
    private String orderIdRelated;  // ID del pedido relacionado
    private List<String> path;      // Secuencia de Corner IDs
    private double distance;
    private int time;

    // Getters y Setters
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getOrderIdRelated() {
        return orderIdRelated;
    }

    public void setOrderIdRelated(String orderIdRelated) {
        this.orderIdRelated = orderIdRelated;
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
