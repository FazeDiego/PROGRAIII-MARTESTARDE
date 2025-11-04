// js/map.js - Usando OpenLayers
let map = null;
let routeLayer = null;
let cornerLayer = null;
let poiLayer = null;
let tooltipOverlay = null;
let courierLayer = null;
let assignmentLayer = null;

export function initMap(center = [-58.4199, -34.5989], zoom = 16) {
  const view = new ol.View({
    center: ol.proj.fromLonLat(center),
    zoom: zoom
  });

  map = new ol.Map({
    target: 'map',
    layers: [
      new ol.layer.Tile({
        source: new ol.source.OSM()
      })
    ],
    view: view
  });

  // Crear elemento para el tooltip
  const tooltipElement = document.createElement('div');
  tooltipElement.className = 'ol-tooltip';
  tooltipElement.style.cssText = `
    position: absolute;
    background: rgba(0, 0, 0, 0.8);
    color: white;
    padding: 4px 8px;
    border-radius: 4px;
    font-size: 12px;
    white-space: nowrap;
    pointer-events: none;
    display: none;
  `;
  
  tooltipOverlay = new ol.Overlay({
    element: tooltipElement,
    offset: [0, -15],
    positioning: 'bottom-center'
  });
  map.addOverlay(tooltipOverlay);

  // Detectar cuando el cursor pasa sobre un feature
  map.on('pointermove', function(evt) {
    const pixel = map.getEventPixel(evt.originalEvent);
    const feature = map.forEachFeatureAtPixel(pixel, function(feature) {
      return feature;
    });

    if (feature) {
      const name = feature.get('name');
      if (name) {
        tooltipElement.style.display = 'block';
        tooltipElement.textContent = name;
        tooltipOverlay.setPosition(evt.coordinate);
        map.getTargetElement().style.cursor = 'pointer';
      }
    } else {
      tooltipElement.style.display = 'none';
      map.getTargetElement().style.cursor = '';
    }
  });

  window.__MAP__ = map;
  setTimeout(() => map.updateSize(), 100);
  window.addEventListener('resize', () => { if (map) map.updateSize(); });
}

export function getMap() { return map; }

export function drawRoute(nodes) {
  clearRoute();
  if (!nodes || nodes.length === 0) return;
  
  const coordinates = nodes.map(n => ol.proj.fromLonLat([n.lng, n.lat]));
  const lineFeature = new ol.Feature({
    geometry: new ol.geom.LineString(coordinates)
  });
  
  lineFeature.setStyle(new ol.style.Style({
    stroke: new ol.style.Stroke({
      color: '#1f78ff',
      width: 5
    })
  }));
  
  const pointFeatures = nodes.map(n => {
    const feature = new ol.Feature({
      geometry: new ol.geom.Point(ol.proj.fromLonLat([n.lng, n.lat]))
    });
    feature.setStyle(new ol.style.Style({
      image: new ol.style.Circle({
        radius: 6,
        fill: new ol.style.Fill({ color: '#1f78ff' }),
        stroke: new ol.style.Stroke({ color: 'white', width: 2 })
      })
    }));
    // Guardar tanto el ID como el nombre para mostrar en el tooltip
    feature.set('name', `${n.id}${n.name ? ' · ' + n.name : ''}`);
    return feature;
  });
  
  routeLayer = new ol.layer.Vector({
    source: new ol.source.Vector({
      features: [lineFeature, ...pointFeatures]
    })
  });
  
  map.addLayer(routeLayer);
  const extent = routeLayer.getSource().getExtent();
  map.getView().fit(extent, { padding: [50, 50, 50, 50], duration: 500 });
}

export function plotCorners(corners) {
  clearCorners();
  if (!corners || corners.length === 0) return;
  
  const features = corners.map(c => {
    const feature = new ol.Feature({
      geometry: new ol.geom.Point(ol.proj.fromLonLat([c.lng, c.lat]))
    });
    feature.setStyle(new ol.style.Style({
      image: new ol.style.Circle({
        radius: 4,
        fill: new ol.style.Fill({ color: '#444' }),
        stroke: new ol.style.Stroke({ color: 'white', width: 1 })
      })
    }));
    feature.set('name', c.name || c.id);
    return feature;
  });
  
  cornerLayer = new ol.layer.Vector({
    source: new ol.source.Vector({
      features: features
    })
  });
  
  map.addLayer(cornerLayer);
}

export function clearRoute() {
  if (routeLayer) { 
    map.removeLayer(routeLayer); 
    routeLayer = null; 
  }
}

export function clearCorners() {
  if (cornerLayer) { 
    map.removeLayer(cornerLayer); 
    cornerLayer = null; 
  }
}

