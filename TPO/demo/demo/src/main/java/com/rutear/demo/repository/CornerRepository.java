package com.rutear.demo.repository;

import com.rutear.demo.model.Corner;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

public interface CornerRepository extends Neo4jRepository<Corner, String> {

  // --- Helpers que ya tenías / útiles para ping/seed ---
  @Query("MATCH (c:Corner) RETURN count(c)")
  long countCorners();

  @Query("""
    MERGE (a:Corner {id: $idA, name: $nameA, lat:$latA, lng:$lngA})
    MERGE (b:Corner {id: $idB, name: $nameB, lat:$latB, lng:$lngB})
    MERGE (a)-[:ROAD {distance:$distance, traffic:$traffic, risk:$risk, timePenalty:$timePenalty}]->(b)
    RETURN a
  """)
  Corner upsertRoad(String idA, String nameA, double latA, double lngA,
                    String idB, String nameB, double latB, double lngB,
                    double distance, double traffic, double risk, double timePenalty);

  // --- PROYECCIÓN: vecinos salientes desde un Corner ---
  interface NeighborProjection {
    String getToId();
    double getDistance();
    double getTraffic();
    double getRisk();
    double getTimePenalty();
  }

  @Query("""
    MATCH (:Corner {id:$id})-[r:ROAD]->(b:Corner)
    RETURN b.id AS toId,
           r.distance AS distance,
           r.traffic AS traffic,
           r.risk AS risk,
           r.timePenalty AS timePenalty
  """)
  List<CornerRepository.NeighborProjection> neighbors(String id);
}
