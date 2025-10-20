package com.rutear.demo.mapper;

import com.rutear.demo.dto.CornerDTO;
import com.rutear.demo.dto.EdgeDTO;
import com.rutear.demo.model.Corner;
import com.rutear.demo.model.Road;

public class GraphMapper {
  public static CornerDTO toDTO(Corner c){
    return new CornerDTO(c.getId(), c.getName(), c.getLat(), c.getLng());
  }
  public static EdgeDTO toDTO(String fromId, Road r){
    return new EdgeDTO(fromId, r.getTo().getId(),
            r.getDistance(), r.getTraffic(), r.getRisk(), r.getTimePenalty());
  }
}
