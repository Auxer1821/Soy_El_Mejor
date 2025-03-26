const selector = document.getElementById("seleccionar_locacion")

function colocarIcono (nombreIcono){

    return L.icon({
        iconUrl: `../../../shared/assets/iconos_heladera/${nombreIcono}.png`,
        iconSize: [120, 120],
        iconAnchor: [22, 94],
        popupAnchor: [-3, -76],
        shadowSize: [68, 95],
        shadowAnchor: [22, 94]})
    
}

// Connection
fetch('../../../shared/heladeras.json')
    .then((response) => response.json())
    .then((json) => json.forEach((heladera) => agregarHeladera(heladera)));

const agregarHeladera = ({lat, lon, name, id, cantidadMaxima, cantidadViandas, icono}) => {

    console.log(lat);
    
    var icono = colocarIcono(icono)

    const marker = L.marker([lat, lon], {icon: icono}).addTo(map)
    const new_option = document.createElement("option")
    new_option.text = name
    new_option.id = id
    new_option.value = [lat, lon]

    const popupContent = `
    <div>
        <p class="h3">${name}</p>
        <p class="h4">Viandas: ${cantidadViandas}/${cantidadMaxima}</p>
        <div class="d-flex justify-content-around align-items-center">
            <div>
                <p>Suscribirme</p>
                <img class="btn img-fluid" src="../../resources/suscribirse.png"/ data-bs-toggle="modal" data-bs-target="#modal-view">
            </div>
            <div>
                <p>Reportar Falla</p>
                <img class="btn img-fluid" src="../../resources/reportar_falla.png"/ data-bs-toggle="modal" data-bs-target="#modal-falla">
            </div>
        </div>
    </div>`;
    
    // Agregar el popup al marcador
    marker.bindPopup(popupContent);

    selector.appendChild(new_option)
}

var map = L.map('map').setView([-34.61784311248886, -58.43367499250259], 13);

L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 500,
    attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
}).addTo(map);

selector.addEventListener("change", function(e){

    if (e.target.value == -1) {
        return
    }
    map.flyTo(e.target.value.split(','), 15);
})

// Función para guardar la ubicación del mapa en el almacenamiento local
function guardarUbicacionMapa() {
    const mapaEstado = {
        latitud: map.getCenter().lat,
        longitud: map.getCenter().lng,
        zoom: map.getZoom()
    };
    localStorage.setItem('mapaEstado', JSON.stringify(mapaEstado));
}

// Función para restaurar la ubicación del mapa desde el almacenamiento local
function restaurarUbicacionMapa() {
    const mapaEstadoGuardado = localStorage.getItem('mapaEstado');
    if (mapaEstadoGuardado) {
        const mapaEstado = JSON.parse(mapaEstadoGuardado);
        map.setView([mapaEstado.latitud, mapaEstado.longitud], mapaEstado.zoom);
    }
}

// Llamada para restaurar la ubicación del mapa cuando la página se carga
restaurarUbicacionMapa();

// Evento para guardar la ubicación del mapa cuando cambia la vista del mapa
map.on('moveend', guardarUbicacionMapa);

