// js/main.js
import { getRoute, traversal, getAllCorners, assignOrders } from "./api.js";
import { initMap, drawRoute, plotCorners, clearAll, plotCouriers, plotAssignments, clearAssignments } from "./map.js";

const $ = (sel) => document.querySelector(sel);
let cornersMap = {};

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
    cornersMap = {};
    corners.forEach(c => cornersMap[c.id] = c);
  } catch (err) {
    setError($("#out"), err);
  }
}

async function onAssign(){
  try {
    const raw = $("#orders").value.trim();
    const orders = raw.length === 0 ? [] : raw.split(',').map(s => s.trim()).filter(s=>s);
    const num = parseInt($("#numCouriers").value) || 1;
    const maxPer = parseInt($("#maxPerCourier").value) || 2;
    $("#out").textContent = 'Asignando...';
    // ensure corners loaded
    if (Object.keys(cornersMap).length === 0) await onLoadCorners();

    const resp = await assignOrders({ orders, numCouriers: num, maxPerCourier: maxPer });
    // enrich with lat/lng
    const enriched = resp.couriers.map((c, idx) => {
      const corner = cornersMap[c.cornerId];
      // choose a color per courier
      const colors = ['#1f77b4','#ff7f0e','#2ca02c','#d62728','#9467bd','#8c564b'];
      const color = colors[idx % colors.length];
      return { courierId: c.courierId, cornerId: c.cornerId, lat: corner ? corner.lat : 0, lng: corner ? corner.lng : 0, orders: c.orders, color };
    });
    clearAssignments();
    plotCouriers(enriched);
    plotAssignments(enriched, cornersMap);
    $("#out").textContent = 'Asignación completa';

    // populate assignment list
    const list = $("#assignList");
    if (list) {
      list.innerHTML = '';
      enriched.forEach(c => {
        const div = document.createElement('div');
        div.className = 'assign-row';
        div.innerHTML = `<div class="swatch" style="background:${c.color}"></div>
          <div class="info"><b>${c.courierId}</b> @ ${c.cornerId}<div class="orders">${(c.orders||[]).map(o=>`<span class="pill">${o}</span>`).join('')}</div></div>
          <div class="actions"><button data-cid="${c.courierId}">Centrar</button></div>`;
        list.appendChild(div);
        div.querySelector('button').addEventListener('click', ()=>{
          map.setView([c.lat, c.lng], 17);
        });
      });
    }
  } catch (err) {
    setError($("#out"), err);
  }
}

function onClearAssignments(){
  clearAssignments();
  $("#out").textContent = '';
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
  $("#btnAssign").addEventListener("click", onAssign);
  $("#btnClearAssignments").addEventListener("click", onClearAssignments);
  $("#btnClear").addEventListener("click", onClear);
}

(function boot(){
  initMap();
  wireEvents();
})();
