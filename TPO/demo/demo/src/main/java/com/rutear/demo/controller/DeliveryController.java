package com.rutear.demo.controller;

import com.rutear.demo.dto.*;
import com.rutear.demo.service.DeliveryAssignmentService;
import com.rutear.demo.repository.GraphDao;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Controlador para la asignación de pedidos a repartidores
 */
@RestController
@RequestMapping("/delivery")
public class DeliveryController {

    private final DeliveryAssignmentService assignmentService;
    private final GraphDao graphDao;

    public DeliveryController(DeliveryAssignmentService assignmentService, GraphDao graphDao) {
        this.assignmentService = assignmentService;
        this.graphDao = graphDao;
    }

    /**
     * Endpoint principal: Asignar pedidos a repartidores usando algoritmo GREEDY
     */
    @PostMapping("/assign")
    public ResponseEntity<AssignmentResponseDTO> assignOrders(@RequestBody AssignmentRequestDTO request) {
        try {
            AssignmentResponseDTO response = assignmentService.assignOrdersGreedy(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Endpoint auxiliar: Generar repartidores aleatorios para testing
     * Usa corners REALES de la base de datos
     */
    @GetMapping("/generate-delivery-persons")
    public ResponseEntity<List<DeliveryPersonDTO>> generateRandomDeliveryPersons(
            @RequestParam(defaultValue = "3") int count) {
        
        // Obtener corners reales de la base de datos
        List<CornerDTO> availableCorners = graphDao.allCorners(200); // Obtener hasta 200 corners
        
        if (availableCorners.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        List<DeliveryPersonDTO> persons = new ArrayList<>();
        Random random = new Random();

        for (int i = 1; i <= count; i++) {
            DeliveryPersonDTO person = new DeliveryPersonDTO();
            person.setId("D" + i);
            person.setName("Repartidor " + i);
            // Seleccionar un corner aleatorio de los que existen
            CornerDTO randomCorner = availableCorners.get(random.nextInt(availableCorners.size()));
            person.setCurrentLocationId(randomCorner.getId());
            person.setMaxOrders(2);
            persons.add(person);
        }

        return ResponseEntity.ok(persons);
    }

    /**
     * Endpoint auxiliar: Generar pedidos aleatorios para testing
     * Usa corners REALES de la base de datos
     */
    @GetMapping("/generate-orders")
    public ResponseEntity<List<OrderDTO>> generateRandomOrders(
            @RequestParam(defaultValue = "5") int count) {
        
        // Obtener corners reales de la base de datos
        List<CornerDTO> availableCorners = graphDao.allCorners(200);
        
        if (availableCorners.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        List<OrderDTO> orders = new ArrayList<>();
        Random random = new Random();

        for (int i = 1; i <= count; i++) {
            OrderDTO order = new OrderDTO();
            order.setId("O" + i);
            
            // Seleccionar corners aleatorios de los que existen
            CornerDTO pickupCorner = availableCorners.get(random.nextInt(availableCorners.size()));
            CornerDTO deliveryCorner;
            do {
                deliveryCorner = availableCorners.get(random.nextInt(availableCorners.size()));
            } while (deliveryCorner.getId().equals(pickupCorner.getId()));  // Asegurar que pickup != delivery
            
            order.setPickupLocationId(pickupCorner.getId());
            order.setDeliveryLocationId(deliveryCorner.getId());
            order.setPriority(random.nextInt(3) + 1);  // Prioridad 1-3
            order.setDescription("Pedido " + i);
            orders.add(order);
        }

        return ResponseEntity.ok(orders);
    }
}
