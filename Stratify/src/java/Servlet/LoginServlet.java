package Servlet;

import Controlador.UsuarioDAO;
import Modelo.Usuario;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        response.getWriter().write("{\"status\":\"error\",\"mensaje\":\"Metodo GET no permitido. Use POST.\"}");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        PrintWriter out = response.getWriter();

        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"status\":\"error\",\"mensaje\":\"Email y contraseña requeridos.\"}");
            out.flush();
            return;
        }

        UsuarioDAO dao = new UsuarioDAO();
        Usuario usuario = dao.validarLogin(email.trim(), password);

        if (usuario != null) {
            if (usuario.isEstado()) {
                HttpSession session = request.getSession(true);
                session.setAttribute("id_usuario", usuario.getId_usuario());
                session.setAttribute("nombre", usuario.getNombre());
                session.setAttribute("apellido", usuario.getApellido());
                session.setAttribute("email", usuario.getEmail());
                session.setAttribute("rol_id_rol", usuario.getRol_id_rol());

                // === LÓGICA DE ENRUTAMIENTO BASADA EN ROLES ===
                int rol = usuario.getRol_id_rol();
                String urlDestino = "";

// Ajusta los números según los IDs de tu tabla 'rol' en MySQL
                if (rol == 3) {
                    urlDestino = "admin/admin.jsp";   // Redirige a Admin si el rol es 3
                } else if (rol == 1) {
                    urlDestino = "usuario/usuario.jsp"; // Redirige a Usuario estándar
                } else if (rol == 2){
                    urlDestino = "proveedor/proveedor.jsp";
                }
                else {
                    urlDestino = "index.jsp?error=rol_desconocido";
                }

                response.setStatus(HttpServletResponse.SC_OK);
                // Retornamos el campo "redirect" dentro de la respuesta JSON
                out.print("{\"status\":\"success\","
                        + "\"mensaje\":\"Bienvenido " + escapeJson(usuario.getNombre()) + "\","
                        + "\"redirect\":\"" + urlDestino + "\","
                        + "\"nombre\":\"" + escapeJson(usuario.getNombre()) + "\","
                        + "\"apellido\":\"" + escapeJson(usuario.getApellido()) + "\","
                        + "\"email\":\"" + escapeJson(usuario.getEmail()) + "\","
                        + "\"rol\":" + rol + "}");
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"status\":\"error\",\"mensaje\":\"Usuario inactivo.\"}");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"status\":\"error\",\"mensaje\":\"Credenciales incorrectas.\"}");
        }
        out.flush();
    }

    private String escapeJson(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
