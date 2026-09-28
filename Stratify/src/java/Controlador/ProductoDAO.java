package Controlador;

import Modelo.Producto;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    private Conexion miConexion = new Conexion();

    public Producto consultarProducto(String nombre_producto) {
        Producto miProducto = null;
        Connection conn = miConexion.getConn();
        if (conn == null) {
            return null;
        }
        String querySQL = "SELECT p.id_producto, p.codigo_producto, p.nombre_producto, p.costo_unitario, "
                + "p.fecha_ingreso, p.fecha_vencimiento, p.fecha_retiro, p.estado, "
                + "p.proveedor_id_proveedor, p.unidad_medida_id_unidad_medida, "
                + "p.ubicacion_almacen_id_ubicacion, p.inventario_id_inventario, "
                + "COALESCE(i.stock, 0) AS stock "
                + "FROM producto p "
                + "LEFT JOIN inventario i ON p.inventario_id_inventario = i.id_inventario "
                + "WHERE p.nombre_producto = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ps.setString(1, nombre_producto);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                miProducto = mapRow(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error consultarProducto: " + e.getMessage());
        }
        return miProducto;
    }

    public Producto consultarProductoPorId(int id_producto) {
        Producto miProducto = null;
        Connection conn = miConexion.getConn();
        if (conn == null) {
            return null;
        }
        String querySQL = "SELECT p.id_producto, p.codigo_producto, p.nombre_producto, p.costo_unitario, "
                + "p.fecha_ingreso, p.fecha_vencimiento, p.fecha_retiro, p.estado, "
                + "p.proveedor_id_proveedor, p.unidad_medida_id_unidad_medida, "
                + "p.ubicacion_almacen_id_ubicacion, p.inventario_id_inventario, "
                + "COALESCE(i.stock, 0) AS stock "
                + "FROM producto p "
                + "LEFT JOIN inventario i ON p.inventario_id_inventario = i.id_inventario "
                + "WHERE p.id_producto = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ps.setInt(1, id_producto);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                miProducto = mapRow(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error consultarProductoPorId: " + e.getMessage());
        }
        return miProducto;
    }

    private Producto mapRow(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setId_producto(rs.getInt("id_producto"));
        p.setCodigo_producto(rs.getString("codigo_producto"));
        p.setNombre_producto(rs.getString("nombre_producto"));
        p.setCosto_unitario(rs.getBigDecimal("costo_unitario"));
        p.setFecha_ingreso(rs.getDate("fecha_ingreso"));
        p.setFecha_vencimiento(rs.getDate("fecha_vencimiento"));
        p.setFecha_retiro(rs.getDate("fecha_retiro"));
        p.setEstado(rs.getBoolean("estado"));
        p.setProveedor_id_proveedor(rs.getInt("proveedor_id_proveedor"));
        p.setUnidad_medida_id_unidad_medida(rs.getInt("unidad_medida_id_unidad_medida"));
        p.setUbicacion_almacen_id_ubicacion(rs.getInt("ubicacion_almacen_id_ubicacion"));
        p.setInventario_id_inventario(rs.getInt("inventario_id_inventario"));
        p.setStock(rs.getInt("stock"));
        return p;
    }

    public String InsertarProducto(Producto miProducto) {
        Connection conn = miConexion.getConn();
        if (conn == null) {
            return "No hay conexión con la base de datos.";
        }
        try {
            conn.setAutoCommit(false);

            try (Statement stmtFk = conn.createStatement()) {
                stmtFk.execute("SET FOREIGN_KEY_CHECKS = 0;");
            }

            // 1. Crear el registro en inventario
            String queryInventario = "INSERT INTO inventario (stock, fecha_actualizacion) VALUES (?, NOW())";
            PreparedStatement psInv = conn.prepareStatement(queryInventario, Statement.RETURN_GENERATED_KEYS);
            psInv.setInt(1, miProducto.getStock() > 0 ? miProducto.getStock() : 0);
            psInv.executeUpdate();

            ResultSet rsKeys = psInv.getGeneratedKeys();
            int idInventarioGenerado = 0;
            if (rsKeys.next()) {
                idInventarioGenerado = rsKeys.getInt(1);
            } else {
                conn.rollback();
                return "No se pudo generar el registro de inventario.";
            }

            // 2. Control de valores obligatorios
            java.math.BigDecimal costo = miProducto.getCosto_unitario() != null ? miProducto.getCosto_unitario() : java.math.BigDecimal.ZERO;
            
            java.sql.Date fIngreso = miProducto.getFecha_ingreso() != null 
                    ? new java.sql.Date(miProducto.getFecha_ingreso().getTime()) 
                    : new java.sql.Date(System.currentTimeMillis());

            java.sql.Date fVencimiento = miProducto.getFecha_vencimiento() != null 
                    ? new java.sql.Date(miProducto.getFecha_vencimiento().getTime()) 
                    : fIngreso;

            java.sql.Date fRetiro = miProducto.getFecha_retiro() != null 
                    ? new java.sql.Date(miProducto.getFecha_retiro().getTime()) 
                    : fIngreso;

            int idUnidad = miProducto.getUnidad_medida_id_unidad_medida() > 0 ? miProducto.getUnidad_medida_id_unidad_medida() : 1;
            int idUbicacion = miProducto.getUbicacion_almacen_id_ubicacion() > 0 ? miProducto.getUbicacion_almacen_id_ubicacion() : 1;

            // 3. Consulta de inserción
            String querySQL = "INSERT INTO producto (codigo_producto, nombre_producto, costo_unitario, "
                    + "fecha_ingreso, fecha_vencimiento, fecha_retiro, unidad_medida_id_unidad_medida, "
                    + "proveedor_id_proveedor, ubicacion_almacen_id_ubicacion, inventario_id_inventario, estado) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(querySQL);
            ps.setString(1, miProducto.getCodigo_producto());
            ps.setString(2, miProducto.getNombre_producto());
            ps.setBigDecimal(3, costo);
            ps.setDate(4, fIngreso);
            ps.setDate(5, fVencimiento);
            ps.setDate(6, fRetiro);
            ps.setInt(7, idUnidad);
            ps.setInt(8, miProducto.getProveedor_id_proveedor());
            ps.setInt(9, idUbicacion);
            ps.setInt(10, idInventarioGenerado);
            ps.setBoolean(11, miProducto.isEstado());

            ps.executeUpdate();

            try (Statement stmtFk = conn.createStatement()) {
                stmtFk.execute("SET FOREIGN_KEY_CHECKS = 1;");
            }

            conn.commit();
            return "OK";
        } catch (SQLException e) {
            System.out.println("Error InsertarProducto: " + e.getMessage());
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
            return e.getMessage();
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public boolean actualizarProducto(Producto miProducto) {
        boolean actualizar = false;
        Connection conn = miConexion.getConn();
        if (conn == null) {
            return false;
        }
        try {
            String querySQL = "UPDATE producto SET codigo_producto=?, nombre_producto=?, costo_unitario=?, "
                    + "fecha_ingreso=?, fecha_vencimiento=?, fecha_retiro=?, estado=?, "
                    + "proveedor_id_proveedor=?, unidad_medida_id_unidad_medida=?, "
                    + "ubicacion_almacen_id_ubicacion=?, inventario_id_inventario=? "
                    + "WHERE id_producto=?";
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ps.setString(1, miProducto.getCodigo_producto());
            ps.setString(2, miProducto.getNombre_producto());
            ps.setBigDecimal(3, miProducto.getCosto_unitario());

            ps.setDate(4, miProducto.getFecha_ingreso() != null ? new java.sql.Date(miProducto.getFecha_ingreso().getTime()) : null);
            ps.setDate(5, miProducto.getFecha_vencimiento() != null ? new java.sql.Date(miProducto.getFecha_vencimiento().getTime()) : null);
            ps.setDate(6, miProducto.getFecha_retiro() != null ? new java.sql.Date(miProducto.getFecha_retiro().getTime()) : null);

            ps.setBoolean(7, miProducto.isEstado());
            ps.setInt(8, miProducto.getProveedor_id_proveedor());
            ps.setInt(9, miProducto.getUnidad_medida_id_unidad_medida());
            ps.setInt(10, miProducto.getUbicacion_almacen_id_ubicacion());
            ps.setInt(11, miProducto.getInventario_id_inventario());
            ps.setInt(12, miProducto.getId_producto());
            if (ps.executeUpdate() > 0) {
                actualizar = true;
            }
        } catch (SQLException e) {
            System.out.println("Error actualizarProducto: " + e.getMessage());
        }
        return actualizar;
    }

    public boolean activarProducto(int id_producto) {
        boolean ok = false;
        String querySQL = "UPDATE producto SET estado = 1 WHERE id_producto = ?";
        Connection conn = miConexion.getConn();
        if (conn == null) {
            return false;
        }
        try {
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ps.setInt(1, id_producto);
            if (ps.executeUpdate() > 0) {
                ok = true;
            }
        } catch (SQLException e) {
            System.out.println("Error activarProducto: " + e.getMessage());
        }
        return ok;
    }

    public boolean inactivarProducto(int id_producto) {
        boolean ok = false;
        String querySQL = "UPDATE producto SET estado = 0 WHERE id_producto = ?";
        Connection conn = miConexion.getConn();
        if (conn == null) {
            return false;
        }
        try {
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ps.setInt(1, id_producto);
            if (ps.executeUpdate() > 0) {
                ok = true;
            }
        } catch (SQLException e) {
            System.out.println("Error inactivarProducto: " + e.getMessage());
        }
        return ok;
    }

    public boolean eliminarProducto(int id_producto) {
        boolean ok = false;
        Connection conn = miConexion.getConn();
        if (conn == null) {
            return false;
        }
        try {
            conn.setAutoCommit(false);

            try (Statement stmtFk = conn.createStatement()) {
                stmtFk.execute("SET FOREIGN_KEY_CHECKS = 0;");
            }

            int idInventario = 0;
            PreparedStatement psGet = conn.prepareStatement("SELECT inventario_id_inventario FROM producto WHERE id_producto = ?");
            psGet.setInt(1, id_producto);
            ResultSet rs = psGet.executeQuery();
            if (rs.next()) {
                idInventario = rs.getInt("inventario_id_inventario");
            }

            PreparedStatement psHist = conn.prepareStatement("DELETE FROM historial_cambios WHERE id_producto = ?");
            psHist.setInt(1, id_producto);
            psHist.executeUpdate();

            PreparedStatement psProd = conn.prepareStatement("DELETE FROM producto WHERE id_producto = ?");
            psProd.setInt(1, id_producto);
            int filasProd = psProd.executeUpdate();

            if (idInventario > 0) {
                PreparedStatement psInv = conn.prepareStatement("DELETE FROM inventario WHERE id_inventario = ?");
                psInv.setInt(1, idInventario);
                psInv.executeUpdate();
            }

            try (Statement stmtFk = conn.createStatement()) {
                stmtFk.execute("SET FOREIGN_KEY_CHECKS = 1;");
            }

            conn.commit();
            ok = (filasProd > 0);
        } catch (SQLException e) {
            System.out.println("Error eliminarProducto: " + e.getMessage());
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return ok;
    }

    public List<Producto> listarProductos() {
        List<Producto> lista = new ArrayList<>();
        Connection conn = miConexion.getConn();
        if (conn == null) {
            return lista;
        }
        String querySQL = "SELECT p.id_producto, p.codigo_producto, p.nombre_producto, p.costo_unitario, "
                + "p.fecha_ingreso, p.fecha_vencimiento, p.fecha_retiro, p.estado, "
                + "p.proveedor_id_proveedor, p.unidad_medida_id_unidad_medida, "
                + "p.ubicacion_almacen_id_ubicacion, p.inventario_id_inventario, "
                + "COALESCE(i.stock, 0) AS stock, "
                + "COALESCE(pr.nombre_proveedor, '') AS nombre_proveedor "
                + "FROM producto p "
                + "LEFT JOIN inventario i ON p.inventario_id_inventario = i.id_inventario "
                + "LEFT JOIN proveedor pr ON p.proveedor_id_proveedor = pr.id_proveedor";
        try {
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Producto p = mapRow(rs);
                p.setNombreProveedor(rs.getString("nombre_proveedor"));
                lista.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Error listarProductos: " + e.getMessage());
        }
        return lista;
    }

    public List<Producto> listarProductosPorProveedor(int id_proveedor) {
        List<Producto> lista = new ArrayList<>();
        Connection conn = miConexion.getConn();
        if (conn == null) {
            return lista;
        }
        String querySQL = "SELECT p.id_producto, p.codigo_producto, p.nombre_producto, p.costo_unitario, "
                + "p.fecha_ingreso, p.fecha_vencimiento, p.fecha_retiro, p.estado, "
                + "p.proveedor_id_proveedor, p.unidad_medida_id_unidad_medida, "
                + "p.ubicacion_almacen_id_ubicacion, p.inventario_id_inventario, "
                + "COALESCE(i.stock, 0) AS stock, "
                + "COALESCE(pr.nombre_proveedor, '') AS nombre_proveedor "
                + "FROM producto p "
                + "LEFT JOIN inventario i ON p.inventario_id_inventario = i.id_inventario "
                + "LEFT JOIN proveedor pr ON p.proveedor_id_proveedor = pr.id_proveedor "
                + "WHERE p.proveedor_id_proveedor = ? "
                + "ORDER BY p.nombre_producto ASC";
        try {
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ps.setInt(1, id_proveedor);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Producto p = mapRow(rs);
                p.setNombreProveedor(rs.getString("nombre_proveedor"));
                lista.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Error listarProductosPorProveedor: " + e.getMessage());
        }
        return lista;
    }

    public List<Producto> listarProductosPorNombreEmpresa(String nombreEmpresa) {
        List<Producto> lista = new ArrayList<>();
        Connection conn = miConexion.getConn();
        if (conn == null) {
            return lista;
        }
        String querySQL = "SELECT p.id_producto, p.codigo_producto, p.nombre_producto, p.costo_unitario, "
                + "p.fecha_ingreso, p.fecha_vencimiento, p.fecha_retiro, p.estado, "
                + "p.proveedor_id_proveedor, p.unidad_medida_id_unidad_medida, "
                + "p.ubicacion_almacen_id_ubicacion, p.inventario_id_inventario, "
                + "COALESCE(i.stock, 0) AS stock, "
                + "COALESCE(pr.nombre_proveedor, '') AS nombre_proveedor "
                + "FROM producto p "
                + "LEFT JOIN inventario i ON p.inventario_id_inventario = i.id_inventario "
                + "LEFT JOIN proveedor pr ON p.proveedor_id_proveedor = pr.id_proveedor "
                + "WHERE pr.nombre_proveedor = ? "
                + "ORDER BY p.nombre_producto ASC";
        try {
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ps.setString(1, nombreEmpresa);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Producto p = mapRow(rs);
                p.setNombreProveedor(rs.getString("nombre_proveedor"));
                lista.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Error listarProductosPorNombreEmpresa: " + e.getMessage());
        }
        return lista;
    }

    public boolean modificarStock(int inventario_id, int cantidad, String accion,
            int usuario_id, int producto_id, String descripcion) {
        boolean ok = false;
        Connection conn = miConexion.getConn();
        if (conn == null) {
            return false;
        }
        try {
            conn.setAutoCommit(false);

            int stockActual = 0;
            PreparedStatement psRead = conn.prepareStatement(
                    "SELECT stock FROM inventario WHERE id_inventario = ?");
            psRead.setInt(1, inventario_id);
            ResultSet rs = psRead.executeQuery();
            if (rs.next()) {
                stockActual = rs.getInt("stock");
            }

            int stockNuevo;
            if ("RETIRO_STOCK".equals(accion)) {
                // RESTRICCIÓN: Si la cantidad a retirar supera el stock actual, se revierte la operación
                if (cantidad > stockActual) {
                    System.out.println("Error: Cantidad a retirar (" + cantidad + ") excede el stock actual (" + stockActual + ")");
                    conn.rollback();
                    return false;
                }
                stockNuevo = stockActual - cantidad;
            } else {
                stockNuevo = stockActual + cantidad;
            }

            PreparedStatement psUpdate = conn.prepareStatement(
                    "UPDATE inventario SET stock = ?, fecha_actualizacion = NOW() WHERE id_inventario = ?");
            psUpdate.setInt(1, stockNuevo);
            psUpdate.setInt(2, inventario_id);
            psUpdate.executeUpdate();

            PreparedStatement psHist = conn.prepareStatement(
                    "INSERT INTO historial_cambios (id_usuario, id_producto, accion, "
                    + "stock_anterior, cantidad_modificada, stock_nuevo, descripcion, fecha_hora) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, NOW())");
            psHist.setInt(1, usuario_id);
            psHist.setInt(2, producto_id);
            psHist.setString(3, accion);
            psHist.setInt(4, stockActual);
            psHist.setInt(5, cantidad);
            psHist.setInt(6, stockNuevo);
            psHist.setString(7, descripcion);
            psHist.executeUpdate();

            conn.commit();
            ok = true;
        } catch (SQLException e) {
            System.out.println("Error modificarStock: " + e.getMessage());
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return ok;
    }
}