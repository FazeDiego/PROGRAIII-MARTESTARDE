package com.rutear.demo.service;

import com.rutear.demo.dto.MstResponse;

public interface MstService {
  // MST por Prim desde un nodo, cortando en maxNodes para no levantar todo el grafo
  MstResponse prim(String startId, int maxNodes);
}
