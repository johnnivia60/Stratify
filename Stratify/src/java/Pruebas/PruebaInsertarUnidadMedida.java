package Pruebas;
import java.util.Scanner;
import Controlador.UnidadMedidaDAO;
import Modelo.UnidadMedida;
public class PruebaInsertarUnidadMedida {
public static void main(String[] args){
    Scanner sc = new Scanner(System.in);
    UnidadMedida miUnidadMedida = new UnidadMedida();
    UnidadMedidaDAO miUnidadMedidaDAO = new UnidadMedidaDAO();
    System.out.println("Por favor ingrese la descripcion de la unidad de medida: ");
    miUnidadMedida.setDescripcion_unidad_medida(sc.nextLine());
    boolean resultado = miUnidadMedidaDAO.InsertarUnidadMedida(miUnidadMedida);
    if(resultado){ System.out.println("Unidad de medida insertada correctamente"); }
    else{ System.out.println("La unidad de medida no se inserto correctamente"); }
}
}
