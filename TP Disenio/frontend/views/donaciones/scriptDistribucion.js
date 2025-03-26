const data = {
    '11111': {
        heladeraOrigen: 'Valor para código 11111 - Heladera Origen',
        heladeraDestino: 'Valor para código 11111 - Heladera Destino',
        cantidadVianda: 'Valor para código 11111 - Cantidad Vianda'
    },
    '00000': {
        heladeraOrigen: 'Valor para código 00000 - Heladera Origen',
        heladeraDestino: 'Valor para código 00000 - Heladera Destino',
        cantidadVianda: 'Valor para código 00000 - Cantidad Vianda'
    },
    '2037440': {
        heladeraOrigen: 'Bruno',
        heladeraDestino: 'Juan',
        cantidadVianda: 'Sartori'
    },
    '2035637': {
        heladeraOrigen: 'Valentin',
        heladeraDestino: 'Diaz',
        cantidadVianda: 'Arrua'
    }
    // Agrega más códigos y datos según sea necesario
};

document.addEventListener('DOMContentLoaded', function() {
    // Leer el parámetro de la URL
    const urlParams = new URLSearchParams(window.location.search);
    const code = urlParams.get('code');

    const codeInput = document.getElementById('codeInput');
    const section = document.getElementById('dataSection');
    const heladeraOrigen = document.getElementById('heladeraOrigen');
    const heladeraDestino = document.getElementById('heladeraDestino');
    const cantidadVianda = document.getElementById('cantidadVianda');

    // Función para mostrar los datos basados en el código
    function mostrarDatos(code) {
        if (data[code]) {
            heladeraOrigen.textContent = data[code].heladeraOrigen;
            heladeraDestino.textContent = data[code].heladeraDestino;
            cantidadVianda.textContent = data[code].cantidadVianda;
            section.style.display = 'block';
        } else {
            section.style.display = 'none';
        }
    }

    // Si el código viene en la URL, cargarlo en el campo y mostrar los datos
    if (code) {
        codeInput.value = code;
        mostrarDatos(code);
    }

    // Permitir cargar el código manualmente
    codeInput.addEventListener('input', function() {
        const manualCode = codeInput.value;
        mostrarDatos(manualCode);
    });

    // Evento de clic para el botón de aceptar
    document.getElementById('acceptButton').addEventListener('click', function() {
        window.location.href = 'https://www.google.com.ar'; // Reemplaza con el enlace deseado
    });
});
