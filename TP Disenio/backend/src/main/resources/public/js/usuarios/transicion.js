// Ir de la sección inicial (humana) a la segunda (usuario)
document.getElementById("boton_mostrar_usuario").addEventListener("click", () => {
    const sectionInitial = document.getElementById("section_inicial");
    const sectionUsuario = document.getElementById("section_usuario");

    // Añadir animación de salida hacia la izquierda a la primera sección
    sectionInitial.classList.remove("active-left", "active-right");
    sectionInitial.classList.add("section-exit-left");

    // Asegurarse de que la nueva sección entre desde la derecha
    setTimeout(() => {
        sectionInitial.classList.remove("section-exit-left");
        sectionInitial.classList.add("d-none");

        // Mostrar la segunda sección con animación desde la derecha
        sectionUsuario.classList.remove("d-none", "section-exit-right");
        sectionUsuario.classList.add("active-right");
    }, 500); // Tiempo de espera igual al de la animación (0.5s)
});

// Ir de la sección de usuario a la sección inicial (humana)
document.getElementById("boton_regresar").addEventListener("click", () => {
    const sectionHumana = document.getElementById("section_inicial");
    const sectionUsuario = document.getElementById("section_usuario");

    // Añadir animación de salida hacia la derecha a la segunda sección
    sectionUsuario.classList.remove("active-left", "active-right");
    sectionUsuario.classList.add("section-exit-right");

    // Asegurarse de que la primera sección entre desde la izquierda
    setTimeout(() => {
        sectionUsuario.classList.remove("section-exit-right");
        sectionUsuario.classList.add("d-none");

        // Mostrar la primera sección con animación desde la izquierda
        sectionHumana.classList.remove("d-none", "section-exit-left");
        sectionHumana.classList.add("active-left");
    }, 500); // Tiempo de espera igual al de la animación (0.5s)
});
