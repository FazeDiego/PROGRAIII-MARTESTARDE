// main.js
import { getRoute, traversal, getCorner, listCorners, getNearbyPois, assignOrders } from "./api.js";
import { initMap, drawRoute, plotCorners, plotPois, clearAll, clearPois, plotCouriers, plotAssignments, clearAssignments } from "./map.js";

const $ = (s) => document.querySelector(s);

// Mapa global de corners para asignaciones
let cornersMap = {};

async function showServicesFor(startId){
  if (!startId) return;
  const depth = parseInt($("#poiDepth")?.value || "3", 10);
  const types = $("#poiTypes")?.value || "GAS,MECH,ER";
  try {
    const pois = await getNearbyPois(startId, depth, types);
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
  const from = $("#from").value.trim();
  const to   = $("#to").value.trim();
  const mode = $("#mode").value;
  const alg  = $("#alg")?.value || "dijkstra";

  $("#out").textContent = "Calculando...";
  try {
    clearPois(); // limpiá POIs anteriores
    const data = await getRoute(alg, from, to, mode);
    drawRoute(data.nodes);
    setOutMetrics(data);

    // Traer POIs según filtro
    const depth = +($("#poiDepth")?.value || 3);
    const types = $("#poiTypes")?.value || "GAS,MECH,ER";
    const pois  = await getNearbyPois(from, depth, types);
    plotPois(pois);

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
    
    $("#out").textContent = 'Asignando pedidos...';
    
    // Asegurar que tenemos los corners cargados
    if (Object.keys(cornersMap).length === 0) {
      const corners = await listCorners(500);
      corners.forEach(c => {
        cornersMap[c.id] = { lat: c.lat, lng: c.lng, name: c.name };
      });
    }

    const resp = await assignOrders({ orders, numCouriers: num, maxPerCourier: maxPer });
    
    // Enriquecer con lat/lng y colores
    const colors = ['#1f77b4','#ff7f0e','#2ca02c','#d62728','#9467bd','#8c564b','#e377c2','#7f7f7f'];
    const enriched = resp.couriers.map((c, idx) => {
      const corner = cornersMap[c.cornerId];
      const color = colors[idx % colors.length];
      return { 
        courierId: c.courierId, 
        cornerId: c.cornerId, 
        lat: corner ? corner.lat : 0, 
        lng: corner ? corner.lng : 0, 
        orders: c.orders, 
        color 
      };
    });
    
    clearAssignments();
    plotCouriers(enriched);
    plotAssignments(enriched, cornersMap);
    $("#out").textContent = 'Asignación completa';

    // Mostrar lista de asignaciones
    const list = $("#assignList");
    if (list) {
      list.innerHTML = '';
      enriched.forEach(c => {
        const div = document.createElement('div');
        div.style.cssText = 'display:flex; align-items:center; gap:.5rem; margin:.5rem 0; padding:.5rem; background:#f7f7f7; border-radius:6px;';
        div.innerHTML = `
          <div style="width:20px; height:20px; background:${c.color}; border-radius:50%;"></div>
          <div style="flex:1;">
            <b>${c.courierId}</b> @ ${c.cornerId}
            <div style="margin-top:.25rem;">${(c.orders||[]).map(o=>`<span class="pill">${o}</span>`).join('')}</div>
          </div>
        `;
        list.appendChild(div);
      });
    }

  } catch (err) {
    setError($("#out"), err);
  }
}

function onClearAssignments() {
  clearAssignments();
  const list = $("#assignList");
  if (list) list.innerHTML = '';
  $("#out").textContent = '';
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
  
  $("#btnLoadCorners")?.addEventListener("click", async ()=>{
    try { 
      const corners = await listCorners(100);
      plotCorners(corners);
      // También cargar al mapa global
      corners.forEach(c => {
        cornersMap[c.id] = { lat: c.lat, lng: c.lng, name: c.name };
      });
    }
    catch(e){ setError($("#out"), e); }
  });
  
  $("#btnClear")?.addEventListener("click", ()=>{
    clearAll(); 
    $("#out").textContent="";
  });
  
  // 🔄 Botón para actualizar servicios manualmente
  $("#btnRefreshServices")?.addEventListener("click", async ()=>{
    const start = $("#from").value.trim();
    const depth = Number($("#poiDepth").value || 3);
    const types = $("#poiTypes").value || 'GAS,MECH,ER';
    
    if (start) {
      try { 
        const pois = await getNearbyPois(start, depth, types);
        plotPois(pois);
      } catch (e) { 
        console.error("Error al actualizar servicios:", e); 
      }
    }
  });
  
  // 🚚 Botones de asignación de pedidos
  $("#btnAssign")?.addEventListener("click", onAssign);
  $("#btnClearAssignments")?.addEventListener("click", onClearAssignments);
}

(async function boot(){
  // Centrar en UADE (coordenadas fijas: lng, lat)
  initMap([-58.4199, -34.5989], 16);

  // Poblar selects
  try {
    const corners = await listCorners(100);
    fillSelect("#from", corners, "C1");
    fillSelect("#to",   corners, "C5");
  } catch (e) { console.warn(e); }

  wireEvents();
})();
