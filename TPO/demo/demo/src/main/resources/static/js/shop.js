console.log("SHOP JS CARGADO :: v3 ::", new Date().toISOString());
const products = Array.isArray(window.PRODUCTS) ? window.PRODUCTS : [];
const grid = document.getElementById("productGrid");
const cartPanel = document.getElementById("cartPanel");
const cartOverlay = document.getElementById("cartOverlay");
const openCartBtn = document.getElementById("openCart");
const closeCartBtn = document.getElementById("closeCart");
const cartItemsContainer = document.getElementById("cartItems");
const cartCount = document.getElementById("cartCount");
const clearCartBtn = document.getElementById("clearCart");
const slotCount = document.getElementById("slotCount");
const weightCount = document.getElementById("weightCount");
const utilityCount = document.getElementById("utilityCount");
const optimizeBtn = document.getElementById("optimizeBtn");
const __lastAddTS = new Map();
let cart = [];
try {
  cart = JSON.parse(localStorage.getItem("cart") || "[]");
} catch {
  cart = [];
}
function saveCart() {
  localStorage.setItem("cart", JSON.stringify(cart));
}

//carrito
function openCart() {
  if (!cartPanel || !cartOverlay) return;
  cartPanel.classList.add("open");
  cartOverlay.classList.add("show");
  document.body.classList.add("cart-open");
}
function closeCart() {
  if (!cartPanel || !cartOverlay) return;
  cartPanel.classList.remove("open");
  cartOverlay.classList.remove("show");
  document.body.classList.remove("cart-open");
}

if (openCartBtn) {
  openCartBtn.addEventListener("click", openCart, { passive: true });
}

if (closeCartBtn) {
  const fresh = closeCartBtn.cloneNode(true);
  closeCartBtn.replaceWith(fresh);
  fresh.id = "closeCart";
  fresh.addEventListener("click", closeCart, { passive: true });
}
if (cartOverlay) {
  const fresh = cartOverlay.cloneNode(true);
  cartOverlay.replaceWith(fresh);
  fresh.id = "cartOverlay";
  fresh.addEventListener("click", closeCart, { passive: true });
}
document.addEventListener(
  "click",
  (e) => {
    if (!cartPanel) return;
    const open = cartPanel.classList.contains("open");
    if (!open) return;
    const clickedInside = cartPanel.contains(e.target);
    const clickedOpenBtn = document
      .getElementById("openCart")
      ?.contains(e.target);
    if (!clickedInside && !clickedOpenBtn) closeCart();
  },
  true
);
cartPanel?.addEventListener("click", (e) => e.stopPropagation());
document.addEventListener("keydown", (e) => {
  if (e.key === "Escape" && cartPanel?.classList.contains("open")) closeCart();
});

// render productos
if (grid && products.length) {
  grid.innerHTML = products
    .map(
      (p) => `
    <div class="product card-hover" data-pid="${p.id}">
      <div class="img-wrapper">
        <img src="${p.img}" alt="${p.name}">
        <div class="overlay">
          <button class="btn-hover-add" type="button">Agregar</button>
        </div>
      </div>
      <div class="product-info">
        <h3>${p.name}</h3>
        <p>${p.category}</p>
      </div>
    </div>
  `
    )
    .join("");
}

//helpers
function cartTotalQty() {
  return cart.reduce((t, i) => t + (i.qty || 0), 0);
}
function computeTotals() {
  return cart.reduce(
    (acc, it) => {
      acc.slots += (it.slots || 0) * (it.qty || 0);
      acc.weight += (it.weightKg || 0) * (it.qty || 0);
      acc.utility += (it.utility || 0) * (it.qty || 0);
      return acc;
    },
    { slots: 0, weight: 0, utility: 0 }
  );
}
function compressCart(cart) {
  const map = new Map();
  cart.forEach((it) => map.set(it.id, (map.get(it.id) || 0) + (it.qty || 0)));
  return [...map.entries()]
    .map(([id, qty]) => ({ id: Number(id), qty: Number(qty) }))
    .filter((x) => x.qty > 0)
    .sort((a, b) => a.id - b.id);
}
function sameCombo(a, b) {
  if (a.length !== b.length) return false;
  for (let i = 0; i < a.length; i++) {
    if (a[i].id !== b[i].id || a[i].qty !== b[i].qty) return false;
  }
  return true;
}

