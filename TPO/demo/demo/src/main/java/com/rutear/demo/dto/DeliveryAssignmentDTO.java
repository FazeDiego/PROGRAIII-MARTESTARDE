package com.rutear.demo.dto;

import java.util.List;

/**
 * Representa la asignación de pedidos a un repartidor específico
 */
public class DeliveryAssignmentDTO {
    private DeliveryPersonDTO deliveryPerson;
    private List<OrderDTO> assignedOrders;
    private List<RouteSegmentDTO> routes;  // Rutas calculadas con Dijkstra
    private double totalDistance;
    private int totalTime;

    // Getters y Setters
    public DeliveryPersonDTO getDeliveryPerson() {
        return deliveryPerson;
    }

    public void setDeliveryPerson(DeliveryPersonDTO deliveryPerson) {
        this.deliveryPerson = deliveryPerson;
    }

    public List<OrderDTO> getAssignedOrders() {
        return assignedOrders;
    }

    public void setAssignedOrders(List<OrderDTO> assignedOrders) {
        this.assignedOrders = assignedOrders;
    }

    public List<RouteSegmentDTO> getRoutes() {
        return routes;
    }

    public void setRoutes(List<RouteSegmentDTO> routes) {
        this.routes = routes;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }
}
