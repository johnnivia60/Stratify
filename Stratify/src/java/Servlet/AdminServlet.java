package Servlet;

import Controlador.UsuarioDAO;
import Controlador.HistorialDAO;
import Controlador.ProductoDAO;
import Controlador.ProveedorDAO;
import Modelo.Usuario;
import Modelo.Producto;
import Modelo.Proveedor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "AdminServlet", urlPatterns = {"/AdminServlet"})
public class AdminServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("id_usuario") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.write("{\"status\":\"error\",\"mensaje\":\"No autorizado\"}");
            return;
        }

        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "";
        }

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        HistorialDAO historialDAO = new HistorialDAO();

        switch (accion) {
            case "listarClientes":
                List<Usuario> listaUsuarios = usuarioDAO.listarUsuarios();
                out.write(convertirUsuariosAJson(listaUsuarios));
                break;

            case "listarHistorial":
                // Muestra tanto acciones de Admin como movimientos de Usuarios
                List<Map<String, Object>> historial = historialDAO.listarHistorialAdmin();
                out.write(convertirHistorialAJson(historial));
                break;

            case "listarNotificaciones":
                String jsonNotis = "["
                        + "{\"id\": 1, \"titulo\": \"Alerta del Sistema\", \"desc\": \"Nuevo usuario registrado en el sistema.\", \"tiempo\": \"Hace 10 min\", \"icono\": \"🔔\", \"tipo\": \"blue\", \"leida\": false},"
                        + "{\"id\": 2, \"titulo\": \"Inventario\", \"desc\": \"El stock de un producto ha llegado al mínimo.\", \"tiempo\": \"Hace 1 hora\", \"icono\": \"📦\", \"tipo\": \"gold\", \"leida\": true}"
                        + "]";
                out.write(jsonNotis);
                break;

            case "obtenerEstadisticas":
                int total = usuarioDAO.contarUsuariosTotal();
                int activos = usuarioDAO.contarUsuariosPorEstado(true);
                int inhabilitados = usuarioDAO.contarUsuariosPorEstado(false);
                int admins = usuarioDAO.contarUsuariosPorRol(3);

                String jsonStats = "{"
                        + "\"totalClientes\":" + total + ","
                        + "\"clientesActivos\":" + activos + ","
                        + "\"inhabilitados\":" + inhabilitados + ","
                        + "\"administradores\":" + admins + ","
                        + "\"eliminados\":0"
                        + "}";
                out.write(jsonStats);
                break;

            case "listarProductos":
                ProductoDAO pDAO = new ProductoDAO();
                List<Producto> listaProductos = pDAO.listarProductos();

                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < listaProductos.size(); i++) {
                    Producto p = listaProductos.get(i);
                    json.append("{")
                            .append("\"id\":").append(p.getId_producto()).append(",")
                            .append("\"codigo\":\"").append(p.getCodigo_producto() != null ? escapeJson(p.getCodigo_producto()) : "").append("\",")
                            .append("\"nombre\":\"").append(p.getNombre_producto() != null ? escapeJson(p.getNombre_producto()) : "").append("\",")
                            .append("\"stock\":").append(p.getStock()).append(",")
                            .append("\"proveedor\":\"").append(p.getNombreProveedor() != null ? escapeJson(p.getNombreProveedor()) : "").append("\",")
                            .append("\"fechaIngreso\":\"").append(p.getFecha_ingreso() != null ? p.getFecha_ingreso().toString() : "").append("\",")
                            .append("\"fechaVencimiento\":\"").append(p.getFecha_vencimiento() != null ? p.getFecha_vencimiento().toString() : "N/A").append("\"")
                            .append("}");
                    if (i < listaProductos.size() - 1) {
                        json.append(",");
                    }
                }
                json.append("]");
                out.write(json.toString());
                break;

            case "listarProveedores":
                ProveedorDAO proveedorDAO = new ProveedorDAO();
                List<Proveedor> listaProv = proveedorDAO.listarProveedores();

                StringBuilder jsonProv = new StringBuilder("[");
                for (int i = 0; i < listaProv.size(); i++) {
                    Proveedor pr = listaProv.get(i);
                    jsonProv.append("{")
                            .append("\"id\":").append(pr.getId_proveedor()).append(",")
                            .append("\"nombre\":\"").append(pr.getNombre_proveedor() != null ? escapeJson(pr.getNombre_proveedor()) : "").append("\"")
                            .append("}");
                    if (i < listaProv.size() - 1) {
                        jsonProv.append(",");
                    }
                }
                jsonProv.append("]");
                out.write(jsonProv.toString());
                break;

            default:
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"status\":\"error\",\"mensaje\":\"Acción no válida\"}");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("id_usuario") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.write("{\"status\":\"error\",\"mensaje\":\"No autorizado\"}");
            return;
        }

        String accion = request.getParameter("accion");
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        HistorialDAO historialDAO = new HistorialDAO();

        Integer idAdminObj = (Integer) session.getAttribute("id_usuario");
        int idAdmin = idAdminObj != null ? idAdminObj : 1;

        String nombreAdmin = (String) session.getAttribute("nombre");
        if (nombreAdmin == null || nombreAdmin.trim().isEmpty()) {
            nombreAdmin = "Admin";
        }

        if ("cambiarEstado".equals(accion)) {
            int idUsuario = Integer.parseInt(request.getParameter("id"));
            boolean activar = Boolean.parseBoolean(request.getParameter("activar"));

            boolean resultado = activar ? usuarioDAO.activarUsuario(idUsuario) : usuarioDAO.inactivarUsuario(idUsuario);

            if (resultado) {
                out.write("{\"status\":\"success\",\"mensaje\":\"Estado actualizado correctamente\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.write("{\"status\":\"error\",\"mensaje\":\"No se pudo actualizar el estado\"}");
            }
        } else if ("eliminarUsuario".equals(accion)) {
            int idUsuario = Integer.parseInt(request.getParameter("id"));
            boolean resultado = usuarioDAO.eliminarUsuario(idUsuario);

            if (resultado) {
                out.write("{\"status\":\"success\",\"mensaje\":\"Usuario eliminado correctamente\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.write("{\"status\":\"error\",\"mensaje\":\"No se pudo eliminar el usuario\"}");
            }
        } else if ("cambiarRol".equals(accion)) {
            int idUsuario = Integer.parseInt(request.getParameter("id"));
            String rolStr = request.getParameter("rol");

            int rolId = 1;
            if ("admin".equals(rolStr)) {
                rolId = 3;
            } else if ("proveedor".equals(rolStr)) {
                rolId = 2;
            } else if ("usuario".equals(rolStr)) {
                rolId = 1;
            }

            Usuario u = usuarioDAO.consultarUsuarioPorId(idUsuario);
            if (u != null) {
                u.setRol_id_rol(rolId);
                boolean resultado = usuarioDAO.actualizarUsuario(u);
                if (resultado) {
                    out.write("{\"status\":\"success\",\"mensaje\":\"Rol actualizado correctamente\"}");
                    return;
                }
            }
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"status\":\"error\",\"mensaje\":\"No se pudo cambiar el rol\"}");
        } else if ("editarUsuario".equals(accion)) {
            int idUsuario = Integer.parseInt(request.getParameter("id"));
            String nombreCompleto = request.getParameter("nombre");
            String email = request.getParameter("email");
            String rolStr = request.getParameter("rol");

            int rolId = 1;
            if ("admin".equals(rolStr)) {
                rolId = 3; 
            } else if ("proveedor".equals(rolStr)) {
                rolId = 2;
            } else if ("usuario".equals(rolStr)) {
                rolId = 1;
            }

            Usuario u = usuarioDAO.consultarUsuarioPorId(idUsuario);
            if (u != null) {
                u.setEmail(email);
                u.setRol_id_rol(rolId);

                String[] partes = nombreCompleto != null ? nombreCompleto.split(" ", 2) : new String[]{"", ""};
                u.setNombre(partes[0]);
                u.setApellido(partes.length > 1 ? partes[1] : "");

                boolean resultado = usuarioDAO.actualizarUsuario(u);
                if (resultado) {
                    out.write("{\"status\":\"success\",\"mensaje\":\"Usuario actualizado correctamente\"}");
                    return;
                }
            }
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"status\":\"error\",\"mensaje\":\"No se pudo actualizar el usuario\"}");
        } else if ("agregarProducto".equals(accion)) {
            try {
                String nombre = request.getParameter("nombre");
                String codigo = request.getParameter("codigo");

                String cantidadStr = request.getParameter("cantidad");
                int cantidad = (cantidadStr != null && !cantidadStr.trim().isEmpty()) ? Integer.parseInt(cantidadStr.trim()) : 0;

                String proveedorStr = request.getParameter("proveedor");
                if (proveedorStr == null || proveedorStr.trim().isEmpty()) {
                    out.write("{\"status\":\"error\",\"mensaje\":\"Debes seleccionar un proveedor válido.\"}");
                    return;
                }
                int idProveedor = Integer.parseInt(proveedorStr.trim());

                String fechaIngresoStr = request.getParameter("fechaIngreso");
                String fechaVencimientoStr = request.getParameter("fechaVencimiento");

                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                java.util.Date fechaIngreso = (fechaIngresoStr != null && !fechaIngresoStr.trim().isEmpty())
                        ? sdf.parse(fechaIngresoStr.trim()) : new java.util.Date();

                java.util.Date fechaVencimiento = (fechaVencimientoStr != null && !fechaVencimientoStr.trim().isEmpty())
                        ? sdf.parse(fechaVencimientoStr.trim()) : null;

                Producto p = new Producto();
                p.setNombre_producto(nombre);
                p.setCodigo_producto(codigo);
                p.setStock(cantidad);
                p.setProveedor_id_proveedor(idProveedor);
                p.setFecha_ingreso(fechaIngreso);
                p.setFecha_vencimiento(fechaVencimiento);

                p.setCosto_unitario(java.math.BigDecimal.ZERO);
                p.setEstado(true);

                ProductoDAO productoDAO = new ProductoDAO();
                String resultado = productoDAO.InsertarProducto(p);

                if ("OK".equals(resultado)) {
                    // REGISTRO CORRECTO EN LA TABLA HISTORIAL_ADMIN
                    historialDAO.registrarAccionAdmin("agregó el producto", nombreAdmin, nombre);
                    out.write("{\"status\":\"success\",\"mensaje\":\"Producto registrado correctamente\"}");
                } else {
                    out.write("{\"status\":\"error\",\"mensaje\":\"" + escapeJson(resultado) + "\"}");
                }
            } catch (Exception e) {
                e.printStackTrace();
                out.write("{\"status\":\"error\",\"mensaje\":\"Error procesando la solicitud: " + escapeJson(e.getMessage()) + "\"}");
            }
        } else if ("modificarStock".equals(accion)) {
            try {
                int idProducto = Integer.parseInt(request.getParameter("idProducto"));
                int cantidad = Integer.parseInt(request.getParameter("cantidad"));
                String tipoAccion = request.getParameter("tipoAccion");
                String motivo = request.getParameter("motivo");

                ProductoDAO pDAO = new ProductoDAO();
                Producto prod = pDAO.consultarProductoPorId(idProducto);

                if (prod != null) {
                    boolean ok = pDAO.modificarStock(
                        prod.getInventario_id_inventario(), 
                        cantidad, 
                        tipoAccion, 
                        idAdmin, 
                        idProducto, 
                        motivo
                    );

                    if (ok) {
                        out.write("{\"status\":\"success\",\"mensaje\":\"Stock actualizado correctamente\"}");
                    } else {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        out.write("{\"status\":\"error\",\"mensaje\":\"No se pudo actualizar el stock en base de datos\"}");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.write("{\"status\":\"error\",\"mensaje\":\"Producto no encontrado\"}");
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.write("{\"status\":\"error\",\"mensaje\":\"" + escapeJson(e.getMessage()) + "\"}");
            }
        } else if ("eliminarProducto".equals(accion)) {
            try {
                int idProducto = Integer.parseInt(request.getParameter("id"));
                ProductoDAO pDAO = new ProductoDAO();

                Producto prodObj = pDAO.consultarProductoPorId(idProducto);
                String nombreProd = (prodObj != null && prodObj.getNombre_producto() != null) ? prodObj.getNombre_producto() : "ID #" + idProducto;

                boolean ok = pDAO.eliminarProducto(idProducto);

                if (ok) {
                    // REGISTRO CORRECTO EN LA TABLA HISTORIAL_ADMIN
                    historialDAO.registrarAccionAdmin("eliminó el producto", nombreAdmin, nombreProd);
                    out.write("{\"status\":\"success\",\"mensaje\":\"Producto eliminado correctamente\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.write("{\"status\":\"error\",\"mensaje\":\"No se pudo eliminar el registro de producto\"}");
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.write("{\"status\":\"error\",\"mensaje\":\"" + escapeJson(e.getMessage()) + "\"}");
            }
        }
    }

    private String convertirUsuariosAJson(List<Usuario> lista) {
        if (lista == null) {
            return "[]";
        }

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            Usuario u = lista.get(i);

            String nombre = u.getNombre() != null ? u.getNombre() : "";
            String apellido = u.getApellido() != null ? u.getApellido() : "";
            String nombreCompleto = (nombre + " " + apellido).trim();

            int idRol = u.getRol_id_rol();
            String rolStr = (idRol == 3) ? "admin" : (idRol == 2 ? "proveedor" : (idRol == 1 ? "usuario" : "desconocido"));

            String doc = u.getNumero_documento() != null ? u.getNumero_documento() : "";
            String email = u.getEmail() != null ? u.getEmail() : "";

            json.append("{")
                    .append("\"id\":").append(u.getId_usuario()).append(",")
                    .append("\"nombre\":\"").append(escapeJson(nombreCompleto)).append("\",")
                    .append("\"email\":\"").append(escapeJson(email)).append("\",")
                    .append("\"telefono\":\"").append(escapeJson(doc.isEmpty() ? "—" : doc)).append("\",")
                    .append("\"documento\":\"").append(escapeJson(doc)).append("\",")
                    .append("\"rol\":\"").append(rolStr).append("\",")
                    .append("\"estado\":\"").append(u.isEstado() ? "activo" : "inhabilitado").append("\",")
                    .append("\"registro\":\"—\",")
                    .append("\"acceso\":\"—\"")
                    .append("}");

            if (i < lista.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }

    private String convertirHistorialAJson(List<Map<String, Object>> lista) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            Map<String, Object> h = lista.get(i);
            json.append("{")
                    .append("\"id\":").append(h.get("id_historial")).append(",")
                    .append("\"accion\":\"").append(escapeJson((String) h.get("accion"))).append("\",")
                    .append("\"admin\":\"").append(escapeJson((String) h.get("usuario") != null ? (String) h.get("usuario") : "Sistema")).append("\",")
                    .append("\"afectado\":\"").append(escapeJson((String) h.get("producto") != null ? (String) h.get("producto") : "—")).append("\",")
                    .append("\"fecha\":\"").append(escapeJson(String.valueOf(h.get("fecha_hora")))).append("\",")
                    .append("\"tipo\":\"blue\"")
                    .append("}");
            if (i < lista.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        return json.toString();
    }

    private String escapeJson(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}