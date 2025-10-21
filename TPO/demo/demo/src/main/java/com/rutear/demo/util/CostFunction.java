package com.rutear.demo.util;

public final class CostFunction {
  private CostFunction() {}
  public static double cost(double distance, double traffic, double risk, double timePenalty, CostMode mode){
    // pesos tentativos; después los calibramos
    return (mode == CostMode.FAST)
      ? distance + timePenalty + 50*traffic
      : distance + timePenalty + 200*risk + 20*traffic;
  }
}
