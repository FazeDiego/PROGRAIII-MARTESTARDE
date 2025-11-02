// js/map.js
let map, routeLayer = null, cornerLayer = null;
let courierLayer = null, assignmentLayer = null;

const DEFAULT_COLORS = ['#1f77b4','#ff7f0e','#2ca02c','#d62728','#9467bd','#8c564b'];

function colorForIndex(i){ return DEFAULT_COLORS[i % DEFAULT_COLORS.length]; }

export function initMap() {
  map = L.map('map').setView([-34.602, -58.393], 15);
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution:'© OpenStreetMap'
  }).addTo(map);
}

export function drawRoute(nodes) {
  clearRoute();
  const latlngs = nodes.map(n => [n.lat, n.lng]);
  routeLayer = L.polyline(latlngs, { weight: 5 }).addTo(map);
  map.fitBounds(routeLayer.getBounds(), { padding:[20,20] });
  nodes.forEach(n => L.circleMarker([n.lat,n.lng], { radius:4 })
      .addTo(map).bindTooltip(`${n.id} · ${n.name}`));
}

export function plotCorners(corners) {
  clearCorners();
  cornerLayer = L.layerGroup();
  corners.forEach(c => {
    L.circleMarker([c.lat, c.lng], { radius:3 })
      .bindTooltip(`${c.id}`)
      .addTo(cornerLayer);
  });
  cornerLayer.addTo(map);
}

export function plotCouriers(couriers) {
  // couriers: [{courierId, cornerId, lat, lng, color}]
  if (courierLayer) { map.removeLayer(courierLayer); courierLayer = null; }
  courierLayer = L.layerGroup();
  couriers.forEach((c, idx) => {
    const color = c.color || colorForIndex(idx);
    const html = `<div class="courier-icon" style="background:${color};">${c.courierId}</div>`;
    const icon = L.divIcon({ className: 'courier-divicon', html: html, iconSize: [30,30], iconAnchor: [15,15] });
    L.marker([c.lat, c.lng], { icon: icon, title: c.courierId })
      .bindTooltip(`${c.courierId} @ ${c.cornerId}`)
      .addTo(courierLayer);
  });
  courierLayer.addTo(map);
}

export function plotAssignments(couriers, cornersMap) {
  // couriers: [{courierId, cornerId, lat, lng, orders:[orderId...], color}]
  if (assignmentLayer) { map.removeLayer(assignmentLayer); assignmentLayer = null; }
  assignmentLayer = L.layerGroup();
  const bounds = [];
  couriers.forEach(c => {
    const color = c.color || colorForIndex(0);
    (c.orders || []).forEach(ord => {
      const corner = cornersMap[ord];
      if (!corner) return;
      bounds.push([corner.lat, corner.lng]);
      // draw small marker for order
      L.circleMarker([corner.lat, corner.lng], { radius:5, color:color, fillColor: color, fillOpacity:0.9 })
        .bindTooltip(`Order ${ord} <- ${c.courierId}`)
        .addTo(assignmentLayer);
      // draw line from courier to order
      L.polyline([[c.lat, c.lng], [corner.lat, corner.lng]], { color:color, dashArray:'6' })
        .addTo(assignmentLayer);
    });
    bounds.push([c.lat, c.lng]);
  });
  assignmentLayer.addTo(map);
  // fit map to show assignments if any
  if (bounds.length > 0) {
    try { map.fitBounds(bounds, { padding:[40,40] }); } catch(e){}
  }
}

export function clearAssignments() {
  if (courierLayer) { map.removeLayer(courierLayer); courierLayer = null; }
  if (assignmentLayer) { map.removeLayer(assignmentLayer); assignmentLayer = null; }
}

export function clearRoute() {
  if (routeLayer) { map.removeLayer(routeLayer); routeLayer = null; }
}

export function clearCorners() {
  if (cornerLayer) { map.removeLayer(cornerLayer); cornerLayer = null; }
}

export function clearAll() {
  clearRoute();
  clearCorners();
}
