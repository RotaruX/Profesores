document.getElementById('register-form').addEventListener('submit', async function (evento) {
    evento.preventDefault();

    // Cogemos los valores de todos los campos del formulario
    const datos = {
        nombre: document.getElementById('nombre').value,
        apellidos: document.getElementById('apellidos').value,
        fechaNacimiento: document.getElementById('fechaNacimiento').value, // formato "YYYY-MM-DD", igual que espera LocalDate en Java
        dni: document.getElementById('dni').value,
        correo: document.getElementById('correo').value,
        asignatura: document.getElementById('asignatura').value,
        cursos: document.getElementById('cursos').value,
        password: document.getElementById('password').value
    };

    const errorBox = document.getElementById('form-error');
    const successBox = document.getElementById('form-success');
    errorBox.hidden = true;
    successBox.hidden = true;

    try {
        const respuesta = await fetch('http://localhost:8080/auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'include',
            body: JSON.stringify(datos)
        });

        if (respuesta.ok) {
            // Registro correcto -> avisamos y mandamos al login para que inicie sesion
            successBox.textContent = 'Cuenta creada correctamente. Redirigiendo al login...';
            successBox.hidden = false;

            setTimeout(function () {
                window.location.href = 'login.html';
            }, 1500);

        } else {
            // Ej: "Ya existe un profesor registrado con ese correo"
            const mensaje = await respuesta.text();
            errorBox.textContent = mensaje;
            errorBox.hidden = false;
        }

    } catch (error) {
        errorBox.textContent = 'No se pudo conectar con el servidor. Inténtalo de nuevo.';
        errorBox.hidden = false;
    }
});