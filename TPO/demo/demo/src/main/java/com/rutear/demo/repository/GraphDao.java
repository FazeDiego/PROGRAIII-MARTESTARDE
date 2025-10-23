package com.rutear.demo.repository;

import com.rutear.demo.dto.NeighborRow;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class GraphDao {

  private final Neo4jClient neo4j;

  public GraphDao(Neo4jClient neo4j) {
    this.neo4j = neo4j;
  }

  // Nota: devolvemos Collection para alinear con .all()
  public Collection<NeighborRow> neighbors(String id) {
    return neo4j.query("""
        MATCH (:Corner {id:$id})-[r:ROAD]->(b:Corner)
        RETURN b.id AS toId,
               r.distance    AS distance,
               r.traffic     AS traffic,
               r.risk        AS risk,
               r.timePenalty AS timePenalty
        """)
        .bind(id).to("id")
        .fetchAs(NeighborRow.class)
        .mappedBy((ts, rec) -> new NeighborRow(
            rec.get("toId").asString(),
            rec.get("distance").asDouble(),
            rec.get("traffic").asDouble(),
            rec.get("risk").asDouble(),
            rec.get("timePenalty").asDouble()
        ))
        .all();
  }
}
