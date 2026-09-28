package Pruebas;
import java.util.Scanner;
import Controlador.TipoDocumentoDAO;
import Modelo.TipoDocumento;
public class PruebaInsertarTipoDocumento {
public static void main(String[] args){
    Scanner sc = new Scanner(System.in);
    TipoDocumento miTipoDocumento = new TipoDocumento();
    TipoDocumentoDAO miTipoDocumentoDAO = new TipoDocumentoDAO();
    System.out.println("Por favor ingrese la descripcion del tipo de documento: ");
    miTipoDocumento.setDescripcion_tipo_documento(sc.nextLine());
    boolean resultado = miTipoDocumentoDAO.InsertarTipoDocumento(miTipoDocumento);
    if(resultado){ System.out.println("Tipo de documento insertado correctamente"); }
    else{ System.out.println("El tipo de documento no se inserto correctamente"); }
}
}
