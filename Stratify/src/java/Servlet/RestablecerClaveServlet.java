package Servlet;
import Controlador.UsuarioDAO;
import org.mindrot.jbcrypt.BCrypt;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/RestablecerClaveServlet")
public class RestablecerClaveServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String token = request.getParameter("token");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirm_password");

        if (token == null || token.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            out.print("{\"status\":\"error\", \"mensaje\":\"Datos incompletos.\"}");
            return;
        }

        if (!password.equals(confirmPassword)) {
            out.print("{\"status\":\"error\", \"mensaje\":\"Las contraseñas no coinciden.\"}");
            return;
        }

        if (password.length() < 8) {
            out.print("{\"status\":\"error\", \"mensaje\":\"La contraseña debe tener al menos 8 caracteres.\"}");
            return;
        }

        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        boolean actualizado = usuarioDAO.actualizarPasswordPorToken(token, passwordHash);

        if (actualizado) {
            out.print("{\"status\":\"success\", \"mensaje\":\"Contraseña actualizada con éxito.\"}");
        } else {
            out.print("{\"status\":\"error\", \"mensaje\":\"El token ha expirado o no es válido.\"}");
        }
    }
}