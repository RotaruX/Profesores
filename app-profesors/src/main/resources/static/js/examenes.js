async function comprobarSesion() {
    try {
        const respuesta = await fetch('http://localhost:8080/auth/perfil', { credentials: 'include' });
        if (!respuesta.ok) {
            window.location.href = 'login.html';
            return;
        }
        cargarExamenes();
    } catch (error) {
        window.location.href = 'login.html';
    }
}

async function cargarExamenes() {
    const respuesta = await fetch('http://localhost:8080/examenes', { credentials: 'include' });
    const examenes = await respuesta.json();

    const grid = document.getElementById('examenes-grid');
    const vacio = document.getElementById('examenes-empty');
    grid.innerHTML = '';

    if (examenes.length === 0) {
        vacio.hidden = false;
        return;
    }
    vacio.hidden = true;

    examenes.forEach(examen => {
    const card = document.createElement('a');
    card.href = 'crear-examen.html?id=' + examen.id;
    card.className = 'examen-card';
    card.innerHTML =
        '<div class="examen-card-curso">' + examen.curso + '</div>' +
        '<div class="examen-card-fecha">' + (examen.fecha || 'Sin fecha') + '</div>' +
        '<div class="examen-card-preguntas">' + examen.numeroPreguntas + ' pregunta(s)</div>';
    grid.appendChild(card);
});
}

document.getElementById('btn-subir').addEventListener('click', function () {
    alert('Subir examen estará disponible próximamente.');
});

document.getElementById('logout-btn').addEventListener('click', async function () {
    try {
        await fetch('http://localhost:8080/auth/logout', { method: 'POST', credentials: 'include' });
    } catch (error) {}
    window.location.href = 'login.html';
});

comprobarSesion();