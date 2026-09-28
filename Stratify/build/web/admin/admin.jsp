<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    HttpSession sesion = request.getSession(false);
    if (sesion == null || sesion.getAttribute("id_usuario") == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp?error=no_session");
        return;
    }

    Object rolObj = sesion.getAttribute("rol_id_rol");
    int rol = 0;
    if (rolObj != null) {
        try {
            rol = Integer.parseInt(rolObj.toString());
        } catch (NumberFormatException e) {
            rol = 0;
        }
    }

    if (rol != 3) {
        response.sendRedirect(request.getContextPath() + "/usuario/usuario.jsp");
        return;
    }

    String nombreUsuario = sesion.getAttribute("nombre") != null ? sesion.getAttribute("nombre").toString() : "Admin";
    String emailUsuario = sesion.getAttribute("email") != null ? sesion.getAttribute("email").toString() : "admin@stratify.com";
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>STRATIFY — Panel de Administrador</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=1.5">
    </head>
    <body>

        <div class="geo-shapes" aria-hidden="true">
            <span></span><span></span><span></span><span></span><span></span>
        </div>

        <div id="sidebar-overlay"></div>

        <!-- SIDEBAR -->
        <aside id="sidebar">
            <div class="sidebar-brand">
                <div class="brand-name">STRATIFY</div>
                <div class="brand-sub">Inventory System</div>
            </div>

            <nav class="sidebar-nav">
                <div class="nav-label">Principal</div>

                <div class="nav-item active" data-section="dashboard">Inicio</div>
                <div class="nav-item" data-section="productos">Productos</div>
                <div class="nav-item" data-section="clientes">Clientes</div>

                <div class="nav-label">Sistema</div>

                <div class="nav-item" data-section="historial">Historial</div>
                    <span class="nav-badge" id="notifBadge" style="display:none"></span>
                </div>
                <div class="nav-item" data-section="configuracion">Configuración</div>
            </nav>

            <div class="sidebar-footer">
                <div class="nav-item" onclick="cerrarSesion()">Cerrar sesión</div>
            </div>
        </aside>

        <!-- MAIN -->
        <div class="wrapper">
            <main class="main-content">

                <!-- Topbar -->
                <header class="topbar">
                    <div class="topbar-left">
                        <button class="sidebar-toggle" id="sidebarToggle" aria-label="Menú">☰</button>
                        <div>
                            <div id="topbarTitle" style="font-size:1.05rem;font-weight:600">Inicio</div>
                            <div class="topbar-breadcrumb" id="topbarBreadcrumb">
                                Panel de Administrador / <span>Inicio</span>
                            </div>
                        </div>
                    </div>

                    <div class="topbar-right">
                        <button class="btn btn-gold btn-sm me-2" id="btnAbrirModalProducto">
                            + Agregar Producto
                        </button>

                        <div class="topbar-user">
                            <div class="topbar-avatar">A1</div>
                            <div class="topbar-user-info">
                                <div class="name"><%= nombreUsuario%></div>
                                <div class="role">Administrador</div>
                            </div>
                        </div>
                    </div>
                </header>

                <!-- DASHBOARD -->
                <div id="section-dashboard" class="section page-content">
                    <div class="section-header">
                        <div>
                            <div class="section-title">Bienvenido, <%= nombreUsuario%></div>
                            <div class="section-sub">Resumen general del sistema de inventario.</div>
                        </div>
                        <div style="font-size:0.78rem;color:var(--text-muted)">Lunes, 07 septiembre 2026</div>
                    </div>

                    <div class="stats-grid">
                        <div class="stat-card gold"><div class="stat-label">Total clientes</div><div class="stat-value" id="stat-total" data-api-key="totalClientes">—</div></div>
                        <div class="stat-card green"><div class="stat-label">Clientes activos</div><div class="stat-value" id="stat-activos" data-api-key="clientesActivos">—</div></div>
                        <div class="stat-card red"><div class="stat-label">Inhabilitados</div><div class="stat-value" id="stat-inactivos" data-api-key="inhabilitados">—</div></div>
                        <div class="stat-card blue"><div class="stat-label">Administradores</div><div class="stat-value" id="stat-admins" data-api-key="administradores">—</div></div>
                        <div class="stat-card gray"><div class="stat-label">Eliminados</div><div class="stat-value" id="stat-eliminados" data-api-key="eliminados">—</div></div>
                    </div>

                    <div class="row g-3">
                        <div class="col-lg-7">
                            <div class="card">
                                <div class="card-header">
                                    <div class="card-title">Actividad reciente</div>
                                    <button class="btn btn-sm btn-outline" onclick="navigateTo('historial')">Ver historial →</button>
                                </div>
                                <div id="dashboardHistorialLista"><div style="padding:24px; text-align:center; color:var(--text-muted);">Cargando actividad reciente...</div></div>
                            </div>
                        </div>
                        <div class="col-lg-5">
                            <div class="card">
                                <div class="card-header">
                                    <div class="card-title">Notificaciones</div>
                                    <button class="btn btn-sm btn-outline" onclick="navigateTo('notificaciones')">Ver todas →</button>
                                </div>
                                <div class="notif-list" id="dashboardNotifLista"><div style="padding:24px; text-align:center; color:var(--text-muted);">Cargando notificaciones...</div></div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- PRODUCTOS -->
                <div id="section-productos" class="section page-content">
                    <div class="section-header">
                        <div>
                            <div class="section-title">Gestión de Inventario</div>
                            <div class="section-sub">Lista de productos registrados y control de existencias.</div>
                        </div>
                        <button class="btn btn-gold" onclick="abrirModalAgregarProducto()"> Nuevo Producto</button>
                    </div>

                    <div class="card">
                        <div class="table-wrapper" style="overflow: visible;">
                            <table class="data-table">
                                <thead>
                                    <tr>
                                        <th>Código</th>
                                        <th>Producto</th>
                                        <th>Stock</th>
                                        <th>Proveedor</th>
                                        <th>Ingreso</th>
                                        <th>Vencimiento</th>
                                        <th style="text-align:right; padding-right:12px">Acciones</th>
                                    </tr>
                                </thead>
                                <tbody id="productosTableBody">
                                    <tr><td colspan="7" style="text-align:center; padding:24px; color:var(--text-muted)">Cargando inventario de productos...</td></tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <!-- CLIENTES -->
                <div id="section-clientes" class="section page-content">
                    <div class="section-header">
                        <div>
                            <div class="section-title">Gestión de Clientes</div>
                            <div class="section-sub">Administra usuarios, estados y roles del sistema.</div>
                        </div>
                    </div>

                    <div class="card">
                        <div class="toolbar">
                            <div class="search-box"><span></span><input type="text" id="clienteSearch" placeholder="Buscar por nombre o correo..."></div>
                            <select id="filtroRol">
                                <option value="">Todos los roles</option>
                                <option value="admin">Administrador</option>
                                <option value="proveedor">Proveedor</option>
                                <option value="usuario">Usuario</option>
                            </select>
                            <select id="filtroEstado">
                                <option value="">Todos los estados</option>
                                <option value="activo">Activo</option>
                                <option value="inhabilitado">Inhabilitado</option>
                            </select>
                        </div>

                        <div class="table-wrapper" style="overflow: visible;">
                            <table class="data-table">
                                <thead>
                                    <tr>
                                        <th>Usuario</th><th>Rol</th><th>Estado</th><th>Registro</th>
                                        <th style="text-align:right;padding-right:12px">Acciones</th>
                                    </tr>
                                </thead>
                                <tbody id="clientesTableBody"></tbody>
                            </table>
                        </div>

                        <div class="pagination">
                            <div class="pagination-info" id="pageInfo"></div>
                            <div class="pagination-btns" id="paginationBtns"></div>
                        </div>
                    </div>
                </div>

                <!-- ROLES -->
                <div id="section-roles" class="section page-content">
                    <div class="section-header">
                        <div>
                            <div class="section-title">Gestión de Roles</div>
                            <div class="section-sub">Define roles y gestiona quién puede hacer qué.</div>
                        </div>
                        <button class="btn btn-gold" id="btnCrearRol"> Nuevo rol</button>
                    </div>
                    <div class="roles-grid" id="rolesGrid"></div>
                </div>

                <!-- PERMISOS -->
                <div id="section-permisos" class="section page-content">
                    <div class="section-header">
                        <div>
                            <div class="section-title">Gestión de Permisos</div>
                            <div class="section-sub">Asigna permisos específicos a cada rol.</div>
                        </div>
                        <button class="btn btn-gold" id="btnGuardarPermisos">Guardar cambios</button>
                    </div>

                    <div class="card">
                        <div class="perms-header">
                            <div class="perms-role-selector">
                                <label class="form-label" style="margin:0;white-space:nowrap">Rol:</label>
                                <select class="form-control" id="permsRolSelect" style="width:auto"></select>
                            </div>
                        </div>
                        <div class="perms-grid" id="permsGrid"></div>
                    </div>
                </div>

                <!-- HISTORIAL -->
                <div id="section-historial" class="section page-content">
                    <div class="section-header">
                        <div>
                            <div class="section-title">Historial de Acciones</div>
                            <div class="section-sub">Registro de todas las operaciones administrativas.</div>
                        </div>
                    </div>

                    <div class="card">
                        <div class="toolbar">
                            <div class="search-box"><span></span><input type="text" id="histSearch" placeholder="Buscar por admin, usuario o acción..."></div>
                            <select id="histFiltroAccion">
                                <option value="">Todas las acciones</option>
                                <option value="habilitó">Habilitó</option>
                                <option value="inhabilitó">Inhabilitó</option>
                                <option value="eliminó">Eliminó</option>
                                <option value="editó">Editó</option>
                                <option value="cambió rol">Cambió rol</option>
                            </select>
                        </div>
                        <div id="historialLista"></div>
                    </div>
                </div>

                <!-- NOTIFICACIONES -->
                <div id="section-notificaciones" class="section page-content">
                    <div class="section-header">
                        <div>
                            <div class="section-title">Notificaciones</div>
                            <div class="section-sub">Centro de alertas y avisos del sistema.</div>
                        </div>
                        <button class="btn btn-outline btn-sm" onclick="document.querySelectorAll('.notif-item').forEach(n => n.classList.remove('unread'));updateNotifBadge()">
                            Marcar todas como leídas
                        </button>
                    </div>
                    <div class="card">
                        <div class="notif-list" id="notificacionesLista"></div>
                    </div>
                </div>

                <!-- CONFIGURACIÓN -->
                <div id="section-configuracion" class="section page-content">
                    <div class="section-header">
                        <div>
                            <div class="section-title">Configuración</div>
                            <div class="section-sub">Preferencias y ajustes del panel.</div>
                        </div>
                    </div>

                    <div class="row g-3">
                        <div class="col-12">
                            <div class="card">
                                <div class="card-header"><div class="card-title">Cuenta del administrador</div></div>
                                <div class="card-body">
                                    <div class="row g-3">
                                        <div class="col-md-6"><div class="form-group"><label class="form-label">Nombre</label><input type="text" class="form-control" value="<%= nombreUsuario%>"></div></div>
                                        <div class="col-md-6"><div class="form-group"><label class="form-label">Correo</label><input type="email" class="form-control" value="<%= emailUsuario%>"></div></div>
                                        <div class="col-md-6"><div class="form-group"><label class="form-label">Contraseña actual</label><input type="password" class="form-control" placeholder="••••••••"></div></div>
                                        <div class="col-md-6"><div class="form-group"><label class="form-label">Nueva contraseña</label><input type="password" class="form-control" placeholder="••••••••"></div></div>
                                    </div>
                                    <button class="btn btn-gold btn-sm mt-2" onclick="showToast('success', '✅', 'Datos actualizados.')">Actualizar cuenta</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            </main>
        </div>

        <!-- MODALES -->

        <!-- Modal Información Cliente -->
        <div class="modal-overlay" id="modalInfo">
            <div class="modal">
                <div class="modal-header">
                    <div class="modal-title">Información del cliente</div>
                    <button class="modal-close" data-close>✕</button>
                </div>
                <div class="modal-body">
                    <div style="text-align:center;margin-bottom:20px">
                        <div class="client-modal-avatar" id="infoAvatar"></div>
                        <div style="font-size:1.1rem;font-weight:700;color:var(--text-primary)" id="infoNombre"></div>
                    </div>
                    <div class="info-grid">
                        <div class="info-field"><div class="info-label">Correo</div><div class="info-value" id="infoEmail"></div></div>
                        <div class="info-field"><div class="info-label">Teléfono</div><div class="info-value" id="infoTel"></div></div>
                        <div class="info-field"><div class="info-label">Documento</div><div class="info-value" id="infoDoc"></div></div>
                        <div class="info-field"><div class="info-label">Rol</div><div class="info-value" id="infoRol"></div></div>
                        <div class="info-field"><div class="info-label">Estado</div><div class="info-value" id="infoEstado"></div></div>
                        <div class="info-field"><div class="info-label">Fecha de registro</div><div class="info-value" id="infoRegistro"></div></div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-outline" data-close>Cerrar</button>
                </div>
            </div>
        </div>



        <!-- Modal Agregar Producto -->
        <div class="modal-overlay" id="modalAgregarProducto">
            <div class="modal">
                <div class="modal-header">
                    <div class="modal-title">Agregar Producto</div>
                    <button class="modal-close" data-close>✕</button>
                </div>
                <div class="modal-body">
                    <!-- Banner de Notificación de Errores Internos -->
                    <div id="msgAgregarProducto" class="mb-3" style="display:none; color: #f87171; background-color: rgba(239, 68, 68, 0.15); border: 1px solid rgba(239, 68, 68, 0.3); padding: 0.65rem 0.85rem; border-radius: 6px; font-size: 0.85rem; font-weight: 500;"></div>

                    <div class="row g-3">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label">Nombre del Producto</label>
                                <input type="text" class="form-control" id="addNombreProducto" placeholder="Ej: Poker Cerveza 330ml">
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label">Código</label>
                                <input type="text" class="form-control" id="addCodigoProducto" placeholder="Ej: PROD-001">
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label">Cantidad (Stock)</label>
                                <input type="number" class="form-control" id="addCantidadProducto" min="0" value="0">
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label">Proveedor</label>
                                <select class="form-control" id="addProveedorProducto">
                                    <option value="">-- Seleccione un Proveedor --</option>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label">Fecha de Ingreso <span style="font-size: 0.75rem; color: var(--text-muted);">(Automático)</span></label>
                                <input type="date" class="form-control" id="addFechaIngresoProducto" readonly style="background-color: rgba(255, 255, 255, 0.04); cursor: not-allowed;">
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="form-label">Fecha de Vencimiento</label>
                                <input type="date" class="form-control" id="addFechaVencimientoProducto">
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-outline" data-close>Cancelar</button>
                    <button class="btn btn-gold" id="btnGuardarProducto">Guardar Producto</button>
                </div>
            </div>
        </div>

        <!-- Modal Confirmar Eliminar Producto -->
        <div class="modal-overlay" id="modalEliminarProducto">
            <div class="modal modal-sm">
                <div class="modal-header">
                    <div class="modal-title" style="color: #e74c3c;"> Eliminar Producto</div>
                    <button class="modal-close" data-close>✕</button>
                </div>
                <div class="modal-body">
                    <div class="modal-warning-text">
                        ¿Estás seguro de que deseas eliminar el producto <strong id="eliminarProdNombre" style="color:var(--text-primary)"></strong>?<br><br>
                        <span style="font-size:0.8rem; color:var(--text-muted)">Esta acción eliminará el registro del inventario y quedará guardada en el historial.</span>
                    </div>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-outline" data-close>Cancelar</button>
                    <button class="btn btn-danger" id="btnConfirmarEliminarProd">Sí, eliminar</button>
                </div>
            </div>
        </div>

        <!-- Modal Ajustar Stock -->
        <div class="modal-overlay" id="modalAjustarStock">
            <div class="modal modal-sm">
                <div class="modal-header">
                    <div class="modal-title">Ajustar Stock: <span id="stockNombreProd" style="color:var(--gold)"></span></div>
                    <button class="modal-close" data-close>✕</button>
                </div>
                <div class="modal-body">
                    <p style="font-size:0.85rem; color:var(--text-secondary); margin-bottom: 12px;">
                        Stock actual: <strong id="stockActualProd" style="color:var(--text-primary)">0</strong> unidades
                    </p>
                    <div class="form-group mb-2">
                        <label class="form-label">Cantidad a modificar</label>
                        <input type="number" class="form-control" id="inputCantAjuste" min="1" value="1">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Motivo / Descripción</label>
                        <input type="text" class="form-control" id="inputMotivoAjuste" placeholder="Ej: Compra de lote, Merma, Ajuste de conteo">
                    </div>
                </div>
                <div class="modal-footer d-flex justify-content-between">
                    <button class="btn btn-danger btn-sm" onclick="procesarAjusteStock('RETIRO_STOCK')">- Retirar Stock</button>
                    <button class="btn btn-success btn-sm" onclick="procesarAjusteStock('INGRESO_STOCK')">+ Ingresar Stock</button>
                </div>
            </div>
        </div>

        <!-- Modal Cambiar Rol -->
        <div class="modal-overlay" id="modalCambiarRol">
            <div class="modal modal-sm">
                <div class="modal-header">
                    <div class="modal-title">Cambiar rol</div>
                    <button class="modal-close" data-close>✕</button>
                </div>
                <div class="modal-body">
                    <p style="color:var(--text-secondary);font-size:0.86rem;margin-bottom:14px">
                        Cambiar el rol de <strong id="cambioRolNombre" style="color:var(--text-primary)"></strong>:
                    </p>
                    <div class="form-group">
                        <label class="form-label">Nuevo rol</label>
                        <select class="form-control" id="nuevoRol">
                            <option value="admin">Administrador</option>
                            <option value="proveedor">Proveedor</option>
                            <option value="usuario">Usuario</option>
                        </select>
                    </div>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-outline" data-close>Cancelar</button>
                    <button class="btn btn-gold" id="btnConfirmarRol">Confirmar</button>
                </div>
            </div>
        </div>

        <!-- Modal Habilitar Usuario -->
        <div class="modal-overlay" id="modalHabilitar">
            <div class="modal modal-sm">
                <div class="modal-header">
                    <div class="modal-title">Habilitar usuario</div>
                    <button class="modal-close" data-close>✕</button>
                </div>
                <div class="modal-body" style="text-align:center">
                    <div class="modal-warning-text">
                        ¿Habilitar a <strong id="habilitarNombre"></strong>?<br><br>
                        El usuario podrá volver a acceder al sistema.
                    </div>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-outline" data-close>Cancelar</button>
                    <button class="btn btn-success" id="btnHabilitar">Habilitar</button>
                </div>
            </div>
        </div>

        <!-- Modal Inhabilitar Usuario -->
        <div class="modal-overlay" id="modalInhabilitar">
            <div class="modal modal-sm">
                <div class="modal-header">
                    <div class="modal-title">Inhabilitar usuario</div>
                    <button class="modal-close" data-close>✕</button>
                </div>
                <div class="modal-body">
                    <div class="modal-warning-text">
                        ¿Inhabilitar a <strong id="inhabilitarNombre"></strong>?<br><br>
                        El usuario dejará de poder acceder al sistema.
                    </div>
                    <div class="confirm-input-wrapper">
                        <label class="form-label">Motivo (opcional)</label>
                        <textarea class="form-control" id="motivoInhab" rows="3" placeholder="Describe el motivo de la inhabilitación..."></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-outline" data-close>Cancelar</button>
                    <button class="btn btn-danger" id="btnInhabilitar">Inhabilitar</button>
                </div>
            </div>
        </div>

        <!-- Modal Crear Rol -->
        <div class="modal-overlay" id="modalCrearRol">
            <div class="modal modal-sm">
                <div class="modal-header">
                    <div class="modal-title">Crear Nuevo Rol</div>
                    <button class="modal-close" data-close>✕</button>
                </div>
                <div class="modal-body">
                    <div class="form-group mb-2">
                        <label class="form-label">Nombre del Rol</label>
                        <input type="text" class="form-control" id="nuevoRolNombre" placeholder="Ej: Supervisor">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Descripción</label>
                        <textarea class="form-control" id="nuevoRolDescripcion" rows="2" placeholder="Descripción breve de responsabilidades..."></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-outline" data-close>Cancelar</button>
                    <button class="btn btn-gold" id="btnCrearRolConfirmar">Crear Rol</button>
                </div>
            </div>
        </div>

        <!-- Modal Editar Rol -->
        <div class="modal-overlay" id="modalEditarRol">
            <div class="modal modal-sm">
                <div class="modal-header">
                    <div class="modal-title">Editar rol: <span id="editRolNombreActual" style="color:var(--gold)"></span></div>
                    <button class="modal-close" data-close>✕</button>
                </div>
                <div class="modal-body">
                    <div class="form-group">
                        <label class="form-label">Nombre del rol</label>
                        <input type="text" class="form-control" id="editRolNombre">
                    </div>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-outline" data-close>Cancelar</button>
                    <button class="btn btn-gold" onclick="showToast('success', 'Rol actualizado.');closeModal('modalEditarRol');renderRoles()">Guardar</button>
                </div>
            </div>
        </div>

        <!-- Modal Logout -->
        <div class="modal-overlay" id="modalLogout">
            <div class="modal modal-sm">
                <div class="modal-header">
                    <div class="modal-title">Cerrar sesión</div>
                    <button class="modal-close" data-close>✕</button>
                </div>
                <div class="modal-body">
                    <p class="modal-warning-text">¿Estás seguro de que deseas <strong>cerrar sesión</strong> en el sistema?</p>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-outline" data-close>Cancelar</button>
                    <button class="btn btn-danger" id="btnConfirmarLogout">Sí, cerrar sesión</button>
                </div>
            </div>
        </div>

        <!-- TOAST CONTAINER -->
        <div id="toast-container"></div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

        <script>
            window.CONTEXT_PATH = '${pageContext.request.contextPath}';
        </script>
        <script src="${pageContext.request.contextPath}/js/admin.js?v=1.5"></script>

    </body>
</html>