package Controlador;

import Modelo.Solicitud;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SolicitudDAO {

    private Conexion miConexion = new Conexion();

    public boolean crearSolicitud(Solicitud miSolicitud) {
        Connection conn = miConexion.getConn();
        if (conn == null) {
            return false;
        }
        String querySQL = "INSERT INTO solicitud (cantidad, estado, fecha_solicitud, usuario_id_usuario, proveedor_id_proveedor, producto_id_producto) "
                + "VALUES (?, 'PENDIENTE', CURRENT_TIMESTAMP(), ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(querySQL)) {
            ps.setInt(1, miSolicitud.getCantidad());
            ps.setInt(2, miSolicitud.getIdUsuario());
            ps.setInt(3, miSolicitud.getIdProveedor());
            ps.setInt(4, miSolicitud.getIdProducto());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error en crearSolicitud SQL: " + e.getMessage());
        }
        return false;
    }

    public List<Solicitud> listarSolicitudesPendientes(int idProveedor) {
    List<Solicitud> lista = new ArrayList<>();
    Connection conn = miConexion.getConn();
    if (conn == null) {
        return lista;
    }

    String querySQL = "SELECT s.id_solicitud, s.cantidad, s.estado, s.fecha_solicitud, "
            + "s.usuario_id_usuario, s.proveedor_id_proveedor, s.producto_id_producto, "
            + "CONCAT(IFNULL(u.nombre, ''), ' ', IFNULL(u.apellido, '')) AS nombre_cliente, "
            + "IFNULL(p.nombre_producto, 'Producto') AS nombre_producto, "
            + "IFNULL(p.codigo_producto, 'N/A') AS codigo_producto " 
            + "FROM solicitud s "
            + "INNER JOIN usuario u ON s.usuario_id_usuario = u.id_usuario "
            + "INNER JOIN producto p ON s.producto_id_producto = p.id_producto "
            + "WHERE s.estado = 'PENDIENTE' "
            + (idProveedor > 0 ? "AND s.proveedor_id_proveedor = ? " : "")
            + "ORDER BY s.fecha_solicitud DESC";

    try (PreparedStatement ps = conn.prepareStatement(querySQL)) {
        if (idProveedor > 0) {
            ps.setInt(1, idProveedor);
        }
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Solicitud s = new Solicitud();
                s.setIdSolicitud(rs.getInt("id_solicitud"));
                s.setCantidad(rs.getInt("cantidad"));
                s.setEstado(rs.getString("estado"));
                s.setFechaSolicitud(rs.getTimestamp("fecha_solicitud"));
                s.setIdUsuario(rs.getInt("usuario_id_usuario"));
                s.setIdProveedor(rs.getInt("proveedor_id_proveedor"));
                s.setIdProducto(rs.getInt("producto_id_producto"));
                s.setNombreCliente(rs.getString("nombre_cliente"));
                s.setNombreProducto(rs.getString("nombre_producto"));
                s.setCodigoProducto(rs.getString("codigo_producto")); // <-- MAPEO DEL CÓDIGO
                lista.add(s);
            }
        }
    } catch (SQLException e) {
        System.out.println("Error en listarSolicitudesPendientes SQL: " + e.getMessage());
    }
    return lista;
}
    public boolean aceptarSolicitud(int idSolicitud) {
        Connection conn = miConexion.getConn();
        if (conn == null) return false;

        String updateStockSQL = "UPDATE inventario i "
                + "JOIN producto p ON p.inventario_id_inventario = i.id_inventario "
                + "JOIN solicitud s ON s.producto_id_producto = p.id_producto "
                + "SET i.stock = i.stock + s.cantidad, i.fecha_actualizacion = CURRENT_DATE() "
                + "WHERE s.id_solicitud = ?";

        String updateEstadoSQL = "UPDATE solicitud SET estado = 'ACEPTADA' WHERE id_solicitud = ?";

        try {
            conn.setAutoCommit(false);
            try (PreparedStatement psStock = conn.prepareStatement(updateStockSQL);
                 PreparedStatement psEstado = conn.prepareStatement(updateEstadoSQL)) {

                psStock.setInt(1, idSolicitud);
                psStock.executeUpdate();

                psEstado.setInt(1, idSolicitud);
                int filasSolicitud = psEstado.executeUpdate();

                if (filasSolicitud > 0) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                }
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Error en transacción aceptarSolicitud: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.out.println("Error de conexión en aceptarSolicitud: " + e.getMessage());
        }
        return false;
    }

    public boolean eliminarSolicitud(int idSolicitud) {
        Connection conn = miConexion.getConn();
        if (conn == null) return false;

        String querySQL = "DELETE FROM solicitud WHERE id_solicitud = ?";
        try (PreparedStatement ps = conn.prepareStatement(querySQL)) {
            ps.setInt(1, idSolicitud);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error en eliminarSolicitud SQL: " + e.getMessage());
        }
        return false;
    }
}