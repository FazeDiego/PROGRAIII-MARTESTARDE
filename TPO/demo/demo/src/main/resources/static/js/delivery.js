// delivery.js - Módulo para gestión de repartidores y pedidos
import { apiGet, apiPost } from './api.js';
import { clearAll, plotCorners } from './map.js';

let deliveryPersons = [];
let orders = [];
let currentAssignments = null;
let cornersCache = {};  // Cache de corners para obtener coordenadas

const COLORS = ['#FF6B6B', '#4ECDC4', '#45B7D1', '#FFA07A', '#98D8C8', '#F7DC6F'];

/**
 * Inicializar el módulo de delivery
 */
export function initDelivery() {
  document.getElementById('btnGenerateDelivery').addEventListener('click', generateDeliveryScenario);
  document.getElementById('btnAssignOrders').addEventListener('click', assignOrders);
  document.getElementById('btnClearDelivery').addEventListener('click', clearDeliveryView);
  document.getElementById('btnAddDeliveryPerson').addEventListener('click', addDeliveryPersonManually);
  document.getElementById('btnAddOrder').addEventListener('click', addOrderManually);
  
  // Cargar corners en cache
  loadCornersCache();
}

/**
 * Cargar corners en caché para tener coordenadas
 */
async function loadCornersCache() {
  try {
    const corners = await apiGet('/graph/corners?limit=100');
    corners.forEach(c => {
      cornersCache[c.id] = c;
    });
  } catch (e) {
    console.error('Error cargando corners:', e);
  }
}

/**
 * Generar escenario aleatorio de repartidores y pedidos
 */
async function generateDeliveryScenario() {
  try {
    const numDelivery = parseInt(document.getElementById('numDeliveryPersons').value) || 3;
    const numOrders = parseInt(document.getElementById('numOrders').value) || 5;

    // Generar repartidores aleatorios (ahora usa corners reales de la BD)
    const personsResp = await apiGet(`/delivery/generate-delivery-persons?count=${numDelivery}`);
    deliveryPersons = personsResp;

    // Generar pedidos aleatorios (ahora usa corners reales de la BD)
    const ordersResp = await apiGet(`/delivery/generate-orders?count=${numOrders}`);
    orders = ordersResp;

    // Mostrar en la UI
    displayDeliveryPersons();
    displayOrders();
    visualizeOnMap();

    document.getElementById('deliveryOutput').innerHTML = `
      <div class="success">
        ✅ Generados ${deliveryPersons.length} repartidores y ${orders.length} pedidos
      </div>
    `;
  } catch (err) {
    document.getElementById('deliveryOutput').innerHTML = `
      <div class="error">Error generando escenario: ${err.message}</div>
    `;
  }
}

/**
 * Asignar pedidos a repartidores usando algoritmo Greedy
 */
async function assignOrders() {
  if (deliveryPersons.length === 0 || orders.length === 0) {
    document.getElementById('deliveryOutput').innerHTML = `
      <div class="error">⚠️ Primero genera repartidores y pedidos</div>
    `;
    return;
  }

  try {
    const mode = document.getElementById('deliveryMode').value;

    const request = {
      deliveryPersons: deliveryPersons,
      orders: orders,
      mode: mode
    };

    const response = await apiPost('/delivery/assign', request);
    currentAssignments = response;

    // Visualizar resultados
    displayAssignments(response);
    visualizeAssignmentsOnMap(response);

  } catch (err) {
    document.getElementById('deliveryOutput').innerHTML = `
      <div class="error">Error en asignación: ${err.message}</div>
    `;
  }
}

/**
 * Mostrar repartidores en la lista
 */
function displayDeliveryPersons() {
  const container = document.getElementById('deliveryPersonsList');
  container.innerHTML = '<h4>🚴 Repartidores:</h4>';
  
  deliveryPersons.forEach((person, idx) => {
    const color = COLORS[idx % COLORS.length];
    container.innerHTML += `
      <div class="delivery-item" style="border-left: 4px solid ${color}">
        <strong>${person.name}</strong> (${person.id})<br/>
        📍 Ubicación: ${person.currentLocationId}<br/>
        📦 Max pedidos: ${person.maxOrders}
      </div>
    `;
  });
}

/**
 * Mostrar pedidos en la lista
 */
