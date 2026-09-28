package Servlet;

import Controlador.SolicitudDAO;
import Modelo.Solicitud;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "SolicitudServlet", urlPatterns = {"/SolicitudServlet"})
public class SolicitudServlet extends HttpServlet {

    private SolicitudDAO solicitudDAO = new SolicitudDAO();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("id_usuario") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("[]");
            return;
        }

        String idProvStr = request.getParameter("id_proveedor");
        int idProveedor = 0;
        if (idProvStr != null && !idProvStr.isEmpty()) {
            try {
                idProveedor = Integer.parseInt(idProvStr);
            } catch (NumberFormatException ignored) {}
        }

        try {
            List<Solicitud> lista = solicitudDAO.listarSolicitudesPendientes(idProveedor);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(gson.toJson(lista));
        } catch (Exception e) {
            System.out.println("Error en SolicitudServlet doGet: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("[]");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("id_usuario") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"status\":\"error\", \"mensaje\":\"Sesión no activa.\"}");
            return;
        }

        String accion = request.getParameter("accion");
        PrintWriter out = response.getWriter();

        try {
            int idSolicitud = Integer.parseInt(request.getParameter("id_solicitud"));

            if ("aceptar".equals(accion)) {
                boolean ok = solicitudDAO.aceptarSolicitud(idSolicitud);
                if (ok) {
                    out.write("{\"status\":\"success\", \"mensaje\":\"Solicitud aceptada y stock actualizado correctamente.\"}");
                } else {
                    out.write("{\"status\":\"error\", \"mensaje\":\"No se pudo procesar la aceptación de la solicitud.\"}");
                }
            } else if ("rechazar".equals(accion) || "eliminar".equals(accion)) {
                boolean ok = solicitudDAO.eliminarSolicitud(idSolicitud);
                if (ok) {
                    out.write("{\"status\":\"success\", \"mensaje\":\"Solicitud cancelada y eliminada correctamente.\"}");
                } else {
                    out.write("{\"status\":\"error\", \"mensaje\":\"No se pudo eliminar la solicitud.\"}");
                }
            } else {
                out.write("{\"status\":\"error\", \"mensaje\":\"Acción no válida.\"}");
            }
        } catch (Exception e) {
            out.write("{\"status\":\"error\", \"mensaje\":\"ID de solicitud inválido o faltante.\"}");
        }
    }
}