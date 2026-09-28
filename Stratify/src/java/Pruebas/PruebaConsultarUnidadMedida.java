package Pruebas;
import Controlador.UnidadMedidaDAO;
import Modelo.UnidadMedida;
import java.util.Scanner;
public class PruebaConsultarUnidadMedida {
    public static void main (String [] args){
        UnidadMedidaDAO miUnidadMedidaDAO = new UnidadMedidaDAO();
        Scanner leer = new Scanner(System.in);
        int descripcion_unidad_medida;
        System.out.println("Por favor ingrese la descripcion de la unidad de medida que quiera buscar");
        descripcion_unidad_medida = leer.nextInt();
        UnidadMedida miUnidadMedida = miUnidadMedidaDAO.consultarUnidadMedida(descripcion_unidad_medida);
        if (miUnidadMedida != null){
        System.out.println("id_unidad_medida " + miUnidadMedida.getId_unidad_medida());
        System.out.println("descripcion_unidad_medida " + miUnidadMedida.getDescripcion_unidad_medida());
        } else { System.out.println("Unidad de medida no encontrada"); }
    }
}
