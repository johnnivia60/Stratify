package Controlador;

import Modelo.Rol;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;


public class RolDAO {
    private Conexion miConexion = new Conexion();

    public Rol consultarRol(String descripcion_rol){
        Rol miRol = null;
        Connection conn = miConexion.getConn();
        String querySQL = "SELECT id_rol, descripcion_rol FROM rol WHERE descripcion_rol = ?";
        try{
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ps.setString(1, descripcion_rol);
            ResultSet rs = ps.executeQuery();

            if (rs.next()){
                miRol = new Rol();
                miRol.setId_rol(rs.getInt("id_rol"));
                miRol.setDescripcion_rol(rs.getString("descripcion_rol"));
            }
            return miRol;
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return miRol;
    }

    public boolean InsertarRol(Rol miRol){
        boolean insertar = false;
        Connection conn = miConexion.getConn();
        try{
            String querySQL = "INSERT INTO rol (descripcion_rol) values (?)";
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ps.setString(1, miRol.getDescripcion_rol());

            ps.executeUpdate();
            insertar = true;
            System.out.println("Dato insertado");
        } catch (SQLException e){
            System.out.println("Error al insertar el rol " + e.getMessage());
        }
        return insertar;
    }

    public boolean actualizarRol(Rol miRol){
        boolean actualizar = false;
        Connection conn = miConexion.getConn();

        try{
            String querySQL = "update rol set descripcion_rol = ? where id_rol = ?";
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ps.setString(1, miRol.getDescripcion_rol());
            ps.setInt(2, miRol.getId_rol());

            if (ps.executeUpdate() > 0){
                actualizar = true;
            }
            System.out.println("Dato actualizado");
        } catch (SQLException e){
            System.out.println("Error al actualizar el rol " + e.getMessage());
        }
        return actualizar;
    }

    public boolean eliminarRol(int id_rol){
        boolean eliminarRol = false;
        String querySQL = "DELETE FROM rol where id_rol = ?";
        Connection conn = miConexion.getConn();
        try{
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ps.setInt(1, id_rol);
            if (ps.executeUpdate() > 0){
                eliminarRol = true;
            }
        } catch (SQLException e){
            System.out.println("Error al eliminar rol " + e.getMessage());
        }
        return eliminarRol;
    }
}
