'use strict';

const TEMP_STATS = {totalClientes: 0, clientesActivos: 0, inhabilitados: 0, administradores: 0, eliminados: 0};
const TEMP_CLIENTES = [];
const TEMP_ROLES = [];
const TEMP_NOTIFICACIONES = [];
const TEMP_PERMISOS = {
    "Productos": ["Ver productos", "Crear producto", "Editar producto", "Eliminar producto"],
    "Clientes": ["Ver clientes", "Editar cliente", "Cambiar rol", "Inhabilitar cliente"],
    "Inventario": ["Ajustar stock", "Ver historial"]
};

// CORRECCIÓN: Evita tomar /admin/ como context path en Railway
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

const API = {
    async obtenerEstadisticas() {
        try {
            const res = await fetch(ctx('/AdminServlet?accion=obtenerEstadisticas'));
            if (!res.ok) throw new Error('Error al obtener estadísticas');
            return await res.json();
        } catch (e) {
            return TEMP_STATS;
        }
    },
    async obtenerClientes() {
        try {
            const res = await fetch(ctx('/AdminServlet?accion=listarClientes'));
            if (!res.ok) throw new Error('Error al conectar con servidor');
            return await res.json();
        } catch (e) {
            return [...TEMP_CLIENTES];
        }
    },
    async habilitarCliente(id) {
        try {
            const res = await fetch(ctx('/AdminServlet'), {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body: `accion=cambiarEstado&id=${id}&activar=true`
            });
            return await res.json();
        } catch (e) {
            return {status: 'success'};
        }
    },
    async inhabilitarCliente(id, motivo) {
        try {
            const res = await fetch(ctx('/AdminServlet'), {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body: `accion=cambiarEstado&id=${id}&activar=false`
            });
            return await res.json();
        } catch (e) {
            return {status: 'success'};
        }
    },
    async eliminarCliente(id) {
        try {
            const res = await fetch(ctx('/AdminServlet'), {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body: `accion=eliminarUsuario&id=${id}`
            });
            return await res.json();
        } catch (e) {
            return {status: 'error', mensaje: 'Error de red'};
        }
    },
    async obtenerRoles() {
        try {
            const res = await fetch(ctx('/AdminServlet?accion=listarRoles'));
            if (!res.ok) throw new Error('Error al conectar');
            return await res.json();
        } catch (e) {
            return [...TEMP_ROLES];
        }
    },
    async crearRol(rol) {
        try {
            const res = await fetch(ctx('/AdminServlet'), {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body: `accion=crearRol&nombre=${encodeURIComponent(rol.nombre)}&descripcion=${encodeURIComponent(rol.descripcion || '')}`
            });
            return await res.json();
        } catch (e) {
            return {status: 'success'};
        }
    },
    async eliminarRol(id) {
        try {
            const res = await fetch(ctx('/AdminServlet'), {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body: `accion=eliminarRol&id=${id}`
            });
            return await res.json();
        } catch (e) {
            return {status: 'success'};
        }
    },
    async obtenerProveedores() {
        try {
            const res = await fetch(ctx('/AdminServlet?accion=listarProveedores'));
            if (!res.ok) throw new Error('Error al listar proveedores');
            return await res.json();
        } catch (e) {
            return [];
        }
    },
    async obtenerHistorial() {
        try {
            const res = await fetch(ctx('/AdminServlet?accion=listarHistorial'));
            if (!res.ok) throw new Error('Error al conectar con historial');
            return await res.json();
        } catch (e) {
            return [];
        }
    },
    async obtenerNotificaciones() {
        try {
            const res = await fetch(ctx('/AdminServlet?accion=listarNotificaciones'));
            if (!res.ok) throw new Error('Error al conectar con notificaciones');
            return await res.json();
        } catch (e) {
            return [...TEMP_NOTIFICACIONES];
        }
    },
    async obtenerProductos() {
        try {
            const res = await fetch(ctx('/AdminServlet?accion=listarProductos'));
            if (!res.ok) throw new Error('Error al conectar con productos');
            return await res.json();
        } catch (e) {
            return [];
        }
    },
    async obtenerPermisos(rolId) {
        try {
            const res = await fetch(ctx(`/AdminServlet?accion=obtenerPermisos&rolId=${rolId}`));
            if (!res.ok) throw new Error('Error al obtener permisos');
            return await res.json();
        } catch (e) {
            return [];
        }
    },
    async guardarPermisos(rolId, permisos) {
        try {
            const res = await fetch(ctx('/AdminServlet'), {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body: `accion=guardarPermisos&rolId=${rolId}&permisos=${encodeURIComponent(JSON.stringify(permisos))}`
            });
            return await res.json();
        } catch (e) {
            return {status: 'success'};
        }
    }
};

const State = {
    clientes: [],
    roles: [],
    historial: [],
    notificaciones: [],
    clienteActivo: null,
    rolPermsActivo: null,
    currentPage: 1,
    itemsPerPage: 6,
    filtroRol: '',
    filtroEstado: '',
    filtroBusqueda: ''
};

function initNav() {
    const sidebar = document.getElementById('sidebar');
    const overlay = document.getElementById('sidebar-overlay');
    const toggle = document.getElementById('sidebarToggle');

    toggle?.addEventListener('click', () => {
        sidebar.classList.toggle('open');
        overlay.classList.toggle('open');
    });
    overlay?.addEventListener('click', closeSidebar);

    document.querySelectorAll('.nav-item[data-section]').forEach(item => {
        item.addEventListener('click', () => {
            const section = item.dataset.section;
            navigateTo(section);
            if (window.innerWidth < 900) closeSidebar();
        });
    });
}

