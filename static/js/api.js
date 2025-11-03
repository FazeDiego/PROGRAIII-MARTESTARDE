// js/api.js
const BASE = ""; // si servís el front desde el mismo backend, dejá vacío

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
