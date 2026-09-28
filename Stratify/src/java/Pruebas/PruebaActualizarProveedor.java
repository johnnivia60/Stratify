package Pruebas;

import Controlador.ProveedorDAO;
import Modelo.Proveedor;
import java.util.Scanner;
public class PruebaActualizarProveedor {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Proveedor miProveedor = new Proveedor();
        ProveedorDAO miProveedorDAO = new ProveedorDAO();
        System.out.println("Por favor ingrese el ID del proveedor:");
        miProveedor.setId_proveedor(sc.nextInt());
        sc.nextLine();
        System.out.println("Por favor ingrese el nombre actualizado:");
        miProveedor.setNombre_proveedor(sc.nextLine());
        System.out.println("Por favor ingrese la direccion actualizada:");
        miProveedor.setDireccion(sc.nextLine());
        System.out.println("Por favor ingrese el correo actualizado:");
        miProveedor.setCorreo_proveedor(sc.nextLine());
        System.out.println("Por favor ingrese el telefono actualizado:");
        miProveedor.setTelefono(sc.nextLine());

        boolean resultado = miProveedorDAO.actualizarProveedor(miProveedor);
        if (resultado){
            System.out.println("El proveedor se actualizo Correctamente");
        } else {
            System.out.println("El proveedor no se pudo actualizar");
        }
        sc.close();
    }
}
