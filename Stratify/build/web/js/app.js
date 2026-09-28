'use strict';

function $(id) {
    return document.getElementById(id);
}

function escapeHTML(valor) {
    if (valor === null || valor === undefined)
        return '';
    return String(valor)
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#039;');
}

// CORRECCIÓN: Detecta si la ruta actual contiene un archivo (ej: index.jsp) para no usarlo como carpeta de contexto
function ctx(path) {
    if (!path)
        return '';
    if (!path.startsWith('/'))
        path = '/' + path;

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

// Muestra la alerta dentro del modal y la mantiene visible por 6 segundos
function mostrarMensajeModal(mensaje, tipo) {
    var body = $('modalProductosProveedorBody');
    if (!body)
        return;

    var alertaExistente = $('alertaModalProductos');
    if (alertaExistente)
        alertaExistente.remove();

    var alertDiv = document.createElement('div');
    alertDiv.id = 'alertaModalProductos';
    alertDiv.className = 'alert alert-' + (tipo || 'info') + ' alert-dismissible fade show mb-3';
    alertDiv.role = 'alert';
    alertDiv.innerHTML = '<i class="bi ' + (tipo === 'success' ? 'bi-check-circle-fill' : 'bi-exclamation-triangle-fill') + ' me-2"></i>' +
            escapeHTML(mensaje) +
            '<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Cerrar"></button>';

    body.insertBefore(alertDiv, body.firstChild);

    setTimeout(function () {
        if (alertDiv && alertDiv.parentNode) {
            alertDiv.remove();
        }
    }, 6000);
}

var allProducts = [];
var allProviders = [];
var allHistory = [];
var providersLoaded = false;
var historyLoaded = false;

var STATUS_MAP = {
    disponible: ['stf-badge--ok', 'Disponible'],
    bajo: ['stf-badge--low', 'Stock bajo'],
    agotado: ['stf-badge--out', 'Agotado']
};

function statusBadgeHTML(status) {
    var estado = STATUS_MAP[status] || STATUS_MAP.disponible;
    return '<span class="stf-badge ' + estado[0] + '">' + estado[1] + '</span>';
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

// =====================================================
// GESTIÓN DE INVENTARIO
// =====================================================
function productCardHTML(producto) {
    var cantidad = Number(producto.cantidad || producto.quantity || 0);
    var status = producto.estado || (cantidad === 0 ? 'agotado' : (cantidad <= 10 ? 'bajo' : 'disponible'));
    var nombre = producto.nombre_producto || producto.nombre || producto.name || '';
    var codigo = producto.codigo_producto || producto.codigo || producto.code || '';
    var fecha = producto.fecha_vencimiento || producto.fecha_expiracion || '';
    var proveedor = producto.nombre_proveedor || producto.proveedor || '';

    return '<article class="stf-card">' +
            '<div class="stf-product__top">' +
            '<div>' +
            '<div class="stf-product__name">' + escapeHTML(nombre) + '</div>' +
            (codigo ? '<div class="stf-product__code">Código: ' + escapeHTML(codigo) + '</div>' : '') +
            '</div>' +
            statusBadgeHTML(status) +
            '</div>' +
            '<div class="stf-product__meta">' +
            (proveedor ? '<div><span>Proveedor:</span> ' + escapeHTML(proveedor) + '</div>' : '') +
            (fecha ? '<div><span>Vence:</span> ' + escapeHTML(fecha) + '</div>' : '') +
            '</div>' +
            '<div class="stf-product__footer">' +
            '<div class="stf-stock">' + cantidad + ' <small>unidades</small></div>' +
            '</div>' +
            '</article>';
}

function populateStockSelect(lista) {
    var sel = $('stockProductoCodigo');
    if (!sel || sel.tagName !== 'SELECT')
        return;
    var current = sel.value;
    sel.innerHTML = '<option value="">Selecciona un producto...</option>';
    (lista || []).forEach(function (p) {
        var codigo = p.codigo_producto || p.codigo || p.code || '';
        var nombre = p.nombre_producto || p.nombre || p.name || '';
        var opt = document.createElement('option');
        opt.value = codigo;
        opt.textContent = nombre + (codigo ? ' (' + codigo + ')' : '');
        sel.appendChild(opt);
    });
    if (current)
        sel.value = current;
}

function renderInventory(lista) {
    lista = lista || allProducts;
    var grid = $('inv-grid');
    if (!grid)
        return;

    if (lista.length > 0) {
        var html = '';
        for (var i = 0; i < lista.length; i++) {
            html += productCardHTML(lista[i]);
        }
        grid.innerHTML = html;
    } else {
        grid.innerHTML = emptyStateHTML('No hay productos registrados', 'Los productos registrados aparecerán aquí.');
    }

    if ($('inv-count')) {
        $('inv-count').textContent = '· ' + lista.length + ' producto' + (lista.length !== 1 ? 's' : '');
    }
    populateStockSelect(lista);
}

async function loadInventory() {
    var grid = $('inv-grid');
    if (!grid)
        return;
    try {
        var response = await fetch(ctx('/InventarioServlet'));
        if (response.status === 401) {
            grid.innerHTML = emptyStateHTML('Sesión no iniciada', 'Inicia sesión para ver el inventario.');
            return;
        }
        if (!response.ok)
            throw new Error('HTTP ' + response.status);
        allProducts = await response.json();
        renderInventory(allProducts);
    } catch (error) {
        console.error('Error cargando inventario:', error);
        grid.innerHTML = emptyStateHTML('Error de conexión', 'No se pudieron cargar los productos.');
    }
}

function filterProducts(lista, texto) {
    var query = texto.trim().toLowerCase();
    if (!query)
        return lista;
    return lista.filter(function (p) {
        var nombre = String(p.nombre_producto || p.nombre || '').toLowerCase();
        var codigo = String(p.codigo_producto || p.codigo || '').toLowerCase();
        return nombre.indexOf(query) !== -1 || codigo.indexOf(query) !== -1;
    });
}

async function modificarStock() {
    var productoCodigoEl = $('stockProductoCodigo') || $('stockProductoId');
    var cantidadEl = $('stockCantidad');
    var tipoEl = $('stockTipo');
    var msgEl = $('stockMsg');

    var codigoIngresado = productoCodigoEl ? productoCodigoEl.value.trim() : '';
    var cantidadSolicitada = cantidadEl ? parseInt(cantidadEl.value, 10) : 0;
    var tipo = 'RETIRO_STOCK';

    if (!codigoIngresado || isNaN(cantidadSolicitada) || cantidadSolicitada < 1) {
        if (msgEl)
            msgEl.innerHTML = '<span style="color:#e05050">Ingresa el código del producto y una cantidad válida.</span>';
        return;
    }

    var producto = allProducts.find(function (p) {
        var cod = String(p.codigo_producto || p.codigo || p.code || '').trim();
        return cod.toLowerCase() === codigoIngresado.toLowerCase() || (p.id_producto || p.id) == codigoIngresado;
    });

    if (!producto) {
        if (msgEl)
            msgEl.innerHTML = '<span style="color:#e05050">Producto no encontrado con el código ingresado.</span>';
        return;
    }

    // Obtener el stock actual disponible en el frontend
    var stockDisponible = Number(producto.cantidad || producto.quantity || producto.stock || 0);

    // RESTRICCIÓN: No permitir retirar más del stock existente
    if (tipo === 'RETIRO_STOCK' && cantidadSolicitada > stockDisponible) {
        if (msgEl) {
            msgEl.innerHTML = '<span style="color:#e05050; font-weight: 600;">' +
                    '<i class="bi bi-exclamation-triangle-fill me-1"></i>' +
                    'No puedes retirar ' + cantidadSolicitada + ' unidades. El stock máximo disponible es ' + stockDisponible + '.' +
                    '</span>';
        }
        return;
    }

    var inventarioId = producto.inventario_id_inventario || producto.inventario_id;
    var productoId = producto.id_producto || producto.id;

    try {
        var body = new URLSearchParams({
            accion: 'modificar_stock',
            inventario_id: inventarioId,
            producto_id: productoId,
            cantidad: cantidadSolicitada,
            tipo_accion: tipo,
            descripcion: 'Retiro de ' + cantidadSolicitada + ' unidades - ' + (producto.nombre_producto || producto.nombre)
        });

        var response = await fetch(ctx('/InventarioServlet'), {method: 'POST', body: body});
        var data = await response.json();

        if (data.status === 'success') {
            if (msgEl)
                msgEl.innerHTML = '<span style="color:#4caf50">Stock actualizado correctamente.</span>';
            if (productoCodigoEl)
                productoCodigoEl.value = '';
            if (cantidadEl)
                cantidadEl.value = '';
            await loadInventory();
        } else {
            if (msgEl)
                msgEl.innerHTML = '<span style="color:#e05050">' + escapeHTML(data.mensaje) + '</span>';
        }
    } catch (e) {
        if (msgEl)
            msgEl.innerHTML = '<span style="color:#e05050">Error de conexión al modificar stock.</span>';
    }
}
// =====================================================
// GESTIÓN DE PROVEEDORES
// =====================================================
function providerCardHTML(proveedor) {
    var id = proveedor.id_proveedor || proveedor.id || '';
    var nit = proveedor.nit_proveedor || proveedor.nit || '';
    var nombre = proveedor.nombre_proveedor || proveedor.nombre || '';
    var correo = proveedor.email_proveedor || proveedor.correo_proveedor || proveedor.correo || '';
    var telefono = proveedor.telefono_proveedor || proveedor.telefono || '';
    var direccion = proveedor.direccion_proveedor || proveedor.direccion || '';
    var totalProductos = Number(proveedor.productos_asociados || proveedor.total_productos || 0);

    return '<article class="stf-card">' +
            '<div class="stf-product__top">' +
            '<div>' +
            '<div class="stf-product__name">' + escapeHTML(nombre) + '</div>' +
            (nit ? '<div class="stf-product__code">NIT: ' + escapeHTML(nit) + '</div>' : '') +
            '</div>' +
            '<span class="stf-badge ' + (totalProductos > 0 ? 'stf-badge--ok' : 'stf-badge--low') + '">' +
            '<span class="stf-badge__dot"></span>' + totalProductos + ' producto' + (totalProductos !== 1 ? 's' : '') +
            '</span>' +
            '</div>' +
            '<div class="stf-product__meta">' +
            (correo ? '<div><span>Correo:</span> ' + escapeHTML(correo) + '</div>' : '') +
            (telefono ? '<div><span>Teléfono:</span> ' + escapeHTML(telefono) + '</div>' : '') +
            (direccion ? '<div><span>Dirección:</span> ' + escapeHTML(direccion) + '</div>' : '') +
            '</div>' +
            '<div class="stf-product__footer">' +
            '<button type="button" class="btn-primary-gold w-100 btn-ver-productos-prov" data-id="' + escapeHTML(String(id)) + '" data-nombre="' + escapeHTML(nombre) + '">' +
            '<i class="bi bi-box-seam me-1"></i> Solicitar / Ver productos' +
            '</button>' +
            '</div>' +
            '</article>';
}
async function verProductosProveedor(idProveedor, nombreProveedor) {
    var modalEl = $('modalSolicitudProducto') || $('modalProductosProveedor');
    if (!modalEl)
        return;

    var titulo = $('modalNombreProveedor') || $('modalProductosProveedorNombre');
    var body = $('modalProductosProveedorBody');
    if (titulo)
        titulo.textContent = nombreProveedor || '';
    if (body)
        body.innerHTML = '<p class="text-muted text-center py-3"><span class="spinner-border spinner-border-sm me-2" role="status"></span>Cargando productos de la empresa...</p>';

    var bsModal = bootstrap.Modal.getOrCreateInstance(modalEl);
    bsModal.show();

    try {
        var response = await fetch(ctx('/ProveedorServlet?accion=productos&id_proveedor=' + encodeURIComponent(idProveedor) + '&empresa=' + encodeURIComponent(nombreProveedor || '')));
        if (response.status === 401) {
            if (body)
                body.innerHTML = emptyStateHTML('Sesión expirada', 'Por favor vuelve a iniciar sesión.');
            return;
        }
        if (!response.ok)
            throw new Error('HTTP ' + response.status);
        var productos = await response.json();

        if (!productos || !productos.length) {
            if (body)
                body.innerHTML = emptyStateHTML('Sin productos asociados', 'Esta empresa aún no tiene productos registrados disponibles.');
            return;
        }

        var html = '<div class="table-responsive"><table class="table table-dark table-hover align-middle mb-0">' +
                '<thead><tr><th>Código</th><th>Producto</th><th>Stock</th><th>Precio</th><th style="width:110px;">Cantidad</th><th class="text-center">Acción</th></tr></thead><tbody>';
        for (var i = 0; i < productos.length; i++) {
            var p = productos[i];
            var idProd = p.id_producto || p.id || p.codigo || i;
            var nomProd = p.nombre_producto || p.nombre || p.name || '';
            var codProd = p.codigo_producto || p.codigo || p.code || '-';
            var stockProd = p.stock != null ? p.stock : (p.cantidad != null ? p.cantidad : 0);
            var precioProd = p.precio != null ? p.precio : (p.costo_unitario != null ? p.costo_unitario : 0);

            html += '<tr>' +
                    '<td><code>' + escapeHTML(codProd) + '</code></td>' +
                    '<td class="fw-bold">' + escapeHTML(nomProd) + '</td>' +
                    '<td><span class="badge bg-secondary">' + escapeHTML(String(stockProd)) + ' u.</span></td>' +
                    '<td>$' + escapeHTML(String(precioProd)) + '</td>' +
                    '<td><input type="number" id="cant_sol_' + escapeHTML(String(idProd)) + '" value="1" min="1" class="form-control form-control-sm input-dark text-center"></td>' +
                    '<td class="text-center">' +
                    '<button type="button" class="btn btn-warning btn-sm fw-semibold" onclick="solicitarProductoIndividual(\'' + escapeHTML(String(idProd)) + '\', \'' + escapeHTML(nomProd.replace(/'/g, "\\'")) + '\', \'' + escapeHTML(String(idProveedor)) + '\')">' +
                    '<i class="bi bi-cart-plus me-1"></i>Solicitar' +
                    '</button>' +
                    '</td>' +
                    '</tr>';
        }
        html += '</tbody></table></div>';
        if (body)
            body.innerHTML = html;
    } catch (error) {
        console.error('Error cargando productos del proveedor:', error);
        if (body)
            body.innerHTML = emptyStateHTML('Error de conexión', 'No se pudieron cargar los productos de esta empresa.');
    }
}

window.solicitarProductoIndividual = async function (idProducto, nombreProducto, idProveedor) {
    var inputCant = $('cant_sol_' + idProducto);
    var cantidad = inputCant ? parseInt(inputCant.value, 10) : 1;

    if (isNaN(cantidad) || cantidad <= 0) {
        mostrarMensajeModal('Por favor ingresa una cantidad válida.', 'warning');
        return;
    }

    try {
        var params = new URLSearchParams({
            accion: 'solicitar',
            id_producto: idProducto,
            producto_id_producto: idProducto,
            proveedor_id_proveedor: idProveedor,
            cantidad: cantidad,
            unidad_medida_id_unidad_medida: '1',
            ubicacion_almacen_id_ubicacion: '1'
        });

        var response = await fetch(ctx('/ProductoServlet'), {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: params
        });

        if (!response.ok) {
            if (response.status === 404) {
                mostrarMensajeModal('Error 404: El Servlet de Producto no se encuentra desplegado.', 'danger');
            } else if (response.status === 401) {
                mostrarMensajeModal('Sesión expirada. Por favor vuelve a iniciar sesión.', 'danger');
            } else {
                mostrarMensajeModal('Error en el servidor (Código ' + response.status + ').', 'danger');
            }
            return;
        }

        var data = await response.json();

        if (data.status === 'success' || data.exito) {
            if (typeof loadInventory === 'function')
                await loadInventory();

            var nombreProv = ($('modalNombreProveedor') || $('modalProductosProveedorNombre'))?.textContent || '';
            await verProductosProveedor(idProveedor, nombreProv);

            mostrarMensajeModal('¡Solicitud de ' + cantidad + ' unidad(es) de "' + nombreProducto + '" enviada con éxito!', 'success');
        } else {
            mostrarMensajeModal(data.mensaje || 'Error al procesar la solicitud.', 'danger');
        }
    } catch (e) {
        console.error('Error al solicitar producto:', e);
        mostrarMensajeModal('Error al comunicarse con el servidor.', 'danger');
    }
};

function abrirModalSolicitud(idProveedor, nombreProveedor) {
    verProductosProveedor(idProveedor, nombreProveedor);
}

function abrirModalEditarProveedor(idProveedor) {
    var proveedor = allProviders.find(function (p) {
        return (p.id_proveedor || p.id) == idProveedor;
    });
    if (!proveedor)
        return;

    var modalEl = $('modalEditarProveedor');
    if (!modalEl)
        return;

    if ($('editProveedorId'))
        $('editProveedorId').value = proveedor.id_proveedor || proveedor.id || '';
    if ($('editProvNit'))
        $('editProvNit').value = proveedor.nit_proveedor || proveedor.nit || '';
    if ($('editProvNombre'))
        $('editProvNombre').value = proveedor.nombre_proveedor || proveedor.nombre || '';
    if ($('editProvTelefono'))
        $('editProvTelefono').value = proveedor.telefono_proveedor || proveedor.telefono || '';
    if ($('editProvEmail'))
        $('editProvEmail').value = proveedor.email_proveedor || proveedor.correo_proveedor || proveedor.correo || '';
    if ($('editProvDireccion'))
        $('editProvDireccion').value = proveedor.direccion_proveedor || proveedor.direccion || '';

    var bsModal = bootstrap.Modal.getOrCreateInstance(modalEl);
    bsModal.show();
}

async function solicitarProducto(event) {
    event.preventDefault();
    var form = event.target;
    var params = new URLSearchParams(new FormData(form));

    if (!params.get('proveedor_id_proveedor')) {
        mostrarMensajeModal('Debes seleccionar un proveedor válido.', 'warning');
        return;
    }

    try {
        var response = await fetch(ctx('/ProductoServlet'), {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: params
        });

        if (!response.ok)
            throw new Error('HTTP Error ' + response.status);
        var data = await response.json();

        if (data.status === 'success') {
            var modalEl = $('modalSolicitudProducto');
            if (modalEl) {
                var bsModal = bootstrap.Modal.getInstance(modalEl);
                if (bsModal)
                    bsModal.hide();
            }
            form.reset();
            await loadInventory();
        } else {
            mostrarMensajeModal(data.mensaje || 'Error al procesar la solicitud.', 'danger');
        }
    } catch (e) {
        console.error('Error al solicitar producto:', e);
        mostrarMensajeModal('Error de conexión con el servidor.', 'danger');
    }
}

function renderProviders(lista) {
    lista = lista || allProviders;
    var grid = $('prov-grid');
    if (!grid)
        return;

    if (lista.length > 0) {
        var html = '';
        for (var i = 0; i < lista.length; i++) {
            html += providerCardHTML(lista[i]);
        }
        grid.innerHTML = html;
    } else {
        grid.innerHTML = emptyStateHTML('No hay proveedores registrados', 'Los proveedores registrados aparecerán aquí.');
    }

    if ($('prov-count')) {
        $('prov-count').textContent = '· ' + lista.length + ' proveedor' + (lista.length !== 1 ? 'es' : '');
    }
}

async function loadProviders() {
    var grid = $('prov-grid');
    if (!grid)
        return;

    try {
        var response = await fetch(ctx('/ProveedorServlet'));
        if (response.status === 401) {
            grid.innerHTML = emptyStateHTML('Sesión no iniciada', 'Inicia sesión para ver los proveedores.');
            return;
        }
        if (!response.ok)
            throw new Error('HTTP ' + response.status);
        allProviders = await response.json();
        providersLoaded = true;
        renderProviders(allProviders);
    } catch (error) {
        console.error('Error cargando proveedores:', error);
        grid.innerHTML = emptyStateHTML('Error de conexión', 'No se pudieron cargar los proveedores.');
    }
}

function filterProviders(lista, texto) {
    var query = texto.trim().toLowerCase();
    if (!query)
        return lista;
    return lista.filter(function (p) {
        var nombre = String(p.nombre_proveedor || p.nombre || '').toLowerCase();
        var nit = String(p.nit_proveedor || p.nit || '').toLowerCase();
        var correo = String(p.email_proveedor || p.correo_proveedor || p.correo || '').toLowerCase();
        return nombre.indexOf(query) !== -1 || nit.indexOf(query) !== -1 || correo.indexOf(query) !== -1;
    });
}

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
            if (msgEl)
                msgEl.innerHTML = '<span style="color:#4caf50">Proveedor registrado correctamente.</span>';
            form.reset();
            await loadProviders();
        } else {
            if (msgEl)
                msgEl.innerHTML = '<span style="color:#e05050">' + escapeHTML(data.mensaje) + '</span>';
        }
    } catch (e) {
        if (msgEl)
            msgEl.innerHTML = '<span style="color:#e05050">Error de conexión al registrar proveedor.</span>';
    }
}

async function actualizarProveedor(event) {
    event.preventDefault();
    var form = event.target;
    var params = new URLSearchParams(new FormData(form));

    try {
        var response = await fetch(ctx('/ProveedorServlet'), {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: params
        });

        var data = await response.json();

        if (data.status === 'success') {
            var bsModal = bootstrap.Modal.getInstance($('modalEditarProveedor'));
            if (bsModal)
                bsModal.hide();
            await loadProviders();
        } else {
            mostrarMensajeModal(data.mensaje || 'Error al actualizar el proveedor.', 'danger');
        }
    } catch (e) {
        mostrarMensajeModal('Error de conexión al actualizar el proveedor.', 'danger');
    }
}

// =====================================================
// GESTIÓN DE HISTORIAL
// =====================================================
function historyItemHTML(item) {
    var usuario = item.usuario || '';
    var accion = item.accion || '';
    var objetivo = item.objetivo || '';
    var fecha = item.fecha || '';
    var hora = item.hora || '';
    var stockAnt = item.stock_anterior;
    var stockNuevo = item.stock_nuevo;
    var cantidad = item.cantidad_modificada;

    return '<div class="stf-history-item" data-action="' + escapeHTML(accion) + '">' +
            '<span class="stf-history-item__dot"></span>' +
            '<div class="stf-history-card">' +
            '<div class="stf-history-top">' +
            '<div class="stf-history-user">' +
            escapeHTML(usuario) + (accion ? ' <span>· ' + escapeHTML(accion) + '</span>' : '') +
            '</div>' +
            '<div class="stf-history-time">' +
            escapeHTML(fecha) + (hora ? ' · ' + escapeHTML(hora) : '') +
            '</div>' +
            '</div>' +
            (objetivo ? '<div class="stf-history-target">' + escapeHTML(objetivo) + '</div>' : '') +
            (stockAnt !== undefined && stockAnt !== null ? '<div class="stf-history-stock">Anterior: ' + escapeHTML(String(stockAnt)) + ' → Nuevo: ' + escapeHTML(String(stockNuevo)) + ' (' + (cantidad >= 0 ? '+' : '') + escapeHTML(String(cantidad)) + ')</div>' : '') +
            '</div>' +
            '</div>';
}

function renderHistory(lista) {
    lista = lista || allHistory;
    var timeline = $('hist-timeline');
    if (!timeline)
        return;

    if (lista.length > 0) {
        var html = '';
        for (var i = 0; i < lista.length; i++) {
            html += historyItemHTML(lista[i]);
        }
        timeline.innerHTML = html;
    } else {
        timeline.innerHTML = emptyStateHTML('No hay actividad registrada', 'Los cambios realizados aparecerán aquí.');
    }

    if ($('hist-count')) {
        $('hist-count').textContent = '· ' + lista.length + ' registro' + (lista.length !== 1 ? 's' : '');
    }
}

async function loadHistory() {
    if (historyLoaded)
        return;
    var timeline = $('hist-timeline');
    if (!timeline)
        return;
    historyLoaded = true;

    try {
        var response = await fetch(ctx('/HistorialServlet'));
        if (response.status === 401) {
            timeline.innerHTML = emptyStateHTML('Sesión no iniciada', 'Inicia sesión para ver el historial.');
            return;
        }
        if (!response.ok)
            throw new Error('HTTP ' + response.status);
        allHistory = await response.json();
        renderHistory(allHistory);
    } catch (error) {
        console.error('Error cargando historial:', error);
        timeline.innerHTML = emptyStateHTML('Error de conexión', 'No se pudo cargar el historial.');
    }
}

// =====================================================
// INICIALIZACIÓN GENERAL DEL DASHBOARD
// =====================================================
function initDashboard() {
    if ($('prov-grid')) {
        loadProviders();
    }

    var tabEl = document.getElementById('proveedores-tab');
    if (tabEl) {
        tabEl.addEventListener('shown.bs.tab', function () {
            if (!providersLoaded)
                loadProviders();
        });
    }

    var histTabEl = document.getElementById('historial-tab');
    if (histTabEl) {
        histTabEl.addEventListener('shown.bs.tab', function () {
            if (!historyLoaded)
                loadHistory();
        });
    }

    var invSearch = $('inv-search');
    if (invSearch) {
        invSearch.addEventListener('input', function (event) {
            renderInventory(filterProducts(allProducts, event.target.value));
        });
    }

    var provSearch = $('prov-search');
    if (provSearch) {
        provSearch.addEventListener('input', function (event) {
            renderProviders(filterProviders(allProviders, event.target.value));
        });
    }

    var btnModStock = $('btnModificarStock');
    if (btnModStock)
        btnModStock.addEventListener('click', modificarStock);

    var formCrearProv = $('formCrearProveedor');
    if (formCrearProv)
        formCrearProv.addEventListener('submit', guardarProveedor);

    var formEditarProv = $('formEditarProveedor');
    if (formEditarProv)
        formEditarProv.addEventListener('submit', actualizarProveedor);

    var provGrid = $('prov-grid');
    if (provGrid) {
        provGrid.addEventListener('click', function (e) {
            var btnVerProductos = e.target.closest('.btn-ver-productos-prov');
            if (btnVerProductos) {
                e.preventDefault();
                var idVer = btnVerProductos.getAttribute('data-id');
                var nombreVer = btnVerProductos.getAttribute('data-nombre');

                verProductosProveedor(idVer, nombreVer);
                return;
            }

            var btnEditar = e.target.closest('.btn-editar-prov');
            if (btnEditar) {
                e.preventDefault();
                var idEdit = btnEditar.getAttribute('data-id');
                if ($('modalEditarProveedor')) {
                    abrirModalEditarProveedor(idEdit);
                }
                return;
            }
        });
    }

    var formSolicitud = $('formSolicitarProducto');
    if (formSolicitud) {
        formSolicitud.addEventListener('submit', solicitarProducto);
    }

    if ($('inv-grid')) {
        loadInventory();
    }
}

// =====================================================
// CONTROL DEL MODAL DE REGISTRO
// =====================================================
function initRegisterModal() {
    const modal = $('register-modal');
    const closeBtn = $('modal-close-btn');

    const btnNavReg = $('nav-register-btn');
    const btnMobileReg = $('mobile-register-btn');
    const btnHeroReg = $('hero-register-btn');

    function openModal(e) {
        if (e)
            e.preventDefault();
        if (modal)
            modal.classList.remove('hidden');
    }

    function closeModal(e) {
        if (e)
            e.preventDefault();
        if (modal)
            modal.classList.add('hidden');
    }

    if (btnNavReg)
        btnNavReg.addEventListener('click', openModal);
    if (btnMobileReg)
        btnMobileReg.addEventListener('click', openModal);
    if (btnHeroReg)
        btnHeroReg.addEventListener('click', openModal);
    if (closeBtn)
        closeBtn.addEventListener('click', closeModal);

    if (modal) {
        modal.addEventListener('click', function (event) {
            if (event.target === modal) {
                closeModal();
            }
        });
    }

    const toggleRegPass = $('toggle-reg-pass');
    const regPassword = $('reg-password');
    if (toggleRegPass && regPassword) {
        toggleRegPass.addEventListener('click', function () {
            const type = regPassword.getAttribute('type') === 'password' ? 'text' : 'password';
            regPassword.setAttribute('type', type);
        });
    }

    const toggleRegConfirm = $('toggle-reg-confirm');
    const regConfirm = $('reg-confirm');
    if (toggleRegConfirm && regConfirm) {
        toggleRegConfirm.addEventListener('click', function () {
            const type = regConfirm.getAttribute('type') === 'password' ? 'text' : 'password';
            regConfirm.setAttribute('type', type);
        });
    }

    // Modal términos y condiciones
    const terminosLink = $('terminos-link');
    const terminosModal = $('terminos-modal');
    const terminosCloseBtn = $('terminos-close-btn');
    const terminosRechazarBtn = $('terminos-rechazar-btn');
    const terminosAceptarBtn = $('terminos-aceptar-btn');
    const regTerminos = $('reg-terminos');

    function openTerminosModal(e) {
        if (e)
            e.preventDefault();
        if (terminosModal)
            terminosModal.classList.remove('hidden');
    }

    function closeTerminosModal() {
        if (terminosModal)
            terminosModal.classList.add('hidden');
    }

    if (terminosLink)
        terminosLink.addEventListener('click', openTerminosModal);
    if (terminosCloseBtn)
        terminosCloseBtn.addEventListener('click', closeTerminosModal);
    if (terminosRechazarBtn)
        terminosRechazarBtn.addEventListener('click', closeTerminosModal);

    if (terminosAceptarBtn) {
        terminosAceptarBtn.addEventListener('click', function () {
            if (regTerminos) {
                regTerminos.checked = true;
                const terminosError = $('terminos-error');
                if (terminosError)
                    terminosError.style.display = 'none';
            }
            closeTerminosModal();
        });
    }

    if (terminosModal) {
        terminosModal.addEventListener('click', function (event) {
            if (event.target === terminosModal)
                closeTerminosModal();
        });
    }
}

// =====================================================
// VALIDACIÓN DE REGISTRO
// =====================================================
function initRegisterValidation() {
    const regFechaNac = $('reg-fecha-nac');
    const registerForm = $('register-form');
    const dateError = $('date-error');
    const regEmail = $('reg-email');
    const emailError = $('email-error');
    const regPassword = $('reg-password');
    const regConfirm = $('reg-confirm');
    const passMismatch = $('pass-mismatch');
    const generalError = $('register-general-error');

    function mostrarErrorUI(mensaje) {
        if (generalError) {
            generalError.textContent = mensaje;
            generalError.style.display = 'block';
            generalError.scrollIntoView({behavior: 'smooth', block: 'nearest'});
        }
    }

    function limpiarErrorUI() {
        if (generalError) {
            generalError.textContent = '';
            generalError.style.display = 'none';
        }
    }

    if (regEmail && emailError) {
        regEmail.addEventListener('input', function () {
            const val = this.value.trim();
            const isValid = val.includes('@') && val.includes('.') && val.length > 5;
            if (!isValid && val.length > 0) {
                emailError.style.display = 'block';
            } else {
                emailError.style.display = 'none';
            }
        });
    }

    function checkPasswordsMatch() {
        if (regPassword && regConfirm && passMismatch) {
            if (regConfirm.value.length > 0 && regPassword.value !== regConfirm.value) {
                passMismatch.style.display = 'block';
            } else {
                passMismatch.style.display = 'none';
            }
        }
    }

    if (regPassword)
        regPassword.addEventListener('input', checkPasswordsMatch);
    if (regConfirm)
        regConfirm.addEventListener('input', checkPasswordsMatch);

    // Bloquear números en nombre y apellido en tiempo real
    const regNombre = $('reg-nombre');
    const regApellido = $('reg-apellido');
    const soloLetrasRegex = /^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ\s'-]+$/;

    if (regNombre) {
        regNombre.addEventListener('input', function () {
            if (this.value.length > 0 && !soloLetrasRegex.test(this.value)) {
                this.setCustomValidity('El nombre no puede contener números.');
            } else {
                this.setCustomValidity('');
            }
        });
    }

    if (regApellido) {
        regApellido.addEventListener('input', function () {
            if (this.value.length > 0 && !soloLetrasRegex.test(this.value)) {
                this.setCustomValidity('El apellido no puede contener números.');
            } else {
                this.setCustomValidity('');
            }
        });
    }

    if (regFechaNac) {
        const today = new Date();

        const maxYear = today.getFullYear() - 18;
        const maxMonth = String(today.getMonth() + 1).padStart(2, '0');
        const maxDay = String(today.getDate()).padStart(2, '0');
        regFechaNac.setAttribute('max', `${maxYear}-${maxMonth}-${maxDay}`);

        const minYear = today.getFullYear() - 120;
        const minMonth = String(today.getMonth() + 1).padStart(2, '0');
        const minDay = String(today.getDate()).padStart(2, '0');
        regFechaNac.setAttribute('min', `${minYear}-${minMonth}-${minDay}`);

        regFechaNac.addEventListener('change', function () {
            const birthDate = new Date(this.value);
            if (isNaN(birthDate.getTime()))
                return;

            let age = today.getFullYear() - birthDate.getFullYear();
            const m = today.getMonth() - birthDate.getMonth();
            if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
                age--;
            }

            if (age < 18) {
                if (dateError) {
                    dateError.textContent = 'Debes tener al menos 18 años cumplidos para registrarte.';
                    dateError.style.display = 'block';
                }
                this.setCustomValidity('Menor de edad');
            } else if (age > 120) {
                if (dateError) {
                    dateError.textContent = 'Por favor, ingresa una fecha de nacimiento válida.';
                    dateError.style.display = 'block';
                }
                this.setCustomValidity('Fecha inválida');
            } else {
                if (dateError) {
                    dateError.style.display = 'none';
                }
                this.setCustomValidity('');
            }
        });
    }

    if (registerForm) {
        registerForm.addEventListener('submit', async function (event) {
            event.preventDefault();
            limpiarErrorUI();

            const nombre = $('reg-nombre')?.value.trim();
            const apellido = $('reg-apellido')?.value.trim();
            const email = $('reg-email')?.value.trim();
            const tipoDoc = $('reg-tipo-doc')?.value;
            const numDoc = $('reg-num-doc')?.value.trim();
            const fechaNac = regFechaNac?.value;
            const password = regPassword?.value;
            const confirmPass = $('reg-confirm')?.value;
            const terminosCheck = $('reg-terminos');

            if (!nombre || !apellido || !email || !tipoDoc || !numDoc || !fechaNac || !password || !confirmPass) {
                mostrarErrorUI('Todos los campos del registro son obligatorios y no pueden estar vacíos.');
                return;
            }

            // Restricción: nombre y apellido sin números
            const soloLetras = /^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ\s'-]+$/;
            if (!soloLetras.test(nombre)) {
                mostrarErrorUI('El nombre no puede contener números ni caracteres especiales.');
                return;
            }
            if (!soloLetras.test(apellido)) {
                mostrarErrorUI('El apellido no puede contener números ni caracteres especiales.');
                return;
            }

            // Restricción: número de documento entre 7 y 20 dígitos (solo números)
            const soloDigitos = /^\d+$/;
            if (!soloDigitos.test(numDoc) || numDoc.length < 7 || numDoc.length > 20) {
                mostrarErrorUI('El número de documento debe contener solo dígitos y tener entre 7 y 20 caracteres.');
                return;
            }

            // Restricción: contraseña mínimo 8 caracteres, una mayúscula y un carácter especial
            const passValida = /^(?=.*[A-Z])(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?`~]).{8,}$/;
            if (!passValida.test(password)) {
                mostrarErrorUI('La contraseña debe tener mínimo 8 caracteres, al menos una letra mayúscula y un carácter especial.');
                return;
            }

            if (password !== confirmPass) {
                mostrarErrorUI('Error: Las contraseñas no coinciden.');
                return;
            }

            // Restricción: términos y condiciones
            const terminosError = $('terminos-error');
            if (!terminosCheck || !terminosCheck.checked) {
                if (terminosError)
                    terminosError.style.display = 'block';
                mostrarErrorUI('Debes aceptar los Términos y Condiciones para continuar.');
                return;
            }
            if (terminosError)
                terminosError.style.display = 'none';

            const today = new Date();
            const birthDate = new Date(fechaNac);
            let age = today.getFullYear() - birthDate.getFullYear();
            const m = today.getMonth() - birthDate.getMonth();
            if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
                age--;
            }

            if (age < 18) {
                mostrarErrorUI('Acceso denegado: Debes ser mayor de 18 años para registrarte en el sistema.');
                return;
            } else if (age > 120) {
                mostrarErrorUI('Acceso denegado: La fecha de nacimiento ingresada no es válida.');
                return;
            }

            // Verificar documento duplicado antes de enviar al servidor
            try {
                const submitBtn = $('reg-submit-btn');
                if (submitBtn) {
                    submitBtn.disabled = true;
                    submitBtn.dataset.originalText = submitBtn.innerHTML;
                    submitBtn.textContent = 'Verificando...';
                }

                const checkBody = new URLSearchParams({numero_documento: numDoc});
                const checkResp = await fetch(ctx('/VerificarDocumentoServlet'), {
                    method: 'POST',
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                    body: checkBody
                });

                if (checkResp.ok) {
                    const checkData = await checkResp.json();
                    if (checkData.existe === true) {
                        mostrarErrorUI('Error: El número de documento ya está siendo ocupado.');
                        if (submitBtn) {
                            submitBtn.disabled = false;
                            submitBtn.innerHTML = submitBtn.dataset.originalText;
                        }
                        return;
                    }
                }

                if (submitBtn) {
                    submitBtn.disabled = false;
                    submitBtn.innerHTML = submitBtn.dataset.originalText;
                }
            } catch (e) {
                // Si el servlet no existe aún, se ignora y se deja pasar al servidor
                const submitBtn = $('reg-submit-btn');
                if (submitBtn) {
                    submitBtn.disabled = false;
                    submitBtn.innerHTML = submitBtn.dataset.originalText;
                }
            }

            // Todas las validaciones pasaron — enviar el formulario
            registerForm.submit();
        });
    }
}

// =====================================================
// VALIDACIÓN DE INICIO DE SESIÓN
// =====================================================
function initLoginHandler() {
    const loginForm = $('login-form');
    const loginError = $('login-error');
    const loginEmail = $('login-email');
    const loginPassword = $('login-password');
    const toggleLoginPass = $('toggle-login-pass');

    if (toggleLoginPass && loginPassword) {
        toggleLoginPass.addEventListener('click', function () {
            const type = loginPassword.getAttribute('type') === 'password' ? 'text' : 'password';
            loginPassword.setAttribute('type', type);
        });
    }

    if (loginForm) {
        loginForm.addEventListener('submit', async function (event) {
            event.preventDefault();

            const email = loginEmail ? loginEmail.value.trim() : '';
            const password = loginPassword ? loginPassword.value : '';

            if (!email || !password) {
                if (loginError) {
                    loginError.textContent = 'Por favor, completa todos los campos .';
                    loginError.style.display = 'block';
                }
                return;
            }

            try {
                const body = new URLSearchParams({email: email, password: password});
                const response = await fetch(ctx('/LoginServlet'), {
                    method: 'POST',
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                    body: body
                });

                if (!response.ok)
                    throw new Error('Error en el servidor');
                const data = await response.json();

                if (data.status === 'success') {
                    if (loginError)
                        loginError.style.display = 'none';
                    // CORRECCIÓN: Pasa la ruta de redirección devuelta por el Servlet a través de ctx()
                    var destino = data.redirect || 'admin/admin.jsp';
                    window.location.href = ctx(destino);
                } else {
                    if (loginError) {
                        loginError.textContent = data.mensaje || 'Correo o contraseña incorrectos.';
                        loginError.style.display = 'block';
                    }
                }
            } catch (e) {
                if (loginError) {
                    loginError.textContent = 'credenciales inválidas.';
                    loginError.style.display = 'block';
                }
            }
        });
    }
}

// =====================================================
// GESTIÓN DE RECUPERACIÓN DE CONTRASEÑA
// =====================================================
function initForgotPasswordHandler() {
    const forgotLink = $('forgot-password-link');
    const forgotModal = $('forgot-modal');
    const forgotCloseBtn = $('modal-forgot-close-btn');
    const forgotForm = $('forgot-form');
    const forgotEmail = $('forgot-email');
    const forgotError = $('forgot-general-error');
    const forgotSuccess = $('forgot-general-success');
    const forgotSubmitBtn = $('forgot-submit-btn');

    function openModal(e) {
        if (e)
            e.preventDefault();
        if (forgotModal) {
            limpiarMensajes();
            forgotModal.classList.remove('hidden');
        }
    }

    function closeModal(e) {
        if (e)
            e.preventDefault();
        if (forgotModal)
            forgotModal.classList.add('hidden');
    }

    function limpiarMensajes() {
        if (forgotError) {
            forgotError.textContent = '';
            forgotError.style.display = 'none';
        }
        if (forgotSuccess) {
            forgotSuccess.textContent = '';
            forgotSuccess.style.display = 'none';
        }
    }

    function mostrarError(msg) {
        if (forgotSuccess)
            forgotSuccess.style.display = 'none';
        if (forgotError) {
            forgotError.textContent = msg;
            forgotError.style.display = 'block';
        }
    }

    function mostrarExito(msg) {
        if (forgotError)
            forgotError.style.display = 'none';
        if (forgotSuccess) {
            forgotSuccess.textContent = msg;
            forgotSuccess.style.display = 'block';
        }
    }

    if (forgotLink)
        forgotLink.addEventListener('click', openModal);
    if (forgotCloseBtn)
        forgotCloseBtn.addEventListener('click', closeModal);

    if (forgotModal) {
        forgotModal.addEventListener('click', function (event) {
            if (event.target === forgotModal) {
                closeModal();
            }
        });
    }

    if (forgotForm) {
        forgotForm.addEventListener('submit', async function (event) {
            event.preventDefault();
            limpiarMensajes();

            const email = forgotEmail ? forgotEmail.value.trim() : '';

            if (!email) {
                mostrarError('Por favor, ingresa tu correo electrónico.');
                return;
            }

            if (forgotSubmitBtn) {
                forgotSubmitBtn.disabled = true;
                forgotSubmitBtn.dataset.originalText = forgotSubmitBtn.innerHTML;
                forgotSubmitBtn.textContent = 'Enviando...';
            }

            try {
                const body = new URLSearchParams({email: email});
                const response = await fetch(ctx('/RecuperarClaveServlet'), {
                    method: 'POST',
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                    body: body
                });

                const data = await response.json();

                if (data.status === 'success') {
                    mostrarExito(data.mensaje || 'Se ha enviado un correo con las instrucciones de recuperación.');
                    if (forgotForm)
                        forgotForm.reset();
                } else {
                    mostrarError(data.mensaje || 'No se pudo procesar la solicitud.');
                }
            } catch (e) {
                mostrarError('Error de conexión al enviar la solicitud.');
            } finally {
                if (forgotSubmitBtn) {
                    forgotSubmitBtn.disabled = false;
                    forgotSubmitBtn.innerHTML = forgotSubmitBtn.dataset.originalText;
                }
            }
        });
    }
}

document.addEventListener('DOMContentLoaded', function () {
    initDashboard();
    initRegisterModal();
    initRegisterValidation();
    initLoginHandler();
    initForgotPasswordHandler();
});

// =====================================================
// GESTIÓN DEL MODAL DE PERFIL DE USUARIO
// =====================================================
function initUserProfileModal() {
    const modalEl = document.getElementById('modalEditarPerfil');
    if (!modalEl)
        return;

    // Alternar visibilidad de contraseña
    document.querySelectorAll('.profile-toggle-pass').forEach(function (toggleBtn) {
        toggleBtn.addEventListener('click', function () {
            const targetId = this.getAttribute('data-target');
            const input = document.getElementById(targetId);
            const icon = this.querySelector('i');
            if (input) {
                if (input.type === 'password') {
                    input.type = 'text';
                    icon.classList.replace('bi-eye', 'bi-eye-slash');
                } else {
                    input.type = 'password';
                    icon.classList.replace('bi-eye-slash', 'bi-eye');
                }
            }
        });
    });

    const nombreInput = document.getElementById('editNombre');
    const apellidoInput = document.getElementById('editApellido');
    const emailInput = document.getElementById('editEmail');
    const passInput = document.getElementById('editPassword');
    const confirmInput = document.getElementById('editConfirmPassword');

    const nombreError = document.getElementById('editNombreError');
    const apellidoError = document.getElementById('editApellidoError');
    const emailError = document.getElementById('editEmailError');
    const passError = document.getElementById('editPassError');
    const formMsg = document.getElementById('profileFormMsg');

    const regexLetras = /^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ\s'-]+$/;
    const regexEmail = /^[^\s@]+@[^\s@]+\.[a-zA-Z]{2,}$/;

    // Validaciones en tiempo real
    if (nombreInput && nombreError) {
        nombreInput.addEventListener('input', function () {
            if (this.value && !regexLetras.test(this.value.trim())) {
                nombreError.textContent = 'El nombre no puede contener números ni caracteres especiales.';
            } else {
                nombreError.textContent = '';
            }
        });
    }

    if (apellidoInput && apellidoError) {
        apellidoInput.addEventListener('input', function () {
            if (this.value && !regexLetras.test(this.value.trim())) {
                apellidoError.textContent = 'El apellido no puede contener números ni caracteres especiales.';
            } else {
                apellidoError.textContent = '';
            }
        });
    }

    if (emailInput && emailError) {
        emailInput.addEventListener('input', function () {
            if (this.value && !regexEmail.test(this.value.trim())) {
                emailError.textContent = 'Ingresa un correo electrónico válido (ej. usuario@dominio.com)';
            } else {
                emailError.textContent = '';
            }
        });
    }

    function checkPasswords() {
        if (!passError)
            return;
        if (passInput && confirmInput && (passInput.value || confirmInput.value)) {
            if (confirmInput.value !== passInput.value) {
                passError.textContent = 'Las contraseñas no coinciden.';
            } else {
                passError.textContent = '';
            }
        } else {
            passError.textContent = '';
        }
    }

    if (confirmInput)
        confirmInput.addEventListener('input', checkPasswords);
    if (passInput)
        passInput.addEventListener('input', checkPasswords);

    const btnGuardar = document.getElementById('btnGuardarPerfil');
    if (btnGuardar) {
        btnGuardar.addEventListener('click', async function () {
            const nombre = nombreInput ? nombreInput.value.trim() : '';
            const apellido = apellidoInput ? apellidoInput.value.trim() : '';
            const email = emailInput ? emailInput.value.trim() : '';
            const password = passInput ? passInput.value : '';
            const confirmPassword = confirmInput ? confirmInput.value : '';

            // Limpiar errores previos
            if (nombreError)
                nombreError.textContent = '';
            if (apellidoError)
                apellidoError.textContent = '';
            if (emailError)
                emailError.textContent = '';
            if (passError)
                passError.textContent = '';
            if (formMsg) {
                formMsg.textContent = '';
                formMsg.style.display = 'none';
            }

            let hasError = false;

            // 1. Validar nombre
            if (!nombre) {
                if (nombreError)
                    nombreError.textContent = 'El nombre no puede estar vacío.';
                hasError = true;
            } else if (!regexLetras.test(nombre)) {
                if (nombreError)
                    nombreError.textContent = 'El nombre solo puede contener letras.';
                hasError = true;
            }

            // 2. Validar apellido
            if (apellido && !regexLetras.test(apellido)) {
                if (apellidoError)
                    apellidoError.textContent = 'El apellido solo puede contener letras.';
                hasError = true;
            }

            // 3. Validar correo
            if (email && !regexEmail.test(email)) {
                if (emailError)
                    emailError.textContent = 'El correo electrónico no es válido.';
                hasError = true;
            }

            // 4. Validar contraseña
            if (password || confirmPassword) {
                if (password !== confirmPassword) {
                    if (passError)
                        passError.textContent = 'Las contraseñas no coinciden.';
                    hasError = true;
                } else if (password.length < 8) {
                    if (passError)
                        passError.textContent = 'La contraseña debe tener al menos 8 caracteres.';
                    hasError = true;
                }
            }

            if (hasError)
                return;

            const btn = this;
            btn.disabled = true;
            btn.innerHTML = '<span class="spinner-border spinner-border-sm me-1"></span> Guardando...';

            try {
                const body = new URLSearchParams();
                body.append('nombre', nombre);
                body.append('apellido', apellido);
                if (email)
                    body.append('email', email);
                if (password)
                    body.append('password', password);

                const response = await fetch(window.CONTEXT_PATH + '/EditarPerfilServlet', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                    body: body
                });

                const data = await response.json();

                if (data.status === 'success') {
                    if (formMsg) {
                        formMsg.textContent = data.mensaje || 'Perfil actualizado correctamente.';
                        formMsg.className = 'profile-msg profile-msg--success alert alert-success py-2 fs-6';
                        formMsg.style.display = 'block';
                    }
                    const nombreSpan = document.getElementById('usuarioNombre');
                    if (nombreSpan)
                        nombreSpan.textContent = nombre + (apellido ? ' ' + apellido : '');
                    if (passInput)
                        passInput.value = '';
                    if (confirmInput)
                        confirmInput.value = '';

                    setTimeout(() => {
                        const bsModal = bootstrap.Modal.getInstance(modalEl);
                        if (bsModal)
                            bsModal.hide();
                    }, 1500);
                } else {
                    if (formMsg) {
                        formMsg.textContent = data.mensaje || 'No se pudo actualizar el perfil.';
                        formMsg.className = 'profile-msg profile-msg--error alert alert-danger py-2 fs-6';
                        formMsg.style.display = 'block';
                    }
                }
            } catch (e) {
                if (formMsg) {
                    formMsg.textContent = 'Error de conexión con el servidor.';
                    formMsg.className = 'profile-msg profile-msg--error alert alert-danger py-2 fs-6';
                    formMsg.style.display = 'block';
                }
            } finally {
                btn.disabled = false;
                btn.innerHTML = '<i class="bi bi-check2-circle me-1"></i> Guardar cambios';
            }
        });
    }
}
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initUserProfileModal);
} else {
    initUserProfileModal();
}