//agregar al carrito
window.addToCartCore = function (id) {
  const now = performance.now();
  const last = __lastAddTS.get(id) || 0;
  if (now - last < 250) return;
  __lastAddTS.set(id, now);

  const prod = products.find((p) => p.id === id);
  if (!prod) return;

  const found = cart.find((i) => i.id === id);
  if (found) found.qty++;
  else cart.push({ ...prod, qty: 1 });
  saveCart();
  renderCart();
  const icon = document.getElementById("openCart");

  if (icon) {
    setTimeout(() => {
      icon.classList.remove("cart-pop-arrival-soft");
      void icon.offsetWidth;
      icon.classList.add("cart-pop-arrival-soft");

      setTimeout(() => icon.classList.remove("cart-pop-arrival-soft"), 600);
    }, 550);
  }
};

if (grid) {
  grid.addEventListener("click", (e) => {
    const btn = e.target.closest(".btn-hover-add");
    if (!btn) return;
    const pid = Number(btn.closest(".product")?.dataset?.pid);
    if (!pid) return;
    window.addToCartCore(pid);
  });
}

// vaciar carrito
if (clearCartBtn) {
  const fresh = clearCartBtn.cloneNode(true);
  clearCartBtn.replaceWith(fresh);
  fresh.id = "clearCart";
  fresh.addEventListener("click", () => {
    cart = [];
    saveCart();
    renderCart();
    document.getElementById("recoBox")?.remove();
  });
}

// render carrito
function renderCart() {
  const cartCountEl = document.getElementById("cartCount");
  cartCountEl && (cartCountEl.textContent = String(cartTotalQty())); // ⬅️ agrega esta línea
  const { slots, weight, utility } = computeTotals();
  if (cartItemsContainer) {
    if (cart.length === 0) {
      cartItemsContainer.innerHTML = `<p style="color:#6b7a80; margin:6px 0;">Tu carrito está vacío.</p>`;
    } else {
      cartItemsContainer.innerHTML = cart
        .map(
          (item) => `
        <div class="cart-item">
          <img src="${item.img}" alt="${item.name}">
          <div class="item-info">
            <strong>${item.name}</strong><br>
            <button class="qty-btn" onclick="dec(${item.id})">-</button>
            ${item.qty}
            <button class="qty-btn" onclick="inc(${item.id})">+</button>
          </div>
        </div>
      `
        )
        .join("");
    }
  }

  slotCount && (slotCount.textContent = String(slots));
  weightCount &&
    (weightCount.textContent = Number.isFinite(weight)
      ? weight.toFixed(1)
      : "0.0");
  utilityCount && (utilityCount.textContent = String(utility));

  document.getElementById("recoBox")?.remove();
}
window.inc = function (id) {
  const it = cart.find((i) => i.id === id);
  if (!it) return;
  it.qty++;
  saveCart();
  renderCart();
};
window.dec = function (id) {
  const it = cart.find((i) => i.id === id);
  if (!it) return;
  it.qty--;
  if (it.qty <= 0) cart = cart.filter((i) => i.id !== id);
  saveCart();
  renderCart();
};

renderCart();

// mochila
const CAP_SLOTS = 10;
const CAP_WEIGHT_KG = 5;
const WUNIT = 20; // 0.05 kg

function unitsFromCart(cart) {
  const units = [];
  cart.forEach((item) => {
    for (let q = 0; q < item.qty; q++) {
      const wInt = Math.ceil((item.weightKg || 0) * WUNIT);
      units.push({
        uid: `${item.id}#${q + 1}`,
        pid: item.id,
        name: item.name,
        slots: item.slots,
        w: wInt,
        u: item.utility,
        img: item.img,
        wReal: item.weightKg || 0,
      });
    }
  });
  return units;
}

//algoritmo: programacion dinamica
function knapBest(
  units,
  capSlots = CAP_SLOTS,
  capW = Math.floor(CAP_WEIGHT_KG * WUNIT)
) {
  const S = capSlots;
  const W = Math.max(0, Math.floor(capW));

  const dp = Array.from({ length: S + 1 }, () =>
    Array.from({ length: W + 1 }, () => ({ val: 0, prev: null }))
  );

  // tabla DP por capas
  const layers = [dp];
  for (let i = 0; i < units.length; i++) {
    const u = units[i];
    const prev = layers[i];
    const cur = Array.from({ length: S + 1 }, (_, s) =>
      Array.from({ length: W + 1 }, (_, w) => ({ ...prev[s][w] }))
    );

    // transición de estados (tomar/no tomar)
    for (let s = u.slots; s <= S; s++) {
      for (let w = u.w; w <= W; w++) {
        const take = prev[s - u.slots][w - u.w].val + u.u;
        if (take > cur[s][w].val) {
          cur[s][w] = {
            val: take,
            prev: { i, s: s - u.slots, w: w - u.w, take: true },
          };
        }
      }
    }
    // relleno de "prev" para el caso de no tomar
    for (let s = 0; s <= S; s++) {
      for (let w = 0; w <= W; w++) {
        if (!cur[s][w].prev) cur[s][w].prev = { i, s, w, take: false };
      }
    }
    layers.push(cur);
  }

  //algoritmo: backtracking (reconstruccion de dp)
  let s = S,
    w = W,
    pickIdx = [];
  for (let i = units.length - 1; i >= 0; i--) {
    const cell = layers[i + 1][s][w].prev;
    if (cell.take) {       // si este ítem fue tomado
      pickIdx.push(i);     // lo agrego
      s = cell.s;          // y retrocedo en la tabla
      w = cell.w;
    }
  }
  pickIdx.reverse();
  return makeSolution(pickIdx, units); //arma solucion
}

