<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Controlador.UsuarioDAO" %>
<%
    String token = request.getParameter("token");
    boolean tokenValido = false;

    if (token != null && !token.trim().isEmpty()) {
        UsuarioDAO dao = new UsuarioDAO();
        tokenValido = dao.validarToken(token);
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Restablecer Contraseña - Stratify</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Rajdhani:wght@400;500;600;700&family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.2/css/bootstrap.min.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" />
  <style>
    body {
      background-color: #0d0f12;
      color: #ffffff;
      font-family: 'Inter', sans-serif;
      display: flex;
      align-items: center;
      justify-content: center;
      min-height: 100vh;
      margin: 0;
    }
    .reset-card {
      background: #161a1e;
      border: 1px solid rgba(218, 165, 32, 0.3);
      border-radius: 8px;
      padding: 2.5rem;
      width: 100%;
      max-width: 440px;
      box-shadow: 0 10px 30px rgba(0,0,0,0.5);
    }
    .reset-title {
      font-family: 'Rajdhani', sans-serif;
      color: #daa520;
      font-weight: 700;
      margin-bottom: 0.5rem;
    }
  </style>
</head>
<body>

<div class="reset-card">
  <% if (!tokenValido) { %>
    <div class="text-center">
      <h2 class="reset-title">Enlace vencido o no válido</h2>
      <p class="text-muted mt-3 mb-4">El enlace de recuperación ha expirado o no existe en el sistema.</p>
      <a href="${pageContext.request.contextPath}/index.jsp" class="btn btn-warning w-100 fw-bold" style="background-color: #daa520; border: none; color: #000;">Volver al Inicio</a>
    </div>
  <% } else { %>
    <div class="text-center mb-4">
      <h2 class="reset-title">Restablecer Contraseña</h2>
      <p class="inv-form__subtitle">Crea una nueva contraseña para tu cuenta</p>
    </div>

    <p class="inv-field__error" id="reset-error" style="display:none; color: #e05050; background: rgba(224,80,80,0.1); padding: 0.75rem; border-radius: 4px; margin-bottom: 1rem; font-size: 0.9rem;"></p>
    <p id="reset-success" style="display:none; color: #2ecc71; background: rgba(46,204,113,0.1); border: 1px solid rgba(46,204,113,0.2); padding: 0.75rem; border-radius: 4px; margin-bottom: 1rem; font-size: 0.9rem;"></p>

    <form id="reset-form" action="${pageContext.request.contextPath}/RestablecerClaveServlet" method="POST" novalidate>
      <input type="hidden" name="token" value="<%= token %>" />

      <div class="inv-field mb-3">
        <label class="inv-field__label" for="new-pass">Nueva Contraseña</label>
        <div class="inv-field__wrapper">
          <span class="inv-field__icon">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
          </span>
          <input id="new-pass" type="password" name="password" class="inv-field__input" placeholder="Mínimo 8 caracteres" minlength="8" required />
        </div>
      </div>

      <div class="inv-field mb-4">
        <label class="inv-field__label" for="confirm-pass">Confirmar Contraseña</label>
        <div class="inv-field__wrapper">
          <span class="inv-field__icon">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
          </span>
          <input id="confirm-pass" type="password" name="confirm_password" class="inv-field__input" placeholder="Repite tu contraseña" required />
        </div>
      </div>

      <button type="submit" id="reset-btn" class="inv-form__submit w-100">
        Actualizar Contraseña
      </button>
    </form>
  <% } %>
</div>

<script>
  document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('reset-form');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
      e.preventDefault();
      const pass = document.getElementById('new-pass').value.trim();
      const confirm = document.getElementById('confirm-pass').value.trim();
      const errorEl = document.getElementById('reset-error');
      const successEl = document.getElementById('reset-success');
      const btn = document.getElementById('reset-btn');

      errorEl.style.display = 'none';
      successEl.style.display = 'none';

      if (pass.length < 8) {
        errorEl.textContent = 'La contraseña debe tener al menos 8 caracteres.';
        errorEl.style.display = 'block';
        return;
      }

      if (pass !== confirm) {
        errorEl.textContent = 'Las contraseñas no coinciden.';
        errorEl.style.display = 'block';
        return;
      }

      btn.disabled = true;
      btn.textContent = 'Actualizando...';

      try {
        const body = new URLSearchParams(new FormData(form));
        const res = await fetch(form.action, {
          method: 'POST',
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
          body: body
        });

        const data = await res.json();

        if (data.status === 'success') {
          form.style.display = 'none';
          successEl.textContent = data.mensaje + ' Redirigiendo al inicio de sesión...';
          successEl.style.display = 'block';
          setTimeout(() => {
            window.location.href = '${pageContext.request.contextPath}/index.jsp';
          }, 2000);
        } else {
          errorEl.textContent = data.mensaje;
          errorEl.style.display = 'block';
          btn.disabled = false;
          btn.textContent = 'Actualizar Contraseña';
        }
      } catch (err) {
        errorEl.textContent = 'Error de conexión al procesar la solicitud.';
        errorEl.style.display = 'block';
        btn.disabled = false;
        btn.textContent = 'Actualizar Contraseña';
      }
    });
  });
</script>
</body>
</html>