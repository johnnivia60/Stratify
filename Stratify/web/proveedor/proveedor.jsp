<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%
    // Control de caché para prevenir navegación con botón "Atrás" tras cerrar sesión
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    HttpSession userSession = request.getSession(false);
    if (userSession == null || userSession.getAttribute("id_usuario") == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp?error=sesion");
        return;
    }

    String nombreUsuario = (String) userSession.getAttribute("nombre");
    String apellidoUsuario = (String) userSession.getAttribute("apellido");
    if (nombreUsuario == null) {
        nombreUsuario = "Usuario";
    }
    if (apellidoUsuario == null) {
        apellidoUsuario = "";
    }

    String inicial = nombreUsuario.length() > 0 ? String.valueOf(nombreUsuario.charAt(0)).toUpperCase() : "U";
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Stratify — Gestión de Proveedores</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    </head>
    <body>

        <header class="topbar">
            <div class="user-area">
                <div class="avatar"><%= inicial%></div>
                <span id="usuarioNombre"><%= nombreUsuario%> <%= apellidoUsuario%></span>
                <a href="${pageContext.request.contextPath}/LogoutServlet" class="btn-logout">
                    <i class="bi bi-box-arrow-right"></i>
                    Salir
                </a>
            </div>
        </header>

        <main class="container-fluid main-container">

            <section class="welcome-section">
                <h1>Gestión de <span>Proveedores</span></h1>
                <p>Administra las solicitudes entrantes y la información de contacto de tus proveedores.</p>
            </section>

            <!-- ÚNICAMENTE 2 PESTAÑAS -->
            <ul class="nav nav-pills custom-tabs" id="proveedorTabs" role="tablist">
                <li class="nav-item" role="presentation">
                    <button class="nav-link active" id="solicitudes-tab"
                            data-bs-toggle="pill" data-bs-target="#solicitudes-pendientes"
                            type="button" role="tab">
                        <i class="bi bi-inbox-fill me-1"></i>
                        Solicitudes Pendientes
                    </button>
                </li>
                <li class="nav-item" role="presentation">
                    <button class="nav-link" id="nuevo-proveedor-tab"
                            data-bs-toggle="pill" data-bs-target="#nuevo-proveedor"
                            type="button" role="tab">
                        <i class="bi bi-person-plus"></i>
                        Registrar Proveedor
                    </button>
                </li>
            </ul>

            <div class="tab-content" id="proveedorTabsContent">

                <!-- 1. SOLICITUDES PENDIENTES (SECCIÓN ACTIVA POR DEFECTO) -->
                <section class="tab-pane fade show active" id="solicitudes-pendientes" role="tabpanel">
                    <div class="section-header">
                        <div>
                            <h2><span class="gold-dot"></span> Solicitudes de Pedidos Entrantes <small id="sol-count"></small></h2>
                        </div>
                        <div class="search-box">
                            <i class="bi bi-search"></i>
                            <input id="sol-search" type="search" placeholder="Buscar por cliente o producto...">
                        </div>
                    </div>

                    <div id="solicitudes-grid" class="mt-3"></div>
                </section>

                <!-- REGISTRAR PROVEEDOR -->
                <section class="tab-pane fade" id="nuevo-proveedor" role="tabpanel">
                    <div class="form-card mt-3">
                        <div class="form-title text-warning">
                            <i class="bi bi-building-add fs-4 me-2"></i>
                            <h3 class="d-inline mb-0 text-light">Nuevo Proveedor</h3>
                        </div>
                        <form id="formCrearProveedor" action="${pageContext.request.contextPath}/ProveedorServlet" method="POST" class="mt-3">
                            <input type="hidden" name="accion" value="crear">

                            <div class="row g-3">
                                <div class="col-md-6">
                                    <label for="provNit" class="form-label text-light font-medium">NIT / Documento</label>
                                    <input type="text" class="form-control input-dark" id="provNit" name="nit_proveedor" placeholder="Ej: 900123456-1" required autocomplete="off">
                                </div>
                                <div class="col-md-6">
                                    <label for="provNombre" class="form-label text-light font-medium">Razón Social / Nombre</label>
                                    <input type="text" class="form-control input-dark" id="provNombre" name="nombre_proveedor" placeholder="Nombre de la empresa o proveedor" required autocomplete="off">
                                </div>
                                <div class="col-md-4">
                                    <label for="provTelefono" class="form-label text-light font-medium">Teléfono de Contacto</label>
                                    <input type="tel" class="form-control input-dark" id="provTelefono" name="telefono_proveedor" placeholder="Ej: 3001234567" required>
                                </div>
                                <div class="col-md-4">
                                    <label for="provEmail" class="form-label text-light font-medium">Correo Electrónico</label>
                                    <input type="email" class="form-control input-dark" id="provEmail" name="email_proveedor" placeholder="contacto@proveedor.com">
                                </div>
                                <div class="col-md-4">
                                    <label for="provDireccion" class="form-label text-light font-medium">Dirección</label>
                                    <input type="text" class="form-control input-dark" id="provDireccion" name="direccion_proveedor" placeholder="Ej: Calle 10 # 20-30">
                                </div>
                            </div>

                            <button class="btn-primary-gold mt-4 py-2 px-4 fw-bold" type="submit" id="btnGuardarProveedor">
                                <i class="bi bi-save me-1"></i>
                                Guardar Proveedor
                            </button>
                            <div id="provMsg" class="mt-2 fw-semibold"></div>
                        </form>
                    </div>
                </section>
            </div>
        </main>
        <!-- MODAL DE NOTIFICACIÓN DE SOLICITUDES -->
        <div class="modal fade" id="modalRespuestaProveedor" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered modal-sm">
                <div class="modal-content text-center p-3" style="background-color: #141417; border: 1px solid #232328; border-radius: 12px; color: #ffffff;">
                    <div class="modal-body">
                        <div id="modalRespuestaIcono" class="mb-3 fs-1"></div>
                        <h5 id="modalRespuestaTitulo" class="fw-bold mb-2 text-white"></h5>
                        <p id="modalRespuestaMensaje" class="small mb-4" style="color: #a1a1aa !important;"></p>
                        <button type="button" class="btn btn-warning w-100 fw-bold py-2" data-bs-dismiss="modal">Entendido</button>
                    </div>
                </div>
            </div>
        </div>
        <!-- MODAL DE CONFIRMACIÓN PERSONALIZADO (Elimina el confirm de localhost) -->
        <div class="modal fade" id="modalConfirmarAccion" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered modal-sm">
                <div class="modal-content text-center p-3" style="background-color: #141417; border: 1px solid #232328; border-radius: 12px; color: #ffffff;">
                    <div class="modal-body">
                        <div class="mb-3 fs-1 text-warning">
                            <i class="bi bi-question-circle-fill"></i>
                        </div>
                        <h5 id="modalConfirmarTitulo" class="fw-bold mb-2 text-white">¿Estás seguro?</h5>
                        <p id="modalConfirmarMensaje" class="small mb-4" style="color: #a1a1aa !important;"></p>
                        <div class="d-flex gap-2">
                            <button type="button" class="btn btn-outline-secondary flex-grow-1" data-bs-dismiss="modal">Cancelar</button>
                            <button type="button" id="btnEjecutarConfirmacion" class="btn btn-warning flex-grow-1 fw-bold">Confirmar</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            window.CONTEXT_PATH = '<%= request.getContextPath()%>';
        </script>
        <script src="${pageContext.request.contextPath}/js/proveedor.js"></script>
    </body>
</html>