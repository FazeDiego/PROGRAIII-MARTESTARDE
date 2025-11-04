package com.rutear.demo.service;

import com.rutear.demo.dto.*;
import com.rutear.demo.util.CostMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Servicio que implementa el algoritmo GREEDY para asignar pedidos a repartidores
 * basándose en la distancia más cercana
 */
@Service
public class DeliveryAssignmentService {

    private static final Logger logger = LoggerFactory.getLogger(DeliveryAssignmentService.class);

    private final RoutingService routingService;
    private final GraphService graphService;

    public DeliveryAssignmentService(RoutingService routingService, GraphService graphService) {
        this.routingService = routingService;
        this.graphService = graphService;
    }

    /**
     * ALGORITMO GREEDY: Asigna pedidos a repartidores
     * Estrategia: Para cada pedido (ordenado por prioridad), asignarlo al repartidor
     * más cercano que aún tenga capacidad
     */
    @Transactional(readOnly = true)
    public AssignmentResponseDTO assignOrdersGreedy(AssignmentRequestDTO request) {
        long startTime = System.currentTimeMillis();

        AssignmentResponseDTO response = new AssignmentResponseDTO();
        response.setAlgorithm("GREEDY");

        List<DeliveryAssignmentDTO> assignments = new ArrayList<>();
        List<OrderDTO> unassignedOrders = new ArrayList<>();

        // Cache para evitar recalcular rutas
        Map<String, PathResponse> routeCache = new HashMap<>();
        CostMode mode = CostMode.valueOf(request.getMode());

        // Inicializar asignaciones para cada repartidor
        Map<String, DeliveryAssignmentDTO> assignmentMap = new HashMap<>();
        Map<String, Integer> orderCount = new HashMap<>();
        Map<String, String> currentLocationMap = new HashMap<>(); // Rastrear ubicación actual de cada repartidor

        for (DeliveryPersonDTO person : request.getDeliveryPersons()) {
            DeliveryAssignmentDTO assignment = new DeliveryAssignmentDTO();
            assignment.setDeliveryPerson(person);
            assignment.setAssignedOrders(new ArrayList<>());
            assignment.setRoutes(new ArrayList<>());
            assignment.setTotalDistance(0.0);
            assignment.setTotalTime(0);
            
            assignmentMap.put(person.getId(), assignment);
            orderCount.put(person.getId(), 0);
            currentLocationMap.put(person.getId(), person.getCurrentLocationId()); // Inicializar ubicación
        }

        // Ordenar pedidos por prioridad (mayor prioridad primero)
        List<OrderDTO> sortedOrders = new ArrayList<>(request.getOrders());
        sortedOrders.sort((o1, o2) -> Integer.compare(o2.getPriority(), o1.getPriority()));

        // GREEDY: Para cada pedido, asignar al repartidor más cercano con capacidad
        for (OrderDTO order : sortedOrders) {
            logger.info("Procesando pedido {}: {} -> {}", 
                order.getId(), order.getPickupLocationId(), order.getDeliveryLocationId());
            
            String bestDeliveryPersonId = null;
            double minDistance = Double.MAX_VALUE;

            // Buscar el repartidor más cercano al punto de recogida del pedido
            for (DeliveryPersonDTO person : request.getDeliveryPersons()) {
                // Verificar si el repartidor tiene capacidad
                if (orderCount.get(person.getId()) >= person.getMaxOrders()) {
                    continue;
                }

                // Calcular distancia desde la ubicación ACTUAL del repartidor hasta el pickup
                String currentLoc = currentLocationMap.get(person.getId());
                try {
                    String routeKey = currentLoc + "->" + order.getPickupLocationId() + "-" + mode;
                    PathResponse route;
                    
                    if (routeCache.containsKey(routeKey)) {
                        route = routeCache.get(routeKey);
                    } else {
                        route = routingService.dijkstra(
                            currentLoc,
                            order.getPickupLocationId(),
                            mode
                        );
                        routeCache.put(routeKey, route);
                    }

                    double distance = route.getTotalDistance();
                    
                    // GREEDY: Seleccionar el de menor distancia
                    if (distance < minDistance) {
                        minDistance = distance;
                        bestDeliveryPersonId = person.getId();
                    }
                } catch (Exception e) {
                    // Si no hay ruta, ignorar este repartidor para este pedido
                    logger.warn("No se pudo calcular ruta desde {} ({}) hasta {}: {}", 
                        person.getId(), currentLoc, 
                        order.getPickupLocationId(), e.getMessage());
                    continue;
                }
            }

            // Asignar el pedido al mejor repartidor encontrado
            if (bestDeliveryPersonId != null) {
                logger.info("Pedido {} asignado a {} (distancia: {} km)", 
                    order.getId(), bestDeliveryPersonId, minDistance);
                
                DeliveryAssignmentDTO assignment = assignmentMap.get(bestDeliveryPersonId);
                assignment.getAssignedOrders().add(order);
                orderCount.put(bestDeliveryPersonId, orderCount.get(bestDeliveryPersonId) + 1);
                
                // CRÍTICO: Actualizar ubicación del repartidor al punto de entrega del pedido
                currentLocationMap.put(bestDeliveryPersonId, order.getDeliveryLocationId());
                logger.info("Repartidor {} ahora está en {} (después de entregar pedido {})", 
                    bestDeliveryPersonId, order.getDeliveryLocationId(), order.getId());
            } else {
                // No se pudo asignar el pedido
                logger.warn("Pedido {} NO pudo ser asignado a ningún repartidor", order.getId());
                unassignedOrders.add(order);
            }
        }

        // Calcular rutas para cada repartidor con sus pedidos asignados
        for (DeliveryAssignmentDTO assignment : assignmentMap.values()) {
            if (!assignment.getAssignedOrders().isEmpty()) {
                calculateRoutesForAssignment(assignment, mode, routeCache);
                assignments.add(assignment);
            }
        }

        response.setAssignments(assignments);
        response.setUnassignedOrders(unassignedOrders);
        response.setComputationTimeMs(System.currentTimeMillis() - startTime);
        
        logger.info("Asignación completada: {} repartidores, {} pedidos asignados, {} no asignados, {} ms", 
            assignments.size(), 
            assignments.stream().mapToInt(a -> a.getAssignedOrders().size()).sum(),
            unassignedOrders.size(),
            response.getComputationTimeMs());

        return response;
    }

