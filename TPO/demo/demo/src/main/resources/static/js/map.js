// js/map.js - Usando OpenLayers
let map = null;
let routeLayer = null;
let cornerLayer = null;
let poiLayer = null;
let tooltipOverlay = null;

export function initMap(center = [-58.393, -34.602], zoom = 15) {
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
        fill: new ol.style.Fill({ color: '#22c55e' }),
        stroke: new ol.style.Stroke({ color: 'white', width: 2 })
      })
    }));
    feature.set('name', `${p.name} (${p.type})`);
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
}

export function refreshMapSize() { 
  if (map) map.updateSize(); 
}
