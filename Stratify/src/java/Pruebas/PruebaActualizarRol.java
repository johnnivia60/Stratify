package Pruebas;
import Controlador.RolDAO;
import Modelo.Rol;
import java.util.Scanner;
public class PruebaActualizarRol {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Rol miRol = new Rol();
        RolDAO miRolDAO = new RolDAO();
        System.out.println("Por favor ingrese el ID del rol:");
        miRol.setId_rol(sc.nextInt());
        sc.nextLine();
        System.out.println("Por favor ingrese la descripcion actualizada:");
        miRol.setDescripcion_rol(sc.nextLine());
        boolean resultado = miRolDAO.actualizarRol(miRol);
        if (resultado){ System.out.println("El rol se actualizo Correctamente"); }
        else { System.out.println("El rol no se pudo actualizar"); }
        sc.close();
    }
}
