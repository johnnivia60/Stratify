package Modelo;
import java.math.BigDecimal;
import java.util.Date;

public class Producto {
    private int id_producto;
    private String codigo_producto;
    private String nombre_producto;
    private BigDecimal costo_unitario;
    private Date fecha_ingreso;
    private Date fecha_vencimiento;
    private Date fecha_retiro;
    private boolean estado;
    private int proveedor_id_proveedor;
    private int unidad_medida_id_unidad_medida;
    private int ubicacion_almacen_id_ubicacion;
    private int inventario_id_inventario;
    // campos extra para vistas
    private int stock;
    private String nombreProveedor;

    public int getId_producto() { return id_producto; }
    public void setId_producto(int id_producto) { this.id_producto = id_producto; }

    public String getCodigo_producto() { return codigo_producto; }
    public void setCodigo_producto(String codigo_producto) { this.codigo_producto = codigo_producto; }

    public String getNombre_producto() { return nombre_producto; }
    public void setNombre_producto(String nombre_producto) { this.nombre_producto = nombre_producto; }

    public BigDecimal getCosto_unitario() { return costo_unitario; }
    public void setCosto_unitario(BigDecimal costo_unitario) { this.costo_unitario = costo_unitario; }

    public Date getFecha_ingreso() { return fecha_ingreso; }
    public void setFecha_ingreso(Date fecha_ingreso) { this.fecha_ingreso = fecha_ingreso; }

    public Date getFecha_vencimiento() {
        return fecha_vencimiento;
    }

    public void setFecha_vencimiento(Date fecha_vencimiento) {
        this.fecha_vencimiento = fecha_vencimiento;
    }


    public Date getFecha_retiro() { return fecha_retiro; }
    public void setFecha_retiro(Date fecha_retiro) { this.fecha_retiro = fecha_retiro; }

    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }

    public int getProveedor_id_proveedor() { return proveedor_id_proveedor; }
    public void setProveedor_id_proveedor(int proveedor_id_proveedor) { this.proveedor_id_proveedor = proveedor_id_proveedor; }

    public int getUnidad_medida_id_unidad_medida() { return unidad_medida_id_unidad_medida; }
    public void setUnidad_medida_id_unidad_medida(int unidad_medida_id_unidad_medida) { this.unidad_medida_id_unidad_medida = unidad_medida_id_unidad_medida; }

    public int getUbicacion_almacen_id_ubicacion() { return ubicacion_almacen_id_ubicacion; }
    public void setUbicacion_almacen_id_ubicacion(int ubicacion_almacen_id_ubicacion) { this.ubicacion_almacen_id_ubicacion = ubicacion_almacen_id_ubicacion; }

    // Mantener compatibilidad con nombre antiguo usado en Pruebas
    public int getUbicacion_id_ubicion() { return ubicacion_almacen_id_ubicacion; }
    public void setUbicacion_id_ubicion(int v) { this.ubicacion_almacen_id_ubicacion = v; }

    public int getInventario_id_inventario() { return inventario_id_inventario; }
    public void setInventario_id_inventario(int inventario_id_inventario) { this.inventario_id_inventario = inventario_id_inventario; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getNombreProveedor() { return nombreProveedor; }
    public void setNombreProveedor(String nombreProveedor) { this.nombreProveedor = nombreProveedor; }
}