function makeSolution(pickSet, units) {
  let slots = 0,
    wInt = 0,
    u = 0,
    wReal = 0;
  const byProd = new Map();
  pickSet.forEach((idx) => {
    const it = units[idx];
    slots += it.slots;
    wInt += it.w;
    wReal += it.wReal;
    u += it.u;
    byProd.set(it.pid, (byProd.get(it.pid) || 0) + 1);
  });
  const items = [...byProd.entries()].map(([pid, qty]) => {
    const one = units.find((u) => u.pid === pid);
    return { id: pid, name: one.name, img: one.img, qty };
  });
  return {
    items,
    totalSlots: slots,
    totalWeightKg: +(wInt / WUNIT).toFixed(2),
    realWeightKg: +wReal.toFixed(2),
    totalUtility: u,
  };
}

// algoritmo: divide and conquer  
// nota: se utilizó una variante de divide and conquer adaptada al de la mochila en la cual dividimos el espacio 
// de soluciones generando subproblemas "perturbados" (excluyendo los items dominantes o reduciendo capacidades), 
// resolvemos cada subproblema con dp y luego comparamos las soluciones resultantes :)
function topRecommendations(cart) {
  const units = unitsFromCart(cart);
  if (!units.length) return [];
  const best = knapBest(units);

  const uniques = new Map(); //parto con la mas optima
  const sig = (s) =>
    JSON.stringify(
      s.items.map((i) => ({ id: i.id, qty: i.qty })).sort((a, b) => a.id - b.id)
    );
  const pushU = (s) => {
    const k = sig(s);
    if (!uniques.has(k)) uniques.set(k, s);
  };
  const feasible = (s) =>
    s.totalSlots <= CAP_SLOTS && s.realWeightKg <= CAP_WEIGHT_KG + 1e-9;
  if (feasible(best)) pushU(best);

  //1.excluir productos dominantes para explorar otra region del espacio de soluciones
  const counts = new Map();
  best.items.forEach((it) => counts.set(it.id, it.qty));
  const topPids = [...counts.entries()]
    .sort((a, b) => b[1] - a[1])
    .slice(0, 3)
    .map(([pid]) => pid);

  topPids.forEach((pid) => {
    const u2 = units.filter((u) => u.pid !== pid);
    if (u2.length) {
      const s2 = knapBest(u2);
      if (feasible(s2)) pushU(s2);
    }
  });

  //2.reducir la capacidad de slots o peso para generar soluciones similares pero mas livianas
  [
    { s: CAP_SLOTS - 1, w: Math.floor(CAP_WEIGHT_KG * WUNIT) },
    { s: CAP_SLOTS, w: Math.floor((CAP_WEIGHT_KG - 0.5) * WUNIT) },
  ].forEach((c) => {
    const s2 = knapBest(units, Math.max(0, c.s), Math.max(0, c.w));
    if (feasible(s2)) pushU(s2);
  });

  //
  const byDensity = [...units].sort((a, b) => {
    const da = a.u / Math.max(1, a.slots) / Math.max(0.01, a.w);
    const db = b.u / Math.max(1, b.slots) / Math.max(0.01, b.w);
    return db - da;
  });
  let s = 0,
    w = 0,
    pick = [];
  for (const u of byDensity) {
    if (
      s + u.slots <= CAP_SLOTS &&
      w + u.w <= Math.floor(CAP_WEIGHT_KG * WUNIT)
    ) {
      pick.push(u);
      s += u.slots;
      w += u.w;
    }
  }
  if (pick.length) {
    const idxs = pick.map((u) => units.indexOf(u)).filter((i) => i >= 0);
    const greedySol = makeSolution(idxs, units);
    if (feasible(greedySol)) pushU(greedySol);
  }

  const sols = [...uniques.values()].sort((a, b) => {
    if (b.totalUtility !== a.totalUtility)
      return b.totalUtility - a.totalUtility;
    if (a.realWeightKg !== b.realWeightKg)
      return a.realWeightKg - b.realWeightKg;
    return a.totalSlots - b.totalSlots;
  });
  // siempre devolvemos AL MENOS la óptima
  return sols.slice(0, 4);
}

