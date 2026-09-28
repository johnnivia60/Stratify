package Pruebas;

import Controlador.Conexion;
import java.sql.Connection;

public class PruebaConexion {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       
   
        Conexion ConexionNueva = new Conexion();
        Connection Connection = ConexionNueva.getConn();
    }
}