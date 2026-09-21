const params = new URLSearchParams(window.location.search);
const preguntaIdPreseleccionada = params.get('preguntaId');

async function iniciar() {
    const profesor = await comprobarSesion();
    if (!profesor) return;

    await rellenarPreguntas();
    cargarSugerencias();
}

async function rellenarPreguntas() {
    const respuesta = await fetch(API_URL + '/preguntas', { credentials: 'include' });
    const preguntas = await respuesta.json();

    const select = document.getElementById('sugerencia-pregunta');
    const aviso = document.getElementById('sin-preguntas');
    select.innerHTML = '';

    if (preguntas.length === 0) {
        aviso.hidden = false;
        document.getElementById('btn-anadir-sugerencia').disabled = true;
        return;
    }
    aviso.hidden = true;

    preguntas.forEach(pregunta => {
        const option = document.createElement('option');
        option.value = pregunta.id;
        option.textContent = pregunta.texto.length > 60
            ? pregunta.texto.slice(0, 60) + '...'
            : pregunta.texto;
        select.appendChild(option);
    });

    if (preguntaIdPreseleccionada) {
        select.value = preguntaIdPreseleccionada;
    }
}

async function cargarSugerencias() {
    const respuesta = await fetch(API_URL + '/sugerencias', { credentials: 'include' });
    const sugerencias = await respuesta.json();

    const lista = document.getElementById('sugerencias-lista');
    const vacio = document.getElementById('sugerencias-vacio');
    lista.innerHTML = '';

    if (sugerencias.length === 0) {
        vacio.hidden = false;
        return;
    }
    vacio.hidden = true;

    sugerencias.forEach(sugerencia => {
        const item = document.createElement('div');
        item.className = 'sugerencia-item';
        item.innerHTML =
            '<div class="sugerencia-item-pregunta">' + sugerencia.preguntaTexto + '</div>' +
            '<p class="sugerencia-item-texto">' + sugerencia.texto + '</p>' +
            '<div class="sugerencia-item-footer">' +
                '<span class="sugerencia-item-fecha">' + (sugerencia.fecha || '') + '</span>' +
                '<button type="button" class="link-btn link-btn--danger btn-eliminar-sugerencia">Eliminar</button>' +
            '</div>';

        item.querySelector('.btn-eliminar-sugerencia').addEventListener('click', async function () {
            const confirmar = confirm('¿Eliminar esta sugerencia?');
            if (!confirmar) return;

            const respuesta = await fetch(API_URL + '/sugerencias/' + sugerencia.id, {
                method: 'DELETE',
                credentials: 'include'
            });

            if (respuesta.ok) {
                item.remove();
            } else {
                alert('No se pudo eliminar la sugerencia.');
            }
        });

        lista.appendChild(item);
    });
}

document.getElementById('btn-anadir-sugerencia').addEventListener('click', async function () {
    const preguntaId = document.getElementById('sugerencia-pregunta').value;
    const texto = document.getElementById('sugerencia-texto').value.trim();

    if (!preguntaId) { alert('Selecciona una pregunta.'); return; }
    if (!texto) { alert('Escribe tu sugerencia.'); return; }

    const respuesta = await fetch(API_URL + '/sugerencias', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ preguntaId: Number(preguntaId), texto })
    });

    if (respuesta.ok) {
        document.getElementById('sugerencia-texto').value = '';
        cargarSugerencias();
    } else {
        const mensaje = await respuesta.text();
        alert(mensaje);
    }
});

activarLogout();
iniciar();