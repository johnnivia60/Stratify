package Pruebas;
import Controlador.TipoDocumentoDAO;
import Modelo.TipoDocumento;
import java.util.Scanner;
public class PruebaActualizarTipoDocumento {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        TipoDocumento miTipoDocumento = new TipoDocumento();
        TipoDocumentoDAO miTipoDocumentoDAO = new TipoDocumentoDAO();
        System.out.println("Por favor ingrese el ID del tipo de documento:");
        miTipoDocumento.setId_tipo_documento(sc.nextInt());
        sc.nextLine();
        System.out.println("Por favor ingrese la descripcion actualizada:");
        miTipoDocumento.setDescripcion_tipo_documento(sc.nextLine());
        boolean resultado = miTipoDocumentoDAO.actualizarTipoDocumento(miTipoDocumento);
        if (resultado){ System.out.println("El tipo de documento se actualizo Correctamente"); }
        else { System.out.println("El tipo de documento no se pudo actualizar"); }
        sc.close();
    }
}
