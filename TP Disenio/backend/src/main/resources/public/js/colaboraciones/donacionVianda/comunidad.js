const modalView = document.getElementById('donarModal');

modalView.addEventListener('show.bs.modal', function (event) {
    const nombre_comunidad = document.getElementById('nombreComunidad').textContent;
    const direccion_comunidad = document.getElementById('direccionComunidad').textContent;

    const modalTitle = modalView.querySelector('.modal-body .h2');
    modalTitle.textContent = `Donar a: ${nombre_comunidad}`;

    document.getElementById('nComunidad').value = nombre_comunidad;
    document.getElementById('dComunidad').value = direccion_comunidad;
});