function closeSidebar() {
    document.getElementById('sidebar')?.classList.remove('open');
    document.getElementById('sidebar-overlay')?.classList.remove('open');
}

function navigateTo(section) {
    document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));
    document.querySelector(`.nav-item[data-section="${section}"]`)?.classList.add('active');

    document.querySelectorAll('.section').forEach(s => s.classList.remove('active'));
    document.getElementById(`section-${section}`)?.classList.add('active');

    const titles = {
        dashboard: 'Inicio',
        productos: 'Gestión de Productos',
        clientes: 'Clientes',
        roles: 'Roles',
        permisos: 'Permisos',
        historial: 'Historial',
        notificaciones: 'Notificaciones',
        configuracion: 'Configuración'
    };

    document.getElementById('topbarTitle').textContent = titles[section] || section;
    document.getElementById('topbarBreadcrumb').innerHTML = `Panel de Administrador / <span>${titles[section] || ''}</span>`;

    if (section === 'dashboard') renderDashboard();
    if (section === 'productos') renderProductos();
    if (section === 'clientes') renderClientes();
    if (section === 'roles') renderRoles();
    if (section === 'permisos') renderPermisos();
    if (section === 'historial') renderHistorial();
    if (section === 'notificaciones') renderNotificaciones();
}

async function renderDashboard() {
    const stats = await API.obtenerEstadisticas();
    animateNumber('stat-total', stats.totalClientes || 0);
    animateNumber('stat-activos', stats.clientesActivos || 0);
    animateNumber('stat-inactivos', stats.inhabilitados || 0);
    animateNumber('stat-admins', stats.administradores || 0);
    animateNumber('stat-eliminados', stats.eliminados || 0);

    try {
        const historial = await API.obtenerHistorial();
        const cards = document.querySelectorAll('#section-dashboard .card');
        if (cards.length >= 1) {
            const histBody = cards[0].querySelector('.card-body') || cards[0];
            if (!historial || historial.length === 0) {
                histBody.innerHTML = `<div style="padding:16px; text-align:center; color:var(--text-muted); font-size:0.85rem;">No hay actividad reciente.</div>`;
            } else {
                histBody.innerHTML = historial.slice(0, 4).map(h => {
                    const adminStr = h.admin || h.usuario || 'Sistema';
                    const accionStr = h.accion || 'realizó acción';
                    const afectadoStr = h.afectado || h.producto || '—';
                    const fechaStr = h.fecha || h.fecha_hora || '';
                    const tipoDot = h.tipo || 'gold';
                    return `
                    <div class="history-item" style="padding:10px 0; border-bottom:1px solid var(--border);">
                        <div style="display:flex; align-items:center; gap:8px;">
                            <div class="history-dot ${tipoDot}" style="width:6px; height:6px;"></div>
                            <div style="font-size:0.82rem; color:var(--text-primary);"><em>${adminStr}</em> ${accionStr} sobre <em>${afectadoStr}</em></div>
                        </div>
                        <div style="font-size:0.72rem; color:var(--text-muted); margin-left:14px; margin-top:2px;">${fechaStr}</div>
                    </div>`;
                }).join('');
            }
        }
    } catch (e) {}

    try {
        const notifs = await API.obtenerNotificaciones();
        const cards = document.querySelectorAll('#section-dashboard .card');
        if (cards.length >= 2) {
            const notifBody = cards[1].querySelector('.card-body') || cards[1];
            if (!notifs || notifs.length === 0) {
                notifBody.innerHTML = `<div style="padding:16px; text-align:center; color:var(--text-muted); font-size:0.85rem;">No hay notificaciones recientes.</div>`;
            } else {
                notifBody.innerHTML = notifs.slice(0, 4).map(n => `
                    <div class="notif-item" style="display:flex; align-items:flex-start; gap:10px; padding:10px 0; border-bottom:1px solid var(--border);">
                        <div style="font-size:1rem;"></div>
                        <div style="flex:1;">
                            <div style="font-size:0.82rem; font-weight:500; color:var(--text-primary);">${n.titulo}</div>
                            <div style="font-size:0.75rem; color:var(--text-muted);">${n.desc}</div>
                        </div>
                    </div>`).join('');
            }
        }
    } catch (e) {}
}

function animateNumber(id, target) {
    const el = document.getElementById(id);
    if (!el) return;
    let current = 0;
    const step = Math.ceil(target / 30) || 1;
    const interval = setInterval(() => {
        current = Math.min(current + step, target);
        el.textContent = current.toLocaleString();
        if (current >= target) clearInterval(interval);
    }, 30);
}

