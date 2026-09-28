package Pruebas;
import java.util.Scanner;
import Controlador.UsuarioDAO;
public class PruebaActivarUsuario {
     public static void main (String[]args){
        Scanner sc = new Scanner (System.in);
        UsuarioDAO dao = new UsuarioDAO();
        System.out.println("Por favor ingrese el usuario a activar");
        int id_usuario = sc.nextInt();
        
        if (dao.activarUsuario(id_usuario)) {
            System.out.println("Se activo con exito");
        }else{
        System.out.println("No se encontro el usuario");
        }
        }
}
