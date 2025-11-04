package com.rutear.demo.dto;

/**
 * Representa un repartidor con su ubicación actual
 */
public class DeliveryPersonDTO {
    private String id;
    private String name;
    private String currentLocationId;  // ID del Corner donde está actualmente
    private Double lat;  // Para visualización en el mapa
    private Double lon;  // Para visualización en el mapa
    private int maxOrders;  // Máximo de pedidos que puede llevar

    public DeliveryPersonDTO() {
        this.maxOrders = 2;  // Por defecto 2 pedidos
    }

    public DeliveryPersonDTO(String id, String name, String currentLocationId) {
        this.id = id;
        this.name = name;
        this.currentLocationId = currentLocationId;
        this.maxOrders = 2;
    }

    // Getters y Setters
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

    public String getCurrentLocationId() {
        return currentLocationId;
    }

    public void setCurrentLocationId(String currentLocationId) {
        this.currentLocationId = currentLocationId;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public int getMaxOrders() {
        return maxOrders;
    }

    public void setMaxOrders(int maxOrders) {
        this.maxOrders = maxOrders;
    }
}
