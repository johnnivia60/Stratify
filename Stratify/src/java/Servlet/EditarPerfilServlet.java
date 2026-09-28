package servlet;

import Modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import Controlador.UsuarioDAO;
import java.io.IOException;
import java.io.PrintWriter;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/EditarPerfilServlet")
public class EditarPerfilServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("id_usuario") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"status\":\"error\", \"mensaje\":\"Sesión no válida o expirada.\"}");
            return;
        }

        int idUsuario = (int) session.getAttribute("id_usuario");
        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        UsuarioDAO usuarioDAO = new UsuarioDAO();

        // Si el correo viene vacío, mantenemos el correo actual registrado en BD
        if (email == null || email.trim().isEmpty()) {
            Usuario actual = usuarioDAO.consultarUsuarioPorId(idUsuario);
            if (actual != null) {
                email = actual.getEmail();
            }
        }

        // Si el usuario ingresó contraseña nueva, la encriptamos con BCrypt
        String passwordHash = null;
        if (password != null && !password.trim().isEmpty()) {
            passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
        }

        // Ejecutar actualización directa de perfil
        boolean actualizado = usuarioDAO.actualizarPerfil(idUsuario, nombre, apellido, email, passwordHash);

        if (actualizado) {
            // Refrescar los datos en la sesión activa
            session.setAttribute("nombre", nombre);
            session.setAttribute("apellido", apellido);

            out.print("{\"status\":\"success\", \"mensaje\":\"Perfil actualizado correctamente en la base de datos.\"}");
        } else {
            out.print("{\"status\":\"error\", \"mensaje\":\"No se pudo actualizar el registro en la base de datos.\"}");
        }
    }
}