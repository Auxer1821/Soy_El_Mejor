package utils.emailSender;

import utils.PropertiesManager;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {
    public void mailSend(String receiverEmail, String titulo, String cuerpo) {
        PropertiesManager credenciales = new PropertiesManager("properties/datosONG.properties");


        // Datos de correo del remitente
        String from = credenciales.getPropertyString("EMAILSENDER");
        String password = credenciales.getPropertyString("PASSWORDAPP");

        // Servidor SMTP de Gmail
        String host = "smtp.gmail.com";

        // Propiedades del sistema
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.trust", host);

        // Obtener la sesión predeterminada
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password); // Reemplaza con tu contraseña de aplicación si tienes verificación en dos pasos
            }
        });

        try {
            // Crear un objeto MimeMessage predeterminado
            MimeMessage message = new MimeMessage(session);

            // Configurar el remitente
            message.setFrom(new InternetAddress(from));

            // Configurar el destinatario
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiverEmail));

            // Configurar el asunto
            message.setSubject(titulo);

            // Configurar el cuerpo del mensaje
            message.setText(cuerpo);

            // Enviar el mensaje
            Transport.send(message);

        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
    public static void main(String[] args) {

        EmailSender sender = new EmailSender();
        sender.mailSend("josevalentinutn@gmail.com","Bruno es el mejor", "Solo para que sepas que to ego es muy alto y que realmente Bruno es mejor que vos \nenter\nenter\n\nfirma: El mejor \tbruno");
    }
}
