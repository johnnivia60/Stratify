package Pruebas;
import Controlador.UnidadMedidaDAO;
import Modelo.UnidadMedida;
import java.util.Scanner;
public class PruebaActualizarUnidadMedida {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        UnidadMedida miUnidadMedida = new UnidadMedida();
        UnidadMedidaDAO miUnidadMedidaDAO = new UnidadMedidaDAO();
        System.out.println("Por favor ingrese el ID de la unidad de medida:");
        miUnidadMedida.setId_unidad_medida(sc.nextInt());
        System.out.println("Por favor ingrese la descripcion actualizada:");
        miUnidadMedida.setDescripcion_unidad_medida(sc.nextLine());
        boolean resultado = miUnidadMedidaDAO.actualizarUnidadMedida(miUnidadMedida);
        if (resultado){ System.out.println("La unidad de medida se actualizo Correctamente"); }
        else { System.out.println("La unidad de medida no se pudo actualizar"); }
        sc.close();
    }
}
