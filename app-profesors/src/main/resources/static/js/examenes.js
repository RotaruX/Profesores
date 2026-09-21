async function cargarExamenes() {
    const respuesta = await fetch(API_URL + '/examenes', { credentials: 'include' });
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

async function iniciar() {
    const profesor = await comprobarSesion();
    if (!profesor) return;
    cargarExamenes();
}

document.getElementById('btn-subir').addEventListener('click', function () {
    alert('Subir examen estará disponible próximamente.');
});

activarLogout();
iniciar();