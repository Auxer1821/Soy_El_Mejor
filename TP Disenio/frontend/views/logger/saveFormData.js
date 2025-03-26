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
        const existingData = localStorage.getItem('formData');
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
        localStorage.setItem('formData', JSON.stringify(data));
        console.log('Saved Data:', JSON.stringify(data));
    }

    // Guarda los datos al modificar cualquier campo
    form.addEventListener('input', saveFormData);

    // Guarda los datos al enviar el formulario
    form.addEventListener('submit', function(event) {
        event.preventDefault(); // Previene el envío del formulario
        saveFormData(); // Guarda los datos en localStorage
        window.location.href = 'crearUsuario.html'; // Redirige a la siguiente página
    });

    // Restaura los datos si están disponibles
    let contactCounter = document.querySelectorAll('.dynamic-contact').length;
    const savedData = localStorage.getItem('formData');
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

        // Restaurar los contactos dinámicos
        const contactContainer = document.querySelector('.dynamic-contacts');
        console.log('----Restaurar Contactos Dinamicos---', contactContainer);
        if (contactContainer) {
            contactContainer.innerHTML = ''; // Limpia el contenedor antes de restaurar

            // Obtener los datos de contactos desde el localStorage
            const contactMethods = data['contactMethod[]'] || [];
            const contacts = data['contact[]'] || [];
            console.log('----contactMethods:', contactMethods);


            contactMethods.forEach((method, index) => {
                console.log('----contactCounter:', contactCounter);
                addContactFields(method, contacts[index], contactCounter>0); // Añadir con skipCounter en true
            });
        }
    }

    // Función para agregar un nuevo conjunto de campos de contacto
    function addContactFields(contactMethod = '', contact = '', skipCounter = false) {

        contactCounter++;
        const contactContainer = document.querySelector('.dynamic-contacts');
        console.log('----contactCounter:', contactCounter);

        if(skipCounter){
        const contactHtml = `
        <div class="row mb-3 dynamic-contact">
            <div class="col-md-6">
                <label for="contactMethod${contactCounter}" class="form-label">Medio de contacto (opcional)</label>
                <select class="form-select" id="contactMethod${contactCounter}" name="contactMethod[]" data-index="${contactCounter}">
                    <option value="">Seleccione...</option>
                    <option value="email" ${contactMethod === 'email' ? 'selected' : ''}>Email</option>
                    <option value="whatsapp" ${contactMethod === 'whatsapp' ? 'selected' : ''}>WhatsApp</option>
                    <option value="telegram" ${contactMethod === 'telegram' ? 'selected' : ''}>Telegram</option>
                </select>
            </div>
            <div class="col-md-6">
                <label for="contact${contactCounter}" class="form-label">Contacto (opcional)</label>
                <input type="text" class="form-control" id="contact${contactCounter}" name="contact[]" data-index="${contactCounter}" value="${contact}">
            </div>
            <div class="col-12 mt-2">
                <button type="button" class="btn btn-danger btn-remove-contact" data-index="${contactCounter}">Eliminar</button>
            </div>
        </div>
        `;
        contactContainer.insertAdjacentHTML('beforeend', contactHtml);
        }
    }

    // Función para eliminar un contacto
    function removeContact(event) {
        const button = event.target;
        if (button.classList.contains('btn-remove-contact')) {
            button.closest('.dynamic-contact').remove();
            saveFormData(); // Actualiza localStorage después de eliminar un contacto
        }
    }

    // Agregar evento para eliminar un contacto
    document.querySelector('.dynamic-contacts').addEventListener('click', removeContact);

    
    document.querySelector('.btn-add-contact').addEventListener('click', () => addContactFields());
});
