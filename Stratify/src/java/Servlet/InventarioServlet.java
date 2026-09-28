package Servlet;

import Controlador.ProductoDAO;
import Modelo.Producto;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "InventarioServlet", urlPatterns = {"/InventarioServlet"})
public class InventarioServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        // Verificar sesion
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("id_usuario") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Sesion no iniciada.\"}");
            return;
        }

        try (PrintWriter out = response.getWriter()) {
            ProductoDAO dao = new ProductoDAO();
            List<Producto> lista = dao.listarProductos();

            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < lista.size(); i++) {
                Producto p = lista.get(i);
                String fechaVenc = p.getFecha_vencimiento() != null ? p.getFecha_vencimiento().toString() : "";

                String estado;
                if (p.getStock() == 0) {
                    estado = "agotado";
                } else if (p.getStock() <= 10) {
                    estado = "bajo";
                } else {
                    estado = "disponible";
                }

                sb.append("{");
                sb.append("\"id\":").append(p.getId_producto()).append(",");
                sb.append("\"codigo\":\"").append(escapeJson(p.getCodigo_producto())).append("\",");
                sb.append("\"nombre\":\"").append(escapeJson(p.getNombre_producto())).append("\",");
                sb.append("\"fecha_expiracion\":\"").append(fechaVenc).append("\",");
                sb.append("\"cantidad\":").append(p.getStock()).append(",");
                sb.append("\"estado\":\"").append(estado).append("\",");
                sb.append("\"proveedor\":\"").append(escapeJson(p.getNombreProveedor())).append("\",");
                sb.append("\"inventario_id\":").append(p.getInventario_id_inventario());
                sb.append("}");
                if (i < lista.size() - 1) sb.append(",");
            }
            sb.append("]");
            out.print(sb.toString());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error interno: " + escapeJson(e.getMessage()) + "\"}");
        }
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

        int usuarioId = (int) session.getAttribute("id_usuario");
        String accion = request.getParameter("accion");
        PrintWriter out = response.getWriter();

        if ("modificar_stock".equals(accion)) {
            try {
                int inventarioId = Integer.parseInt(request.getParameter("inventario_id"));
                int productoId = Integer.parseInt(request.getParameter("producto_id"));
                int cantidad = Integer.parseInt(request.getParameter("cantidad"));
                String tipoAccion = request.getParameter("tipo_accion"); // RETIRO_STOCK o INGRESO_STOCK
                String descripcion = request.getParameter("descripcion");
                if (descripcion == null) descripcion = tipoAccion;

                ProductoDAO dao = new ProductoDAO();
                boolean ok = dao.modificarStock(inventarioId, cantidad, tipoAccion, usuarioId, productoId, descripcion);
                if (ok) {
                    out.print("{\"status\":\"success\",\"mensaje\":\"Stock actualizado correctamente.\"}");
                } else {
                    response.setStatus(500);
                    out.print("{\"status\":\"error\",\"mensaje\":\"No se pudo actualizar el stock.\"}");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\",\"mensaje\":\"Parametros invalidos.\"}");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"status\":\"error\",\"mensaje\":\"Accion no reconocida.\"}");
        }
        out.flush();
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
    }
}
