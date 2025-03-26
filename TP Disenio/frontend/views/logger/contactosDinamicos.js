const add_contact_button = document.getElementById("image-button-add");
const contactContainer = document.querySelector('.dynamic-contacts');

const add_contact_form = () => {
    contactContainer.innerHTML += `
        <div class="row mb-3 dynamic-contact">
            <div class="col-md-6">
                <label class="form-label">Medio de contacto (opcional)</label>
                <select class="form-select id="contactMethod1" name="contactMethod[]"">
                    <option value="">Seleccione...</option>
                    <option value="email"}>Email</option>
                    <option value="whatsapp"}>WhatsApp</option>
                    <option value="telegram"}>Telegram</option>
                </select>
            </div>
            <div class="col-md-6">
                <label class="form-label">Contacto (opcional)</label>
                <input type="text" class="form-control" id="contact1" name="contact[]">
            </div>
            <div class="col-12 mt-2">
                <button type="button" class="btn btn-danger btn-remove-contact">Eliminar</button>
            </div>
        </div>
        `;
}

const delete_contact_form = (event) => {
    const contactForm = event.target.closest('.dynamic-contact');
    contactForm.remove();
}

window.addEventListener('load', () => {
    add_contact_button.addEventListener('click', () => {
        add_contact_form();
    })

    contactContainer.addEventListener('click', (event) => {
        if (event.target.classList.contains('btn-remove-contact')) {
            delete_contact_form(event);
        }
    });
})