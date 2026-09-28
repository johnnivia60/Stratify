package Servlet;

import Modelo.Producto;
import Modelo.Solicitud;
import Controlador.ProductoDAO;
import Controlador.SolicitudDAO;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@WebServlet(name = "ProductoServlet", urlPatterns = {"/ProductoServlet"})
public class ProductoServlet extends HttpServlet {

    private ProductoDAO productoDAO = new ProductoDAO();
    private SolicitudDAO solicitudDAO = new SolicitudDAO();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("id_usuario") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"status\":\"error\", \"mensaje\":\"Sesión no válida.\"}");
            return;
        }

        String accion = request.getParameter("accion");
        PrintWriter out = response.getWriter();

        if ("consultar".equals(accion)) {
            String nombre = request.getParameter("nombre_producto");
            Producto p = productoDAO.consultarProducto(nombre);
            out.write(gson.toJson(p));
        } else if ("consultarPorId".equals(accion)) {
            try {
                int id = Integer.parseInt(request.getParameter("id_producto"));
                Producto p = productoDAO.consultarProductoPorId(id);
                out.write(gson.toJson(p));
            } catch (Exception e) {
                out.write("null");
            }
        } else if ("por_proveedor".equals(accion)) {
            try {
                int idProv = Integer.parseInt(request.getParameter("id_proveedor"));
                List<Producto> lista = productoDAO.listarProductosPorProveedor(idProv);
                out.write(gson.toJson(lista));
            } catch (Exception e) {
                out.write("[]");
            }
        } else if ("por_empresa".equals(accion)) {
            String empresa = request.getParameter("empresa");
            List<Producto> lista = productoDAO.listarProductosPorNombreEmpresa(empresa);
            out.write(gson.toJson(lista));
        } else {
            List<Producto> lista = productoDAO.listarProductos();
            out.write(gson.toJson(lista));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("id_usuario") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"status\":\"error\", \"mensaje\":\"Sesión no activa.\"}");
            return;
        }

        String accion = request.getParameter("accion");
        PrintWriter out = response.getWriter();

        if ("solicitar".equals(accion)) {
            try {
                Object objUser = session.getAttribute("id_usuario");
                if (objUser == null) {
                    out.write("{\"status\":\"error\", \"mensaje\":\"Sesión no válida. Por favor, vuelve a iniciar sesión.\"}");
                    return;
                }

                // Conversión segura de Integer o String
                int idUsuario = (objUser instanceof Integer) ? (Integer) objUser : Integer.parseInt(objUser.toString());

                String strIdProd = request.getParameter("id_producto");
                if (strIdProd == null || strIdProd.trim().isEmpty()) {
                    strIdProd = request.getParameter("producto_id_producto");
                }

                int idProducto = Integer.parseInt(strIdProd);
                int idProveedor = Integer.parseInt(request.getParameter("proveedor_id_proveedor"));
                int cantidad = Integer.parseInt(request.getParameter("cantidad"));

                Solicitud miSolicitud = new Solicitud();
                miSolicitud.setCantidad(cantidad);
                miSolicitud.setIdUsuario(idUsuario);
                miSolicitud.setIdProveedor(idProveedor);
                miSolicitud.setIdProducto(idProducto);

                boolean exito = solicitudDAO.crearSolicitud(miSolicitud);

                if (exito) {
                    out.write("{\"status\":\"success\", \"mensaje\":\"Solicitud enviada al proveedor correctamente.\"}");
                } else {
                    out.write("{\"status\":\"error\", \"mensaje\":\"No se pudo registrar la solicitud en la base de datos.\"}");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error de formato numérico en ProductoServlet: " + e.getMessage());
                out.write("{\"status\":\"error\", \"mensaje\":\"Los parámetros enviados no son válidos.\"}");
            } catch (Exception e) {
                System.out.println("Error general procesando solicitud: " + e.getMessage());
                out.write("{\"status\":\"error\", \"mensaje\":\"Error interno al procesar la solicitud.\"}");
            }
            } else if ("insertar".equals(accion) || "crear".equals(accion)) {
            try {
                Producto p = parseProductoFromRequest(request);
                String resp = productoDAO.InsertarProducto(p);

                if ("OK".equals(resp)) {
                    out.write("{\"status\":\"success\", \"mensaje\":\"Producto registrado correctamente.\"}");
                } else {
                    out.write("{\"status\":\"error\", \"mensaje\":\"" + resp.replace("\"", "'") + "\"}");
                }
            } catch (Exception e) {
                out.write("{\"status\":\"error\", \"mensaje\":\"Error al procesar el producto: " + e.getMessage() + "\"}");
            }

        } else if ("actualizar".equals(accion)) {
            try {
                Producto p = parseProductoFromRequest(request);
                p.setId_producto(Integer.parseInt(request.getParameter("id_producto")));
                boolean ok = productoDAO.actualizarProducto(p);

                if (ok) {
                    out.write("{\"status\":\"success\", \"mensaje\":\"Producto actualizado correctamente.\"}");
                } else {
                    out.write("{\"status\":\"error\", \"mensaje\":\"No se pudo actualizar el producto.\"}");
                }
            } catch (Exception e) {
                out.write("{\"status\":\"error\", \"mensaje\":\"Error al actualizar: " + e.getMessage() + "\"}");
            }

        } else if ("eliminar".equals(accion)) {
            try {
                int id = Integer.parseInt(request.getParameter("id_producto"));
                boolean ok = productoDAO.eliminarProducto(id);

                if (ok) {
                    out.write("{\"status\":\"success\", \"mensaje\":\"Producto eliminado con éxito.\"}");
                } else {
                    out.write("{\"status\":\"error\", \"mensaje\":\"No se pudo eliminar el producto.\"}");
                }
            } catch (Exception e) {
                out.write("{\"status\":\"error\", \"mensaje\":\"ID de producto no válido.\"}");
            }

        } else if ("activar".equals(accion)) {
            try {
                int id = Integer.parseInt(request.getParameter("id_producto"));
                boolean ok = productoDAO.activarProducto(id);
                out.write("{\"status\":\"" + (ok ? "success" : "error") + "\"}");
            } catch (Exception e) {
                out.write("{\"status\":\"error\"}");
            }

        } else if ("inactivar".equals(accion)) {
            try {
                int id = Integer.parseInt(request.getParameter("id_producto"));
                boolean ok = productoDAO.inactivarProducto(id);
                out.write("{\"status\":\"" + (ok ? "success" : "error") + "\"}");
            } catch (Exception e) {
                out.write("{\"status\":\"error\"}");
            }
        }
    }

    private Producto parseProductoFromRequest(HttpServletRequest request) {
        Producto p = new Producto();
        p.setCodigo_producto(request.getParameter("codigo_producto"));
        p.setNombre_producto(request.getParameter("nombre_producto"));

        String costoStr = request.getParameter("costo_unitario");
        if (costoStr != null && !costoStr.isEmpty()) {
            p.setCosto_unitario(new BigDecimal(costoStr));
        }

        String stockStr = request.getParameter("stock");
        if (stockStr != null && !stockStr.isEmpty()) {
            p.setStock(Integer.parseInt(stockStr));
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            String fIngreso = request.getParameter("fecha_ingreso");
            if (fIngreso != null && !fIngreso.isEmpty()) {
                p.setFecha_ingreso(sdf.parse(fIngreso));
            }
            String fVenc = request.getParameter("fecha_vencimiento");
            if (fVenc != null && !fVenc.isEmpty()) {
                p.setFecha_vencimiento(sdf.parse(fVenc));
            }
            String fRetiro = request.getParameter("fecha_retiro");
            if (fRetiro != null && !fRetiro.isEmpty()) {
                p.setFecha_retiro(sdf.parse(fRetiro));
            }
        } catch (Exception e) {
            // Manejo silencioso utilizando fecha por defecto en DAO
        }

        String provStr = request.getParameter("proveedor_id_proveedor");
        if (provStr != null && !provStr.isEmpty()) {
            p.setProveedor_id_proveedor(Integer.parseInt(provStr));
        }

        String unStr = request.getParameter("unidad_medida_id_unidad_medida");
        if (unStr != null && !unStr.isEmpty()) {
            p.setUnidad_medida_id_unidad_medida(Integer.parseInt(unStr));
        }

        String ubStr = request.getParameter("ubicacion_almacen_id_ubicacion");
        if (ubStr != null && !ubStr.isEmpty()) {
            p.setUbicacion_almacen_id_ubicacion(Integer.parseInt(ubStr));
        }

        String invStr = request.getParameter("inventario_id_inventario");
        if (invStr != null && !invStr.isEmpty()) {
            p.setInventario_id_inventario(Integer.parseInt(invStr));
        }

        String estStr = request.getParameter("estado");
        p.setEstado(estStr == null || "1".equals(estStr) || "true".equalsIgnoreCase(estStr));

        return p;
    }
}
