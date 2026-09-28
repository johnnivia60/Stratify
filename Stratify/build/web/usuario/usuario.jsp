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
        <title>Stratify — Panel de Usuario</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    </head>
    <body>

        <header class="topbar">
            <div class="user-area">
                <div class="avatar"><%= inicial%></div>
                <div class="user-dropdown-wrapper">
                    <!-- Botón corregido con fondo transparente y texto claro -->
                    <button class="user-name-btn btn btn-dark border-0 bg-transparent text-light d-flex align-items-center gap-1" id="userNameBtn" type="button" data-bs-toggle="modal" data-bs-target="#modalEditarPerfil">
                        <span id="usuarioNombre"><%= nombreUsuario%> <%= apellidoUsuario%></span>
                        <i class="bi bi-pencil-square ms-1 text-warning"></i>
                    </button>
                </div>
                <a href="${pageContext.request.contextPath}/LogoutServlet" class="btn-logout" type="button">
                    <i class="bi bi-box-arrow-right"></i>
                    Salir
                </a>
            </div>
        </header>
        <main class="container-fluid main-container">

            <section class="welcome-section">
                <h1>Bienvenido a <span>Stratify</span></h1>
                <p>Gestiona y consulta la información de tu inventario de manera sencilla.</p>
            </section>

            <ul class="nav nav-pills custom-tabs" id="usuarioTabs" role="tablist">
                <li class="nav-item" role="presentation">
                    <button class="nav-link active" id="inventario-tab"
                            data-bs-toggle="pill" data-bs-target="#inventario"
                            type="button" role="tab">
                        <i class="bi bi-box-seam"></i>
                        Inventario
                    </button>
                </li>
                <li class="nav-item" role="presentation">
                    <button class="nav-link" id="proveedores-tab"
                            data-bs-toggle="pill" data-bs-target="#proveedores"
                            type="button" role="tab">
                        <i class="bi bi-person"></i>
                        Proveedores
                    </button>
                </li>
                <li class="nav-item" role="presentation">
                    <button class="nav-link" id="historial-tab"
                            data-bs-toggle="pill" data-bs-target="#historial"
                            type="button" role="tab">
                        <i class="bi bi-clock-history"></i>
                        Historial
                    </button>
                </li>
            </ul>

            <div class="tab-content" id="usuarioTabsContent">

                <!-- INVENTARIO -->
                <section class="tab-pane fade show active" id="inventario" role="tabpanel">
                    <div class="section-header">
                        <div>
                            <h2><span class="gold-dot"></span> Inventario <small id="inv-count"></small></h2>
                        </div>
                        <div class="search-box">
                            <i class="bi bi-search"></i>
                            <input id="inv-search" type="search" placeholder="Buscar producto...">
                        </div>
                    </div>

                    <div id="inv-grid" class="products-grid"></div>

                    <!-- Formulario modificar stock -->
                    <div class="form-card mt-4">
                        <div class="form-title">
                            <i class="bi bi-arrow-left-right"></i>
                            <h3>Modificar Stock</h3>
                        </div>
                        <div class="row g-3">
                            <div class="col-md-4">
                                <label for="stockProductoCodigo">Producto</label>
                                <select id="stockProductoCodigo">
                                    <option value="">Selecciona un producto...</option>
                                </select>
                            </div>
                            <div class="col-md-4">
                                <label for="stockCantidad">Cantidad</label>
                                <input id="stockCantidad" type="number" min="1" placeholder="Cantidad">
                            </div>
                            <div class="col-md-4">
                                <label for="stockTipo">Acción</label>
                                <input id="stockTipo" type="text" value="Retiro de stock" readonly>
                            </div>
                        </div>
                        <button class="btn-primary-gold mt-3" type="button" id="btnModificarStock">
                            <i class="bi bi-arrow-repeat"></i>
                            Aplicar cambio de stock
                        </button>
                        <div id="stockMsg" class="mt-2"></div>
                    </div>
                </section>

                <!-- PROVEEDORES -->
                <section class="tab-pane fade" id="proveedores" role="tabpanel">
                    <div class="section-header">
                        <div>
                            <h2><span class="gold-dot"></span> Proveedores <small id="prov-count"></small></h2>
                        </div>
                        <div class="search-box">
                            <i class="bi bi-search"></i>
                            <input id="prov-search" type="search" placeholder="Buscar proveedor...">
                        </div>
                    </div>
                    <div id="prov-grid" class="providers-grid"></div>
                </section>

                <!-- HISTORIAL -->
                <section class="tab-pane fade" id="historial" role="tabpanel">
                    <div class="section-header">
                        <div>
                            <h2><span class="gold-dot"></span> Historial de cambios <small id="hist-count"></small></h2>
                        </div>
                    </div>
                    <div id="hist-timeline" class="history-list"></div>
                </section>

            </div>
        </main>

        <!-- MODAL: Editar Perfil -->
        <div class="modal fade" id="modalEditarPerfil" tabindex="-1" aria-labelledby="modalEditarPerfilLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content modal-dark">
                    <div class="modal-header border-secondary">
                        <h5 class="modal-title" id="modalEditarPerfilLabel">
                            <i class="bi bi-person-circle text-warning me-2"></i>
                            Editar Perfil
                        </h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Cerrar"></button>
                    </div>
                    <div class="modal-body">
                        <div id="profileFormMsg" class="profile-msg mb-3" style="display:none;"></div>

                        <div class="row g-3">
                            <div class="col-md-6">
                                <label class="form-label" for="editNombre">Nombre</label>
                                <input class="form-control input-dark" id="editNombre" type="text" placeholder="Nombre" value="<%= nombreUsuario%>">
                                <span class="text-danger small mt-1 d-block" id="editNombreError"></span>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label" for="editApellido">Apellido</label>
                                <input class="form-control input-dark" id="editApellido" type="text" placeholder="Apellido" value="<%= apellidoUsuario%>">
                                <span class="text-danger small mt-1 d-block" id="editApellidoError"></span>
                            </div>
                            <div class="col-12">
                                <label class="form-label" for="editEmail">Correo electrónico</label>
                                <input class="form-control input-dark" id="editEmail" type="email" placeholder="correo@ejemplo.com">
                                <span class="text-danger small mt-1 d-block" id="editEmailError"></span>
                            </div>
                            <div class="col-12">
                                <label class="form-label" for="editPassword">Nueva contraseña</label>
                                <div class="input-group">
                                    <input class="form-control input-dark" id="editPassword" type="password" placeholder="Dejar vacío para no cambiar">
                                    <button class="btn btn-outline-secondary profile-toggle-pass" type="button" data-target="editPassword">
                                        <i class="bi bi-eye"></i>
                                    </button>
                                </div>
                            </div>
                            <div class="col-12">
                                <label class="form-label" for="editConfirmPassword">Confirmar contraseña</label>
                                <div class="input-group">
                                    <input class="form-control input-dark" id="editConfirmPassword" type="password" placeholder="Repite la contraseña">
                                    <button class="btn btn-outline-secondary profile-toggle-pass" type="button" data-target="editConfirmPassword">
                                        <i class="bi bi-eye"></i>
                                    </button>
                                </div>
                                <span class="text-danger small mt-1 d-block" id="editPassError"></span>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer border-secondary">
                        <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button class="btn btn-warning fw-bold" id="btnGuardarPerfil" type="button">
                            <i class="bi bi-check2-circle me-1"></i> Guardar cambios
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- MODAL: Productos del proveedor y solicitar -->
        <div class="modal fade" id="modalSolicitudProducto" tabindex="-1" aria-labelledby="modalSolicitudLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered modal-lg">
                <div class="modal-content modal-dark">
                    <div class="modal-header border-secondary">
                        <h5 class="modal-title" id="modalSolicitudLabel">
                            <i class="bi bi-box-seam text-warning me-2"></i>
                            Productos de <span id="modalNombreProveedor" class="text-warning"></span>
                        </h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Cerrar"></button>
                    </div>
                    <div class="modal-body" id="modalProductosProveedorBody">
                        <!-- Productos cargados dinámicamente con botón solicitar y casilla de cantidad -->
                    </div>
                    <div class="modal-footer border-secondary">
                        <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cerrar</button>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            window.CONTEXT_PATH = '<%= request.getContextPath()%>';
        </script>
        <script src="${pageContext.request.contextPath}/js/app.js"></script>
    </body>
</html>