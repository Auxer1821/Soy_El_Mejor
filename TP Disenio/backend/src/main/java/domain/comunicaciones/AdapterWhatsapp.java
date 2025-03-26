package domain.comunicaciones;

import utils.whatsappSender.WhatsappSender;

public class AdapterWhatsapp implements NotificadorAdapter{

    @Override
    public void notificar(Mensaje mensaje, Contacto contacto) {
        WhatsappSender whatsappSender = new WhatsappSender();
        whatsappSender.whatsappSend(contacto.getDatoContacto(), mensaje.getMensaje());
    }
}
