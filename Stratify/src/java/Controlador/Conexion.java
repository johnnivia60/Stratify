package Controlador;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexion {

    private Connection conn;
    private String driver = "com.mysql.cj.jdbc.Driver";
    private String user = "root";
    private String password = "rseaNOaugawaTTNdnYpyComQGrXnWyMg";
    private String baseDatos = "railway";
    private String url = "jdbc:mysql://altaria.proxy.rlwy.net:33980/" + baseDatos
            + "?useTimezone=true&serverTimezone=America/Bogota&useSSL=false&allowPublicKeyRetrieval=true";

    public Conexion() {
        conn = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, password);
            if (conn == null) {
                System.out.println("No se establecio la conexion: " + url);
            } else {
                System.out.println("Conexion exitosa con: " + baseDatos);
            }
        } catch (Exception e) {
            System.err.println("Error conexion JDBC: " + e.getMessage());
        }
    }

    public Connection getConn() {
        return conn;
    }
}
