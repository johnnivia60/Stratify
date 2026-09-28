package Pruebas;
import Controlador.TipoDocumentoDAO;
import Modelo.TipoDocumento;
import java.util.Scanner;
public class PruebaConsultarTipoDocumento {
    public static void main (String [] args){
        TipoDocumentoDAO miTipoDocumentoDAO = new TipoDocumentoDAO();
        Scanner leer = new Scanner(System.in);
        String descripcion_tipo_documento;
        System.out.println("Por favor ingrese la descripcion del tipo de documento que quiera buscar");
        descripcion_tipo_documento = leer.nextLine();
        TipoDocumento miTipoDocumento = miTipoDocumentoDAO.consultarTipoDocumento(descripcion_tipo_documento);
        if (miTipoDocumento != null){
        System.out.println("id_tipo_documento " + miTipoDocumento.getId_tipo_documento());
        System.out.println("descripcion_tipo_documento " + miTipoDocumento.getDescripcion_tipo_documento());
        } else { System.out.println("Tipo de documento no encontrado"); }
    }
}
