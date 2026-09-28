
package Modelo;
import java.util.Date;

public class Usuario {
    private int id_usuario;
    private String nombre;
    private String apellido;
    private String password;
    private String email;
    private String numero_documento;
    private Date fecha_nacimiento;
    private Date fecha_vencimiento;
    private String autorizacion;
    private boolean estado;
    private int tipo_documento_id_tipo_documento;
    private int rol_id_rol;
    

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumero_documento() {
        return numero_documento;
    }

    public void setNumero_documento(String numero_documento) {
        this.numero_documento = numero_documento;
    }

    public Date getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    public void setFecha_nacimiento(Date fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }

    public Date getFecha_vencimiento() {
        return fecha_vencimiento;
    }

    public void setFecha_vencimiento(Date fecha_vencimiento) {
        this.fecha_vencimiento = fecha_vencimiento;
    }

    public String getAutorizacion() {
        return autorizacion;
    }

    public void setAutorizacion(String autorizacion) {
        this.autorizacion = autorizacion;
    }

    public int getTipo_documento_id_tipo_documento() {
        return tipo_documento_id_tipo_documento;
    }

    public void setTipo_documento_id_tipo_documento(int tipo_documento_id_tipo_documento) {
        this.tipo_documento_id_tipo_documento = tipo_documento_id_tipo_documento;
    }

    public int getRol_id_rol() {
        return rol_id_rol;
    }

    public void setRol_id_rol(int rol_id_rol) {
        this.rol_id_rol = rol_id_rol;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    
    
    
}
