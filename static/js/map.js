// js/map.js
let map, routeLayer = null, cornerLayer = null;

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
