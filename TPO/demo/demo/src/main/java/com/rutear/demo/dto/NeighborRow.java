package com.rutear.demo.dto;

// Java 17: record = getters implícitos (toId(), distance(), etc.)
public record NeighborRow(
    String toId,
    double distance,
    double traffic,
    double risk,
    double timePenalty
) {}
