<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Stratify Sistema de Inventa rios</title>
  <link rel="preconnect" href="https://fonts.googleapis.com" />
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
  <link href="https://fonts.googleapis.com/css2?family=Rajdhani:wght@400;500;600;700&family=Inter:wght@400;500;600;700&family=Space+Grotesk:wght@400;500;600;700&display=swap" rel="stylesheet" />
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.2/css/bootstrap.min.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" />
  <style>
    .inv-login__forgot-link {
      display: inline-block;
      margin-top: 0.5rem;
      font-size: 0.85rem;
      color: #daa520;
      text-decoration: none;
      transition: opacity 0.2s ease, text-decoration 0.2s ease;
    }
    .inv-login__forgot-link:hover {
      color: #ffd700;
      text-decoration: underline;
    }
  </style>
</head>
<body>

<!-- =====================================================
     VISTA: LANDING
===================================================== -->
<div class="landing-view active" id="landing-view">

  <!-- Fondo de cajas animadas -->
  <div class="bg-boxes" aria-hidden="true">
    <div class="bg-boxes__grid"></div>
    <svg class="bg-boxes__svg" viewBox="0 0 1440 900" preserveAspectRatio="xMidYMid slice">
      <g class="box-float" style="--duration:7s;--delay:0s;">
        <rect x="80" y="120" width="90" height="75" rx="4" fill="none" stroke="rgba(218,165,32,0.12)" stroke-width="1.2"/>
        <line x1="80" y1="140" x2="170" y2="140" stroke="rgba(218,165,32,0.08)" stroke-width="1"/>
        <line x1="125" y1="120" x2="125" y2="195" stroke="rgba(218,165,32,0.08)" stroke-width="1"/>
      </g>
      <g class="box-float" style="--duration:9s;--delay:1.2s;">
        <rect x="300" y="60" width="70" height="58" rx="3" fill="none" stroke="rgba(218,165,32,0.10)" stroke-width="1"/>
        <line x1="300" y1="78" x2="370" y2="78" stroke="rgba(218,165,32,0.07)" stroke-width="1"/>
      </g>
      <g class="box-float" style="--duration:6.5s;--delay:0.7s;">
        <rect x="1250" y="100" width="110" height="88" rx="5" fill="none" stroke="rgba(218,165,32,0.14)" stroke-width="1.2"/>
        <line x1="1250" y1="124" x2="1360" y2="124" stroke="rgba(218,165,32,0.09)" stroke-width="1"/>
        <line x1="1305" y1="100" x2="1305" y2="188" stroke="rgba(218,165,32,0.09)" stroke-width="1"/>
      </g>
      <g class="box-float" style="--duration:8s;--delay:2s;">
        <rect x="1100" y="500" width="85" height="70" rx="4" fill="none" stroke="rgba(218,165,32,0.10)" stroke-width="1"/>
        <line x1="1100" y1="520" x2="1185" y2="520" stroke="rgba(218,165,32,0.07)" stroke-width="1"/>
      </g>
      <g class="box-float" style="--duration:10s;--delay:3s;">
        <rect x="50" y="600" width="100" height="82" rx="4" fill="none" stroke="rgba(218,165,32,0.09)" stroke-width="1"/>
        <line x1="50" y1="622" x2="150" y2="622" stroke="rgba(218,165,32,0.06)" stroke-width="1"/>
        <line x1="100" y1="600" x2="100" y2="682" stroke="rgba(218,165,32,0.06)" stroke-width="1"/>
      </g>
      <g class="box-float" style="--duration:7.5s;--delay:1.5s;">
        <rect x="680" y="780" width="78" height="64" rx="3" fill="none" stroke="rgba(218,165,32,0.08)" stroke-width="1"/>
        <line x1="680" y1="798" x2="758" y2="798" stroke="rgba(218,165,32,0.06)" stroke-width="1"/>
      </g>
      <g class="box-float" style="--duration:8.5s;--delay:0.4s;">
        <rect x="420" y="700" width="65" height="52" rx="3" fill="none" stroke="rgba(218,165,32,0.09)" stroke-width="1"/>
        <line x1="420" y1="716" x2="485" y2="716" stroke="rgba(218,165,32,0.06)" stroke-width="1"/>
      </g>
      <g class="box-float" style="--duration:6s;--delay:2.5s;">
        <rect x="1320" y="350" width="95" height="76" rx="4" fill="none" stroke="rgba(218,165,32,0.11)" stroke-width="1"/>
        <line x1="1320" y1="372" x2="1415" y2="372" stroke="rgba(218,165,32,0.08)" stroke-width="1"/>
        <line x1="1367" y1="350" x2="1367" y2="426" stroke="rgba(218,165,32,0.08)" stroke-width="1"/>
      </g>
    </svg>
    <div class="bg-boxes__vignette"></div>
  </div>

  <!-- Navbar -->
  <header>
    <nav class="inv-navbar" id="main-navbar" role="navigation" aria-label="Navegación principal">
      <div class="inv-navbar__brand" role="img" aria-label="Stratify">
        <img
            src="${pageContext.request.contextPath}/img/stratify-logo.png"
            alt="Stratify"
            class="inv-navbar__logo-img">
      </div>
      <div class="inv-navbar__actions">
      
        <button class="btn-nav btn-nav--gold" id="nav-register-btn">Registrarse</button>
      </div>
      <button class="inv-navbar__hamburger" id="hamburger-btn" aria-expanded="false" aria-label="Abrir menu">
        <span></span><span></span><span></span>
      </button>
    </nav>
    <div class="inv-navbar__mobile-menu" id="mobile-menu">
      <button class="btn-nav btn-nav--ghost" id="mobile-login-btn">Iniciar sesión</button>
      <button class="btn-nav btn-nav--gold" id="mobile-register-btn">Registrarse</button>
    </div>
  </header>

  <!-- Hero -->
  <section class="inv-hero" aria-labelledby="hero-headline">
    <div class="inv-hero__container">

      <!-- Columna izquierda -->
      <div class="inv-hero__content">
        <span class="inv-hero__eyebrow" aria-hidden="true">Sistema de inventarios</span>
        <h1 id="hero-headline" class="inv-hero__headline">
          La mejor forma de administrar tus productos <em>sin perdidas</em>
        </h1>
        <p class="inv-hero__sub">
          Organiza, controla y administra tu inventario de manera sencilla.
        </p>
        <div class="inv-hero__cta-group">
          <button class="btn-cta-primary" id="hero-register-btn" aria-label="Ir a formulario de registro">
            Registrarse
          </button>
        </div>
      </div>

      <!-- Columna derecha: Login panel -->
      <div class="inv-hero__login-panel" aria-label="Formulario de acceso">
        <div class="inv-login-wrapper">
          <div class="inv-login-peek">
            <div class="inv-form-panel">
              <div style="margin-bottom:1.5rem;">
                <h2 class="inv-form__title">Iniciar sesión</h2>
                <p class="inv-form__subtitle">Ingrese sus datos</p>
              </div>
              <form id="login-form" novalidate>
                <p class="inv-field__error" id="login-error" style="display:none;"></p>

                <!-- Email -->
                <div class="inv-field">
                  <label class="inv-field__label" for="login-email">Correo electrónico</label>
                  <div class="inv-field__wrapper">
                    <span class="inv-field__icon">
                      <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="4" width="20" height="16" rx="2"/><path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7"/></svg>
                    </span>
                    <input id="login-email" type="email" name="email" class="inv-field__input" placeholder="usuario@empresa.com" autocomplete="email" required />
                  </div>
                </div>

                <!-- Contraseña -->
                <div class="inv-field">
                  <label class="inv-field__label" for="login-password">Contraseña</label>
                  <div class="inv-field__wrapper">
                    <span class="inv-field__icon">
                      <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
                    </span>
                    <input id="login-password" type="password" name="password" class="inv-field__input inv-field__input--password" placeholder="********" autocomplete="current-password" required />
                    <button type="button" class="inv-field__toggle" id="toggle-login-pass" aria-label="Mostrar contraseña">
                      <svg id="eye-icon-login" width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7z"/><circle cx="12" cy="12" r="3"/></svg>
                    </button>
                  </div>
                  <div class="text-end">
                    <a href="#" class="inv-login__forgot-link" id="forgot-password-link">¿Olvidaste tu contraseña?</a>
                  </div>
                </div>

                <button type="submit" class="inv-form__submit" style="margin-top: 1rem;">
                  <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4"/><polyline points="10 17 15 12 10 7"/><line x1="15" y1="12" x2="3" y2="12"/></svg>
                  Iniciar sesión
                </button>
              </form>
            </div>
          </div>
        </div>
      </div>

    </div>
  </section>

