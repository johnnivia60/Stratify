package Pruebas;
import Controlador.ProveedorDAO;
import Modelo.Proveedor;
import java.util.Scanner;
public class PruebaConsultarProveedor {
    public static void main (String [] args){
        ProveedorDAO miProveedorDAO = new ProveedorDAO();
        Scanner leer = new Scanner(System.in);
        String nombre_proveedor;
        System.out.println("Por favor ingrese el nombre del proveedor que quiera buscar");
        nombre_proveedor = leer.nextLine();

        Proveedor miProveedor = miProveedorDAO.consultarProveedor(nombre_proveedor);
        if (miProveedor != null){
        System.out.println("id_proveedor " + miProveedor.getId_proveedor());
        System.out.println("nombre_proveedor " + miProveedor.getNombre_proveedor());
        System.out.println("direccion " + miProveedor.getDireccion());
        System.out.println("correo_proveedor " + miProveedor.getCorreo_proveedor());
        System.out.println("telefono " + miProveedor.getTelefono());
        } else {
        System.out.println("Proveedor no encontrado");
        }
    }
}
