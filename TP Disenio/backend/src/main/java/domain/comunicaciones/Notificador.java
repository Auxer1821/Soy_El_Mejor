package domain.comunicaciones;

import domain.usuario.colaborador.Humana;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Notificador {
    public static NotificadorAdapter adapterNotificator(TipoDeContacto tipoDeContacto) {
        NotificadorAdapter adapter = null;
        switch (tipoDeContacto) {
            case WHATSAPP -> adapter = new AdapterWhatsapp();
            case EMAIL -> adapter = new AdapterEmail();
            case TELEGRAM -> adapter = new AdapterTelegram();
        }
        return adapter;
    }

    public static void notificar(Mensaje mensaje, Contacto contacto){
        NotificadorAdapter adapter = adapterNotificator(contacto.getTipo());
        adapter.notificar(mensaje, contacto);
    }

    public static void main(String[] args) {
        StringBuilder mensaje = new StringBuilder();

        Mensaje mail = new Mensaje(mensaje
                        .append("<h2>Verifica tu cuenta</h2>")
                        .append("<p>Haz clic en el siguiente enlace para verificar tu cuenta:</p>")
                        .append("<a href='verificationLink'>Verificar cuenta</a>").toString(), "Mail prueba"
                );

        Notificador.notificar(mail, new Contacto(TipoDeContacto.EMAIL, "brunojuansartori@hotmail.com", new Humana()));
    }
}
