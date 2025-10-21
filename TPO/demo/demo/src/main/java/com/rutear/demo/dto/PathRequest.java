package com.rutear.demo.dto;

import jakarta.validation.constraints.NotBlank;

public class PathRequest {
  @NotBlank private String from;
  @NotBlank private String to;
  private String mode = "FAST"; // FAST | SAFE
  public String getFrom() { return from; }
  public void setFrom(String from) { this.from = from; }

  public String getTo() { return to; }
  public void setTo(String to) { this.to = to; }

  public String getMode() { return mode; }
  public void setMode(String mode) { this.mode = mode; }
}
