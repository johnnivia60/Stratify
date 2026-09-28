package Pruebas;
import Controlador.TipoDocumentoDAO;
import java.util.Scanner;
public class PruebaEliminarTipoDocumento {
    public static void main(String[] args) {
        Scanner miScan = new Scanner(System.in);
        TipoDocumentoDAO miTipoDocumentoDAO = new TipoDocumentoDAO();
        System.out.println("Por favor ingrese el tipo de documento a eliminar");
        int id = miScan.nextInt();
        if (miTipoDocumentoDAO.eliminarTipoDocumento(id)) { System.out.println("Se Elimino con exito"); }
        else { System.out.println("No se encontro el tipo de documento"); }
    }
}
