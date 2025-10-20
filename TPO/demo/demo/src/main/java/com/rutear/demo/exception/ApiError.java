package com.rutear.demo.exception;

import java.time.Instant;

public class ApiError {
  private Instant timestamp = Instant.now();
  private int status;
  private String error;
  private String message;
  private String path;

  public ApiError() {}
  public ApiError(int status, String error, String message, String path) {
    this.status = status; this.error = error; this.message = message; this.path = path;
  }
  // getters/setters
}
