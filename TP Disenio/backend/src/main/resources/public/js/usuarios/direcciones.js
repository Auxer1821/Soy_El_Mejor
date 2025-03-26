const provincias = [
    "CABA", "Buenos Aires", "Catamarca", "Chaco", "Chubut",
    "Córdoba", "Corrientes", "Entre Ríos", "Formosa", "Jujuy",
    "La Pampa", "La Rioja", "Mendoza", "Misiones", "Neuquén",
    "Río Negro", "Salta", "San Juan", "San Luis", "Santa Cruz",
    "Santa Fe", "Santiago del Estero", "Tierra del Fuego", "Tucumán"
];

function cargarProvincias() {
    const selectProvincia = document.getElementById('provincia');

    provincias.forEach(provincia => {
        let option = document.createElement('option');
        option.value = provincia;
        option.text = provincia;
        selectProvincia.appendChild(option);
    });
}

// Función para actualizar las localidades según la provincia seleccionada
async function actualizarLocalidades() {
    const selectLocalidad = document.getElementById('localidad');
    const provinciaSeleccionada = document.getElementById('provincia').value;

    // Limpiar el select de localidades
    selectLocalidad.innerHTML = '<option value="">Selecciona una localidad</option>';

    // Validar que se haya seleccionado una provincia
    if (!provinciaSeleccionada) return;

    // Parámetros para la solicitud (usando el nombre de la provincia seleccionada)
    const url = `https://apis.datos.gob.ar/georef/api/localidades?provincia=${encodeURIComponent(provinciaSeleccionada)}&campos=id,nombre&max=1000`;

    try {
        // Realizar la solicitud GET a la API de Georef Argentina
        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        // Verificar que la respuesta fue exitosa
        if (!response.ok) {
            throw new Error('Error en la respuesta de la API');
        }

        // Convertir la respuesta en JSON
        const result = await response.json();

        // Usar un Set para eliminar duplicados por nombre
        const nombresUnicos = new Set();

        // Iterar sobre las localidades y agregar opciones al select
        result.localidades
            .sort((a, b) => a.nombre.localeCompare(b.nombre)) // Ordena por nombre
            .forEach(localidad => {
                if (!nombresUnicos.has(localidad.nombre)) { // Si el nombre no está en el Set
                    nombresUnicos.add(localidad.nombre);  // Agregar el nombre al Set
                    let option = document.createElement('option');
                    option.value = localidad.id;
                    option.text = localidad.nombre;
                    selectLocalidad.appendChild(option);  // Agregar la opción al select
                }
            });

        // Agregar un evento para actualizar el campo oculto cuando cambia la localidad
        selectLocalidad.addEventListener('change', function () {
            document.getElementById('nombreLocalidad').value = this.options[this.selectedIndex].text;
        });
    } catch (error) {
        console.error('Error al cargar localidades:', error);
    }
}



// Cargar las provincias al cargar la página
window.onload = cargarProvincias;
