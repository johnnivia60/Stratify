package Pruebas;
import Controlador.UnidadMedidaDAO;
import java.util.Scanner;
public class PruebaEliminarUnidadMedida {
    public static void main(String[] args) {
        Scanner miScan = new Scanner(System.in);
        UnidadMedidaDAO miUnidadMedidaDAO = new UnidadMedidaDAO();
        System.out.println("Por favor ingrese la unidad de medida a eliminar");
        int id = miScan.nextInt();
        if (miUnidadMedidaDAO.eliminarUnidadMedida(id)) { System.out.println("Se Elimino con exito"); }
        else { System.out.println("No se encontro la unidad de medida"); }
    }
}
