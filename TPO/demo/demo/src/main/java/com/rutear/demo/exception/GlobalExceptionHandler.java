package com.rutear.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiError> handleIllegalArg(IllegalArgumentException ex, HttpServletRequest req){
    var body = new ApiError(400, "Bad Request", ex.getMessage(), req.getRequestURI());
    return ResponseEntity.badRequest().body(body);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req){
    var msg = ex.getBindingResult().getAllErrors().stream()
                 .findFirst().map(e -> e.getDefaultMessage()).orElse("Validation error");
    var body = new ApiError(422, "Unprocessable Entity", msg, req.getRequestURI());
    return ResponseEntity.unprocessableEntity().body(body);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req){
    var body = new ApiError(500, "Internal Server Error", ex.getMessage(), req.getRequestURI());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }
}
