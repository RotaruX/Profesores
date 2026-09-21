async function iniciar() {
    const profesor = await comprobarSesion();
    if (!profesor) return;
    cargarPreguntas();
}

async function cargarPreguntas() {
    const respuesta = await fetch(API_URL + '/preguntas', { credentials: 'include' });
    const preguntas = await respuesta.json();

    const grid = document.getElementById('preguntas-grid');
    const vacio = document.getElementById('preguntas-vacio');
    grid.innerHTML = '';

    if (preguntas.length === 0) {
        vacio.hidden = false;
        return;
    }
    vacio.hidden = true;

    preguntas.forEach(pregunta => renderizarCardVista(pregunta));
}

function renderizarCardVista(pregunta) {
    const grid = document.getElementById('preguntas-grid');

    const card = document.createElement('div');
    card.className = 'pregunta-card pregunta-card--gestion';
    card.dataset.id = pregunta.id;

    card.innerHTML =
        '<p class="pregunta-card-texto">' + pregunta.texto + '</p>' +
        (pregunta.asignatura || pregunta.dificultad
            ? '<span class="pregunta-card-tag">' + [pregunta.asignatura, pregunta.dificultad].filter(Boolean).join(' · ') + '</span>'
            : '') +
        '<div class="pregunta-card-actions">' +
            '<button type="button" class="link-btn btn-editar-pregunta">Editar</button>' +
            '<button type="button" class="link-btn link-btn--danger btn-eliminar-pregunta">Eliminar</button>' +
        '</div>';

    card.addEventListener('click', function () {
        card.classList.toggle('pregunta-card--expandida');
    });

    const btnEditar = card.querySelector('.btn-editar-pregunta');
    btnEditar.addEventListener('click', function (evento) {
        evento.stopPropagation();
        renderizarCardEdicion(pregunta, card);
    });

    const btnEliminar = card.querySelector('.btn-eliminar-pregunta');
    btnEliminar.addEventListener('click', async function (evento) {
        evento.stopPropagation();
        const confirmar = confirm('¿Seguro que quieres eliminar esta pregunta?');
        if (!confirmar) return;

        const respuesta = await fetch(API_URL + '/preguntas/' + pregunta.id, {
            method: 'DELETE',
            credentials: 'include'
        });

        if (respuesta.ok) {
            card.remove();
        } else {
            const mensaje = await respuesta.text();
            alert(mensaje);
        }
    });

    grid.appendChild(card);
}

function renderizarCardEdicion(pregunta, cardAnterior) {
    const card = document.createElement('div');
    card.className = 'pregunta-card pregunta-card--edicion pregunta-card--expandida';
    card.dataset.id = pregunta.id;

    card.innerHTML =
        '<textarea class="edicion-texto">' + pregunta.texto + '</textarea>' +
        '<div class="field-row">' +
            '<input type="text" class="edicion-asignatura" value="' + (pregunta.asignatura || '') + '" placeholder="Asignatura">' +
            '<select class="edicion-dificultad">' +
                '<option value="facil">Fácil</option>' +
                '<option value="media">Media</option>' +
                '<option value="dificil">Difícil</option>' +
            '</select>' +
        '</div>' +
        '<div class="pregunta-card-actions">' +
            '<button type="button" class="link-btn btn-cancelar-edicion">Cancelar</button>' +
            '<button type="button" class="btn-primary btn-inline btn-guardar-edicion">Guardar</button>' +
        '</div>';

    card.querySelector('.edicion-dificultad').value = pregunta.dificultad || 'media';

    card.querySelector('.btn-cancelar-edicion').addEventListener('click', function (evento) {
        evento.stopPropagation();
        card.replaceWith(construirCardVista(pregunta));
    });

    card.querySelector('.btn-guardar-edicion').addEventListener('click', async function (evento) {
        evento.stopPropagation();

        const datos = {
            texto: card.querySelector('.edicion-texto').value.trim(),
            asignatura: card.querySelector('.edicion-asignatura').value.trim(),
            dificultad: card.querySelector('.edicion-dificultad').value
        };

        if (!datos.texto) {
            alert('El enunciado no puede quedar vacío.');
            return;
        }

        const respuesta = await fetch(API_URL + '/preguntas/' + pregunta.id, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify(datos)
        });

        if (respuesta.ok) {
            const actualizada = await respuesta.json();
            card.replaceWith(construirCardVista(actualizada));
        } else {
            alert('No se pudo guardar la pregunta.');
        }
    });

    cardAnterior.replaceWith(card);
}

function construirCardVista(pregunta) {
    const grid = document.getElementById('preguntas-grid');
    const antes = grid.children.length;
    renderizarCardVista(pregunta);
    return grid.children[antes];
}

document.getElementById('btn-anadir-pregunta').addEventListener('click', async function () {
    const texto = document.getElementById('nueva-pregunta-texto').value.trim();
    const asignatura = document.getElementById('nueva-pregunta-asignatura').value.trim();
    const dificultad = document.getElementById('nueva-pregunta-dificultad').value;

    if (!texto) {
        alert('Escribe el enunciado de la pregunta.');
        return;
    }

    const respuesta = await fetch(API_URL + '/preguntas', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ texto, asignatura, dificultad })
    });

    if (respuesta.ok) {
        document.getElementById('nueva-pregunta-texto').value = '';
        document.getElementById('nueva-pregunta-asignatura').value = '';
        const vacio = document.getElementById('preguntas-vacio');
        vacio.hidden = true;
        const nueva = await respuesta.json();
        renderizarCardVista(nueva);
    } else {
        alert('No se pudo guardar la pregunta.');
    }
});

activarLogout();
iniciar();