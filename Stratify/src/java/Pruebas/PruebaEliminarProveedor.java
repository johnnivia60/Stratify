package Pruebas;
import Controlador.ProveedorDAO;
import java.util.Scanner;
public class PruebaEliminarProveedor {
    public static void main(String[] args) {
        Scanner miScan = new Scanner(System.in);
        ProveedorDAO miProveedorDAO = new ProveedorDAO();
        System.out.println("Por favor ingrese el proveedor a eliminar");
        int id = miScan.nextInt();
        if (miProveedorDAO.eliminarProveedor(id)) {
            System.out.println("Se Elimino con exito");
        } else {
            System.out.println("No se encontro el proveedor");
        }
    }
}
