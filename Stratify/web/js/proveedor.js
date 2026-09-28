'use strict';

function $(id) {
    return document.getElementById(id);
}

function escapeHTML(valor) {
    if (valor === null || valor === undefined) return '';
    return String(valor)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#039;');
}

// CORRECCIÓN: Evita tomar /proveedor/ como context path en Railway
function ctx(path) {
    if (!path) return '';
    if (!path.startsWith('/')) path = '/' + path;

    var base = '';
    // Si CONTEXT_PATH es '/' o no está definido, se ignora para no duplicar barras
    if (typeof window.CONTEXT_PATH !== 'undefined' && window.CONTEXT_PATH !== '/') {
        base = window.CONTEXT_PATH;
    } else if (typeof window.CONTEXT_PATH === 'undefined') {
        var knownSubfolders = ['admin', 'proveedor', 'usuario', 'vistas'];
        var firstPart = window.location.pathname.split('/')[1] || '';
        var isSubfolder = knownSubfolders.includes(firstPart.toLowerCase()) || firstPart.includes('.');
        base = (firstPart && !isSubfolder) ? '/' + firstPart : '';
    }

    // Fuerza la conversión de // a /
    return (base + path).replace(/\/+/g, '/');
}
function emptyStateHTML(title, description) {
    return '<div class="stf-empty">' +
            '<div class="stf-empty__icon">' +
            '<svg width="46" height="46" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">' +
            '<circle cx="12" cy="12" r="10"/>' +
            '<line x1="8" y1="12" x2="16" y2="12"/>' +
            '</svg>' +
            '</div>' +
            '<h3>' + escapeHTML(title) + '</h3>' +
            '<p>' + escapeHTML(description) + '</p>' +
            '</div>';
}

/* ==========================================================================
   MÓDULO DE SOLICITUDES PENDIENTES
   ========================================================================== */

var allSolicitudes = [];
var solicitudPendienteProcesar = null;

async function loadSolicitudes() {
    var container = $('solicitudes-grid');
    if (!container) return;

    try {
        var response = await fetch(ctx('/SolicitudServlet'));
        if (response.status === 401) {
            container.innerHTML = emptyStateHTML('Sesión no iniciada', 'Inicia sesión para ver las solicitudes pendientes.');
            return;
        }
        if (!response.ok) throw new Error('HTTP ' + response.status);
        allSolicitudes = await response.json();
        renderSolicitudes(allSolicitudes);
    } catch (error) {
        console.error('Error cargando solicitudes:', error);
        container.innerHTML = emptyStateHTML('Error de conexión', 'No se pudieron cargar las solicitudes pendientes.');
    }
}

function renderSolicitudes(lista) {
    lista = lista || allSolicitudes;
    var container = $('solicitudes-grid');
    if (!container) return;

    if ($('sol-count')) {
        $('sol-count').textContent = '· ' + lista.length + ' pendiente' + (lista.length !== 1 ? 's' : '');
    }

    if (lista.length === 0) {
        container.innerHTML = emptyStateHTML('Sin solicitudes pendientes', 'No hay nuevas peticiones de productos por responder.');
        return;
    }

    var html = '<div class="table-responsive"><table class="table table-dark table-hover align-middle mb-0">' +
            '<thead><tr>' +
            '<th>Código</th>' +
            '<th>Cliente</th>' +
            '<th>Producto</th>' +
            '<th>Cantidad</th>' +
            '<th>Fecha</th>' +
            '<th class="text-center">Acciones</th>' +
            '</tr></thead><tbody>';

    for (var i = 0; i < lista.length; i++) {
        var s = lista[i];
        var fecha = s.fechaSolicitud ? s.fechaSolicitud : '-';

        html += '<tr>' +
                '<td><code class="text-warning fw-bold">' + escapeHTML(s.codigoProducto || 'N/A') + '</code></td>' +
                '<td class="fw-bold">' + escapeHTML(s.nombreCliente || 'Cliente') + '</td>' +
                '<td>' + escapeHTML(s.nombreProducto || 'Producto') + '</td>' +
                '<td><span class="badge bg-warning text-dark fs-6">' + escapeHTML(String(s.cantidad)) + ' u.</span></td>' +
                '<td><span style="color: #cbd5e1; font-size: 0.875rem;">' + escapeHTML(fecha) + '</span></td>' +
                '<td class="text-center">' +
                '<div class="btn-group btn-group-sm">' +
                '<button type="button" class="btn btn-success fw-semibold" onclick="procesarSolicitud(' + s.idSolicitud + ', \'aceptar\')">' +
                '<i class="bi bi-check-lg me-1"></i> Aceptar' +
                '</button>' +
                '<button type="button" class="btn btn-danger fw-semibold" onclick="procesarSolicitud(' + s.idSolicitud + ', \'rechazar\')">' +
                '<i class="bi bi-x-lg me-1"></i> Cancelar' +
                '</button>' +
                '</div>' +
                '</td>' +
                '</tr>';
    }
    html += '</tbody></table></div>';
    container.innerHTML = html;
}

function filterSolicitudes(lista, texto) {
    var query = texto.trim().toLowerCase();
    if (!query) return lista;
    return lista.filter(function (s) {
        var cliente = String(s.nombreCliente || '').toLowerCase();
        var producto = String(s.nombreProducto || '').toLowerCase();
        var codigo = String(s.codigoProducto || '').toLowerCase();
        return cliente.indexOf(query) !== -1 || producto.indexOf(query) !== -1 || codigo.indexOf(query) !== -1;
    });
}

/* ==========================================================================
   VENTANAS MODALES PERSONALIZADAS
   ========================================================================== */