export function plotPois(pois) {
  clearPois();
  if (!pois || pois.length === 0) return;
  
  const features = pois.map(p => {
    const feature = new ol.Feature({
      geometry: new ol.geom.Point(ol.proj.fromLonLat([p.lng, p.lat]))
    });
    feature.setStyle(new ol.style.Style({
      image: new ol.style.Circle({
        radius: 6,
        fill: new ol.style.Fill({ color: 'limegreen', opacity: 0.85 }),
        stroke: new ol.style.Stroke({ color: 'green', width: 2 })
      })
    }));
    feature.set('name', `${p.name || p.type} (${p.type})`);
    return feature;
  });
  
  poiLayer = new ol.layer.Vector({
    source: new ol.source.Vector({
      features: features
    })
  });
  
  map.addLayer(poiLayer);
}

export function clearPois() {
  if (poiLayer) { 
    map.removeLayer(poiLayer); 
    poiLayer = null; 
  }
}

export function clearAll() {
  clearRoute();
  clearCorners();
  clearPois();
  clearAssignments();
}

export function refreshMapSize() { 
  if (map) map.updateSize(); 
}

// ========================================
// Funciones para asignación de pedidos
// ========================================

export function plotCouriers(couriers) {
  // couriers: [{courierId, cornerId, lat, lng, color, orders}]
  clearCouriers();
  if (!couriers || couriers.length === 0) return;
  
  const features = [];
  
  couriers.forEach(courier => {
    // Marcador del repartidor (círculo grande)
    const courierFeature = new ol.Feature({
      geometry: new ol.geom.Point(ol.proj.fromLonLat([courier.lng, courier.lat]))
    });
    courierFeature.setStyle(new ol.style.Style({
      image: new ol.style.Circle({
        radius: 10,
        fill: new ol.style.Fill({ color: courier.color || '#1f77b4' }),
        stroke: new ol.style.Stroke({ color: 'white', width: 3 })
      })
    }));
    courierFeature.set('name', `${courier.courierId} @ ${courier.cornerId}`);
    features.push(courierFeature);
  });
  
  courierLayer = new ol.layer.Vector({
    source: new ol.source.Vector({
      features: features
    })
  });
  
  map.addLayer(courierLayer);
}

export function plotAssignments(couriers, cornersMap) {
  // couriers: [{courierId, cornerId, lat, lng, orders:[orderId...], color}]
  // cornersMap: {cornerId: {lat, lng}}
  clearAssignmentLines();
  if (!couriers || couriers.length === 0 || !cornersMap) return;
  
  const features = [];
  
  couriers.forEach(courier => {
    const courierCoord = [courier.lng, courier.lat];
    
    if (courier.orders && courier.orders.length > 0) {
      courier.orders.forEach(orderId => {
        const orderCorner = cornersMap[orderId];
        if (orderCorner) {
          const orderCoord = [orderCorner.lng, orderCorner.lat];
          
          // Línea del repartidor al pedido
          const lineFeature = new ol.Feature({
            geometry: new ol.geom.LineString([
              ol.proj.fromLonLat(courierCoord),
              ol.proj.fromLonLat(orderCoord)
            ])
          });
          lineFeature.setStyle(new ol.style.Style({
            stroke: new ol.style.Stroke({
              color: courier.color || '#1f77b4',
              width: 2,
              lineDash: [5, 5]
            })
          }));
          features.push(lineFeature);
          
          // Marcador del pedido (círculo pequeño)
          const orderFeature = new ol.Feature({
            geometry: new ol.geom.Point(ol.proj.fromLonLat(orderCoord))
          });
          orderFeature.setStyle(new ol.style.Style({
            image: new ol.style.Circle({
              radius: 5,
              fill: new ol.style.Fill({ color: courier.color || '#1f77b4' }),
              stroke: new ol.style.Stroke({ color: 'white', width: 2 })
            })
          }));
          orderFeature.set('name', `Pedido: ${orderId}`);
          features.push(orderFeature);
        }
      });
    }
  });
  
  assignmentLayer = new ol.layer.Vector({
    source: new ol.source.Vector({
      features: features
    })
  });
  
  map.addLayer(assignmentLayer);
}

export function clearCouriers() {
  if (courierLayer) { 
    map.removeLayer(courierLayer); 
    courierLayer = null; 
  }
}

export function clearAssignmentLines() {
  if (assignmentLayer) { 
    map.removeLayer(assignmentLayer); 
    assignmentLayer = null; 
  }
}

export function clearAssignments() {
  clearCouriers();
  clearAssignmentLines();
}
