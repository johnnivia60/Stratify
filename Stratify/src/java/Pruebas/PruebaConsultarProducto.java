
package Pruebas;
import Controlador.ProductoDAO;
import Modelo.Producto;
import java.util.Scanner;
public class PruebaConsultarProducto {
    public static void main (String [] args){
        ProductoDAO miProductoDAO = new ProductoDAO();
        Scanner leer = new Scanner (System.in);
        String nombre_producto;
        System.out.println("Por favor ingrese en nombre de su producto");
        nombre_producto = leer.nextLine();
        
        Producto miProducto = miProductoDAO.consultarProducto(nombre_producto);
        if (miProducto != null){
        System.out.println("id_producto: " + miProducto.getId_producto());
        System.out.println("codigo_producto: " + miProducto.getCodigo_producto());
        System.out.println("Nombre_producto: " + miProducto.getNombre_producto());
        System.out.println("Costo unitario: " + miProducto.getCosto_unitario());
        System.out.println("fecha ingreso: " +  miProducto.getFecha_ingreso());
        System.out.println();
        }
        else{
        System.out.println("Usuario no encontrado");
        }
    
    
    }
}
