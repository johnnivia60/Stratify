package Servlet;

import Modelo.Usuario;
import Controlador.UsuarioDAO;
import org.mindrot.jbcrypt.BCrypt; 

import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/RegistroServlet")
public class RegistroServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        // Captura de datos desde el formulario HTML
        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String numeroDocumento = request.getParameter("numero_documento");
        
        // 2. Encriptación de la contraseña con BCrypt
        String passwordEncriptada = null;
        if (password != null && !password.trim().isEmpty()) {
            passwordEncriptada = BCrypt.hashpw(password, BCrypt.gensalt());
        }
        
        // Conversión segura de fecha de nacimiento
        String fechaStr = request.getParameter("fecha_nacimiento");
        Date fechaNacimiento = null;
        if (fechaStr != null && !fechaStr.trim().isEmpty()) {
            try {
                fechaNacimiento = Date.valueOf(fechaStr);
            } catch (IllegalArgumentException e) {
                System.out.println("Formato de fecha inválido: " + e.getMessage());
            }
        }
        
        // Fecha de vencimiento autocalculada a 10 años
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 10);
        Date fechaVencimiento = new Date(calendar.getTimeInMillis());
        
        // Asignación de rol y autorización por defecto para nuevos registros desde el frontend
        int rolId = 1; // Rol Cliente
        String autorizacion = "Limitado"; 
        
        // Captura del tipo de documento proveniente del <select name="tipo_documento_id_tipo_documento">
        int tipoDocumentoId = 1; // Valor por defecto en caso de error
        try {
            String tipoDocParam = request.getParameter("tipo_documento_id_tipo_documento");
            if (tipoDocParam != null && !tipoDocParam.isEmpty()) {
                tipoDocumentoId = Integer.parseInt(tipoDocParam);
            }
        } catch (NumberFormatException e) {
            System.out.println("Error al parsear el tipo de documento: " + e.getMessage());
        }
        
        // Creación del objeto Usuario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setApellido(apellido);
        nuevoUsuario.setEmail(email);
        
        // 3. Se asigna el hash generado en lugar de la contraseña en texto plano
        nuevoUsuario.setPassword(passwordEncriptada);
        
        nuevoUsuario.setNumero_documento(numeroDocumento);
        nuevoUsuario.setFecha_nacimiento(fechaNacimiento);
        nuevoUsuario.setFecha_vencimiento(fechaVencimiento);
        nuevoUsuario.setAutorizacion(autorizacion);
        nuevoUsuario.setEstado(true);
        nuevoUsuario.setTipo_documento_id_tipo_documento(tipoDocumentoId);
        nuevoUsuario.setRol_id_rol(rolId);
        
        // Inserción mediante DAO
        UsuarioDAO dao = new UsuarioDAO();
        boolean registrado = dao.InsertarUsuario(nuevoUsuario);
        
        if (registrado) {
            response.sendRedirect("index.jsp?exito=1");
        } else {
            response.sendRedirect("index.jsp?error=1");
        }
    }
}