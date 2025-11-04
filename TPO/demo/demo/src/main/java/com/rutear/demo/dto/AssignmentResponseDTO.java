package com.rutear.demo.dto;

import java.util.List;

/**
 * Respuesta con todas las asignaciones realizadas
 */
public class AssignmentResponseDTO {
    private List<DeliveryAssignmentDTO> assignments;
    private List<OrderDTO> unassignedOrders;  // Pedidos que no se pudieron asignar
    private String algorithm;  // "GREEDY"
    private long computationTimeMs;

    // Getters y Setters
    public List<DeliveryAssignmentDTO> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<DeliveryAssignmentDTO> assignments) {
        this.assignments = assignments;
    }

    public List<OrderDTO> getUnassignedOrders() {
        return unassignedOrders;
    }

    public void setUnassignedOrders(List<OrderDTO> unassignedOrders) {
        this.unassignedOrders = unassignedOrders;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public long getComputationTimeMs() {
        return computationTimeMs;
    }

    public void setComputationTimeMs(long computationTimeMs) {
        this.computationTimeMs = computationTimeMs;
    }
}
