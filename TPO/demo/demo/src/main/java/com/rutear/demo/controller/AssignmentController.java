package com.rutear.demo.controller;

import com.rutear.demo.dto.AssignmentRequest;
import com.rutear.demo.dto.AssignmentResponse;
import com.rutear.demo.service.AssignmentService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/assign")
public class AssignmentController {

  private final AssignmentService assignmentService;

  public AssignmentController(AssignmentService assignmentService) {
    this.assignmentService = assignmentService;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> assign(@RequestBody AssignmentRequest req) {
    try {
      AssignmentResponse resp = assignmentService.assign(req);
      return ResponseEntity.ok(resp);
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("error", e.getClass().getSimpleName(), "message", e.getMessage()));
    }
  }
}
