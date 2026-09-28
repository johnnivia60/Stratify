package Pruebas;

import java.util.Scanner;
import Controlador.ProveedorDAO;
import Modelo.Proveedor;
public class PruebaInsertarProveedor {
public static void main(String[] args){
    Scanner sc = new Scanner(System.in);
    Proveedor miProveedor = new Proveedor();
    ProveedorDAO miProveedorDAO = new ProveedorDAO();

    System.out.println("Por favor ingrese el nombre del proveedor: ");
    miProveedor.setNombre_proveedor(sc.nextLine());
    System.out.println("Por favor ingrese la direccion: ");
    miProveedor.setDireccion(sc.nextLine());
    System.out.println("Por favor ingrese el correo del proveedor: ");
    miProveedor.setCorreo_proveedor(sc.nextLine());
    System.out.println("Por favor ingrese el telefono: ");
    miProveedor.setTelefono(sc.nextLine());

    boolean resultado = miProveedorDAO.InsertarProveedor(miProveedor);
    if(resultado){
        System.out.println("Proveedor insertado correctamente");
    }else{
        System.out.println("El proveedor no se inserto correctamente");
    }
}
}