</div><!-- /landing-view -->


<!-- =====================================================
     MODAL: REGISTRO
===================================================== -->
<div class="inv-modal-overlay hidden" id="register-modal" role="dialog" aria-modal="true" aria-labelledby="modal-register-title">
  <div class="inv-modal-content">
    <button type="button" class="inv-modal-close" id="modal-close-btn" aria-label="Cerrar modal">✕</button>

    <div style="margin-bottom: 1.5rem;">
      <h2 class="inv-form__title" id="modal-register-title">Crear cuenta</h2>
      <p class="inv-form__subtitle">Regístrate para acceder al sistema</p>
    </div>

    <p class="inv-field__error" id="register-general-error" style="display:none; color: #e05050; background: rgba(224,80,80,0.1); padding: 0.75rem; border-radius: 4px; margin-bottom: 1rem; font-size: 0.9rem;"></p>

    <form id="register-form" action="${pageContext.request.contextPath}/RegistroServlet" method="POST" novalidate>
        <!-- Nombre + Apellido -->
        <div class="inv-fields-row">
            <div class="inv-field">
                <label class="inv-field__label" for="reg-nombre">Nombre</label>
                <div class="inv-field__wrapper">
                    <span class="inv-field__icon">
                        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                    </span>
                    <input id="reg-nombre" type="text" name="nombre" class="inv-field__input" placeholder="John" autocomplete="given-name" required />
                </div>
            </div>
            <div class="inv-field">
                <label class="inv-field__label" for="reg-apellido">Apellido</label>
                <div class="inv-field__wrapper">
                    <span class="inv-field__icon">
                        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                    </span>
                    <input id="reg-apellido" type="text" name="apellido" class="inv-field__input" placeholder="Nivia" autocomplete="family-name" required />
                </div>
            </div>
        </div>

        <!-- Correo -->
        <div class="inv-field">
            <label class="inv-field__label" for="reg-email">Correo electrónico</label>
            <div class="inv-field__wrapper">
                <span class="inv-field__icon">
                    <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="4" width="20" height="16" rx="2"/><path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7"/></svg>
                </span>
                <input id="reg-email" type="email" name="email" class="inv-field__input" placeholder="correo@empresa.com" autocomplete="email" required />
            </div>
            <p class="inv-field__error" id="email-error" style="display:none;">El correo debe incluir un '@' y un dominio válido (ej. usuario@empresa.com)</p>
        </div>

        <div class="inv-fields-row">
            <div class="inv-field">
                <label class="inv-field__label" for="reg-tipo-doc">Tipo de documento</label>
                <div class="inv-field__wrapper">
                    <span class="inv-field__icon">
                        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>
                    </span>
                    <select id="reg-tipo-doc" name="tipo_documento_id_tipo_documento" class="inv-field__input inv-field__input--select" required>
                        <option value="" disabled selected>Selecciona...</option>
                        <option value="1">Cédula de Ciudadanía</option>
                        <option value="2">Cédula de Extranjería</option>
                        <option value="3">Pasaporte</option>
                    </select>
                </div>
            </div>
            <div class="inv-field">
                <label class="inv-field__label" for="reg-num-doc">Número de documento</label>
                <div class="inv-field__wrapper">
                    <span class="inv-field__icon">
                        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="1" y="4" width="22" height="16" rx="2" ry="2"/><line x1="1" y1="10" x2="23" y2="10"/></svg>
                    </span>
                    <input id="reg-num-doc" type="text" name="numero_documento" class="inv-field__input" placeholder="123456789" inputmode="numeric" required />
                </div>
            </div>
        </div>

        <!-- Fecha de Nacimiento -->
        <div class="inv-field">
            <label class="inv-field__label" for="reg-fecha-nac">Fecha de Nacimiento</label>
            <div class="inv-field__wrapper">
                <input id="reg-fecha-nac" type="date" name="fecha_nacimiento" class="inv-field__input" required />
            </div>
            <p class="inv-field__error" id="date-error" style="display:none;"></p>
        </div>

        <!-- Contraseña -->
        <div class="inv-field">
            <label class="inv-field__label" for="reg-password">Contraseña</label>
            <div class="inv-field__wrapper">
                <span class="inv-field__icon">
                    <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
                </span>
                <input id="reg-password" type="password" name="password" class="inv-field__input inv-field__input--password" placeholder="Mínimo 8 caracteres" autocomplete="new-password" minlength="8" required />
                <button type="button" class="inv-field__toggle" id="toggle-reg-pass" aria-label="Mostrar contraseña">
                    <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7z"/><circle cx="12" cy="12" r="3"/></svg>
                </button>
            </div>
        </div>

        <!-- Confirmar contraseña -->
        <div class="inv-field">
            <label class="inv-field__label" for="reg-confirm">Confirmar contraseña</label>
            <div class="inv-field__wrapper">
                <span class="inv-field__icon" id="confirm-icon">
                    <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
                </span>
                <input id="reg-confirm" type="password" name="confirm" class="inv-field__input inv-field__input--password" placeholder="Repite tu contraseña" autocomplete="new-password" required />
                <button type="button" class="inv-field__toggle" id="toggle-reg-confirm" aria-label="Mostrar contraseña">
                    <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7z"/><circle cx="12" cy="12" r="3"/></svg>
                </button>
            </div>
            <p class="inv-field__error" id="pass-mismatch" style="display:none;">Las contraseñas no coinciden</p>
        </div>

        <!-- Términos y condiciones -->
        <div class="inv-field" style="margin-top: 0.5rem;">
            <label class="inv-field__label--checkbox" for="reg-terminos" style="display:flex; align-items:flex-start; gap:0.6rem; cursor:pointer; font-size:0.85rem; color: var(--color-text-muted, #a0a0b0); line-height:1.4;">
                <input id="reg-terminos" type="checkbox" name="terminos" style="margin-top:2px; accent-color:#daa520; width:15px; height:15px; flex-shrink:0;" required />
                <span>
                    Acepto los
                    <a href="#" id="terminos-link" style="color:#daa520; text-decoration:underline; font-weight:600;">Términos y Condiciones</a>
                    y la política de tratamiento de datos personales de Stratify.
                </span>
            </label>
            <p class="inv-field__error" id="terminos-error" style="display:none; margin-top:0.3rem;">Debes aceptar los Términos y Condiciones para continuar.</p>
        </div>

        <button type="submit" class="inv-form__submit" id="reg-submit-btn">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><line x1="19" y1="8" x2="19" y2="14"/><line x1="22" y1="11" x2="16" y2="11"/></svg>
            Crear cuenta
        </button>
    </form>
  </div>
