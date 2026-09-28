package Pruebas;
import Controlador.UbicacionAlmacenDAO;
import Modelo.UbicacionAlmacen;
import java.util.Scanner;
public class PruebaActualizarUbicacionAlmacen {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        UbicacionAlmacen miUbicacionAlmacen = new UbicacionAlmacen();
        UbicacionAlmacenDAO miUbicacionAlmacenDAO = new UbicacionAlmacenDAO();
        System.out.println("Por favor ingrese el ID de la ubicacion del almacen:");
        miUbicacionAlmacen.setId_ubicacion_almacen(sc.nextInt());
        sc.nextLine();
        System.out.println("Por favor ingrese el lugar de venta actualizado:");
        miUbicacionAlmacen.setLugar_venta(sc.nextLine());
        System.out.println("Por favor ingrese la direccion actualizada:");
        miUbicacionAlmacen.setDireccion(sc.nextLine());
        System.out.println("Por favor ingrese el local actualizado:");
        miUbicacionAlmacen.setLocal(sc.nextLine());
        System.out.println("Por favor ingrese el estado actualizado:");
        miUbicacionAlmacen.setEstado(sc.nextLine());
        boolean resultado = miUbicacionAlmacenDAO.actualizarUbicacionAlmacen(miUbicacionAlmacen);
        if (resultado){ System.out.println("La ubicacion del almacen se actualizo Correctamente"); }
        else { System.out.println("La ubicacion del almacen no se pudo actualizar"); }
        sc.close();
    }
}
