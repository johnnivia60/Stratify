package Pruebas;

    import Controlador.UsuarioDAO;
    import Modelo.Usuario;
    import java.util.Scanner;
public class PruebaActualizarUsuario {
     public static void main(String[] args) {
        Scanner sc = new Scanner (System.in);
        Usuario miUsuario = new Usuario ();
        UsuarioDAO miUsuarioDAO = new UsuarioDAO ();
        System.out.println("Por favor Ingrese su ID de Usuario:");
        miUsuario.setId_usuario(sc.nextInt());
        System.out.println("Por Favor Ingrese su Nombre Actualizado:");
        miUsuario.setNombre(sc.nextLine());
        System.out.println("Por favor Ingrese su Apellido Actualizado:");
        miUsuario.setApellido(sc.nextLine());
        System.out.println("Por favor ingrese una nueva contraseña");
        miUsuario.setPassword(sc.nextLine());
        System.out.println("Por favor Ingrese su Correo Actualizado:");
        miUsuario.setEmail(sc.nextLine());
        System.out.println("Por favor Ingrese su Numero de Documento Actualizado:");
        miUsuario.setNumero_documento(sc.nextLine());
        System.out.println("Por favor ingrese  su fecha de nacimiento actualizada: ano-mes-dia: ");
        miUsuario.setFecha_nacimiento(java.sql.Date.valueOf(sc.nextLine()));
        System.out.println("Por favor ungrese su fecha de vencimiento actualizada: ano-mes-dia");
        miUsuario.setFecha_vencimiento(java.sql.Date.valueOf(sc.nextLine()));
        System.out.println("Por favor Ingrese su ID de tipo de Documento Actualizado:");
        miUsuario.setTipo_documento_id_tipo_documento(sc.nextInt());
        System.out.println("Por favor Ingrese su ID de Rol Actualizado:");
        miUsuario.setRol_id_rol(sc.nextInt());
        System.out.println("Por favor ingrese su estado actualizado: ");
        miUsuario.setEstado(sc.nextBoolean());
       
        boolean resultado = miUsuarioDAO.actualizarUsuario(miUsuario);
        if (resultado){
            System.out.println("El usuario se actualizo Correctamente");
        } else {
            System.out.println ("El usuario no se pudo actualizar");
        }
        sc.close ();
    }
}

   

