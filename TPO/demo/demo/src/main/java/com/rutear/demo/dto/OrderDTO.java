package com.rutear.demo.dto;

/**
 * Representa un pedido con origen y destino
 */
public class OrderDTO {
    private String id;
    private String pickupLocationId;   // Corner de origen (donde se recoge)
    private String deliveryLocationId; // Corner de destino (donde se entrega)
    private int priority;              // Prioridad del pedido (mayor = más urgente)
    private String description;

    public OrderDTO() {
        this.priority = 1;
    }

    public OrderDTO(String id, String pickupLocationId, String deliveryLocationId) {
        this.id = id;
        this.pickupLocationId = pickupLocationId;
        this.deliveryLocationId = deliveryLocationId;
        this.priority = 1;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPickupLocationId() {
        return pickupLocationId;
    }

    public void setPickupLocationId(String pickupLocationId) {
        this.pickupLocationId = pickupLocationId;
    }

    public String getDeliveryLocationId() {
        return deliveryLocationId;
    }

    public void setDeliveryLocationId(String deliveryLocationId) {
        this.deliveryLocationId = deliveryLocationId;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
