package Controlador;

import Modelo.Proveedor;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAO {
    private Conexion miConexion = new Conexion();

    public Proveedor consultarProveedor(String nombre_proveedor) {
        Proveedor miProveedor = null;
        Connection conn = miConexion.getConn();
        if (conn == null) return null;
        String querySQL = "SELECT id_proveedor, nit_proveedor, nombre_proveedor, direccion, correo_proveedor, telefono "
                + "FROM proveedor WHERE nombre_proveedor = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ps.setString(1, nombre_proveedor);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                miProveedor = mapRow(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error consultarProveedor: " + e.getMessage());
        }
        return miProveedor;
    }

    public Proveedor consultarProveedorPorId(int id_proveedor) {
        Proveedor miProveedor = null;
        Connection conn = miConexion.getConn();
        if (conn == null) return null;
        String querySQL = "SELECT id_proveedor, nit_proveedor, nombre_proveedor, direccion, correo_proveedor, telefono "
                + "FROM proveedor WHERE id_proveedor = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ps.setInt(1, id_proveedor);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                miProveedor = mapRow(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error consultarProveedorPorId: " + e.getMessage());
        }
        return miProveedor;
    }

    private Proveedor mapRow(ResultSet rs) throws SQLException {
        Proveedor p = new Proveedor();
        p.setId_proveedor(rs.getInt("id_proveedor"));
        p.setNit_proveedor(rs.getString("nit_proveedor"));
        p.setNombre_proveedor(rs.getString("nombre_proveedor"));
        p.setDireccion(rs.getString("direccion"));
        p.setCorreo_proveedor(rs.getString("correo_proveedor"));
        p.setTelefono(rs.getString("telefono"));
        return p;
    }

    /**
     * Lista todos los proveedores incluyendo el numero de productos
     * asociados a cada uno (util para mostrarlo en el directorio y para
     * saber si se puede eliminar sin romper la integridad referencial).
     */
    public List<Proveedor> listarProveedores() {
        List<Proveedor> lista = new ArrayList<>();
        Connection conn = miConexion.getConn();
        if (conn == null) return lista;
        String querySQL = "SELECT pr.id_proveedor, pr.nit_proveedor, pr.nombre_proveedor, pr.direccion, "
                + "pr.correo_proveedor, pr.telefono, "
                + "(SELECT COUNT(*) FROM producto p WHERE p.proveedor_id_proveedor = pr.id_proveedor) AS total_productos "
                + "FROM proveedor pr ORDER BY pr.nombre_proveedor ASC";
        try {
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Proveedor p = mapRow(rs);
                p.setProductosAsociados(rs.getInt("total_productos"));
                lista.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Error listarProveedores: " + e.getMessage());
        }
        return lista;
    }

    public boolean InsertarProveedor(Proveedor miProveedor) {
        boolean insertar = false;
        Connection conn = miConexion.getConn();
        if (conn == null) return false;
        try {
            String querySQL = "INSERT INTO proveedor (nit_proveedor, nombre_proveedor, direccion, correo_proveedor, telefono) "
                    + "values (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ps.setString(1, miProveedor.getNit_proveedor());
            ps.setString(2, miProveedor.getNombre_proveedor());
            ps.setString(3, miProveedor.getDireccion());
            ps.setString(4, miProveedor.getCorreo_proveedor());
            ps.setString(5, miProveedor.getTelefono());
            ps.executeUpdate();
            insertar = true;
        } catch (SQLException e) {
            System.out.println("Error InsertarProveedor: " + e.getMessage());
        }
        return insertar;
    }

    public boolean actualizarProveedor(Proveedor miProveedor) {
        boolean actualizar = false;
        Connection conn = miConexion.getConn();
        if (conn == null) return false;
        try {
            String querySQL = "UPDATE proveedor SET nit_proveedor=?, nombre_proveedor=?, direccion=?, "
                    + "correo_proveedor=?, telefono=? WHERE id_proveedor=?";
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ps.setString(1, miProveedor.getNit_proveedor());
            ps.setString(2, miProveedor.getNombre_proveedor());
            ps.setString(3, miProveedor.getDireccion());
            ps.setString(4, miProveedor.getCorreo_proveedor());
            ps.setString(5, miProveedor.getTelefono());
            ps.setInt(6, miProveedor.getId_proveedor());
            if (ps.executeUpdate() > 0) {
                actualizar = true;
            }
        } catch (SQLException e) {
            System.out.println("Error actualizarProveedor: " + e.getMessage());
        }
        return actualizar;
    }

    /**
     * Cuenta cuantos productos tiene asociados un proveedor. Se usa antes de
     * eliminar para poder devolver un mensaje claro en vez de un error
     * generico de restriccion de llave foranea.
     */
    public int contarProductosAsociados(int id_proveedor) {
        int total = 0;
        Connection conn = miConexion.getConn();
        if (conn == null) return 0;
        String querySQL = "SELECT COUNT(*) AS total FROM producto WHERE proveedor_id_proveedor = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ps.setInt(1, id_proveedor);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("Error contarProductosAsociados: " + e.getMessage());
        }
        return total;
    }

    public boolean eliminarProveedor(int id_proveedor) {
        boolean ok = false;
        String querySQL = "DELETE FROM proveedor WHERE id_proveedor = ?";
        Connection conn = miConexion.getConn();
        if (conn == null) return false;
        try {
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ps.setInt(1, id_proveedor);
            if (ps.executeUpdate() > 0) {
                ok = true;
            }
        } catch (SQLException e) {
            System.out.println("Error eliminarProveedor: " + e.getMessage());
        }
        return ok;
    }
}
