package com.rutear.demo.dto;

import java.util.List;

public class MstResponse {
  public static class MstEdge {
    private String fromId, toId;
    private double weight;
    public MstEdge() {}
    public MstEdge(String fromId, String toId, double weight){ this.fromId=fromId; this.toId=toId; this.weight=weight; }
    public String getFromId(){ return fromId; }
    public String getToId(){ return toId; }
    public double getWeight(){ return weight; }
    public void setFromId(String s){ this.fromId=s; }
    public void setToId(String s){ this.toId=s; }
    public void setWeight(double w){ this.weight=w; }
  }

  private List<MstEdge> edges;
  private double totalWeight;
  private int nodesUsed;

  public MstResponse() {}
  public MstResponse(List<MstEdge> edges, double totalWeight, int nodesUsed){
    this.edges=edges; this.totalWeight=totalWeight; this.nodesUsed=nodesUsed;
  }
  public List<MstEdge> getEdges(){ return edges; }
  public void setEdges(List<MstEdge> e){ this.edges=e; }
  public double getTotalWeight(){ return totalWeight; }
  public void setTotalWeight(double w){ this.totalWeight=w; }
  public int getNodesUsed(){ return nodesUsed; }
  public void setNodesUsed(int n){ this.nodesUsed=n; }
}
