package Servlet;

import util.EmailUtil;
import Controlador.UsuarioDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.UUID;

@WebServlet("/RecuperarClaveServlet")
public class RecuperarClaveServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String email = request.getParameter("email");

            if (email == null || email.trim().isEmpty()) {
                out.print("{\"status\":\"error\", \"mensaje\":\"El correo electrónico es requerido.\"}");
                return;
            }

            UsuarioDAO usuarioDAO = new UsuarioDAO();

            if (usuarioDAO.consultarUsuario(email) == null) {
                out.print("{\"status\":\"error\", \"mensaje\":\"El correo (" + email + ") no se encuentra registrado en el sistema.\"}");
                return;
            }

            String token = UUID.randomUUID().toString();
            long treintaMinutosMs = 30 * 60 * 1000;
            Timestamp fechaExpiracion = new Timestamp(System.currentTimeMillis() + treintaMinutosMs);

            boolean tokenGuardado = usuarioDAO.guardarTokenRecuperacion(email, token, fechaExpiracion);

            if (!tokenGuardado) {
                out.print("{\"status\":\"error\", \"mensaje\":\"Error en BD: Verifica que existan las columnas token_recuperacion y fecha_expiracion_token en MySQL.\"}");
                return;
            }

            String scheme = request.getScheme();
            String serverName = request.getServerName();
            int serverPort = request.getServerPort();
            String contextPath = request.getContextPath();

            String enlace = scheme + "://" + serverName + ":" + serverPort + contextPath + "/restablecerClave.jsp?token=" + token;

            boolean emailEnviado = EmailUtil.enviarCorreoRecuperacion(email, enlace);

            if (emailEnviado) {
                out.print("{\"status\":\"success\", \"mensaje\":\"Se ha enviado un correo con las instrucciones de recuperación.\"}");
            } else {
                out.print("{\"status\":\"error\", \"mensaje\":\"Error al conectar con el servidor de correo SMTP.\"}");
            }

        } catch (Throwable t) {
            t.printStackTrace();
            String msg = t.getMessage() != null ? t.getMessage().replace("\"", "'").replace("\n", " ") : t.toString();
            out.print("{\"status\":\"error\", \"mensaje\":\"Excepción Java: " + msg + "\"}");
        }
    }
}