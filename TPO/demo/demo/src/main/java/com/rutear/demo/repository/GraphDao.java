package com.rutear.demo.repository;

import com.rutear.demo.dto.CornerDTO;
import com.rutear.demo.dto.NeighborRow;
import com.rutear.demo.dto.PoiDTO; // 👈 nuevo import para los POI
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class GraphDao {

  private final Neo4jClient neo4j;

  public GraphDao(Neo4jClient neo4j) {
    this.neo4j = neo4j;
  }

  // ================= EXISTENTE =================
  // Vecinos (usado por Dijkstra, BFS, DFS)
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

  // ================= NUEVO: CARGAR TODO EL GRAFO =================
  // Obtiene TODAS las aristas del grafo de una sola vez
  public record GraphEdge(String fromId, String toId, double distance, double traffic, double risk, double timePenalty) {}
  
  public List<GraphEdge> loadAllEdges() {
    return neo4j.query("""
        MATCH (a:Corner)-[r:ROAD]->(b:Corner)
        RETURN a.id AS fromId,
               b.id AS toId,
               r.distance AS distance,
               r.traffic AS traffic,
               r.risk AS risk,
               r.timePenalty AS timePenalty
        """)
        .fetchAs(GraphEdge.class)
        .mappedBy((ts, rec) -> new GraphEdge(
            rec.get("fromId").asString(),
            rec.get("toId").asString(),
            rec.get("distance").asDouble(),
            rec.get("traffic").asDouble(),
            rec.get("risk").asDouble(),
            rec.get("timePenalty").asDouble()
        ))
        .all().stream().toList();
  }

  // ================= EXISTENTE =================
  // Listar todos los Corners (para el front)
  public List<CornerDTO> allCorners(int limit) {
    return neo4j.query("""
        MATCH (c:Corner)
        RETURN c.id AS id, c.name AS name, c.lat AS lat, c.lng AS lng
        ORDER BY toInteger(replace(c.id,'C',''))
        LIMIT $limit
        """)
      .bind(limit).to("limit")
      .fetchAs(CornerDTO.class)
      .mappedBy((ts, rec) -> new CornerDTO(
          rec.get("id").asString(),
          rec.get("name").asString(null),
          rec.get("lat").asDouble(),
          rec.get("lng").asDouble()
      ))
      .all().stream().toList();
  }

  // =====================================================
  // 🔍 NUEVO 1: Buscar POIs asociados a una esquina dada
  // =====================================================
  public Collection<PoiDTO> poisAtCorner(String cornerId, List<String> types) {
    return neo4j.query("""
        MATCH (:Corner {id:$id})<-[:NEAR]-(p:POI)
        WHERE $types IS NULL OR p.type IN $types
        RETURN p.id AS id,
               p.name AS name,
               p.type AS type,
               p.lat AS lat,
               p.lng AS lng
        """)
      .bind(cornerId).to("id")
      .bind(types).to("types")
      .fetchAs(PoiDTO.class)
      .mappedBy((ts, rec) -> new PoiDTO(
          rec.get("id").asString(),
          rec.get("name").asString(),
          rec.get("type").asString(),
          rec.get("lat").asDouble(),
          rec.get("lng").asDouble(),
          0 // depth se calcula en GraphServiceImpl
      ))
      .all();
  }

  // =====================================================
  // 🔧 NUEVO 2: Helper opcional para convertir CSV a lista
  // =====================================================
  public static List<String> parseTypes(String csv) {
    if (csv == null || csv.isBlank()) return null;
    return java.util.Arrays.stream(csv.split(","))
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .toList();
  }

  // =====================================================
  // 🔍 NUEVO 3: Buscar POIs usando BFS dentro de un radio
  // =====================================================
  public List<PoiDTO> findPoisBfs(String startId, int maxDepth, String typeRegex) {
    return neo4j.query("""
        MATCH (start:Corner {id:$id})
        MATCH p=(start)-[:ROAD*1..$depth]->(c:Corner)<-[:NEAR]-(poi:POI)
        WHERE poi.type =~ $regex
        RETURN DISTINCT poi.id AS id, poi.name AS name, poi.type AS type,
               poi.lat AS lat, poi.lng AS lng
        ORDER BY name
        """)
        .bind(startId).to("id")
        .bind(maxDepth).to("depth")
        .bind(typeRegex).to("regex")
        .fetchAs(PoiDTO.class)
        .mappedBy((ts, rec) -> new PoiDTO(
            rec.get("id").asString(),
            rec.get("name").asString(null),
            rec.get("type").asString(null),
            rec.get("lat").asDouble(),
            rec.get("lng").asDouble(),
            0 // depth no se calcula en esta query
        ))
        .all().stream().toList();
  }

  // =====================================================
  // 🔍 NUEVO 4: POIs a lo largo de un camino específico
  // =====================================================
  public List<PoiDTO> poisAlongPath(List<String> cornerIds, String typeRegex) {
    if (cornerIds == null || cornerIds.isEmpty()) return List.of();
    return neo4j.query("""
        UNWIND $ids AS cid
        MATCH (c:Corner {id:cid})<-[:NEAR]-(poi:POI)
        WHERE poi.type =~ $regex
        RETURN DISTINCT poi.id AS id, poi.name AS name, poi.type AS type,
               poi.lat AS lat, poi.lng AS lng
        ORDER BY name
        """)
        .bind(cornerIds).to("ids")
        .bind(typeRegex).to("regex")
        .fetchAs(PoiDTO.class)
        .mappedBy((ts, rec) -> new PoiDTO(
            rec.get("id").asString(),
            rec.get("name").asString(null),
            rec.get("type").asString(null),
            rec.get("lat").asDouble(),
            rec.get("lng").asDouble(),
            0 // depth no aplica aquí
        ))
        .all().stream().toList();
  }

}
