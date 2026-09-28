
package Modelo;

public class UbicacionAlmacen {
    private int id_ubicacion_almacen;
    private String lugar_venta;
    private String direccion;
    private String local;
    private String estado;

    public int getId_ubicacion_almacen() {
        return id_ubicacion_almacen;
    }

    public void setId_ubicacion_almacen(int id_ubicacion_almacen) {
        this.id_ubicacion_almacen = id_ubicacion_almacen;
    }

    public String getLugar_venta() {
        return lugar_venta;
    }

    public void setLugar_venta(String lugar_venta) {
        this.lugar_venta = lugar_venta;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
}