</div>

<!-- =====================================================
     MODAL: TÉRMINOS Y CONDICIONES
===================================================== -->
<div class="inv-modal-overlay hidden" id="terminos-modal" role="dialog" aria-modal="true" aria-labelledby="modal-terminos-title">
  <div class="inv-modal-content" style="max-width:640px; max-height:80vh; display:flex; flex-direction:column;">
    <button type="button" class="inv-modal-close" id="terminos-close-btn" aria-label="Cerrar modal">✕</button>

    <div style="margin-bottom: 1rem;">
      <h2 class="inv-form__title" id="modal-terminos-title">Términos y Condiciones</h2>
      <p class="inv-form__subtitle">Stratify — Sistema de Inventarios</p>
    </div>

    <div style="overflow-y:auto; flex:1; padding-right:0.5rem; font-size:0.875rem; color: var(--color-text-muted, #a0a0b0); line-height:1.7;">

      <h3 style="color:#daa520; font-size:0.95rem; margin:1rem 0 0.4rem;">1. Aceptación de los términos</h3>
      <p>Al registrarte en Stratify aceptas estos Términos y Condiciones en su totalidad. Si no estás de acuerdo con alguna de las disposiciones aquí establecidas, no debes registrarte ni usar el sistema.</p>

      <h3 style="color:#daa520; font-size:0.95rem; margin:1rem 0 0.4rem;">2. Uso del sistema</h3>
      <p>Stratify es una plataforma de gestión de inventarios de uso exclusivo para personas autorizadas por la organización. Queda prohibido:</p>
      <ul style="padding-left:1.2rem; margin:0.4rem 0;">
        <li>Compartir tus credenciales de acceso con terceros.</li>
        <li>Manipular, alterar o eliminar información de manera no autorizada.</li>
        <li>Usar el sistema con fines distintos a la gestión de inventario.</li>
        <li>Intentar acceder a módulos o datos que no correspondan a tu rol asignado.</li>
      </ul>

      <h3 style="color:#daa520; font-size:0.95rem; margin:1rem 0 0.4rem;">3. Registro y veracidad de la información</h3>
      <p>Al crear una cuenta te comprometes a proporcionar información verídica, completa y actualizada. El uso de datos falsos o de terceros sin su consentimiento puede resultar en la suspensión inmediata de la cuenta y las acciones legales correspondientes.</p>

      <h3 style="color:#daa520; font-size:0.95rem; margin:1rem 0 0.4rem;">4. Tratamiento de datos personales</h3>
      <p>Los datos personales recopilados (nombre, apellido, correo electrónico, número de documento y fecha de nacimiento) serán tratados conforme a la <strong>Ley 1581 de 2012</strong> y el <strong>Decreto 1377 de 2013</strong> de Colombia. Dichos datos se utilizarán únicamente para:</p>
      <ul style="padding-left:1.2rem; margin:0.4rem 0;">
        <li>Gestionar tu acceso y autenticación en el sistema.</li>
        <li>Garantizar la trazabilidad de las operaciones realizadas.</li>
        <li>Comunicaciones relacionadas con el funcionamiento del sistema.</li>
      </ul>
      <p>No compartiremos tu información con terceros sin tu consentimiento expreso, salvo obligación legal.</p>

      <h3 style="color:#daa520; font-size:0.95rem; margin:1rem 0 0.4rem;">5. Seguridad y confidencialidad</h3>
      <p>Stratify implementa medidas técnicas para proteger la información almacenada. Sin embargo, el usuario es responsable de mantener la confidencialidad de su contraseña. Se recomienda no usar contraseñas reutilizadas de otros servicios.</p>

      <h3 style="color:#daa520; font-size:0.95rem; margin:1rem 0 0.4rem;">6. Suspensión de cuentas</h3>
      <p>La organización se reserva el derecho de suspender o eliminar cuentas que incumplan estos términos, sin previo aviso y sin responsabilidad frente al usuario.</p>

      <h3 style="color:#daa520; font-size:0.95rem; margin:1rem 0 0.4rem;">7. Modificaciones</h3>
      <p>Estos Términos pueden ser actualizados en cualquier momento. El uso continuado del sistema tras la publicación de cambios implica la aceptación de los nuevos términos.</p>

      <h3 style="color:#daa520; font-size:0.95rem; margin:1rem 0 0.4rem;">8. Contacto</h3>
      <p>Para consultas relacionadas con el tratamiento de tus datos o el uso del sistema, puedes comunicarte con el administrador del sistema a través de los canales internos de tu organización.</p>

      <p style="margin-top:1.2rem; font-size:0.78rem; color:#666;">Última actualización: junio de 2025.</p>
    </div>

    <div style="margin-top:1rem; display:flex; justify-content:flex-end; gap:0.75rem; border-top:1px solid rgba(218,165,32,0.15); padding-top:1rem;">
      <button type="button" id="terminos-rechazar-btn" style="padding:0.5rem 1.2rem; background:transparent; border:1px solid rgba(218,165,32,0.3); color:#a0a0b0; border-radius:6px; cursor:pointer; font-size:0.85rem;">Cerrar</button>
      <button type="button" id="terminos-aceptar-btn" style="padding:0.5rem 1.2rem; background:#daa520; border:none; color:#1a1a2e; border-radius:6px; cursor:pointer; font-weight:700; font-size:0.85rem;">Acepto los términos</button>
    </div>
  </div>
