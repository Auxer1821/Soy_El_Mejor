const dropArea = document.getElementById('area-arrastre');
const uploadButton = document.getElementById('boton-carga');
const fileInput = document.getElementById('file-upload');
let uploadedFiles = [];  // Almacena los archivos cargados

// Manejo del área de arrastre
dropArea.addEventListener('dragover', (event) => {
    event.preventDefault();
    dropArea.classList.add('dragover');
});

dropArea.addEventListener('dragleave', () => {
    dropArea.classList.remove('dragover');
});

dropArea.addEventListener('drop', (event) => {
    event.preventDefault();
    dropArea.classList.remove('dragover');

    const files = Array.from(event.dataTransfer.files);
    handleFiles(files);
});

// Manejo del botón de selección de archivos
fileInput.addEventListener('change', (event) => {
    const files = Array.from(event.target.files);
    handleFiles(files);
});

function handleFiles(files) {
    const csvFiles = files.filter(file => file.type === 'text/csv'); // Filtrar solo archivos .csv

    if (csvFiles.length > 0) {
        uploadedFiles = csvFiles; // Guardar archivos cargados
        updateDropArea(); // Actualizar el área de arrastre con las vistas previas
        updateUploadButtonState();
    } else {
        alert('Solo se permiten archivos CSV.');
    }
}

function updateDropArea() {
    clearDropAreaPreviews(); // Limpiar las vistas previas anteriores

    uploadedFiles.forEach((file, index) => {
        // Mostrar un ícono para los archivos CSV
        const icon = document.createElement('img');
        icon.src = '../../../resources/csv_image.png'; // Nueva imagen para archivos CSV
        createPreviewElement(icon, file.name, index);
    });
}

function createPreviewElement(element, filename, index) {
    const container = document.createElement('div');
    container.classList.add('preview-item');

    container.appendChild(element);

    const span = document.createElement('span');
    span.textContent = filename;
    container.appendChild(span);

    // Botón de eliminar
    const deleteButton = document.createElement('button');
    deleteButton.classList.add('delete-button');
    deleteButton.textContent = 'x';
    deleteButton.onclick = () => removeFile(index); // Eliminar archivo al hacer clic
    container.appendChild(deleteButton);

    dropArea.appendChild(container);
}

function removeFile(index) {
    uploadedFiles.splice(index, 1); // Eliminar archivo del array
    updateDropArea(); // Actualizar el área de arrastre con las vistas previas
    updateUploadButtonState();
}

function clearDropAreaPreviews() {
    // Limpiar solo las vistas previas, no el contenido principal (texto e imagen de fondo)
    const previews = dropArea.querySelectorAll('.preview-item');
    previews.forEach(preview => preview.remove());
}

function updateUploadButtonState() {
    uploadButton.disabled = uploadedFiles.length === 0; // Habilita o deshabilita el botón según si hay archivos cargados
}

uploadButton.addEventListener('click', () => {
    if (uploadedFiles.length > 0) {
        // Aquí puedes implementar la lógica para enviar los archivos al servidor
        console.log("Enviando archivos:", uploadedFiles);
        alert("Archivos enviados exitosamente.");
    } else {
        alert("No hay archivos para enviar.");
    }
});
