package Controlador;

import Modelo.Usuario;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

public class UsuarioDAO {

    private Conexion miConexion = new Conexion();

    public Usuario consultarUsuario(String email) {
        Usuario miUsuario = null;
        Connection conn = miConexion.getConn();
        String querySQL = "SELECT id_usuario, nombre, apellido, password, email, numero_documento, fecha_nacimiento, fecha_vencimiento"
                + ", autorizacion, estado, tipo_documento_id_tipo_documento, rol_id_rol FROM usuario WHERE email = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                miUsuario = new Usuario();
                miUsuario.setId_usuario(rs.getInt("id_usuario"));
                miUsuario.setNombre(rs.getString("nombre"));
                miUsuario.setApellido(rs.getString("apellido"));
                miUsuario.setPassword(rs.getString("password"));
                miUsuario.setEmail(rs.getString("email"));
                miUsuario.setNumero_documento(rs.getString("numero_documento"));
                miUsuario.setFecha_nacimiento(rs.getDate("fecha_nacimiento"));
                miUsuario.setFecha_vencimiento(rs.getDate("fecha_vencimiento"));
                miUsuario.setAutorizacion(rs.getString("autorizacion"));
                miUsuario.setEstado(rs.getBoolean("estado"));
                miUsuario.setTipo_documento_id_tipo_documento(rs.getInt("tipo_documento_id_tipo_documento"));
                miUsuario.setRol_id_rol(rs.getInt("rol_id_rol"));
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar usuario: " + e.getMessage());
        }
        return miUsuario;
    }

    public boolean InsertarUsuario(Usuario miUsuario) {
        boolean insertar = false;
        Connection conn = miConexion.getConn();

        if (conn == null) {
            System.out.println("ERROR: La conexión a la base de datos es nula.");
            return false;
        }

        String querySQL = "INSERT INTO usuario (nombre, apellido, password, email, numero_documento, "
                + "fecha_nacimiento, fecha_vencimiento, autorizacion, estado, tipo_documento_id_tipo_documento, rol_id_rol) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ps.setString(1, miUsuario.getNombre());
            ps.setString(2, miUsuario.getApellido());
            ps.setString(3, miUsuario.getPassword());
            ps.setString(4, miUsuario.getEmail());
            ps.setString(5, miUsuario.getNumero_documento());
            ps.setDate(6, (java.sql.Date) miUsuario.getFecha_nacimiento());
            ps.setDate(7, (java.sql.Date) miUsuario.getFecha_vencimiento());
            ps.setString(8, miUsuario.getAutorizacion());
            ps.setBoolean(9, miUsuario.isEstado());
            ps.setInt(10, miUsuario.getTipo_documento_id_tipo_documento());
            ps.setInt(11, miUsuario.getRol_id_rol());

            if (ps.executeUpdate() > 0) {
                insertar = true;
            }
        } catch (Exception e) {
            System.out.println("Error crítico al insertar el usuario en BD: " + e.getMessage());
            e.printStackTrace();
        }
        return insertar;
    }

    public boolean actualizarUsuario(Usuario miUsuario) {
        boolean actualizar = false;
        Connection conn = miConexion.getConn();
        String querySQL = "UPDATE usuario SET nombre = ?, apellido = ?, password = ?, email = ?, "
                + "numero_documento = ?, fecha_nacimiento = ?, fecha_vencimiento = ?, autorizacion = ?, "
                + "tipo_documento_id_tipo_documento = ?, rol_id_rol = ?, estado = ? WHERE id_usuario = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ps.setString(1, miUsuario.getNombre());
            ps.setString(2, miUsuario.getApellido());
            ps.setString(3, miUsuario.getPassword());
            ps.setString(4, miUsuario.getEmail());
            ps.setString(5, miUsuario.getNumero_documento());
            ps.setDate(6, (Date) miUsuario.getFecha_nacimiento());
            ps.setDate(7, (Date) miUsuario.getFecha_vencimiento());
            ps.setString(8, miUsuario.getAutorizacion());
            ps.setInt(9, miUsuario.getTipo_documento_id_tipo_documento());
            ps.setInt(10, miUsuario.getRol_id_rol());
            ps.setBoolean(11, miUsuario.isEstado());
            ps.setInt(12, miUsuario.getId_usuario());

            if (ps.executeUpdate() > 0) {
                actualizar = true;
            }
        } catch (SQLException e) {
            System.out.println("Error al actualizar el usuario: " + e.getMessage());
        }
        return actualizar;
    }

    public boolean activarUsuario(int id_usuario) {
        boolean activar = false;
        String querySQL = "UPDATE usuario SET estado = 1 WHERE id_usuario = ?";
        Connection conn = miConexion.getConn();
        try {
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ps.setInt(1, id_usuario);
            if (ps.executeUpdate() > 0) {
                activar = true;
            }
        } catch (SQLException e) {
            System.out.println("Error al activar el usuario: " + e.getMessage());
        }
        return activar;
    }

    public boolean inactivarUsuario(int id_usuario) {
        boolean inactivar = false;
        String querySQL = "UPDATE usuario SET estado = 0 WHERE id_usuario = ?";
        Connection conn = miConexion.getConn();
        try {
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ps.setInt(1, id_usuario);
            if (ps.executeUpdate() > 0) {
                inactivar = true;
            }
        } catch (SQLException e) {
            System.out.println("Error al inactivar el usuario: " + e.getMessage());
        }
        return inactivar;
    }

    public boolean eliminarUsuario(int id_usuario) {
        boolean eliminar = false;
        String querySQL = "DELETE FROM usuario WHERE id_usuario = ?";
        Connection conn = miConexion.getConn();
        try {
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ps.setInt(1, id_usuario);
            if (ps.executeUpdate() > 0) {
                eliminar = true;
            }
        } catch (SQLException e) {
            System.out.println("Error al eliminar usuario: " + e.getMessage());
        }
        return eliminar;
    }

    public Usuario validarLogin(String email, String password) {
        Usuario usuarioLogin = null;
        Connection conn = miConexion.getConn();
        String querySQL = "SELECT id_usuario, nombre, apellido, email, password, estado, rol_id_rol FROM usuario WHERE email = ?";

        try (PreparedStatement ps = conn.prepareStatement(querySQL)) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashAlmacenado = rs.getString("password");
                    if (hashAlmacenado != null && BCrypt.checkpw(password, hashAlmacenado)) {
                        usuarioLogin = new Usuario();
                        usuarioLogin.setId_usuario(rs.getInt("id_usuario"));
                        usuarioLogin.setNombre(rs.getString("nombre"));
                        usuarioLogin.setApellido(rs.getString("apellido"));
                        usuarioLogin.setEmail(rs.getString("email"));
                        usuarioLogin.setEstado(rs.getBoolean("estado"));
                        usuarioLogin.setRol_id_rol(rs.getInt("rol_id_rol"));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al validar login: " + e.getMessage());
        }

        return usuarioLogin;
    }

    public List<Usuario> listarUsuarios() {
        List<Usuario> lista = new ArrayList<>();
        Connection conn = miConexion.getConn();
        String querySQL = "SELECT id_usuario, nombre, apellido, email, numero_documento, estado, rol_id_rol FROM usuario";
        try {
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId_usuario(rs.getInt("id_usuario"));
                u.setNombre(rs.getString("nombre"));
                u.setApellido(rs.getString("apellido"));
                u.setEmail(rs.getString("email"));
                u.setNumero_documento(rs.getString("numero_documento"));
                u.setEstado(rs.getBoolean("estado"));
                u.setRol_id_rol(rs.getInt("rol_id_rol"));
                lista.add(u);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar usuarios: " + e.getMessage());
        }
        return lista;
    }

    // Guardar Token
    public boolean guardarTokenRecuperacion(String email, String token, Timestamp fechaExpiracion) {
        String sql = "UPDATE usuario SET token_recuperacion = ?, fecha_expiracion_token = ? WHERE email = ?";
        Connection con = miConexion.getConn();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, token);
            ps.setTimestamp(2, fechaExpiracion);
            ps.setString(3, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al guardar token: " + e.getMessage());
            return false;
        }
    }

    // Validar Token
    public boolean validarToken(String token) {
        String sql = "SELECT id_usuario FROM usuario WHERE token_recuperacion = ? AND fecha_expiracion_token > NOW()";
        Connection con = miConexion.getConn();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error al validar token: " + e.getMessage());
            return false;
        }
    }

    // Actualizar Contraseña por Token
    public boolean actualizarPasswordPorToken(String token, String nuevaPasswordHash) {
        String sql = "UPDATE usuario SET password = ?, token_recuperacion = NULL, fecha_expiracion_token = NULL WHERE token_recuperacion = ? AND fecha_expiracion_token > NOW()";
        Connection con = miConexion.getConn();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevaPasswordHash);
            ps.setString(2, token);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar contraseña: " + e.getMessage());
            return false;
        }
    }

    public int contarUsuariosTotal() {
        int total = 0;
        Connection conn = miConexion.getConn();
        String query = "SELECT COUNT(*) FROM usuario";
        try (PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error contarTotal: " + e.getMessage());
        }
        return total;
    }

    public int contarUsuariosPorEstado(boolean estado) {
        int count = 0;
        Connection conn = miConexion.getConn();
        String query = "SELECT COUNT(*) FROM usuario WHERE estado = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setBoolean(1, estado);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error contarEstado: " + e.getMessage());
        }
        return count;
    }

    public int contarUsuariosPorRol(int idRol) {
        int count = 0;
        Connection conn = miConexion.getConn();
        String query = "SELECT COUNT(*) FROM usuario WHERE rol_id_rol = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idRol);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error contarRol: " + e.getMessage());
        }
        return count;
    }
    public boolean actualizarPerfil(int idUsuario, String nombre, String apellido, String email, String password) {
    boolean actualizado = false;
    Connection conn = miConexion.getConn();

    boolean cambiaPassword = (password != null && !password.trim().isEmpty());
    String querySQL = cambiaPassword 
        ? "UPDATE usuario SET nombre = ?, apellido = ?, email = ?, password = ? WHERE id_usuario = ?"
        : "UPDATE usuario SET nombre = ?, apellido = ?, email = ? WHERE id_usuario = ?";

    try (PreparedStatement ps = conn.prepareStatement(querySQL)) {
        ps.setString(1, nombre);
        ps.setString(2, apellido);
        ps.setString(3, email);

        if (cambiaPassword) {
            ps.setString(4, password);
            ps.setInt(5, idUsuario);
        } else {
            ps.setInt(4, idUsuario);
        }

        if (ps.executeUpdate() > 0) {
            actualizado = true;
        }
    } catch (SQLException e) {
        System.out.println("Error al actualizar perfil de usuario: " + e.getMessage());
        e.printStackTrace();
    }
    return actualizado;
}

    public Usuario consultarUsuarioPorId(int idUsuario) {
        Usuario miUsuario = null;
        Connection conn = miConexion.getConn();
        String querySQL = "SELECT id_usuario, nombre, apellido, password, email, numero_documento, fecha_nacimiento, fecha_vencimiento, autorizacion, estado, tipo_documento_id_tipo_documento, rol_id_rol FROM usuario WHERE id_usuario = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                miUsuario = new Usuario();
                miUsuario.setId_usuario(rs.getInt("id_usuario"));
                miUsuario.setNombre(rs.getString("nombre"));
                miUsuario.setApellido(rs.getString("apellido"));
                miUsuario.setPassword(rs.getString("password"));
                miUsuario.setEmail(rs.getString("email"));
                miUsuario.setNumero_documento(rs.getString("numero_documento"));
                miUsuario.setFecha_nacimiento(rs.getDate("fecha_nacimiento"));
                miUsuario.setFecha_vencimiento(rs.getDate("fecha_vencimiento"));
                miUsuario.setAutorizacion(rs.getString("autorizacion"));
                miUsuario.setEstado(rs.getBoolean("estado"));
                miUsuario.setTipo_documento_id_tipo_documento(rs.getInt("tipo_documento_id_tipo_documento"));
                miUsuario.setRol_id_rol(rs.getInt("rol_id_rol"));
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar usuario por ID: " + e.getMessage());
        }
        return miUsuario;
    }
}