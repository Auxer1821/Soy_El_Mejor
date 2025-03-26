package domain.comunicaciones;

import utils.emailSender.EmailSender;

public class AdapterEmail implements NotificadorAdapter{


    @Override
    public void notificar(Mensaje mensaje, Contacto contacto) {
        EmailSender emailSender = new EmailSender();
        emailSender.mailSend(contacto.getDatoContacto(),mensaje.getSubject(),mensaje.getMensaje());
    }
}