function displayOrders() {
  const container = document.getElementById('ordersList');
  container.innerHTML = '<h4>📦 Pedidos:</h4>';
  
  orders.forEach(order => {
    const priorityStars = '⭐'.repeat(order.priority);
    container.innerHTML += `
      <div class="delivery-item">
        <strong>${order.id}</strong> ${priorityStars}<br/>
        🟢 Recogida: ${order.pickupLocationId}<br/>
        🔴 Entrega: ${order.deliveryLocationId}
      </div>
    `;
  });
}

/**
 * Visualizar repartidores y pedidos en el mapa (sin asignaciones)
 */
async function visualizeOnMap() {
  clearAll();
  // Por ahora, solo mostraremos los corners básicos
  // La visualización completa se hará cuando se asignen los pedidos
}

/**
 * Mostrar resultados de la asignación
 */
function displayAssignments(response) {
  let html = `
    <div class="success">
      <h3>✅ Asignación Completada (${response.algorithm})</h3>
      <p>⏱️ Tiempo de cómputo: ${response.computationTimeMs} ms</p>
    </div>
  `;

  response.assignments.forEach((assignment, idx) => {
    const color = COLORS[idx % COLORS.length];
    const person = assignment.deliveryPerson;
    const ordersCount = assignment.assignedOrders.length;
    const totalDist = assignment.totalDistance.toFixed(2);
    const totalTime = assignment.totalTime;

    html += `
      <div class="assignment-box" style="border-left: 4px solid ${color}">
        <h4>${person.name} (${person.id})</h4>
        <p><strong>${ordersCount} pedidos asignados:</strong></p>
        <ul>
    `;

    assignment.assignedOrders.forEach(order => {
      html += `<li>${order.id}: ${order.pickupLocationId} → ${order.deliveryLocationId}</li>`;
    });

    html += `
        </ul>
        <p><strong>📏 Distancia total:</strong> ${totalDist} km</p>
        <p><strong>⏱️ Tiempo total:</strong> ${totalTime} min</p>
      </div>
    `;
  });

  if (response.unassignedOrders.length > 0) {
    html += `
      <div class="error">
        <h4>⚠️ Pedidos no asignados:</h4>
        <ul>
    `;
    response.unassignedOrders.forEach(order => {
      html += `<li>${order.id}</li>`;
    });
    html += `</ul></div>`;
  }

  document.getElementById('deliveryOutput').innerHTML = html;
}

/**
 * Visualizar asignaciones en el mapa con rutas
 */
async function visualizeAssignmentsOnMap(response) {
  clearAll();
  // Por ahora, la visualización detallada se hará en una versión futura
  // Para simplificar, podemos mostrar solo las métricas en el panel
}

/**
 * Agregar repartidor manualmente
 */
function addDeliveryPersonManually() {
  const id = prompt('ID del repartidor (ej: D1):');
  const name = prompt('Nombre del repartidor:');
  const location = prompt('Corner ID de ubicación actual (ej: C10):');

  if (id && name && location) {
    const person = {
      id: id,
      name: name,
      currentLocationId: location,
      maxOrders: 2
    };
    deliveryPersons.push(person);
    displayDeliveryPersons();
    visualizeOnMap();
  }
}

/**
 * Agregar pedido manualmente
 */
function addOrderManually() {
  const id = prompt('ID del pedido (ej: O1):');
  const pickup = prompt('Corner ID de recogida (ej: C5):');
  const delivery = prompt('Corner ID de entrega (ej: C20):');
  const priority = parseInt(prompt('Prioridad (1-3):')) || 1;

  if (id && pickup && delivery) {
    const order = {
      id: id,
      pickupLocationId: pickup,
      deliveryLocationId: delivery,
      priority: priority,
      description: `Pedido ${id}`
    };
    orders.push(order);
    displayOrders();
    visualizeOnMap();
  }
}

/**
 * Limpiar vista de delivery
 */
function clearDeliveryView() {
  deliveryPersons = [];
  orders = [];
  currentAssignments = null;
  document.getElementById('deliveryPersonsList').innerHTML = '';
  document.getElementById('ordersList').innerHTML = '';
  document.getElementById('deliveryOutput').innerHTML = '';
  clearAll();
}
