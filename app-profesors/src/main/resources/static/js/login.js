// Esperamos a que el formulario se envie
document.getElementById('login-form').addEventListener('submit', async function (evento) {

    // evita que el navegador recargue la pagina al enviar el formulario
    // (comportamiento por defecto de un <form>, no lo queremos aqui)
    evento.preventDefault();

    // Cogemos los valores escritos en los campos
    const correo = document.getElementById('correo').value;
    const password = document.getElementById('password').value;

    const errorBox = document.getElementById('form-error');
    errorBox.hidden = true; // ocultamos cualquier error anterior antes de intentar de nuevo

    try {
        const respuesta = await fetch('http://localhost:8080/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            // credentials: 'include' -> IMPRESCINDIBLE para que el navegador
            // guarde y envie la cookie de sesion (JSESSIONID) en esta peticion
            credentials: 'include',
            body: JSON.stringify({ correo, password })
        });

        if (respuesta.ok) {
            // Login correcto -> vamos al panel principal
            window.location.href = 'dashboard.html';
        } else {
            // El backend devuelve el mensaje de error como texto plano
            // (mira el login() de AuthController: .body(e.getMessage()))
            const mensaje = await respuesta.text();
            errorBox.textContent = mensaje;
            errorBox.hidden = false;
        }

    } catch (error) {
        // Esto salta si el servidor ni siquiera responde (apagado, sin red, etc.)
        errorBox.textContent = 'No se pudo conectar con el servidor. Inténtalo de nuevo.';
        errorBox.hidden = false;
    }
});