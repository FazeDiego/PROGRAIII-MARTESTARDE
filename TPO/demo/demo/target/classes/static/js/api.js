// js/api.js
const BASE = ""; // si servís el front desde el mismo backend, dejá vacío

export async function apiGet(endpoint) {
  const res = await fetch(`${BASE}${endpoint}`, { headers: { "Accept": "application/json" } });
  if (!res.ok) throw new Error(await res.text());
  return res.json();
}

export async function apiPost(endpoint, data) {
  const res = await fetch(`${BASE}${endpoint}`, {
    method: 'POST',
    headers: { "Content-Type": "application/json", "Accept": "application/json" },
    body: JSON.stringify(data)
  });
  if (!res.ok) throw new Error(await res.text());
  return res.json();
}

export async function getRoute(alg, from, to, mode) {
  const url = `${BASE}/routes/${alg}?from=${encodeURIComponent(from)}&to=${encodeURIComponent(to)}&mode=${encodeURIComponent(mode)}`;
  const res = await fetch(url, { headers:{ "Accept":"application/json" }});
  if (!res.ok) throw new Error(await res.text());
  return res.json();
}

export async function traversal(type, start){
  const url = `${BASE}/graph/${type}?start=${encodeURIComponent(start)}`;
  const res = await fetch(url, { headers:{ "Accept":"application/json" }});
  if (!res.ok) throw new Error(await res.text());
  return res.json();
}

export async function getAllCorners(){
  const res = await fetch(`${BASE}/graph/corners`, { headers:{ "Accept":"application/json" }});
  if (!res.ok) throw new Error(await res.text());
  return res.json();
}

// Corner por ID (para centrar mapa en UADE, etc.)
export async function getCorner(id) {
  const res = await fetch(`${BASE}/graph/corner/${encodeURIComponent(id)}`, { headers: { "Accept": "application/json" } });
  if (!res.ok) throw new Error(await res.text());
  return res.json();
}

// Listar corners livianos para poblar selects y puntos en el mapa
export async function listCorners(limit = 100) {
  const res = await fetch(`${BASE}/graph/corners?limit=${limit}`, { headers: { "Accept": "application/json" } });
  if (!res.ok) throw new Error(await res.text());
  return res.json();
}

// Buscar POIs cercanos usando BFS
export async function findPoisBfs(start, depth = 3, types = "GAS|MECH|ER"){
  const url = `${BASE}/graph/pois/near?start=${encodeURIComponent(start)}&depth=${depth}&types=${encodeURIComponent(types)}`;
  const res = await fetch(url, { headers:{ "Accept":"application/json" }});
  if (!res.ok) throw new Error(await res.text());
  return res.json(); // [{id,name,type,lat,lng}, ...]
}
