package com.rutear.demo.controller;

import com.rutear.demo.dto.MstResponse;
import com.rutear.demo.service.MstService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/infra")
public class InfraController {

  private final MstService mst;

  public InfraController(MstService mst) { this.mst = mst; }

  @GetMapping(value="/mst/prim", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> prim(@RequestParam String start,
                                @RequestParam(defaultValue = "50") int maxNodes){
    try {
      MstResponse resp = mst.prim(start, Math.max(2, maxNodes));
      return ResponseEntity.ok(resp);
    } catch (Exception e){
      return ResponseEntity.badRequest().body(Map.of(
          "error", e.getClass().getSimpleName(),
          "message", e.getMessage()
      ));
    }
  }
}