</div>


<!-- =====================================================
     MODAL: RECUPERAR CONTRASEÑA
===================================================== -->
<div class="inv-modal-overlay hidden" id="forgot-modal" role="dialog" aria-modal="true" aria-labelledby="modal-forgot-title">
  <div class="inv-modal-content">
    <button type="button" class="inv-modal-close" id="modal-forgot-close-btn" aria-label="Cerrar modal">✕</button>

    <div style="margin-bottom: 1.5rem;">
      <h2 class="inv-form__title" id="modal-forgot-title">Recuperar contraseña</h2>
      <p class="inv-form__subtitle">Ingresa tu correo y te enviaremos las instrucciones para restablecerla.</p>
    </div>

    <p class="inv-field__error" id="forgot-general-error" style="display:none; color: #e05050; background: rgba(224,80,80,0.1); padding: 0.75rem; border-radius: 4px; margin-bottom: 1rem; font-size: 0.9rem;"></p>
    <p id="forgot-general-success" style="display:none; color: #2ecc71; background: rgba(46,204,113,0.1); border: 1px solid rgba(46,204,113,0.2); padding: 0.75rem; border-radius: 4px; margin-bottom: 1rem; font-size: 0.9rem;"></p>

    <form id="forgot-form" action="${pageContext.request.contextPath}/RecuperarClaveServlet" method="POST" novalidate>
      <div class="inv-field">
        <label class="inv-field__label" for="forgot-email">Correo electrónico</label>
        <div class="inv-field__wrapper">
          <span class="inv-field__icon">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="2" y="4" width="20" height="16" rx="2"/><path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7"/></svg>
          </span>
          <input id="forgot-email" type="email" name="email" class="inv-field__input" placeholder="correo@empresa.com" autocomplete="email" required />
        </div>
      </div>

      <button type="submit" class="inv-form__submit" id="forgot-submit-btn" style="margin-top: 1rem;">
        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/></svg>
        Enviar enlace
      </button>
    </form>
  </div>
</div>

<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.2/js/bootstrap.bundle.min.js"></script>
<script>
    window.CONTEXT_PATH = '${pageContext.request.contextPath}';
</script>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>