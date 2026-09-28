package Pruebas;
import Controlador.RolDAO;
import java.util.Scanner;
public class PruebaEliminarRol {
    public static void main(String[] args) {
        Scanner miScan = new Scanner(System.in);
        RolDAO miRolDAO = new RolDAO();
        System.out.println("Por favor ingrese el rol a eliminar");
        int id = miScan.nextInt();
        if (miRolDAO.eliminarRol(id)) { System.out.println("Se Elimino con exito"); }
        else { System.out.println("No se encontro el rol"); }
    }
}