function mostrarVentanaRespuesta(titulo, mensaje, tipo) {
    var modalEl = $('modalRespuestaProveedor');
    if (!modalEl) {
        alert(mensaje);
        return;
    }

    var iconEl = $('modalRespuestaIcono');
    var titleEl = $('modalRespuestaTitulo');
    var msgEl = $('modalRespuestaMensaje');

    if (iconEl) {
        if (tipo === 'success') {
            iconEl.innerHTML = '<i class="bi bi-check-circle-fill text-success"></i>';
        } else if (tipo === 'danger' || tipo === 'error') {
            iconEl.innerHTML = '<i class="bi bi-x-circle-fill text-danger"></i>';
        } else {
            iconEl.innerHTML = '<i class="bi bi-exclamation-triangle-fill text-warning"></i>';
        }
    }

    if (titleEl) titleEl.textContent = titulo;
    if (msgEl) msgEl.textContent = mensaje;

    var bsModal = bootstrap.Modal.getOrCreateInstance(modalEl);
    bsModal.show();
}

window.procesarSolicitud = function (idSolicitud, accion) {
    var esAceptar = (accion === 'aceptar');
    var titulo = esAceptar ? 'Aceptar Solicitud' : 'Cancelar Solicitud';
    var mensaje = esAceptar
            ? '¿Deseas ACEPTAR esta solicitud? El stock del producto se actualizará automáticamente.'
            : '¿Deseas CANCELAR esta solicitud? La petición será eliminada.';

    solicitudPendienteProcesar = { idSolicitud: idSolicitud, accion: accion };

    var titleEl = $('modalConfirmarTitulo');
    var msgEl = $('modalConfirmarMensaje');
    if (titleEl) titleEl.textContent = titulo;
    if (msgEl) msgEl.textContent = mensaje;

    var modalEl = $('modalConfirmarAccion');
    if (modalEl) {
        var bsModal = bootstrap.Modal.getOrCreateInstance(modalEl);
        bsModal.show();
    }
};

async function ejecutarAccionSolicitud() {
    if (!solicitudPendienteProcesar) return;

    var idSolicitud = solicitudPendienteProcesar.idSolicitud;
    var accion = solicitudPendienteProcesar.accion;
    var esAceptar = (accion === 'aceptar');

    var modalConfEl = $('modalConfirmarAccion');
    if (modalConfEl) {
        var bsModalConf = bootstrap.Modal.getInstance(modalConfEl);
        if (bsModalConf) bsModalConf.hide();
    }

    try {
        var params = new URLSearchParams({
            accion: accion,
            id_solicitud: idSolicitud
        });

        var response = await fetch(ctx('/SolicitudServlet'), {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: params
        });

        if (!response.ok) {
            mostrarVentanaRespuesta('Error', 'No se pudo procesar la petición en el servidor (Código ' + response.status + ').', 'danger');
            return;
        }

        var data = await response.json();

        if (data.status === 'success') {
            var tituloModal = esAceptar ? '¡Solicitud Aceptada!' : '¡Solicitud Cancelada!';
            var textoModal = esAceptar 
                ? 'La solicitud fue aprobada exitosamente y el inventario del producto ha sido actualizado.' 
                : 'La solicitud ha sido rechazada y removida del sistema.';

            mostrarVentanaRespuesta(tituloModal, textoModal, 'success');
            await loadSolicitudes();
        } else {
            mostrarVentanaRespuesta('Atención', data.mensaje || 'No se pudo completar la operación.', 'warning');
        }
    } catch (e) {
        console.error('Error al procesar la solicitud:', e);
        mostrarVentanaRespuesta('Error de conexión', 'Ocurrió un fallo al intentar comunicarse con el servidor.', 'danger');
    } finally {
        solicitudPendienteProcesar = null;
    }
}

/* ==========================================================================
   MÓDULO DE REGISTRO DE PROVEEDORES
   ========================================================================== */

async function guardarProveedor(event) {
    event.preventDefault();
    var form = event.target;
    var msgEl = $('provMsg');
    var params = new URLSearchParams(new FormData(form));

    try {
        var response = await fetch(ctx('/ProveedorServlet'), {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: params
        });

        var data = await response.json();

        if (data.status === 'success') {
            if (msgEl) msgEl.innerHTML = '<span style="color:#4caf50">Proveedor registrado correctamente.</span>';
            form.reset();
        } else {
            if (msgEl) msgEl.innerHTML = '<span style="color:#e05050">' + escapeHTML(data.mensaje) + '</span>';
        }
    } catch (e) {
        if (msgEl) msgEl.innerHTML = '<span style="color:#e05050">Error de conexión al registrar proveedor.</span>';
    }
}

/* ==========================================================================
   INICIALIZACIÓN DEL DASHBOARD
   ========================================================================== */

function initDashboard() {
    if ($('solicitudes-grid')) {
        loadSolicitudes();
    }

    var solTabEl = document.getElementById('solicitudes-tab');
    if (solTabEl) {
        solTabEl.addEventListener('shown.bs.tab', function () {
            loadSolicitudes();
        });
    }

    var solSearch = $('sol-search');
    if (solSearch) {
        solSearch.addEventListener('input', function (event) {
            renderSolicitudes(filterSolicitudes(allSolicitudes, event.target.value));
        });
    }

    var formCrearProv = $('formCrearProveedor');
    if (formCrearProv) {
        formCrearProv.addEventListener('submit', guardarProveedor);
    }

    var btnConfirmar = $('btnEjecutarConfirmacion');
    if (btnConfirmar) {
        btnConfirmar.addEventListener('click', ejecutarAccionSolicitud);
    }
}

document.addEventListener('DOMContentLoaded', function () {
    initDashboard();
});