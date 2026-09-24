let examenIdActual = null;

async function iniciar() {
    const profesor = await comprobarSesion();
    if (!profesor) return;
    rellenarCursos(profesor);
}

function rellenarCursos(profesor) {
    const select = document.getElementById('examen-curso');
    select.innerHTML = '';
    const cursos = (profesor.cursos || '').split(',').map(c => c.trim()).filter(c => c.length > 0);
    cursos.forEach(curso => {
        const option = document.createElement('option');
        option.value = curso;
        option.textContent = curso;
        select.appendChild(option);
    });
}

document.getElementById('btn-guardar').addEventListener('click', async function () {
    const curso = document.getElementById('examen-curso').value;
    const fecha = document.getElementById('examen-fecha').value;
    const archivo = document.getElementById('examen-archivo').files[0];
    const errorBox = document.getElementById('form-error');
    errorBox.hidden = true;

    if (!curso) { errorBox.textContent = 'Selecciona un curso.'; errorBox.hidden = false; return; }
    if (!archivo) { errorBox.textContent = 'Selecciona un archivo.'; errorBox.hidden = false; return; }

    const btn = this;
    btn.disabled = true;
    btn.textContent = 'Subiendo...';

    try {
        const respuestaCrear = await fetch(API_URL + '/examenes', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify({ curso: curso, fecha: fecha || null, preguntaIds: [] })
        });
        if (!respuestaCrear.ok) throw new Error(await respuestaCrear.text());
        const examen = await respuestaCrear.json();
        examenIdActual = examen.id;

        const formData = new FormData();
        formData.append('archivo', archivo);

        const respuestaSubir = await fetch(API_URL + '/examenes/' + examen.id + '/subir', {
            method: 'POST',
            credentials: 'include',
            body: formData
        });
        if (!respuestaSubir.ok) throw new Error(await respuestaSubir.text());
        const resultado = await respuestaSubir.json();

        mostrarRevision(resultado.preguntasDetectadas);

        btn.hidden = true;
        document.getElementById('btn-continuar').hidden = false;
        document.getElementById('examen-curso').disabled = true;
        document.getElementById('examen-fecha').disabled = true;
        document.getElementById('examen-archivo').disabled = true;

    } catch (error) {
        errorBox.textContent = error.message || 'No se pudo subir el examen.';
        errorBox.hidden = false;
        btn.disabled = false;
        btn.textContent = 'Subir examen';
    }
});

function mostrarRevision(preguntas) {
    document.getElementById('revision').hidden = false;
    const contenedor = document.getElementById('preguntas-detectadas');
    const vacio = document.getElementById('sin-deteccion');
    contenedor.innerHTML = '';

    if (!preguntas || preguntas.length === 0) {
        vacio.hidden = false;
        return;
    }
    vacio.hidden = true;

    preguntas.forEach((texto, indice) => {
        const item = document.createElement('div');
        item.className = 'pregunta-card pregunta-card--edicion';
        item.innerHTML =
            '<label class="pregunta-item">' +
                '<input type="checkbox" class="check-guardar" checked>' +
                '<span>Guardar esta pregunta</span>' +
            '</label>' +
            '<textarea class="edicion-texto">' + texto + '</textarea>';
        contenedor.appendChild(item);
    });
}

document.getElementById('btn-continuar').addEventListener('click', async function () {
    const btn = this;
    btn.disabled = true;
    btn.textContent = 'Guardando...';

    const items = document.querySelectorAll('#preguntas-detectadas .pregunta-card--edicion');
    const idsCreadas = [];

    for (const item of items) {
        const marcada = item.querySelector('.check-guardar').checked;
        if (!marcada) continue;

        const texto = item.querySelector('.edicion-texto').value.trim();
        if (!texto) continue;

        const respuesta = await fetch(API_URL + '/preguntas', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify({ texto: texto, asignatura: '', dificultad: 'media' })
        });

        if (respuesta.ok) {
            const pregunta = await respuesta.json();
            idsCreadas.push(pregunta.id);
        }
    }

    if (idsCreadas.length > 0) {
        await fetch(API_URL + '/examenes/' + examenIdActual, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify({
                curso: document.getElementById('examen-curso').value,
                fecha: document.getElementById('examen-fecha').value || null,
                preguntaIds: idsCreadas
            })
        });
    }

    window.location.href = 'examenes.html';
});

activarLogout();
iniciar();