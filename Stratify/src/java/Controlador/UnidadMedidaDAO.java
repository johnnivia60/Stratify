package Controlador;

import Modelo.UnidadMedida;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;


public class UnidadMedidaDAO {
    private Conexion miConexion = new Conexion();

    public UnidadMedida consultarUnidadMedida(int descripcion_unidad_medida){
        UnidadMedida miUnidadMedida = null;
        Connection conn = miConexion.getConn();
        String querySQL = "SELECT id_unidad_medida, descripcion_unidad_medida "
                + "FROM unidad_medida WHERE descripcion_unidad_medida = ?";
        try{
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ps.setInt(1, descripcion_unidad_medida);
            ResultSet rs = ps.executeQuery();

            if (rs.next()){
                miUnidadMedida = new UnidadMedida();
                miUnidadMedida.setId_unidad_medida(rs.getInt("id_unidad_medida"));
                miUnidadMedida.setDescripcion_unidad_medida(rs.getString("descripcion_unidad_medida"));
            }
            return miUnidadMedida;
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return miUnidadMedida;
    }

    public boolean InsertarUnidadMedida(UnidadMedida miUnidadMedida){
        boolean insertar = false;
        Connection conn = miConexion.getConn();
        try{
            String querySQL = "INSERT INTO unidad_medida (descripcion_unidad_medida) values (?)";
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ps.setString(1, miUnidadMedida.getDescripcion_unidad_medida());

            ps.executeUpdate();
            insertar = true;
            System.out.println("Dato insertado");
        } catch (SQLException e){
            System.out.println("Error al insertar la unidad de medida " + e.getMessage());
        }
        return insertar;
    }

    public boolean actualizarUnidadMedida(UnidadMedida miUnidadMedida){
        boolean actualizar = false;
        Connection conn = miConexion.getConn();

        try{
            String querySQL = "update unidad_medida set descripcion_unidad_medida = ? "
                    + "where id_unidad_medida = ?";
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ps.setString(1, miUnidadMedida.getDescripcion_unidad_medida());
            ps.setInt(2, miUnidadMedida.getId_unidad_medida());

            if (ps.executeUpdate() > 0){
                actualizar = true;
            }
            System.out.println("Dato actualizado");
        } catch (SQLException e){
            System.out.println("Error al actualizar la unidad de medida " + e.getMessage());
        }
        return actualizar;
    }

    public boolean eliminarUnidadMedida(int id_unidad_medida){
        boolean eliminarUnidadMedida = false;
        String querySQL = "DELETE FROM unidad_medida where id_unidad_medida = ?";
        Connection conn = miConexion.getConn();
        try{
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ps.setInt(1, id_unidad_medida);
            if (ps.executeUpdate() > 0){
                eliminarUnidadMedida = true;
            }
        } catch (SQLException e){
            System.out.println("Error al eliminar unidad de medida " + e.getMessage());
        }
        return eliminarUnidadMedida;
    }
}