async function renderProductos() {
    const tbody = document.getElementById('productosTableBody');
    if (!tbody) return;

    tbody.innerHTML = `<tr><td colspan="7" style="text-align:center; padding:24px; color:var(--text-muted)">Cargando inventario...</td></tr>`;

    const productos = await API.obtenerProductos();

    if (!productos || productos.length === 0) {
        tbody.innerHTML = `<tr><td colspan="7" style="text-align:center; padding:24px; color:var(--text-muted)">No hay productos registrados en el sistema.</td></tr>`;
        return;
    }

    tbody.innerHTML = productos.map(p => {
        const idProd = p.id_producto || p.id;
        const nombreProd = (p.nombre_producto || p.nombre || '').replace(/'/g, "\\'");
        const codigoProd = p.codigo_producto || p.codigo || '—';
        const stockProd = p.cantidad != null ? p.cantidad : (p.stock != null ? p.stock : 0);
        const proveedorProd = p.nombre_proveedor || p.proveedor || '—';
        const fechaIng = p.fecha_ingreso || p.fechaIngreso || '—';
        const fechaVen = p.fecha_vencimiento || p.fechaVencimiento || 'N/A';

        return `
        <tr>
            <td><strong>${codigoProd}</strong></td>
            <td>${p.nombre_producto || p.nombre}</td>
            <td><span class="badge ${stockProd > 5 ? 'badge-active' : 'badge-inactive'}">${stockProd} unids</span></td>
            <td>${proveedorProd}</td>
            <td class="td-muted">${fechaIng}</td>
            <td class="td-muted">${fechaVen}</td>
            <td class="actions-cell" style="text-align:right; padding-right:12px">
                <button class="btn-icon" onclick="abrirModalAjustarStock(${idProd}, '${nombreProd}', ${stockProd})" title="Ajustar Stock">📦</button>
                <button class="btn-icon" style="color:#e74c3c" onclick="eliminarProducto(${idProd}, '${nombreProd}')" title="Eliminar Producto">🗑</button>
            </td>
        </tr>`;
    }).join('');
}

async function abrirModalAgregarProducto() {
    const elNombre = document.getElementById('addNombreProducto');
    const elCodigo = document.getElementById('addCodigoProducto');
    const elCantidad = document.getElementById('addCantidadProducto');
    const elFechaIng = document.getElementById('addFechaIngresoProducto');
    const elFechaVen = document.getElementById('addFechaVencimientoProducto');
    const msgDiv = document.getElementById('msgAgregarProducto');

    if (msgDiv) {
        msgDiv.style.display = 'none';
        msgDiv.textContent = '';
    }

    if (elNombre) elNombre.value = '';
    if (elCodigo) elCodigo.value = '';
    if (elCantidad) elCantidad.value = '0';

    const today = new Date();
    const yyyy = today.getFullYear();
    const mm = String(today.getMonth() + 1).padStart(2, '0');
    const dd = String(today.getDate()).padStart(2, '0');
    const todayStr = `${yyyy}-${mm}-${dd}`;

    if (elFechaIng) {
        elFechaIng.value = todayStr;
    }

    const tomorrow = new Date(today);
    tomorrow.setDate(tomorrow.getDate() + 1);
    const yyyyT = tomorrow.getFullYear();
    const mmT = String(tomorrow.getMonth() + 1).padStart(2, '0');
    const ddT = String(tomorrow.getDate()).padStart(2, '0');
    const tomorrowStr = `${yyyyT}-${mmT}-${ddT}`;

    if (elFechaVen) {
        elFechaVen.value = '';
        elFechaVen.setAttribute('min', tomorrowStr);
    }

    const selectProv = document.getElementById('addProveedorProducto');
    if (selectProv) {
        selectProv.innerHTML = '<option value="">Cargando proveedores...</option>';
        const proveedores = await API.obtenerProveedores();

        if (!proveedores || proveedores.length === 0) {
            selectProv.innerHTML = '<option value="">No hay proveedores en la BD</option>';
        } else {
            let optionsHtml = '<option value="">-- Seleccione un Proveedor --</option>';
            optionsHtml += proveedores.map(p => {
                const idProv = p.id_proveedor || p.id;
                const nomProv = p.nombre_proveedor || p.nombre;
                return `<option value="${idProv}">${nomProv}</option>`;
            }).join('');
            selectProv.innerHTML = optionsHtml;
        }
    }

    openModal('modalAgregarProducto');
}

async function guardarNuevoProducto() {
    const btnGuardar = document.getElementById('btnGuardarProducto');
    let msgDiv = document.getElementById('msgAgregarProducto');

    if (!msgDiv) {
        const modalBody = document.querySelector('#modalAgregarProducto .modal-body');
        if (modalBody) {
            msgDiv = document.createElement('div');
            msgDiv.id = 'msgAgregarProducto';
            msgDiv.style.cssText = 'display:none; color: #f87171; background-color: rgba(239, 68, 68, 0.15); border: 1px solid rgba(239, 68, 68, 0.3); padding: 0.65rem 0.85rem; border-radius: 6px; font-size: 0.85rem; font-weight: 500; margin-bottom: 1rem;';
            modalBody.insertBefore(msgDiv, modalBody.firstChild);
        }
    }

    const mostrarError = (mensaje) => {
        if (msgDiv) {
            msgDiv.innerHTML = '⚠️ ' + mensaje;
            msgDiv.style.display = 'block';
        }
        showToast('error', '⚠️', mensaje);
        if (btnGuardar) btnGuardar.disabled = false;
    };

    if (msgDiv) msgDiv.style.display = 'none';

    const elNombre = document.getElementById('addNombreProducto');
    const elCodigo = document.getElementById('addCodigoProducto');
    const elCantidad = document.getElementById('addCantidadProducto');
    const elProveedor = document.getElementById('addProveedorProducto');
    const elFechaIng = document.getElementById('addFechaIngresoProducto');
    const elFechaVen = document.getElementById('addFechaVencimientoProducto');

    const today = new Date();
    const yyyy = today.getFullYear();
    const mm = String(today.getMonth() + 1).padStart(2, '0');
    const dd = String(today.getDate()).padStart(2, '0');
    const todayStr = `${yyyy}-${mm}-${dd}`;

    if (elFechaIng && !elFechaIng.value) {
        elFechaIng.value = todayStr;
    }

    const nombre = elNombre ? elNombre.value.trim() : '';
    const codigo = elCodigo ? elCodigo.value.trim() : '';
    const cantidad = elCantidad ? elCantidad.value.trim() : '';
    const idProveedor = elProveedor ? elProveedor.value.trim() : '';
    const fechaIngreso = elFechaIng ? elFechaIng.value.trim() : '';
    const fechaVencimiento = elFechaVen ? elFechaVen.value.trim() : '';

    if (!nombre) {
        mostrarError('Por favor ingresa el nombre del producto.');
        return;
    }

    if (!codigo) {
        mostrarError('Por favor ingresa el código del producto.');
        return;
    }

    if (cantidad === '' || isNaN(cantidad) || Number(cantidad) < 0) {
        mostrarError('Por favor ingresa una cantidad de stock válida.');
        return;
    }

    if (!idProveedor) {
        mostrarError('Por favor seleccione un proveedor de la lista.');
        return;
    }

    if (!fechaVencimiento) {
        mostrarError('Por favor ingrese una fecha de vencimiento válida.');
        return;
    }

    if (fechaVencimiento <= todayStr) {
        mostrarError('La fecha de vencimiento debe ser mayor al día de hoy.');
        return;
    }

    if (btnGuardar && btnGuardar.disabled) return;
    if (btnGuardar) btnGuardar.disabled = true;

    try {
        const res = await fetch(ctx('/AdminServlet'), {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' },
            body: `accion=agregarProducto&nombre=${encodeURIComponent(nombre)}&codigo=${encodeURIComponent(codigo)}&cantidad=${cantidad}&proveedor=${encodeURIComponent(idProveedor)}&fechaIngreso=${encodeURIComponent(fechaIngreso)}&fechaVencimiento=${encodeURIComponent(fechaVencimiento)}`
        });

        const textResponse = await res.text();
        let data;
        try {
            data = JSON.parse(textResponse);
        } catch (e) {
            mostrarError('El servidor devolvió una respuesta no válida.');
            return;
        }

        if (data.status === 'success') {
            closeModal('modalAgregarProducto');
            showToast('success', '✅', 'Producto guardado correctamente.');
            await renderProductos();
        } else {
            mostrarError(data.mensaje || 'No se pudo insertar el producto.');
        }
    } catch (e) {
        mostrarError('Error de red al intentar guardar.');
    } finally {
        if (btnGuardar) btnGuardar.disabled = false;
    }
}

function abrirModalAjustarStock(idProducto, nombre, stockActual) {
    window.productoStockActivo = {id: idProducto, stock: stockActual};
    const elNombre = document.getElementById('stockNombreProd');
    const elStock = document.getElementById('stockActualProd');
    const elCant = document.getElementById('inputCantAjuste');
    const elMotivo = document.getElementById('inputMotivoAjuste');

    if (elNombre) elNombre.textContent = nombre;
    if (elStock) elStock.textContent = stockActual;
    if (elCant) elCant.value = '1';
    if (elMotivo) elMotivo.value = '';

    openModal('modalAjustarStock');
}

async function procesarAjusteStock(tipoAccion) {
    if (!window.productoStockActivo) return;

    const cantidad = document.getElementById('inputCantAjuste')?.value;
    const motivo = document.getElementById('inputMotivoAjuste')?.value.trim() || '';

    if (!cantidad || cantidad <= 0) {
        showToast('error', '❌', 'Ingresa una cantidad válida.');
        return;
    }

    try {
        const res = await fetch(ctx('/AdminServlet'), {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
            body: `accion=modificarStock&idProducto=${window.productoStockActivo.id}&cantidad=${cantidad}&tipoAccion=${tipoAccion}&motivo=${encodeURIComponent(motivo)}`
        });

        const data = await res.json();
        if (data.status === 'success') {
            closeModal('modalAjustarStock');
            showToast('success', '✅', 'Stock actualizado en inventario.');
            await renderProductos();
        } else {
            showToast('error', '❌', data.mensaje || 'Error al ajustar stock.');
        }
    } catch (e) {
        showToast('error', '❌', 'Error de comunicación con el servidor.');
    }
}

window.productoAEliminar = null;

function eliminarProducto(id, nombre) {
    if (!id) {
        showToast('error', '❌', 'Identificador de producto no válido.');
        return;
    }

    window.productoAEliminar = {id: id, nombre: nombre};

    const elNombre = document.getElementById('eliminarProdNombre');
    if (elNombre) elNombre.textContent = nombre;

    openModal('modalEliminarProducto');
}

async function confirmarEliminarProducto() {
    if (!window.productoAEliminar) return;

    const {id, nombre} = window.productoAEliminar;
    const btnConfirmar = document.getElementById('btnConfirmarEliminarProd');

    if (btnConfirmar) btnConfirmar.disabled = true;

    try {
        const res = await fetch(ctx('/AdminServlet'), {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
            body: `accion=eliminarProducto&id=${encodeURIComponent(id)}`
        });

        const data = await res.json();
        if (data.status === 'success') {
            closeModal('modalEliminarProducto');
            showToast('info', '🗑️', `Producto "${nombre}" eliminado correctamente.`);
            await renderProductos();
            await renderHistorial();
        } else {
            showToast('error', '❌', data.mensaje || 'No se pudo eliminar el producto.');
        }
    } catch (e) {
        showToast('error', '❌', 'Error de comunicación con el servidor.');
    } finally {
        if (btnConfirmar) btnConfirmar.disabled = false;
        window.productoAEliminar = null;
    }
}

async function renderClientes() {
    State.clientes = await API.obtenerClientes();
    renderTablaClientes();
}

function filtrarClientes() {
    let lista = State.clientes || [];
    const q = State.filtroBusqueda.toLowerCase();
    if (q) lista = lista.filter(c => (c.nombre || '').toLowerCase().includes(q) || (c.email || '').toLowerCase().includes(q));
    if (State.filtroRol) lista = lista.filter(c => c.rol === State.filtroRol);
    if (State.filtroEstado) lista = lista.filter(c => c.estado === State.filtroEstado);
    return lista;
}

function renderTablaClientes() {
    const lista = filtrarClientes();
    const total = lista.length;
    const pages = Math.ceil(total / State.itemsPerPage) || 1;
    const start = (State.currentPage - 1) * State.itemsPerPage;
    const page = lista.slice(start, start + State.itemsPerPage);

    const tbody = document.getElementById('clientesTableBody');
    if (!tbody) return;

    if (page.length === 0) {
        tbody.innerHTML = `<tr><td colspan="6" style="text-align:center;padding:32px;color:var(--text-muted);">No hay clientes que coincidan.</td></tr>`;
    } else {
        tbody.innerHTML = page.map(c => clienteRow(c)).join('');
        attachRowListeners();
    }

    const info = document.getElementById('pageInfo');
    if (info) info.textContent = `${total > 0 ? Math.min(start + 1, total) : 0}–${Math.min(start + State.itemsPerPage, total)} de ${total}`;
    renderPagination(pages);
}

function clienteRow(c) {
    const nombreReal = c.nombre || 'Usuario';
    const emailReal = c.email || '';
    const initials = nombreReal.split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase();
    const estadoStr = c.estado === true || c.estado === 'activo' ? 'activo' : 'inhabilitado';
    const estadoBadge = estadoStr === 'activo'
            ? `<span class="badge badge-active"><span class="badge-dot"></span>Activo</span>`
            : `<span class="badge badge-inactive"><span class="badge-dot"></span>Inhabilitado</span>`;
    const rolStr = c.rol || (c.rol_id_rol === 3 ? 'admin' : 'usuario');
    const rolBadge = rolBadgeHtml(rolStr);
    return `
    <tr>
        <td>
            <div class="user-cell">
                <div class="user-avatar">${initials}</div>
                <div>
                    <div class="user-name">${nombreReal}</div>
                    <div class="user-email">${emailReal}</div>
                </div>
            </div>
        </td>
        <td>${rolBadge}</td>
        <td>${estadoBadge}</td>
        <td class="td-muted">${c.registro || '—'}</td>
        <td class="actions-cell" style="text-align:right;padding-right:12px">
            <div class="dropdown" style="position: relative; display: inline-block;">
                <button class="btn-icon btn-actions" data-id="${c.id || c.id_usuario}" title="Acciones">⋮</button>
                <div class="dropdown-menu" id="dd-${c.id || c.id_usuario}">
                    <button class="dropdown-item" data-action="rol" data-id="${c.id || c.id_usuario}">Cambiar rol</button>
                    <div class="dropdown-divider"></div>
                    ${estadoStr !== 'activo'
            ? `<button class="dropdown-item success" data-action="habilitar" data-id="${c.id || c.id_usuario}">🟢 Habilitar</button>`
            : `<button class="dropdown-item" data-action="inhabilitar" data-id="${c.id || c.id_usuario}">🔴 Inhabilitar</button>`
            }
                </div>
            </div>
        </td>
    </tr>`;
}

function rolBadgeHtml(rol) {
    const map = {
        admin: ['badge-admin', 'Administrador'],
        usuario: ['badge-user', 'Usuario'],
        proveedor: ['badge-provider', 'Proveedor']
    };
    const [cls, label] = map[rol] || ['badge-user', rol];
    return `<span class="badge ${cls}">${label}</span>`;
}

function attachRowListeners() {
    document.querySelectorAll('.btn-actions').forEach(btn => {
        btn.addEventListener('click', e => {
            e.stopPropagation();
            const id = btn.dataset.id;
            const dd = document.getElementById(`dd-${id}`);

            document.querySelectorAll('.menu-acciones-pop.open').forEach(d => {
                if (d !== dd) d.classList.remove('open', 'dropup');
            });

            if (dd) {
                const isOpen = dd.classList.contains('open');
                if (!isOpen) {
                    dd.classList.add('open');

                    const rect = dd.getBoundingClientRect();
                    const windowHeight = window.innerHeight || document.documentElement.clientHeight;

                    if (rect.bottom > windowHeight - 15) {
                        dd.classList.add('dropup');
                    } else {
                        dd.classList.remove('dropup');
                    }
                } else {
                    dd.classList.remove('open', 'dropup');
                }
            }
        });
    });

    document.querySelectorAll('[data-action]').forEach(btn => {
        btn.addEventListener('click', e => {
            e.stopPropagation();
            const {action, id} = btn.dataset;
            const cliente = State.clientes.find(c => (c.id == id || c.id_usuario == id));
            document.querySelectorAll('.menu-acciones-pop.open').forEach(d => d.classList.remove('open', 'dropup'));
            if (!cliente) return;
            State.clienteActivo = cliente;
            switch (action) {
                case 'ver': abrirModalInfoCliente(cliente); break;
                case 'editar': abrirModalEditarCliente(cliente); break;
                case 'rol': abrirModalCambiarRol(cliente); break;
                case 'habilitar': abrirModalHabilitar(cliente); break;
                case 'inhabilitar': abrirModalInhabilitar(cliente); break;
            }
        });
    });
}

function renderPagination(pages) {
    const cont = document.getElementById('paginationBtns');
    if (!cont) return;
    let html = `<button class="page-btn" id="prevPage" ${State.currentPage === 1 ? 'disabled' : ''}>&#8249;</button>`;
    for (let i = 1; i <= pages; i++) {
        html += `<button class="page-btn ${i === State.currentPage ? 'active' : ''}" data-page="${i}">${i}</button>`;
    }
    html += `<button class="page-btn" id="nextPage" ${State.currentPage === pages ? 'disabled' : ''}>&#8250;</button>`;
    cont.innerHTML = html;

    cont.querySelectorAll('[data-page]').forEach(b => {
        b.addEventListener('click', () => {
            State.currentPage = +b.dataset.page;
            renderTablaClientes();
        });
    });
    cont.querySelector('#prevPage')?.addEventListener('click', () => {
        if (State.currentPage > 1) {
            State.currentPage--;
            renderTablaClientes();
        }
    });
    cont.querySelector('#nextPage')?.addEventListener('click', () => {
        if (State.currentPage < pages) {
            State.currentPage++;
            renderTablaClientes();
        }
    });
}

function initClientesFiltros() {
    const search = document.getElementById('clienteSearch');
    const fRol = document.getElementById('filtroRol');
    const fEst = document.getElementById('filtroEstado');

    search?.addEventListener('input', () => {
        State.filtroBusqueda = search.value;
        State.currentPage = 1;
        renderTablaClientes();
    });
    fRol?.addEventListener('change', () => {
        State.filtroRol = fRol.value;
        State.currentPage = 1;
        renderTablaClientes();
    });
    fEst?.addEventListener('change', () => {
        State.filtroEstado = fEst.value;
        State.currentPage = 1;
        renderTablaClientes();
    });
}

function abrirModalInfoCliente(c) {
    const el = document.getElementById('modalInfo');
    if (!el) return;
    el.querySelector('#infoNombre').textContent = c.nombre || '';
    el.querySelector('#infoEmail').textContent = c.email || '';
    el.querySelector('#infoTel').textContent = c.telefono || '—';
    el.querySelector('#infoDoc').textContent = c.documento || c.numero_documento || '—';
    el.querySelector('#infoRol').innerHTML = rolBadgeHtml(c.rol || 'usuario');
    const estadoStr = c.estado === true || c.estado === 'activo' ? 'activo' : 'inhabilitado';
    el.querySelector('#infoEstado').innerHTML = estadoStr === 'activo'
            ? `<span class="badge badge-active"><span class="badge-dot"></span>Activo</span>`
            : `<span class="badge badge-inactive"><span class="badge-dot"></span>Inhabilitado</span>`;
    el.querySelector('#infoRegistro').textContent = c.registro || '—';
    el.querySelector('#infoAvatar').textContent = (c.nombre || 'U').split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase();
    openModal('modalInfo');
}

function abrirModalEditarCliente(c) {
    const el = document.getElementById('modalEditar');
    if (!el) return;
    el.querySelector('#editNombre').value = c.nombre || '';
    el.querySelector('#editEmail').value = c.email || '';
    el.querySelector('#editTelefono').value = c.telefono || '';
    el.querySelector('#editDocumento').value = c.documento || c.numero_documento || '';
    openModal('modalEditar');
}

async function guardarEdicion() {
    if (!State.clienteActivo) return;
    const idUsr = State.clienteActivo.id || State.clienteActivo.id_usuario;
    const nombre = document.getElementById('editNombre').value;
    const email = document.getElementById('editEmail').value;
    const telefono = document.getElementById('editTelefono').value;
    const documento = document.getElementById('editDocumento').value;
    const rol = State.clienteActivo.rol || 'usuario';

    try {
        const res = await fetch(ctx('/AdminServlet'), {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
            body: `accion=editarUsuario&id=${idUsr}&nombre=${encodeURIComponent(nombre)}&email=${encodeURIComponent(email)}&documento=${encodeURIComponent(documento)}&rol=${rol}`
        });
        const data = await res.json();
        if (data.status === 'success') {
            closeModal('modalEditar');
            await renderClientes();
            showToast('success', '✅', 'Cliente actualizado correctamente.');
        } else {
            showToast('error', '❌', data.mensaje || 'Error al actualizar');
        }
    } catch (e) {
        showToast('error', '❌', 'Error de conexión con el servidor.');
    }
}

function abrirModalCambiarRol(c) {
    const el = document.getElementById('modalCambiarRol');
    if (!el) return;
    el.querySelector('#cambioRolNombre').textContent = c.nombre || '';
    el.querySelector('#nuevoRol').value = c.rol || 'usuario';
    openModal('modalCambiarRol');
}

async function confirmarCambioRol() {
    if (!State.clienteActivo) return;
    const idUsr = State.clienteActivo.id || State.clienteActivo.id_usuario;
    const nuevoRol = document.getElementById('nuevoRol').value;

    try {
        const res = await fetch(ctx('/AdminServlet'), {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
            body: `accion=cambiarRol&id=${idUsr}&rol=${nuevoRol}`
        });
        const data = await res.json();
        if (data.status === 'success') {
            closeModal('modalCambiarRol');
            await renderClientes();
            showToast('success', '✅', `Rol cambiado a ${nuevoRol}.`);
        } else {
            showToast('error', '❌', data.mensaje || 'Error al cambiar rol');
        }
    } catch (e) {
        showToast('error', '❌', 'Error de conexión con el servidor.');
    }
}

function abrirModalHabilitar(c) {
    document.getElementById('habilitarNombre').textContent = c.nombre || '';
    openModal('modalHabilitar');
}

async function confirmarHabilitar() {
    if (!State.clienteActivo) return;
    const idUsr = State.clienteActivo.id || State.clienteActivo.id_usuario;
    await API.habilitarCliente(idUsr);
    closeModal('modalHabilitar');
    await renderClientes();
    showToast('success', '🟢', `${State.clienteActivo.nombre || 'Usuario'} habilitado.`);
}

function abrirModalInhabilitar(c) {
    document.getElementById('inhabilitarNombre').textContent = c.nombre || '';
    document.getElementById('motivoInhab').value = '';
    openModal('modalInhabilitar');
}

async function confirmarInhabilitar() {
    if (!State.clienteActivo) return;
    const idUsr = State.clienteActivo.id || State.clienteActivo.id_usuario;
    const motivo = document.getElementById('motivoInhab')?.value.trim() || '';
    await API.inhabilitarCliente(idUsr, motivo);
    closeModal('modalInhabilitar');
    await renderClientes();
    showToast('warning', '🔴', `${State.clienteActivo.nombre || 'Usuario'} inhabilitado.`);
}

async function renderRoles() {
    State.roles = await API.obtenerRoles();
    const cont = document.getElementById('rolesGrid');
    if (!cont) return;

    cont.innerHTML = (State.roles || []).map(r => `
        <div class="role-card">
            <div class="role-card-header">
                <div>
                    <div class="role-name">${r.nombre}</div>
                    <div class="role-count">${r.cantidad || 0} usuarios</div>
                </div>
                <div class="role-actions">
                    <button class="btn-icon" onclick="abrirModalEditarRol(${r.id})" title="Editar">✏️</button>
                    <button class="btn-icon" style="color:#e74c3c" onclick="eliminarRol(${r.id})" title="Eliminar">🗑</button>
                </div>
            </div>
            <div class="role-perms">
                ${(r.permisos || []).slice(0, 4).map(p => `<span class="perm-chip">${p}</span>`).join('')}
                ${(r.permisos || []).length > 4 ? `<span class="perm-chip">+${r.permisos.length - 4} más</span>` : ''}
            </div>
        </div>
    `).join('');
}

function abrirModalCrearRol() {
    document.getElementById('nuevoRolNombre').value = '';
    document.getElementById('nuevoRolDescripcion').value = '';
    openModal('modalCrearRol');
}

async function confirmarCrearRol() {
    const nombre = document.getElementById('nuevoRolNombre')?.value.trim();
    if (!nombre) {
        showToast('error', '❌', 'El nombre del rol es obligatorio.');
        return;
    }
    await API.crearRol({nombre, descripcion: document.getElementById('nuevoRolDescripcion')?.value || '', color: 'blue', permisos: []});
    closeModal('modalCrearRol');
    await renderRoles();
    showToast('success', '✅', `Rol "${nombre}" creado correctamente.`);
}

async function eliminarRol(id) {
    const rol = State.roles.find(r => r.id === id);
    if (!rol) return;
    if (!confirm(`¿Eliminar el rol "${rol.nombre}"?`)) return;
    await API.eliminarRol(id);
    await renderRoles();
    showToast('info', '🗑', `Rol "${rol.nombre}" eliminado.`);
}

function abrirModalEditarRol(id) {
    const rol = State.roles.find(r => r.id === id);
    if (!rol) return;
    document.getElementById('editRolNombreActual').textContent = rol.nombre;
    document.getElementById('editRolNombre').value = rol.nombre;
    openModal('modalEditarRol');
}

async function renderPermisos() {
    State.roles = await API.obtenerRoles();
    const sel = document.getElementById('permsRolSelect');
    if (!sel) return;
    sel.innerHTML = (State.roles || []).map(r => `<option value="${r.id}">${r.nombre}</option>`).join('');
    if (!State.rolPermsActivo) State.rolPermsActivo = State.roles[0]?.id;
    sel.value = State.rolPermsActivo;
    if (State.rolPermsActivo) await renderPermisosRol(State.rolPermsActivo);
}

async function renderPermisosRol(rolId) {
    const permsActivos = await API.obtenerPermisos(rolId);
    const cont = document.getElementById('permsGrid');
    if (!cont) return;

    cont.innerHTML = Object.entries(TEMP_PERMISOS).map(([grupo, perms]) => `
        <div class="perm-group">
            <div class="perm-group-title">${grupo.toUpperCase()}</div>
            ${perms.map(p => {
            const checked = (permsActivos || []).includes(p);
            const safeId = p.replace(/\s+/g, '_');
            return `
                <div class="perm-item">
                    <label for="perm_${safeId}">
                        <input type="checkbox" class="perm-checkbox" id="perm_${safeId}" value="${p}" ${checked ? 'checked' : ''}>
                        <span class="perm-check-box">✓</span>
                        ${p}
                    </label>
                </div>`;
        }).join('')}
        </div>
    `).join('');
}

async function guardarPermisos() {
    const sel = document.getElementById('permsRolSelect');
    if (!sel) return;
    const rolId = sel.value;
    const checks = document.querySelectorAll('.perm-checkbox:checked');
    const permisos = Array.from(checks).map(c => c.value);
    await API.guardarPermisos(rolId, permisos);
    showToast('success', '✅', 'Permisos guardados correctamente.');
}

async function renderHistorial() {
    State.historial = await API.obtenerHistorial();
    renderHistorialLista(State.historial);
}

function renderHistorialLista(lista) {
    const cont = document.getElementById('historialLista');
    if (!cont) return;
    if (!lista || lista.length === 0) {
        cont.innerHTML = `<div style="padding:32px;text-align:center;color:var(--text-muted)">No hay registros.</div>`;
        return;
    }
    cont.innerHTML = lista.map(h => {
        const adminStr = h.admin || h.usuario || 'Sistema';
        const accionStr = h.accion || 'realizó acción';
        const afectadoStr = h.afectado || h.producto || '—';
        const fechaStr = h.fecha || h.fecha_hora || '';
        const tipoDot = h.tipo || 'gold';
        return `
        <div class="history-item">
            <div class="history-dot ${tipoDot}"></div>
            <div class="history-content">
                <div class="history-action"><em>${adminStr}</em> ${accionStr} sobre <em>${afectadoStr}</em></div>
                <div class="history-meta">${fechaStr}</div>
            </div>
        </div>`;
    }).join('');
}

function filtrarHistorial() {
    const q = document.getElementById('histSearch')?.value.toLowerCase() || '';
    const fAcc = document.getElementById('histFiltroAccion')?.value || '';
    let lista = State.historial || [];
    if (q) lista = lista.filter(h => (h.admin || h.usuario || '').toLowerCase().includes(q) || (h.afectado || h.producto || '').toLowerCase().includes(q) || (h.accion || '').toLowerCase().includes(q));
    if (fAcc) lista = lista.filter(h => (h.accion || '').includes(fAcc));
    renderHistorialLista(lista);
}

async function renderNotificaciones() {
    State.notificaciones = await API.obtenerNotificaciones();
    const cont = document.getElementById('notificacionesLista');
    if (!cont) return;
    cont.innerHTML = (State.notificaciones || []).map(n => `
        <div class="notif-item ${n.leida ? '' : 'unread'}" data-notif="${n.id}">
            <div class="notif-icon ${n.tipo}">${n.icono}</div>
            <div style="flex:1">
                <div class="notif-title">${n.titulo}</div>
                <div class="notif-desc">${n.desc}</div>
            </div>
            <div class="notif-time">${n.tiempo}</div>
        </div>
    `).join('');

    cont.querySelectorAll('.notif-item').forEach(item => {
        item.addEventListener('click', () => {
            item.classList.remove('unread');
            updateNotifBadge();
        });
    });

    updateNotifBadge();
}

function updateNotifBadge() {
    const unread = document.querySelectorAll('.notif-item.unread').length;
    const badge = document.getElementById('notifBadge');
    const dot = document.getElementById('topbarNotifDot');
    if (badge) badge.textContent = unread || '';
    if (badge) badge.style.display = unread ? 'inline-flex' : 'none';
    if (dot) dot.style.display = unread ? 'block' : 'none';
}

function openModal(id) {
    document.getElementById(id)?.classList.add('open');
}

function closeModal(id) {
    document.getElementById(id)?.classList.remove('open');
}

function initModalClosers() {
    document.querySelectorAll('.modal-overlay').forEach(overlay => {
        overlay.addEventListener('click', e => {
            if (e.target === overlay) overlay.classList.remove('open');
        });
    });
    document.querySelectorAll('.modal-close, [data-close]').forEach(btn => {
        btn.addEventListener('click', () => {
            btn.closest('.modal-overlay')?.classList.remove('open');
        });
    });
}

function showToast(tipo, icono, mensaje, duracion = 3500) {
    if (mensaje === undefined) {
        mensaje = icono;
        icono = tipo === 'success' ? '✅' : (tipo === 'error' ? '❌' : 'ℹ️');
    }
    const cont = document.getElementById('toast-container');
    if (!cont) {
        alert(`${icono} ${mensaje}`);
        return;
    }
    const id = 'toast-' + Date.now();
    const el = document.createElement('div');
    el.className = `toast ${tipo}`;
    el.id = id;
    el.innerHTML = `<span class="toast-icon">${icono}</span><span>${mensaje}</span><button class="toast-close" onclick="document.getElementById('${id}')?.remove()">✕</button>`;
    cont.appendChild(el);
    setTimeout(() => el?.remove(), duracion);
}

function cerrarSesion() {
    openModal('modalLogout');
}

document.addEventListener('DOMContentLoaded', () => {
    initNav();
    initModalClosers();
    initClientesFiltros();

    document.getElementById('btnAbrirModalProducto')?.addEventListener('click', abrirModalAgregarProducto);
    document.getElementById('btnGuardarProducto')?.addEventListener('click', guardarNuevoProducto);
    document.getElementById('btnCrearRol')?.addEventListener('click', abrirModalCrearRol);
    document.getElementById('btnGuardarEdicion')?.addEventListener('click', guardarEdicion);
    document.getElementById('btnConfirmarRol')?.addEventListener('click', confirmarCambioRol);
    document.getElementById('btnHabilitar')?.addEventListener('click', confirmarHabilitar);
    document.getElementById('btnInhabilitar')?.addEventListener('click', confirmarInhabilitar);
    document.getElementById('btnCrearRolConfirmar')?.addEventListener('click', confirmarCrearRol);
    document.getElementById('btnGuardarPermisos')?.addEventListener('click', guardarPermisos);
    document.getElementById('btnConfirmarEliminarProd')?.addEventListener('click', confirmarEliminarProducto);

    document.getElementById('btnConfirmarLogout')?.addEventListener('click', () => {
        window.location.href = ctx('/LogoutServlet');
    });

    document.getElementById('permsRolSelect')?.addEventListener('change', e => {
        State.rolPermsActivo = e.target.value;
        renderPermisosRol(e.target.value);
    });

    document.getElementById('histSearch')?.addEventListener('input', filtrarHistorial);
    document.getElementById('histFiltroAccion')?.addEventListener('change', filtrarHistorial);

    navigateTo('dashboard');
});