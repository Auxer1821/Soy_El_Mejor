document.addEventListener("DOMContentLoaded", function() {
    const form = document.getElementById('createUserForm');
    console.log('----Tengo el Formulario----');
    
    // Función para guardar los datos del formulario en localStorage
    function saveFormData() {
        console.log('----Entre en los Saves----');
        const formData = new FormData(form);
        const newData = {};

        console.log('----formdata = ',formData);

        formData.forEach((value, key) => {
            if (!newData[key]) {
                newData[key] = [];
            }
            console.log('----DATA,VALOR = ',JSON.stringify(key),",",JSON.stringify(value),"----");
            newData[key].push(value);
        });

        // Recupera los datos existentes de localStorage
        const existingData = localStorage.getItem('formDataVulnerable');
        let data = {};

        if (existingData) {
            data = JSON.parse(existingData);
        }

        // Combina los nuevos datos con los existentes
        Object.keys(newData).forEach(key => {
        if (!newData[key]) {
            data[key] = data[key];
        }
        else
        {
            data[key] = newData[key];
        }
        // Asegúrate de que los datos existentes y los nuevos se combinen correctamente
        });

        

        // Guarda los datos en localStorage
        localStorage.setItem('formDataVulnerable', JSON.stringify(data));
        console.log('Saved Data:', JSON.stringify(data));
    }

    // Guarda los datos al modificar cualquier campo
    form.addEventListener('input', saveFormData);

    // Guarda los datos al enviar el formulario
    form.addEventListener('submit', function(event) {
        event.preventDefault(); // Previene el envío del formulario
        saveFormData(); // Guarda los datos en localStorage
        //window.location.href = 'crearUsuario.html'; // Redirige a la siguiente página
    });

    // Restaura los datos si están disponibles
    let childrenCounter = document.querySelectorAll('.dynamic-children').length;
    const savedData = localStorage.getItem('formDataVulnerable');
    console.log('Saved Data from localStorage:', savedData);

    if (savedData) {
        const data = JSON.parse(savedData);
        console.log('Parsed Data:', data);

        // Restaurar datos generales
        Object.keys(data).forEach(key => {
            const values = data[key];
            const inputs = document.querySelectorAll(`[name="${key}"]`);
            inputs.forEach((input, index) => {
                if (input) {
                    input.value = values[index] || '';
                }
            });
        });

        // Restaurar los hijos dinámicos
        const childrenContainer = document.querySelector('.dynamic-childrens');
        console.log('----Restaurar childrenos Dinamicos---', childrenContainer);
        if (childrenContainer) {
            childrenContainer.innerHTML = ''; // Limpia el contenedor antes de restaurar

            // Obtener los datos de childrenos desde el localStorage
            const childrenMethods = data['childrenMethod[]'] || [];
            const childrens = data['children[]'] || [];
            console.log('----childrenMethods:', childrenMethods);


            childrenMethods.forEach((method, index) => {
                console.log('----childrenCounter:', childrenCounter);
                addchildrenFields(method, childrens[index], childrenCounter>0); // Añadir con skipCounter en true
            });
        }
    }

    // Función para agregar un nuevo conjunto de campos de childreno
    function addchildrenFields(childrenMethod = '', children = '', skipCounter = false) {

        childrenCounter++;
        const childrenContainer = document.querySelector('.dynamic-childrens');
        console.log('----childrenCounter:', childrenCounter);

        if(skipCounter){
        const childrenHtml = `
        <div class="row mb-3 dynamic-children">

        <div class="row mb-3">
  
            <div class="col-md-6">
                <label for="firstName" class="form-label">Nombre ${count}*</label>
                <input type="text" class="form-control" id="firstName${count}" name="firstName" placeholder="ej: Bruno" required>
            </div>
  
            <div class="col-md-6">
                <label for="lastName" class="form-label">Apellido*</label>
                <input type="text" class="form-control" id="lastName${count}" name="lastName" placeholder="ej: Juan Sartori"required>
            </div>
  
        </div>
  
        <!-- Opciones 4: Documento -->
        <div class="mb-3 d-flex align-items-center">
          <!-- Campo de tipo de documento a la izquierda -->
          <div class="flex-shrink-1 me-3">
              <label for="documentType" class="form-label">Tipo de documento*</label>
              <select class="form-select" id="documentType${count}" name="documentType" required>
                  <option value="">Seleccione...</option>
                  <option value="dni">DNI</option>
                  <option value="libreta-civica">Libreta Cívica</option>
                  <option value="libreta-electronica">Libreta Electrónica</option>
              </select>
          </div>
          
          <!-- Campo de número de documento a la derecha -->
          <div class="flex-grow-1">
              <label for="documentNumber" class="form-label">Número de documento*</label>
              <input type="text" class="form-control" id="documentNumber${count}" name="documentNumber" placeholder="ej: 44552722"required>
          </div>
        </div>


         <div class="mb-3">
            <label for="birthdate" class="form-label">Fecha de nacimiento*</label>
            <input type="date" class="form-control" id="birthdate${count}" name="birthdate" required>
        </div>

        <div class="col-12 mt-2">
                <button type="button" class="btn btn-danger btn-remove-children">Eliminar</button>
        </div>

     </div>
        `;
        childrenContainer.insertAdjacentHTML('beforeend', childrenHtml);
        }
    }

    // Función para eliminar un childreno
    function removechildren(event) {
        const button = event.target;
        if (button.classList.contains('btn-remove-children')) {
            button.closest('.dynamic-children').remove();
            saveFormData(); // Actualiza localStorage después de eliminar un childreno
        }
    }

    // Agregar evento para eliminar un childreno
    document.querySelector('.dynamic-childrens').addEventListener('click', removechildren);

    
    document.querySelector('.btn-add-children').addEventListener('click', () => addchildrenFields());
});
