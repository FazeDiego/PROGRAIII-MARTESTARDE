package com.rutear.demo.dto;

import java.util.List;

/**
 * Request para asignar pedidos a repartidores
 */
public class AssignmentRequestDTO {
    private List<DeliveryPersonDTO> deliveryPersons;
    private List<OrderDTO> orders;
    private String mode;  // "FAST" o "SAFE" para el cálculo de rutas

    public AssignmentRequestDTO() {
        this.mode = "SAFE";
    }

    // Getters y Setters
    public List<DeliveryPersonDTO> getDeliveryPersons() {
        return deliveryPersons;
    }

    public void setDeliveryPersons(List<DeliveryPersonDTO> deliveryPersons) {
        this.deliveryPersons = deliveryPersons;
    }

    public List<OrderDTO> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderDTO> orders) {
        this.orders = orders;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