    /**
     * Calcula las rutas óptimas para un repartidor con sus pedidos asignados
     * usando Dijkstra con caché
     */
    private void calculateRoutesForAssignment(DeliveryAssignmentDTO assignment, CostMode mode, Map<String, PathResponse> routeCache) {
        DeliveryPersonDTO person = assignment.getDeliveryPerson();
        List<OrderDTO> orders = assignment.getAssignedOrders();
        List<RouteSegmentDTO> routes = new ArrayList<>();
        
        logger.info("Calculando rutas para {} con {} pedidos asignados", 
            person.getId(), orders.size());
        
        double totalDistance = 0.0;
        int totalTime = 0;

        String currentLocation = person.getCurrentLocationId();

        // Para cada pedido asignado, calcular:
        // 1. Ruta desde ubicación actual -> punto de recogida
        // 2. Ruta desde punto de recogida -> punto de entrega
        for (OrderDTO order : orders) {
            // Ruta 1: Ubicación actual -> Pickup
            try {
                String routeKey1 = currentLocation + "->" + order.getPickupLocationId() + "-" + mode;
                PathResponse routeToPickup;
                
                if (routeCache.containsKey(routeKey1)) {
                    routeToPickup = routeCache.get(routeKey1);
                } else {
                    routeToPickup = routingService.dijkstra(currentLocation, order.getPickupLocationId(), mode);
                    routeCache.put(routeKey1, routeToPickup);
                }

                RouteSegmentDTO segment1 = new RouteSegmentDTO();
                segment1.setFrom(currentLocation);
                segment1.setTo(order.getPickupLocationId());
                segment1.setOrderIdRelated(order.getId());
                // Usar nodeIds para evitar lazy loading de Corners
                segment1.setPath(routeToPickup.getNodeIds());
                segment1.setDistance(routeToPickup.getTotalDistance());
                segment1.setTime((int) routeToPickup.getTotalTimePenalty());
                
                routes.add(segment1);
                totalDistance += routeToPickup.getTotalDistance();
                totalTime += routeToPickup.getTotalTimePenalty();

                // Ruta 2: Pickup -> Delivery
                String routeKey2 = order.getPickupLocationId() + "->" + order.getDeliveryLocationId() + "-" + mode;
                PathResponse routeToDelivery;
                
                if (routeCache.containsKey(routeKey2)) {
                    routeToDelivery = routeCache.get(routeKey2);
                } else {
                    routeToDelivery = routingService.dijkstra(order.getPickupLocationId(), order.getDeliveryLocationId(), mode);
                    routeCache.put(routeKey2, routeToDelivery);
                }

                RouteSegmentDTO segment2 = new RouteSegmentDTO();
                segment2.setFrom(order.getPickupLocationId());
                segment2.setTo(order.getDeliveryLocationId());
                segment2.setOrderIdRelated(order.getId());
                // Usar nodeIds para evitar lazy loading de Corners
                segment2.setPath(routeToDelivery.getNodeIds());
                segment2.setDistance(routeToDelivery.getTotalDistance());
                segment2.setTime((int) routeToDelivery.getTotalTimePenalty());

                routes.add(segment2);
                totalDistance += routeToDelivery.getTotalDistance();
                totalTime += routeToDelivery.getTotalTimePenalty();

                // Actualizar ubicación actual para el siguiente pedido
                currentLocation = order.getDeliveryLocationId();

            } catch (Exception e) {
                // En caso de error, continuar con el siguiente pedido
                logger.error("Error calculando ruta para pedido {}: {}", order.getId(), e.getMessage());
            }
        }

        logger.info("{} completado: {} km, {} min", person.getId(), 
            String.format("%.2f", totalDistance), totalTime);
        
        assignment.setRoutes(routes);
        assignment.setTotalDistance(totalDistance);
        assignment.setTotalTime((int) totalTime);
    }
}
