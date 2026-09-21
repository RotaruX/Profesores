async function iniciar() {
    const profesor = await comprobarSesion(); // viene de common.js
    if (!profesor) return; // comprobarSesion ya redirige si no hay sesion

    document.getElementById('saludo').textContent = 'Buenos días, ' + profesor.nombre;
}

activarLogout(); // viene de common.js
iniciar();