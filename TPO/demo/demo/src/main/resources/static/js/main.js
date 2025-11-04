// main.js
import { getRoute, traversal, getCorner, listCorners, findPoisBfs } from "./api.js";
import { initMap, drawRoute, plotCorners, plotPois, clearAll, clearPois } from "./map.js";
import { initDelivery } from "./delivery.js";

const $ = (s) => document.querySelector(s);

function getServicesParams(){
  const depth = parseInt($("#svcDepth")?.value || "3", 10);
  const types = $("#svcTypes")?.value?.trim() || "GAS|MECH|ER";
  return { depth: isNaN(depth) ? 3 : depth, types };
}

async function showServicesFor(startId){
  if (!startId) return;
  const { depth, types } = getServicesParams();
  try {
    const pois = await findPoisBfs(startId, depth, types);
    plotPois(pois);
  } catch(e) {
    console.error("Error cargando POIs:", e);
  }
}

function setOutMetrics(data){
  $("#out").innerHTML = `
    <div class="metric">Distancia total: <b>${data.totalDistance.toFixed(0)} m</b></div>
    <div class="metric">Penalidad tiempo: <b>${data.totalTimePenalty.toFixed(0)} s</b></div>
    <div class="metric">Riesgo total: <b>${data.totalRisk.toFixed(2)}</b></div>
    <div class="metric">Costo: <b>${data.totalCost.toFixed(1)}</b></div>
    <div style="margin-top:.5rem">Path: ${data.nodes.map(n => `<span class="pill">${n.id}</span>`).join('')}</div>
  `;
}
const setError = (el, err) => el.innerHTML = `<div class="error">${(err && err.message) ? err.message : err}</div>`;

async function onSubmitRoute(ev){
  ev.preventDefault();
  const from = $("#from").value;
  const to   = $("#to").value;
  const mode = $("#mode").value;
  const alg  = $("#alg").value || "dijkstra";

  $("#out").textContent = "Calculando...";
  try {
    const data = await getRoute(alg, from, to, mode);
    drawRoute(data.nodes);
    setOutMetrics(data);

    // 🔎 Mostrar servicios si el checkbox está marcado
    if ($("#showServices")?.checked) {
      await showServicesFor(from);
    } else {
      // Si el toggle está apagado, limpiamos capa verde
      clearPois();
    }
  } catch (err) {
    setError($("#out"), err);
  }
}

async function onBfs(){
  $("#walk").textContent = "BFS...";
  try {
    const data = await traversal("bfs", "C1");
    $("#walk").innerHTML = `<b>BFS</b>: ${data.order.join(" → ")}`;
  } catch (err) {
    setError($("#walk"), err);
  }
}

async function onDfs(){
  $("#walk").textContent = "DFS...";
  try {
    const data = await traversal("dfs", "C1");
    $("#walk").innerHTML = `<b>DFS</b>: ${data.order.join(" → ")}`;
  } catch (err) {
    setError($("#walk"), err);
  }
}

function fillSelect(inputId, corners, defaultId){
  // Llenamos el datalist compartido
  const datalist = $("#cornersList");
  datalist.innerHTML = "";
  corners.forEach(c => {
    const opt = document.createElement("option");
    opt.value = c.id;
    opt.label = c.name || c.id; // Mostrará el nombre como sugerencia
    datalist.appendChild(opt);
  });
  
  // Establecemos el valor por defecto en el input
  if (defaultId) {
    const input = $(inputId);
    input.value = defaultId;
  }
}

function wireEvents(){
  $("#routeForm").addEventListener("submit", onSubmitRoute);
  $("#btnBfs")?.addEventListener("click", onBfs);
  $("#btnDfs")?.addEventListener("click", onDfs);
  $("#btnLoadCorners")?.addEventListener("click", async ()=>{
    try { plotCorners(await listCorners(100)); }
    catch(e){ setError($("#out"), e); }
  });
  $("#btnClear")?.addEventListener("click", ()=>{
    clearAll(); $("#out").textContent=""; $("#walk").textContent="";
  });
  
  // 🔄 Botón para actualizar servicios manualmente
  $("#btnRefreshServices")?.addEventListener("click", async ()=>{
    const from = $("#from").value.trim();
    if (from) {
      try { 
        await showServicesFor(from); 
      } catch (e) { 
        console.error("Error al actualizar servicios:", e); 
      }
    }
  });
}

(async function boot(){
  // Centrar en UADE (C1) si existe
  try {
    const c1 = await getCorner("C1");
    initMap([c1.lat, c1.lng], 16);
  } catch {
    initMap([-34.617, -58.382], 16);
  }

  // Poblar selects
  try {
    const corners = await listCorners(100);
    fillSelect("#from", corners, "C1");
    fillSelect("#to",   corners, "C5");
  } catch (e) { console.warn(e); }

  wireEvents();
  
  // Inicializar módulo de delivery
  initDelivery();
})();
