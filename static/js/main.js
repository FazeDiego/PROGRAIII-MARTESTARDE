// js/main.js
import { getRoute, traversal, getAllCorners } from "./api.js";
import { initMap, drawRoute, plotCorners, clearAll } from "./map.js";

const $ = (sel) => document.querySelector(sel);

function setOutMetrics(data){
  $("#out").innerHTML = `
    <div class="metric">Distancia total: <b>${data.totalDistance.toFixed(0)} m</b></div>
    <div class="metric">Penalidad tiempo: <b>${data.totalTimePenalty.toFixed(0)} s</b></div>
    <div class="metric">Riesgo total: <b>${data.totalRisk.toFixed(2)}</b></div>
    <div class="metric">Costo: <b>${data.totalCost.toFixed(1)}</b></div>
    <div style="margin-top:.5rem">Path: ${data.nodes.map(n => `<span class="pill">${n.id}</span>`).join('')}</div>
  `;
}

function setError(el, err){
  el.innerHTML = `<div class="error">${(err && err.message) ? err.message : err}</div>`;
}

async function onSubmitRoute(ev){
  ev.preventDefault();
  const from = $("#from").value.trim();
  const to   = $("#to").value.trim();
  const mode = $("#mode").value;
  const alg  = $("#alg").value;

  $("#out").textContent = "Calculando...";
  try {
    const data = await getRoute(alg, from, to, mode);
    drawRoute(data.nodes);
    setOutMetrics(data);
  } catch (err) {
    setError($("#out"), err);
  }
}

async function onBfs(){
  $("#walk").textContent = "BFS...";
  try {
    const data = await traversal("bfs","C1");
    $("#walk").innerHTML = `<b>BFS</b>: ${data.order.join(" → ")}`;
  } catch (err) {
    setError($("#walk"), err);
  }
}
async function onDfs(){
  $("#walk").textContent = "DFS...";
  try {
    const data = await traversal("dfs","C1");
    $("#walk").innerHTML = `<b>DFS</b>: ${data.order.join(" → ")}`;
  } catch (err) {
    setError($("#walk"), err);
  }
}

async function onLoadCorners(){
  try {
    const corners = await getAllCorners();
    plotCorners(corners);
  } catch (err) {
    setError($("#out"), err);
  }
}

function onClear(){
  clearAll();
  $("#out").textContent = "";
  $("#walk").textContent = "";
}

function wireEvents(){
  $("#routeForm").addEventListener("submit", onSubmitRoute);
  $("#btnBfs").addEventListener("click", onBfs);
  $("#btnDfs").addEventListener("click", onDfs);
  $("#btnLoadCorners").addEventListener("click", onLoadCorners);
  $("#btnClear").addEventListener("click", onClear);
}

(function boot(){
  initMap();
  wireEvents();
})();
