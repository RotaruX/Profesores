let profesorActual = null;
let preguntasSeleccionadas = new Set();

// Si la URL trae ?id=5, estamos editando ese examen; si no, estamos creando uno nuevo
const params = new URLSearchParams(window.location.search);
const examenId = params.get('id');

async function comprobarSesion() {
    try {
        const respuesta = await fetch('http://localhost:8080/auth/perfil', { credentials: 'include' });
        if (!respuesta.ok) { window.location.href = 'login.html'; return; }
        profesorActual = await respuesta.json();

        if (examenId) {
            document.querySelector('.dashboard-title').textContent = 'Editar examen';
            document.getElementById('btn-guardar-examen').textContent = 'Guardar cambios';
            await cargarExamenExistente();
        }

        rellenarCursos();
        cargarPreguntas();
    } catch (error) {
        window.location.href = 'login.html';
    }
}

async function cargarExamenExistente() {
    const respuesta = await fetch('http://localhost:8080/examenes/' + examenId, { credentials: 'include' });
    if (!respuesta.ok) {
        alert('No se pudo cargar el examen.');
        window.location.href = 'examenes.html';
        return;
    }
    const examen = await respuesta.json();

    document.getElementById('examen-fecha').value = examen.fecha || '';
    // Guardamos el curso para aplicarlo despues de rellenar el desplegable
    document.getElementById('examen-curso').dataset.valorInicial = examen.curso;

    // Marcamos como seleccionadas las preguntas que YA tenia el examen
    examen.preguntas.forEach(p => preguntasSeleccionadas.add(p.id));
}

function rellenarCursos() {
    const select = document.getElementById('examen-curso');
    select.innerHTML = '';

    const cursos = (profesorActual.cursos || '')
        .split(',').map(c => c.trim()).filter(c => c.length > 0);

    cursos.forEach(curso => {
        const option = document.createElement('option');
        option.value = curso;
        option.textContent = curso;
        select.appendChild(option);
    });

    // Si veniamos de editar un examen, seleccionamos su curso en el desplegable
    if (select.dataset.valorInicial) {
        select.value = select.dataset.valorInicial;
    }

    actualizarCursoActual();
    select.addEventListener('change', actualizarCursoActual);
}

function actualizarCursoActual() {
    const select = document.getElementById('examen-curso');
    const texto = select.value ? ('Curso: ' + select.value) : 'Selecciona el curso para este examen.';
    document.getElementById('curso-actual').textContent = texto;
}

async function cargarPreguntas() {
    const respuesta = await fetch('http://localhost:8080/preguntas', { credentials: 'include' });
    const preguntas = await respuesta.json();

    const grid = document.getElementById('preguntas-grid');
    const vacio = document.getElementById('preguntas-vacio');
    grid.innerHTML = '';

    if (preguntas.length === 0) {
        vacio.hidden = false;
        return;
    }
    vacio.hidden = true;

    preguntas.forEach(pregunta => {
        const card = document.createElement('div');
        card.className = 'pregunta-card';
        card.dataset.id = pregunta.id;
        if (preguntasSeleccionadas.has(pregunta.id)) {
            card.classList.add('pregunta-card--seleccionada');
        }

        card.innerHTML =
            '<button type="button" class="pregunta-card-check" aria-label="Seleccionar pregunta"></button>' +
            '<p class="pregunta-card-texto">' + pregunta.texto + '</p>' +
            (pregunta.asignatura ? '<span class="pregunta-card-tag">' + pregunta.asignatura + '</span>' : '');

        card.addEventListener('click', function () {
            card.classList.toggle('pregunta-card--expandida');
        });

        const check = card.querySelector('.pregunta-card-check');
        check.addEventListener('click', function (evento) {
            evento.stopPropagation();
            const id = Number(card.dataset.id);
            if (preguntasSeleccionadas.has(id)) {
                preguntasSeleccionadas.delete(id);
                card.classList.remove('pregunta-card--seleccionada');
            } else {
                preguntasSeleccionadas.add(id);
                card.classList.add('pregunta-card--seleccionada');
            }
        });

        grid.appendChild(card);
    });
}

document.getElementById('btn-anadir-pregunta').addEventListener('click', async function () {
    const texto = document.getElementById('nueva-pregunta-texto').value.trim();
    const asignatura = document.getElementById('nueva-pregunta-asignatura').value.trim();
    const dificultad = document.getElementById('nueva-pregunta-dificultad').value;

    if (!texto) {
        alert('Escribe el enunciado de la pregunta.');
        return;
    }

    const respuesta = await fetch('http://localhost:8080/preguntas', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ texto, asignatura, dificultad })
    });

    if (respuesta.ok) {
        document.getElementById('nueva-pregunta-texto').value = '';
        document.getElementById('nueva-pregunta-asignatura').value = '';
        cargarPreguntas();
    } else {
        alert('No se pudo guardar la pregunta.');
    }
});

document.getElementById('btn-guardar-examen').addEventListener('click', async function () {
    const curso = document.getElementById('examen-curso').value;
    const fecha = document.getElementById('examen-fecha').value;
    const errorBox = document.getElementById('form-error');
    errorBox.hidden = true;

    if (!curso) {
        errorBox.textContent = 'Selecciona un curso.';
        errorBox.hidden = false;
        return;
    }

    // Si hay examenId, editamos (PUT); si no, creamos uno nuevo (POST)
    const url = examenId
        ? 'http://localhost:8080/examenes/' + examenId
        : 'http://localhost:8080/examenes';
    const metodo = examenId ? 'PUT' : 'POST';

    try {
        const respuesta = await fetch(url, {
            method: metodo,
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify({
                curso: curso,
                fecha: fecha || null,
                preguntaIds: Array.from(preguntasSeleccionadas)
            })
        });

        if (respuesta.ok) {
            window.location.href = 'examenes.html';
        } else {
            const mensaje = await respuesta.text();
            errorBox.textContent = mensaje;
            errorBox.hidden = false;
        }
    } catch (error) {
        errorBox.textContent = 'No se pudo conectar con el servidor.';
        errorBox.hidden = false;
    }
});

document.getElementById('logout-btn').addEventListener('click', async function () {
    try {
        await fetch('http://localhost:8080/auth/logout', { method: 'POST', credentials: 'include' });
    } catch (error) {}
    window.location.href = 'login.html';
});

comprobarSesion();