package Pruebas;
import Controlador.RolDAO;
import Modelo.Rol;
import java.util.Scanner;
public class PruebaConsultarRol {
    public static void main (String [] args){
        RolDAO miRolDAO = new RolDAO();
        Scanner leer = new Scanner(System.in);
        String descripcion_rol;
        System.out.println("Por favor ingrese la descripcion del rol que quiera buscar");
        descripcion_rol = leer.nextLine();
        Rol miRol = miRolDAO.consultarRol(descripcion_rol);
        if (miRol != null){
        System.out.println("id_rol " + miRol.getId_rol());
        System.out.println("descripcion_rol " + miRol.getDescripcion_rol());
        } else { System.out.println("Rol no encontrado"); }
    }
}