// render recomendaciones
function renderRecommendations(solutions) {
  const hostList = document.getElementById("cartItems");
  if (!hostList) return;

  let box = document.getElementById("recoBox");
  if (!box) {
    box = document.createElement("div");
    box.id = "recoBox";
    box.style.margin = "12px 0 0 0";
    hostList.appendChild(box);
  }

  if (!solutions.length) {
    box.innerHTML = `
      <div style="padding:12px;border:1px solid #e6eef0;border-radius:10px;background:#f8fbfc">
        <strong>No pude generar recomendaciones en este estado.</strong><br>
        Probá quitar algún ítem o reducir peso/slots.
      </div>`;
    return;
  }
  const best = solutions[0];
  const current = compressCart(cart);
  const bestItems = best.items
    .map((i) => ({ id: i.id, qty: i.qty }))
    .sort((a, b) => a.id - b.id);

  const isAlreadyBest = sameCombo(current, bestItems);

  if (isAlreadyBest) {
    box.innerHTML = `
      <div style="padding:12px;border:1px solid #e6eef0;border-radius:10px;background:#f8fbfc">
        <strong>No hay combinaciones que mejoren tu selección.</strong><br>
        Tu pedido ya es óptimo para las restricciones! 
      </div>`;
    return;
  }

  // render de tarjetas
  const card = (s) => `
    <div style="border:1px solid #e6eef0;border-radius:12px;padding:10px;margin-bottom:10px;background:#fff">
      <div style="font-weight:700;margin-bottom:6px">
        Utility: ${s.totalUtility} · Slots: ${
    s.totalSlots
  }/${CAP_SLOTS} · Peso: ${s.totalWeightKg.toFixed(1)}/${CAP_WEIGHT_KG} kg
      </div>
      <div style="display:flex;flex-wrap:wrap;gap:6px;margin-bottom:8px">
        ${s.items
          .map(
            (it) =>
              `<span style="background:#eef6f8;border:1px solid #d9e8e9;border-radius:8px;padding:4px 8px;font-size:12px">${it.name} × ${it.qty}</span>`
          )
          .join("")}
      </div>
      <button data-apply class="btn-primary" style="width:100%">Aplicar esta recomendación</button>
    </div>
  `;

  box.innerHTML = `
    <h3 style="margin:8px 0">Recomendaciones</h3>
    ${solutions.map(card).join("")}
  `;

  box.querySelectorAll("[data-apply]").forEach((btn, idx) => {
    btn.addEventListener("click", () => {
      const s = solutions[idx];
      const newCart = [];
      s.items.forEach((sel) => {
        const p = products.find((pp) => pp.id === sel.id);
        if (p) newCart.push({ ...p, qty: sel.qty });
      });
      cart = newCart;
      saveCart();
      renderCart();
      btn.textContent = "Aplicado! ";
      setTimeout(() => {
        btn.textContent = "Aplicar esta recomendación";
      }, 1000);
    });
  });
}

// generar recomendacion
(() => {
  if (!optimizeBtn) return;
  const clean = optimizeBtn.cloneNode(true);
  optimizeBtn.replaceWith(clean);
  clean.id = "optimizeBtn";
  clean.addEventListener(
    "click",
    (ev) => {
      ev.preventDefault();
      const sols = topRecommendations(cart);
      renderRecommendations(sols);
    },
    { passive: true }
  );
})();

// modal como funciona
(function setupInfoModal() {
  const infoBtn = document.getElementById("openInfo");
  const infoOverlay = document.getElementById("infoOverlay");
  const infoModal = document.getElementById("infoModal");
  const closeInfo = document.getElementById("closeInfo");
  const closeInfo2 = document.getElementById("closeInfo2");

  if (!infoBtn || !infoOverlay || !infoModal) return;

  function openInfo() {
    infoOverlay.classList.add("show");
    infoModal.classList.add("show");
    document.body.classList.add("info-open");
    infoModal.setAttribute("tabindex", "-1");
    infoModal.focus();
  }
  function closeInfoFn() {
    infoOverlay.classList.remove("show");
    infoModal.classList.remove("show");
    document.body.classList.remove("info-open");
  }

  const freshBtn = infoBtn.cloneNode(true);
  infoBtn.replaceWith(freshBtn);
  freshBtn.id = "openInfo";
  freshBtn.addEventListener("click", openInfo);

  closeInfo && closeInfo.addEventListener("click", closeInfoFn);
  closeInfo2 && closeInfo2.addEventListener("click", closeInfoFn);
  infoOverlay && infoOverlay.addEventListener("click", closeInfoFn);

  document.addEventListener("keydown", (e) => {
    if (e.key === "Escape" && infoModal.classList.contains("show"))
      closeInfoFn();
  });
})();
