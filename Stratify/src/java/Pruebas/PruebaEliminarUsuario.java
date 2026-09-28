package Pruebas;
import Controlador.UsuarioDAO;

import java.util.Scanner;

public class PruebaEliminarUsuario {

    public static void main(String[] args) {
        Scanner miScan = new Scanner(System.in);
        UsuarioDAO miUsuarioDAO = new UsuarioDAO();
       
        System.out.println("Por favor ingrese el usuario a eliminar");
        int id = miScan.nextInt();
       
        if (miUsuarioDAO.eliminarUsuario(id)) {
            System.out.println("Se Elimino con exito");
        } else {
       
            System.out.println("No se encontro el usuario");
        }
    }
   
}


