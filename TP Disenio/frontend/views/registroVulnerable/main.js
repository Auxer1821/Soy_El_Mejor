const add_data_to_table = (form_menores) => {
    let inputs_data = form_menores.querySelectorAll('input');
    let body_form = form_menores.querySelector('tbody');

    const register = document.createElement('tr');

    inputs_data.forEach(input => {
        register.innerHTML += `<td>${input.value}</td>`
    });

    // Add buttons using Bootstrap
    const btns = document.createElement('tr')

    btns.innerHTML = `<td>
                    <button class="btn btn-light">Editar</button>
                    <button class="btn btn-danger">Eliminar</button>
                </td>
                `

    body_form.appendChild(register);
    body_form.appendChild(btns);
}

window.addEventListener('load', () => {
    const active_form_btn = document.getElementById('active_form');
    const form = document.querySelector('#form_menores');
    const add_btn = document.querySelector('#add_btn')

    const activeForm = () => form.classList.remove('d-none');

    active_form_btn.addEventListener('click', () => {
        activeForm();
    })

    add_btn.addEventListener('click', () => {
        add_data_to_table(form);
    })
})
