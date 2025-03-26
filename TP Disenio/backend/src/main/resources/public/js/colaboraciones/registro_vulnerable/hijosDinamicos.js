const add_contact_button = document.getElementById("image-button-add");
const contactContainer = document.querySelector('.dynamic-children');


let count = 0;
const add_children_form = () => {
    count++;
    contactContainer.insertAdjacentHTML('beforeend', `
    <div class="row mb-3 dynamic-children separator-children p-2">
        <h5>Hijo</h5>
        <div class="row mb-3">
  
            <div class="col-md-6">
                <label for="firstName" class="form-label">Nombre*</label>
                <input type="text" class="form-control" id="firstName${count}" name="nombre" placeholder="ej: Bruno" required>
            </div>
  
            <div class="col-md-6">
                <label for="lastName" class="form-label">Apellido*</label>
                <input type="text" class="form-control" id="lastName${count}" name="apellido" placeholder="ej: Juan Sartori"required>
            </div>
  
        </div>
  
        <!-- Opciones 4: Documento -->
        <div class="mb-3 d-flex align-items-center">
          <!-- Campo de tipo de documento a la izquierda -->
          <div class="flex-shrink-1 me-3">
              <label for="documentType" class="form-label">Tipo de documento*</label>
              <select class="form-select" id="documentType${count}" name="tipo_documento" required>
                  <option value="">Seleccione...</option>
                  <option value="dni">DNI</option>
                  <option value="libreta-civica">Libreta Cívica</option>
                  <option value="libreta-electronica">Libreta Electrónica</option>
              </select>
          </div>
          
          <!-- Campo de número de documento a la derecha -->
          <div class="flex-grow-1">
              <label for="documentNumber" class="form-label">Número de documento*</label>
              <input type="text" class="form-control" id="documentNumber${count}" name="numero_documento" placeholder="ej: 44552722"required>
          </div>
        </div>


         <div class="mb-3">
            <label for="birthdate" class="form-label">Fecha de nacimiento*</label>
            <input type="date" class="form-control" id="birthdate${count}" name="dob" required>
        </div>

        <div class="col-12 mt-2">
                <button type="button" class="btn btn-danger btn-remove-children">Eliminar</button>
        </div>

     </div>
        `);
}

const delete_children_form = (event) => {
    const contactForm = event.target.closest('.dynamic-children');
    contactForm.remove();
}

window.addEventListener('load', () => {
    add_contact_button.addEventListener('click', () => {
        add_children_form();

        contactContainer.addEventListener('click', (event) => {
            if (event.target.classList.contains('btn-remove-children')) {
                delete_children_form(event);
            }
        });

    })
})