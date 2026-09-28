package Pruebas;

import java.util.Scanner;
import Controlador.UsuarioDAO;
import Modelo.Usuario;
import java.util.Date;
public class PruebaInsertarUsuario {
public static void main(String[] args){
    Scanner sc = new Scanner(System.in);
    Usuario miUsuario = new Usuario();
    UsuarioDAO miUsuarioDAO = new UsuarioDAO();
    
    System.out.println("Por favcr ingrese un nombre: ");
    miUsuario.setNombre(sc.nextLine());
    System.out.println("Por favor ingrese un apellido: ");
    miUsuario.setApellido(sc.nextLine());
    System.out.println("Por favor ingrese una contrasena: ");
    miUsuario.setPassword(sc.nextLine());
    System.out.println("Por favor ingrese un correo: ");
    miUsuario.setEmail(sc.nextLine());
    System.out.println("Por favor ingrese un numero de documento: ");
    miUsuario.setNumero_documento(sc.nextLine());
    System.out.println("Por favor ingrese su fecha de nacimiento ano-mes-dia: ");
    miUsuario.setFecha_nacimiento(java.sql.Date.valueOf(sc.nextLine()));
    System.out.println("Por favor ingrese la fecha de vencimiento ano-mes-dia: ");
    miUsuario.setFecha_vencimiento(java.sql.Date.valueOf(sc.nextLine()));
    System.out.println("Por favor ingrese la autorizacion: ");
    miUsuario.setAutorizacion(sc.nextLine());
    System.out.println("Por favor ingrese estado: (ej: true o false)");
    miUsuario.setEstado(sc.nextBoolean());
    System.out.println("Por favor ingrese El tipo de documento: ");
    miUsuario.setTipo_documento_id_tipo_documento(sc.nextInt());
    System.out.println("Por favor ingrese su rol: ");
    miUsuario.setRol_id_rol(sc.nextInt());
    
    
    boolean resultado = miUsuarioDAO.InsertarUsuario(miUsuario);
    if(resultado){
        System.out.println("Usuario insertado correctamente");
    }else{
        System.out.println("El usuario no se inserto correctamente");
    }
}
}
