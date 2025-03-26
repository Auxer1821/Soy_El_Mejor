const boton = document.getElementById("boton_mostrar_usuario")
const boton_regresar = document.getElementById("boton_regresar_humana")
const s_humana = document.getElementById("section_humana")
const s_usuario = document.getElementById("section_usuario")

function cambiar_form(){
    s_humana.classList.toggle("d-none")
    s_usuario.classList.toggle("d-none")
}

window.addEventListener("load", () => { 
    boton.addEventListener("click", cambiar_form)
    boton_regresar.addEventListener("click", cambiar_form)
})

 