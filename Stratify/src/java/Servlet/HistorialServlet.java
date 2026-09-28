package Servlet;

import Controlador.HistorialDAO;
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

@WebServlet(name = "HistorialServlet", urlPatterns = {"/HistorialServlet"})
public class HistorialServlet extends HttpServlet {

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

        HistorialDAO dao = new HistorialDAO();
        List<Map<String, Object>> lista = dao.listarHistorial();

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            Map<String, Object> row = lista.get(i);
            String fechaHora = String.valueOf(row.get("fecha_hora"));
            String[] partes = fechaHora.contains(" ") ? fechaHora.split(" ") : new String[]{fechaHora, ""};
            String fecha = partes[0];
            String hora = partes.length > 1 ? partes[1] : "";

            sb.append("{");
            sb.append("\"id\":").append(row.get("id_historial")).append(",");
            sb.append("\"usuario\":\"").append(escapeJson(String.valueOf(row.get("usuario")))).append("\",");
            sb.append("\"accion\":\"").append(escapeJson(String.valueOf(row.get("accion")))).append("\",");
            sb.append("\"objetivo\":\"").append(escapeJson(String.valueOf(row.get("producto")))).append("\",");
            sb.append("\"stock_anterior\":").append(row.get("stock_anterior")).append(",");
            sb.append("\"cantidad_modificada\":").append(row.get("cantidad_modificada")).append(",");
            sb.append("\"stock_nuevo\":").append(row.get("stock_nuevo")).append(",");
            sb.append("\"descripcion\":\"").append(escapeJson(String.valueOf(row.get("descripcion")))).append("\",");
            sb.append("\"fecha\":\"").append(escapeJson(fecha)).append("\",");
            sb.append("\"hora\":\"").append(escapeJson(hora)).append("\"");
            sb.append("}");
            if (i < lista.size() - 1) sb.append(",");
        }
        sb.append("]");
        response.getWriter().print(sb.toString());
    }

    private String escapeJson(String s) {
        if (s == null || "null".equals(s)) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
    }
}
