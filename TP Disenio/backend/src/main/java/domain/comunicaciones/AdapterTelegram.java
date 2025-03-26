package domain.comunicaciones;

import utils.telegramSender.TelegramSender;

public class AdapterTelegram implements NotificadorAdapter{
    @Override
    public void notificar(Mensaje mensaje, Contacto contacto) {
        TelegramSender.telegramSend(Long.parseLong( contacto.getDatoContacto()), mensaje.getMensaje());
    }
}
