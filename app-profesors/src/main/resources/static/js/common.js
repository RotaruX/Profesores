// URL base de la API. Si algun dia cambia (otro puerto, produccion...),
// solo hay que tocarla aqui, no en cada archivo.
const API_URL = 'http://localhost:8080';

// Comprueba si hay sesion activa. Si NO la hay, redirige al login y devuelve null.
// Si SI la hay, devuelve los datos del profesor logueado.
async function comprobarSesion() {
    try {
        const respuesta = await fetch(API_URL + '/auth/perfil', { credentials: 'include' });
        if (!respuesta.ok) {
            window.location.href = 'login.html';
            return null;
        }
        return await respuesta.json();
    } catch (error) {
        window.location.href = 'login.html';
        return null;
    }
}

// Conecta el boton de cerrar sesion, si la pagina tiene uno con id="logout-btn"
function activarLogout() {
    const boton = document.getElementById('logout-btn');
    if (!boton) return;

    boton.addEventListener('click', async function () {
        try {
            await fetch(API_URL + '/auth/logout', { method: 'POST', credentials: 'include' });
        } catch (error) {
            // seguimos al login aunque falle la peticion
        }
        window.location.href = 'login.html';
    });
}