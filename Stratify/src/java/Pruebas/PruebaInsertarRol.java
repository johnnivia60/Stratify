package Pruebas;
import java.util.Scanner;
import Controlador.RolDAO;
import Modelo.Rol;
public class PruebaInsertarRol {
public static void main(String[] args){
    Scanner sc = new Scanner(System.in);
    Rol miRol = new Rol();
    RolDAO miRolDAO = new RolDAO();
    System.out.println("Por favor ingrese la descripcion del rol: ");
    miRol.setDescripcion_rol(sc.nextLine());
    boolean resultado = miRolDAO.InsertarRol(miRol);
    if(resultado){ System.out.println("Rol insertado correctamente"); }
    else{ System.out.println("El rol no se inserto correctamente"); }
}
}
