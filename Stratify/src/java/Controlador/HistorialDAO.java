package Controlador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class HistorialDAO {

    private Conexion miConexion = new Conexion();

    // 1. Historial exclusivo para Usuarios (Kardex / historial_cambios)
    public List<Map<String, Object>> listarHistorial() {
        List<Map<String, Object>> lista = new ArrayList<>();
        Connection conn = miConexion.getConn();
        if (conn == null) return lista;
        String querySQL = "SELECT h.id_historial, h.accion, h.stock_anterior, h.cantidad_modificada, "
                + "h.stock_nuevo, h.descripcion, h.fecha_hora, "
                + "CONCAT(u.nombre, ' ', u.apellido) AS usuario, "
                + "p.nombre_producto AS producto "
                + "FROM historial_cambios h "
                + "LEFT JOIN usuario u ON h.id_usuario = u.id_usuario "
                + "LEFT JOIN producto p ON h.id_producto = p.id_producto "
                + "ORDER BY h.fecha_hora DESC LIMIT 100";
        try {
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("id_historial", rs.getInt("id_historial"));
                row.put("accion", rs.getString("accion"));
                row.put("stock_anterior", rs.getInt("stock_anterior"));
                row.put("cantidad_modificada", rs.getInt("cantidad_modificada"));
                row.put("stock_nuevo", rs.getInt("stock_nuevo"));
                row.put("descripcion", rs.getString("descripcion"));
                row.put("fecha_hora", rs.getString("fecha_hora"));
                row.put("usuario", rs.getString("usuario"));
                row.put("producto", rs.getString("producto"));
                lista.add(row);
            }
        } catch (SQLException e) {
            System.out.println("Error listarHistorial: " + e.getMessage());
        }
        return lista;
    }

    // 2. Insertar acción en historial_admin
    public boolean registrarAccionAdmin(String accion, String usuario, String afectado) {
        boolean registrado = false;
        Connection conn = miConexion.getConn();
        if (conn == null) return false;

        String sql = "INSERT INTO historial_admin (accion, usuario, afectado, fecha_hora) VALUES (?, ?, ?, NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accion);
            ps.setString(2, usuario);
            ps.setString(3, afectado);
            registrado = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al registrar accion admin: " + e.getMessage());
        }
        return registrado;
    }

    // 3. Consultar registros exclusivos de la tabla historial_admin
    public List<Map<String, Object>> listarHistorialAdmin() {
        List<Map<String, Object>> lista = new ArrayList<>();
        Connection conn = miConexion.getConn();
        if (conn == null) return lista;

        String sql = "SELECT id_historial, accion, usuario, afectado AS producto, fecha_hora "
                   + "FROM historial_admin ORDER BY fecha_hora DESC LIMIT 100";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("id_historial", rs.getInt("id_historial"));
                row.put("accion", rs.getString("accion"));
                row.put("usuario", rs.getString("usuario"));
                row.put("producto", rs.getString("producto"));
                row.put("fecha_hora", rs.getString("fecha_hora"));
                lista.add(row);
            }
        } catch (SQLException e) {
            System.out.println("Error listarHistorialAdmin: " + e.getMessage());
        }
        return lista;
    }
}