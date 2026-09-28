package Controlador;

import Modelo.UbicacionAlmacen;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;


public class UbicacionAlmacenDAO {
    private Conexion miConexion = new Conexion();

    public UbicacionAlmacen consultarUbicacionAlmacen(String lugar_venta){
        UbicacionAlmacen miUbicacionAlmacen = null;
        Connection conn = miConexion.getConn();
        String querySQL = "SELECT id_ubicacion_almacen, lugar_venta, direccion, local, estado "
                + "FROM ubicacion_almacen WHERE lugar_venta = ?";
        try{
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ps.setString(1, lugar_venta);
            ResultSet rs = ps.executeQuery();

            if (rs.next()){
                miUbicacionAlmacen = new UbicacionAlmacen();
                miUbicacionAlmacen.setId_ubicacion_almacen(rs.getInt("id_ubicacion_almacen"));
                miUbicacionAlmacen.setLugar_venta(rs.getString("lugar_venta"));
                miUbicacionAlmacen.setDireccion(rs.getString("direccion"));
                miUbicacionAlmacen.setLocal(rs.getString("local"));
                miUbicacionAlmacen.setEstado(rs.getString("estado"));
            }
            return miUbicacionAlmacen;
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return miUbicacionAlmacen;
    }

    public boolean InsertarUbicacionAlmacen(UbicacionAlmacen miUbicacionAlmacen){
        boolean insertar = false;
        Connection conn = miConexion.getConn();
        try{
            String querySQL = "INSERT INTO ubicacion_almacen (lugar_venta, direccion, local, estado) "
                    + "values (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ps.setString(1, miUbicacionAlmacen.getLugar_venta());
            ps.setString(2, miUbicacionAlmacen.getDireccion());
            ps.setString(3, miUbicacionAlmacen.getLocal());
            ps.setString(4, miUbicacionAlmacen.getEstado());

            ps.executeUpdate();
            insertar = true;
            System.out.println("Dato insertado");
        } catch (SQLException e){
            System.out.println("Error al insertar la ubicacion del almacen " + e.getMessage());
        }
        return insertar;
    }

    public boolean actualizarUbicacionAlmacen(UbicacionAlmacen miUbicacionAlmacen){
        boolean actualizar = false;
        Connection conn = miConexion.getConn();

        try{
            String querySQL = "update ubicacion_almacen set lugar_venta = ?, direccion = ?, "
                    + "local = ?, estado = ? where id_ubicacion_almacen = ?";
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ps.setString(1, miUbicacionAlmacen.getLugar_venta());
            ps.setString(2, miUbicacionAlmacen.getDireccion());
            ps.setString(3, miUbicacionAlmacen.getLocal());
            ps.setString(4, miUbicacionAlmacen.getEstado());
            ps.setInt(5, miUbicacionAlmacen.getId_ubicacion_almacen());

            if (ps.executeUpdate() > 0){
                actualizar = true;
            }
            System.out.println("Dato actualizado");
        } catch (SQLException e){
            System.out.println("Error al actualizar la ubicacion del almacen " + e.getMessage());
        }
        return actualizar;
    }

    public boolean eliminarUbicacionAlmacen(int id_ubicacion_almacen){
        boolean eliminarUbicacionAlmacen = false;
        String querySQL = "DELETE FROM ubicacion_almacen where id_ubicacion_almacen = ?";
        Connection conn = miConexion.getConn();
        try{
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ps.setInt(1, id_ubicacion_almacen);
            if (ps.executeUpdate() > 0){
                eliminarUbicacionAlmacen = true;
            }
        } catch (SQLException e){
            System.out.println("Error al eliminar ubicacion del almacen " + e.getMessage());
        }
        return eliminarUbicacionAlmacen;
    }
}
