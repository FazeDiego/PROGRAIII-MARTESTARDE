package com.rutear.demo.service;

import com.rutear.demo.dto.AssignmentRequest;
import com.rutear.demo.dto.AssignmentResponse;

public interface AssignmentService {
  AssignmentResponse assign(AssignmentRequest req);
}
