package Controlador;

import Modelo.TipoDocumento;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;


public class TipoDocumentoDAO {
    private Conexion miConexion = new Conexion();

    public TipoDocumento consultarTipoDocumento(String descripcion_tipo_documento){
        TipoDocumento miTipoDocumento = null;
        Connection conn = miConexion.getConn();
        String querySQL = "SELECT id_tipo_documento, descripcion_tipo_documento "
                + "FROM tipo_documento WHERE descripcion_tipo_documento = ?";
        try{
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ps.setString(1, descripcion_tipo_documento);
            ResultSet rs = ps.executeQuery();

            if (rs.next()){
                miTipoDocumento = new TipoDocumento();
                miTipoDocumento.setId_tipo_documento(rs.getInt("id_tipo_documento"));
                miTipoDocumento.setDescripcion_tipo_documento(rs.getString("descripcion_tipo_documento"));
            }
            return miTipoDocumento;
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return miTipoDocumento;
    }

    public boolean InsertarTipoDocumento(TipoDocumento miTipoDocumento){
        boolean insertar = false;
        Connection conn = miConexion.getConn();
        try{
            String querySQL = "INSERT INTO tipo_documento (descripcion_tipo_documento) values (?)";
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ps.setString(1, miTipoDocumento.getDescripcion_tipo_documento());

            ps.executeUpdate();
            insertar = true;
            System.out.println("Dato insertado");
        } catch (SQLException e){
            System.out.println("Error al insertar el tipo de documento " + e.getMessage());
        }
        return insertar;
    }

    public boolean actualizarTipoDocumento(TipoDocumento miTipoDocumento){
        boolean actualizar = false;
        Connection conn = miConexion.getConn();

        try{
            String querySQL = "update tipo_documento set descripcion_tipo_documento = ? "
                    + "where id_tipo_documento = ?";
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ps.setString(1, miTipoDocumento.getDescripcion_tipo_documento());
            ps.setInt(2, miTipoDocumento.getId_tipo_documento());

            if (ps.executeUpdate() > 0){
                actualizar = true;
            }
            System.out.println("Dato actualizado");
        } catch (SQLException e){
            System.out.println("Error al actualizar el tipo de documento " + e.getMessage());
        }
        return actualizar;
    }

    public boolean eliminarTipoDocumento(int id_tipo_documento){
        boolean eliminarTipoDocumento = false;
        String querySQL = "DELETE FROM tipo_documento where id_tipo_documento = ?";
        Connection conn = miConexion.getConn();
        try{
            PreparedStatement ps = conn.prepareStatement(querySQL);
            ps.setInt(1, id_tipo_documento);
            if (ps.executeUpdate() > 0){
                eliminarTipoDocumento = true;
            }
        } catch (SQLException e){
            System.out.println("Error al eliminar tipo de documento " + e.getMessage());
        }
        return eliminarTipoDocumento;
    }
}
