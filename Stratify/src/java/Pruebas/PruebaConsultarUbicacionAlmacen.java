package Pruebas;
import Controlador.UbicacionAlmacenDAO;
import Modelo.UbicacionAlmacen;
import java.util.Scanner;
public class PruebaConsultarUbicacionAlmacen {
    public static void main (String [] args){
        UbicacionAlmacenDAO miUbicacionAlmacenDAO = new UbicacionAlmacenDAO();
        Scanner leer = new Scanner(System.in);
        String lugar_venta;
        System.out.println("Por favor ingrese el lugar de venta que quiera buscar");
        lugar_venta = leer.nextLine();
        UbicacionAlmacen miUbicacionAlmacen = miUbicacionAlmacenDAO.consultarUbicacionAlmacen(lugar_venta);
        if (miUbicacionAlmacen != null){
        System.out.println("id_ubicacion_almacen " + miUbicacionAlmacen.getId_ubicacion_almacen());
        System.out.println("lugar_venta " + miUbicacionAlmacen.getLugar_venta());
        System.out.println("direccion " + miUbicacionAlmacen.getDireccion());
        System.out.println("local " + miUbicacionAlmacen.getLocal());
        System.out.println("estado " + miUbicacionAlmacen.getEstado());
        } else { System.out.println("Ubicacion del almacen no encontrada"); }
    }
}
