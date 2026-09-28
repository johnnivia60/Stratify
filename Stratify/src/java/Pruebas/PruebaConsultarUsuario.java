/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Pruebas;

import Controlador.UsuarioDAO;
import Modelo.Usuario;
import java.util.Scanner;
public class PruebaConsultarUsuario {
    public static void main (String [] args){
        UsuarioDAO miUsuarioDAO = new UsuarioDAO();
        Scanner leer = new Scanner (System.in);
        String correo;
        System.out.println("Por favor ingrese el correo del usuario que quiera buscar");
        correo = leer.nextLine();
        
        Usuario miUsuario= miUsuarioDAO.consultarUsuario(correo);
        if (miUsuario != null){
        System.out.println("id_usuario " + miUsuario.getId_usuario());
        System.out.println("nombre "+ miUsuario.getNombre());
        System.out.println("apellido " + miUsuario.getApellido());
        System.out.println("password "+ miUsuario.getPassword());
        System.out.println("correo "+ miUsuario.getEmail());
        System.out.println("numero_documento "+  miUsuario.getNumero_documento());
        System.out.println("fecha_nacimiento "+ miUsuario.getFecha_nacimiento());
        System.out.println("fecha_vencimiento "+ miUsuario.getFecha_vencimiento());
        System.out.println("autorizacion "+ miUsuario.getAutorizacion());
        System.out.println("tipo_documento_id_tipo_documento " + miUsuario.getTipo_documento_id_tipo_documento());
        System.out.println("rol_id_rol "+ miUsuario.getRol_id_rol());
        }
        else{
        System.out.println("Usuario no encontrado");
        }
    
    
    }
}
