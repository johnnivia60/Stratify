package util;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtil {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "465"; 
    private static final String EMAIL_REMITENTE = "amasu3297@gmail.com";
    private static final String PASSWORD_REMITENTE = "xcsjwxoxvyrovpqs"; 

    public static boolean enviarCorreoRecuperacion(String destinatario, String enlaceRecuperacion) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        
        // Configuración para SSL (Puerto 465)
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.socketFactory.port", SMTP_PORT);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        // CRÍTICO: Timeouts para evitar que el servidor se congelé
        props.put("mail.smtp.connectiontimeout", "8000"); // 8 segundos para conectar
        props.put("mail.smtp.timeout", "8000");           // 8 segundos para leer datos
        props.put("mail.smtp.writetimeout", "8000");      // 8 segundos para escribir datos

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_REMITENTE, PASSWORD_REMITENTE);
            }
        });

        // Muestra el proceso de envío en la consola de Railway
        session.setDebug(true);

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_REMITENTE, "Stratify Sistema de Inventarios"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject("Recuperación de Contraseña - Stratify");

            String contenidoHtml = "<html>"
                    + "<body style='font-family: Arial, sans-serif; background-color: #0d0f12; color: #ffffff; padding: 20px;'>"
                    + "<div style='max-width: 500px; margin: auto; background-color: #161a1e; border: 1px solid #daa520; padding: 25px; border-radius: 8px;'>"
                    + "<h2 style='color: #daa520; text-align: center;'>Stratify</h2>"
                    + "<p>Has solicitado restablecer tu contraseña.</p>"
                    + "<p>Haz clic en el siguiente botón para continuar. Este enlace expira en 30 minutos:</p>"
                    + "<div style='text-align: center; margin: 30px 0;'>"
                    + "<a href='" + enlaceRecuperacion + "' style='background-color: #daa520; color: #000; padding: 12px 25px; text-decoration: none; font-weight: bold; border-radius: 4px; display: inline-block;'>Restablecer Contraseña</a>"
                    + "</div>"
                    + "<p style='font-size: 0.85em; color: #888;'>Si no solicitaste este cambio, puedes ignorar este correo de forma segura.</p>"
                    + "</div>"
                    + "</body>"
                    + "</html>";

            message.setContent(contenidoHtml, "text/html; charset=utf-8");

            Transport.send(message);
            return true;

        } catch (Exception e) {
            System.err.println("Error al enviar correo " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}