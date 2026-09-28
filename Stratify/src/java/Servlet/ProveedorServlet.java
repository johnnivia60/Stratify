package Servlet;

import Controlador.ProductoDAO;
import Controlador.ProveedorDAO;
import Modelo.Producto;
import Modelo.Proveedor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "ProveedorServlet", urlPatterns = {"/ProveedorServlet"})
public class ProveedorServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("id_usuario") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Sesion no iniciada.\"}");
            return;
        }

        String accion = request.getParameter("accion");
        PrintWriter out = response.getWriter();

        if ("productos".equals(accion)) {
            // Devuelve los productos asociados a un proveedor especifico (por ID o por Nombre de Empresa)
            String idParam = request.getParameter("id_proveedor");
            String empresaParam = request.getParameter("empresa");
            int idProveedor = 0;
            try {
                if (idParam != null && !idParam.trim().isEmpty()) {
                    idProveedor = Integer.parseInt(idParam);
                }
            } catch (NumberFormatException ignored) {}

            ProductoDAO productoDao = new ProductoDAO();
            List<Producto> productos = null;

            if (idProveedor > 0) {
                productos = productoDao.listarProductosPorProveedor(idProveedor);
            }
            
            if ((productos == null || productos.isEmpty()) && empresaParam != null && !empresaParam.trim().isEmpty()) {
                productos = productoDao.listarProductosPorNombreEmpresa(empresaParam.trim());
            }

            if (productos == null) {
                productos = java.util.Collections.emptyList();
            }

            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < productos.size(); i++) {
                Producto p = productos.get(i);
                sb.append("{");
                sb.append("\"id\":").append(p.getId_producto()).append(",");
                sb.append("\"id_producto\":").append(p.getId_producto()).append(",");
                sb.append("\"codigo\":\"").append(escapeJson(p.getCodigo_producto())).append("\",");
                sb.append("\"codigo_producto\":\"").append(escapeJson(p.getCodigo_producto())).append("\",");
                sb.append("\"nombre\":\"").append(escapeJson(p.getNombre_producto())).append("\",");
                sb.append("\"nombre_producto\":\"").append(escapeJson(p.getNombre_producto())).append("\",");
                sb.append("\"costo_unitario\":").append(p.getCosto_unitario() != null ? p.getCosto_unitario() : 0).append(",");
                sb.append("\"precio\":").append(p.getCosto_unitario() != null ? p.getCosto_unitario() : 0).append(",");
                sb.append("\"stock\":").append(p.getStock()).append(",");
                sb.append("\"cantidad\":").append(p.getStock()).append(",");
                sb.append("\"estado\":").append(p.isEstado()).append(",");
                sb.append("\"fecha_vencimiento\":\"").append(p.getFecha_vencimiento() != null ? p.getFecha_vencimiento().toString() : "").append("\"");
                sb.append("}");
                if (i < productos.size() - 1) sb.append(",");
            }
            sb.append("]");
            out.print(sb.toString());
            out.flush();
            return;
        }

        ProveedorDAO dao = new ProveedorDAO();
        List<Proveedor> lista = dao.listarProveedores();

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            Proveedor p = lista.get(i);
            sb.append("{");
            sb.append("\"id\":").append(p.getId_proveedor()).append(",");
            sb.append("\"id_proveedor\":").append(p.getId_proveedor()).append(",");
            sb.append("\"nit_proveedor\":\"").append(escapeJson(p.getNit_proveedor())).append("\",");
            sb.append("\"nombre\":\"").append(escapeJson(p.getNombre_proveedor())).append("\",");
            sb.append("\"nombre_proveedor\":\"").append(escapeJson(p.getNombre_proveedor())).append("\",");
            sb.append("\"direccion\":\"").append(escapeJson(p.getDireccion())).append("\",");
            sb.append("\"correo\":\"").append(escapeJson(p.getCorreo_proveedor())).append("\",");
            sb.append("\"correo_proveedor\":\"").append(escapeJson(p.getCorreo_proveedor())).append("\",");
            sb.append("\"telefono\":\"").append(escapeJson(p.getTelefono())).append("\",");
            sb.append("\"productos_asociados\":").append(p.getProductosAsociados());
            sb.append("}");
            if (i < lista.size() - 1) sb.append(",");
        }
        sb.append("]");
        response.getWriter().print(sb.toString());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("id_usuario") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"status\":\"error\",\"mensaje\":\"Sesion no iniciada.\"}");
            return;
        }

        String accion = request.getParameter("accion");
        PrintWriter out = response.getWriter();
        ProveedorDAO dao = new ProveedorDAO();

        if ("crear".equals(accion)) {
            String nombre = trimOrNull(request.getParameter("nombre_proveedor"));
            String telefono = trimOrNull(firstNonNull(
                    request.getParameter("telefono_proveedor"), request.getParameter("telefono")));
            String correo = trimOrNull(firstNonNull(
                    request.getParameter("email_proveedor"), request.getParameter("correo_proveedor")));
            String direccion = trimOrNull(firstNonNull(
                    request.getParameter("direccion_proveedor"), request.getParameter("direccion")));
            String nit = trimOrNull(request.getParameter("nit_proveedor"));

            if (nombre == null || telefono == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\",\"mensaje\":\"El nombre y el telefono del proveedor son obligatorios.\"}");
                out.flush();
                return;
            }

            Proveedor p = new Proveedor();
            p.setNombre_proveedor(nombre);
            p.setDireccion(direccion != null ? direccion : "");
            p.setCorreo_proveedor(correo != null ? correo : "");
            p.setTelefono(telefono);
            p.setNit_proveedor(nit != null ? nit : "");

            boolean ok = dao.InsertarProveedor(p);
            if (ok) {
                out.print("{\"status\":\"success\",\"mensaje\":\"Proveedor creado.\"}");
            } else {
                response.setStatus(500);
                out.print("{\"status\":\"error\",\"mensaje\":\"No se pudo crear el proveedor.\"}");
            }

        } else if ("actualizar".equals(accion)) {
            try {
                String nombre = trimOrNull(request.getParameter("nombre_proveedor"));
                String telefono = trimOrNull(firstNonNull(
                        request.getParameter("telefono_proveedor"), request.getParameter("telefono")));
                String correo = trimOrNull(firstNonNull(
                        request.getParameter("email_proveedor"), request.getParameter("correo_proveedor")));
                String direccion = trimOrNull(firstNonNull(
                        request.getParameter("direccion_proveedor"), request.getParameter("direccion")));
                String nit = trimOrNull(request.getParameter("nit_proveedor"));

                if (nombre == null || telefono == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"status\":\"error\",\"mensaje\":\"El nombre y el telefono del proveedor son obligatorios.\"}");
                    out.flush();
                    return;
                }

                Proveedor p = new Proveedor();
                p.setId_proveedor(Integer.parseInt(request.getParameter("id_proveedor")));
                p.setNombre_proveedor(nombre);
                p.setDireccion(direccion != null ? direccion : "");
                p.setCorreo_proveedor(correo != null ? correo : "");
                p.setTelefono(telefono);
                p.setNit_proveedor(nit != null ? nit : "");

                boolean ok = dao.actualizarProveedor(p);
                if (ok) {
                    out.print("{\"status\":\"success\",\"mensaje\":\"Proveedor actualizado.\"}");
                } else {
                    response.setStatus(500);
                    out.print("{\"status\":\"error\",\"mensaje\":\"No se pudo actualizar el proveedor.\"}");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\",\"mensaje\":\"ID invalido.\"}");
            }

        } else if ("eliminar".equals(accion)) {
            try {
                int id = Integer.parseInt(request.getParameter("id_proveedor"));

                int productosAsociados = dao.contarProductosAsociados(id);
                if (productosAsociados > 0) {
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    out.print("{\"status\":\"error\",\"mensaje\":\"No se puede eliminar: el proveedor tiene "
                            + productosAsociados + " producto(s) asociado(s). Reasigna o elimina esos productos primero.\"}");
                    out.flush();
                    return;
                }

                boolean ok = dao.eliminarProveedor(id);
                if (ok) {
                    out.print("{\"status\":\"success\",\"mensaje\":\"Proveedor eliminado.\"}");
                } else {
                    response.setStatus(500);
                    out.print("{\"status\":\"error\",\"mensaje\":\"No se pudo eliminar el proveedor.\"}");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\",\"mensaje\":\"ID invalido.\"}");
            }

        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"status\":\"error\",\"mensaje\":\"Accion no reconocida.\"}");
        }
        out.flush();
    }

    private String trimOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private String firstNonNull(String a, String b) {
        return (a != null && !a.trim().isEmpty()) ? a : b;
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
    }
}