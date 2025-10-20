package com.rutear.demo.dto;

import jakarta.validation.constraints.NotBlank;

public class PathRequest {
  @NotBlank private String from;
  @NotBlank private String to;
  private String mode = "FAST"; // FAST | SAFE
  // getters/setters
}
