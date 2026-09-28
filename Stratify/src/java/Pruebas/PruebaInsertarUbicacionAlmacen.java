package Pruebas;
import java.util.Scanner;
import Controlador.UbicacionAlmacenDAO;
import Modelo.UbicacionAlmacen;
public class PruebaInsertarUbicacionAlmacen {
public static void main(String[] args){
    Scanner sc = new Scanner(System.in);
    UbicacionAlmacen miUbicacionAlmacen = new UbicacionAlmacen();
    UbicacionAlmacenDAO miUbicacionAlmacenDAO = new UbicacionAlmacenDAO();
    System.out.println("Por favor ingrese el lugar de venta: ");
    miUbicacionAlmacen.setLugar_venta(sc.nextLine());
    System.out.println("Por favor ingrese la direccion: ");
    miUbicacionAlmacen.setDireccion(sc.nextLine());
    System.out.println("Por favor ingrese el local: ");
    miUbicacionAlmacen.setLocal(sc.nextLine());
    System.out.println("Por favor ingrese el estado: ");
    miUbicacionAlmacen.setEstado(sc.nextLine());
    boolean resultado = miUbicacionAlmacenDAO.InsertarUbicacionAlmacen(miUbicacionAlmacen);
    if(resultado){ System.out.println("Ubicacion del almacen insertada correctamente"); }
    else{ System.out.println("La ubicacion del almacen no se inserto correctamente"); }
}
}
