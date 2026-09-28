====================================================
STRATIFY - Instrucciones de configuracion
====================================================

1. BASE DE DATOS
   - Asegurate de tener MySQL corriendo en el puerto 3307
   - Crea la base de datos "stratify" si no existe:
       CREATE DATABASE IF NOT EXISTS stratify CHARACTER SET utf8mb4;
   - Importa el script SQL original de tu proyecto (schema + datos)
   - Luego ejecuta: database_updates.sql
       mysql -u root stratify < database_updates.sql
   
   NOTA: Si tu columna en "inventario" se llama "id_factura" en vez de
   "id_inventario", descomenta la linea ALTER TABLE en database_updates.sql
   ANTES de ejecutarlo.

2. CONEXION MYSQL
   Archivo: src/java/Controlador/Conexion.java
   - Puerto:     3307 (ajustar si usas 3306)
   - Usuario:    root
   - Password:   (vacia por defecto)
   - BD:         stratify
   - Timezone:   America/Bogota (ajustar si es necesario)

3. ABRIR EN NETBEANS
   - File → Open Project → seleccionar carpeta Stratify_CORREGIDO
   - Verificar que GlassFish 7 este configurado como servidor

4. EJECUTAR CON GLASSFISH 7
   - Click derecho en el proyecto → Run
   - URL por defecto: http://localhost:8080/Stratify/

5. FLUJO DE USO
   - index.jsp:       Login y registro
   - usuario.jsp:     Panel (inventario, proveedores, historial)
   - Tras login:      redirige a /usuario/usuario.jsp con sesion Java
   - Logout:          /LogoutServlet → invalida sesion → index.jsp

====================================================
SERVLETS DISPONIBLES
====================================================
   /LoginServlet       POST  - Autenticacion
   /LogoutServlet      GET   - Cerrar sesion
   /RegistroServlet    POST  - Registro de usuario
   /InventarioServlet  GET   - Listar productos (JSON, stock real)
                       POST  - Modificar stock (accion=modificar_stock)
   /ProductoServlet    POST  - Crear/activar/inactivar/eliminar producto
   /ProveedorServlet   GET   - Listar proveedores (JSON)
                       POST  - Crear/actualizar/eliminar proveedor
   /HistorialServlet   GET   - Listar historial de cambios (JSON)

====================================================
ERRORES CORREGIDOS
====================================================
   - Conexion JDBC: URL malformada (?serverTimeZone → ?serverTimezone=...)
   - ProductoDAO: UPDATE apuntaba a tabla "usuario" en vez de "producto"
   - ProductoDAO: columna "ubicacion_id_ubicion" → "ubicacion_almacen_id_ubicacion"
   - ProductoDAO: parametros PreparedStatement en orden incorrecto
   - ProductoDAO: listarProductos() usaba stock fijo 15 → ahora JOIN con inventario
   - Inventario.java: campo "id_factura" → "id_inventario"
   - InventarioServlet: devuelvia cantidad fija 15 → ahora stock real de BD
   - LoginServlet: no guardaba HttpSession → corregido
   - app.js: login leia data.message → corregido a data.mensaje
   - app.js: loadProviders() no consultaba BD → ahora llama ProveedorServlet
   - app.js: loadHistory() no consultaba BD → ahora llama HistorialServlet
   - usuario.jsp: rutas CSS/JS absolutas → contextPath relativas
   - index.jsp: sin page directive JSP → agregado
   - web.xml: faltaban mapeos de InventarioServlet, ProductoServlet,
               ProveedorServlet, HistorialServlet, LogoutServlet

====================================================
