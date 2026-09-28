package Pruebas;
import Controlador.UbicacionAlmacenDAO;
import java.util.Scanner;
public class PruebaEliminarUbicacionAlmacen {
    public static void main(String[] args) {
        Scanner miScan = new Scanner(System.in);
        UbicacionAlmacenDAO miUbicacionAlmacenDAO = new UbicacionAlmacenDAO();
        System.out.println("Por favor ingrese la ubicacion del almacen a eliminar");
        int id = miScan.nextInt();
        if (miUbicacionAlmacenDAO.eliminarUbicacionAlmacen(id)) { System.out.println("Se Elimino con exito"); }
        else { System.out.println("No se encontro la ubicacion del almacen"); }
    }
}
