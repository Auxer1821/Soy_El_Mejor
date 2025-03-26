package utils.telegramSender;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import domain.heladera.Estado;
import domain.heladera.Heladera;
import domain.heladera.incidente.EstadoIncidente;
import domain.heladera.incidente.Incidente;
import domain.usuario.tecnico.Visita;
import net.bytebuddy.asm.Advice;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import config.ServiceLocator;
import domain.comunicaciones.Contacto;
import domain.comunicaciones.TipoDeContacto;
import domain.usuario.tecnico.Tecnico;
import repositorios.RepoGenerico;
import repositorios.contactos.RepoContactos;
import repositorios.heladera.RepoHeladera;
import repositorios.tecnico.RepoTecnicos;
import utils.PropertiesManager;


public class TelegramSender extends TelegramLongPollingBot {
    private final Map<Long, Visita> visitas = new HashMap<>();
    private final Map<Long, String> userStates = new HashMap<>();
    RepoTecnicos repoTecnicos = ServiceLocator.instanceOf(RepoTecnicos.class);
    RepoContactos repoContacto = ServiceLocator.instanceOf(RepoContactos.class);
    RepoHeladera repoHeladera = ServiceLocator.instanceOf(RepoHeladera.class);
    PropertiesManager propertiesManager = new PropertiesManager("properties/datosONG.properties");
    String username = propertiesManager.getPropertyString("USERNAMETELEGRAM");
    String token = propertiesManager.getPropertyString("TOKENTELEGRAM");

    public static void telegramSend(long id, String message) {
        TelegramSender sender = new TelegramSender();
        sender.sendMessage(sender.generateSendMessage(id, message));
    }

    public static void startTelegramBot() {
        try {
            TelegramBotsApi chatBot = new TelegramBotsApi(DefaultBotSession.class);
            chatBot.registerBot(new TelegramSender());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    public static boolean esNumericoYOnceDigitos(String mensaje) {
        return mensaje.matches("\\d{11}");
    }

    private String handlePhoto(Update update) {
        Long chatId = update.getMessage().getChatId();
        try {
            List<PhotoSize> photos = update.getMessage().getPhoto();
            PhotoSize largestPhoto = photos.get(photos.size() - 1);
            String fileId = largestPhoto.getFileId();
            String sanitizedFileId = fileId.replaceAll("[\\\\/:*?\"<>|_-]", "");

            if (fileId != null) {
                GetFile getFileRequest = new GetFile(fileId);
                String filePath = execute(getFileRequest).getFilePath();
                String fileUrl = "https://api.telegram.org/file/bot" + getBotToken() + "/" + filePath;
                Path folder = Paths.get("backend/src/main/resources/public/img");

                if (!Files.exists(folder)) {
                    Files.createDirectories(folder);
                }
                Path photoPath = folder.resolve(sanitizedFileId + ".jpg");
                try (InputStream inputStream = new URL(fileUrl).openStream()) {
                    Files.copy(inputStream, photoPath);

                    telegramSend(chatId, "Foto recibida y guardada con éxito.");

                    return "/img/" + sanitizedFileId + ".jpg";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            telegramSend(chatId, "Error al procesar la foto.");
            telegramSend(chatId, "(Escribe VOLVER para reiniciar)");
            return null;
        }
        return null;
    }

    public void handleRegistrarChatID(Update update) {
        Long chatId = update.getMessage().getChatId();
        String message = update.getMessage().getText();
        if (TelegramSender.esNumericoYOnceDigitos(message)) {
            System.out.println(message);
            System.out.println(chatId);
            Tecnico tecnico = repoTecnicos.buscarPorCUIL(message);

            if (tecnico != null && tecnico.getContacto() == null) {
                Contacto contacto = new Contacto(TipoDeContacto.TELEGRAM, String.valueOf(chatId), tecnico);

                tecnico.setContacto(contacto);
                repoTecnicos.beginTransaction();
                repoTecnicos.persist(tecnico);
                repoTecnicos.persist(contacto);
                repoTecnicos.commitTransaction();
                System.out.println("LLegué aca 2");

                telegramSend(chatId, "Tecnico " + tecnico.getNombre() + " cargado satisfactoriamente");
            } else {
                telegramSend(chatId, "El cuil no es valido, intente nuevamente");
                telegramSend(chatId, "(Escribe VOLVER para reiniciar)");
            }
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        Long chatId = update.getMessage().getChatId();

        if (Objects.equals(update.getMessage().getText(), "VOLVER")) {

            telegramSend(chatId, "Has vuelto al inicio. Selecciona la operación: \n 1. Registrar chat \n 2. Registrar visita");
            userStates.put(chatId, "OPERACION");
            return;
        }
        String currentState = userStates.getOrDefault(chatId, "START");

        switch (currentState) {
            case "START":
                telegramSend(chatId, "(Puedes escribir VOLVER para reiniciar en cualquier momento)");
                telegramSend(chatId, "Seleccionar la operación: \n 1. Registrar chat \n 2. Registrar visita");
                userStates.put(chatId, "OPERACION");
                break;

            case "OPERACION":
                String operacion = update.getMessage().getText();

                if (Objects.equals(operacion, "1")) {

                    telegramSend(chatId, "Ingresa tu cuil: ");
                    userStates.put(chatId, "CUIL");

                } else if (Objects.equals(operacion, "2")) {

                    telegramSend(chatId, "Ingrese el estado: \n 1. Arreglado \n 2. Pendiente");
                    userStates.put(chatId, "VISITA");

                } else {
                    telegramSend(chatId, "Codigo invalido, ingrese nuevamente: \n 1. Registrar chat \n 2. Registrar visita");
                }

                break;

            case "CUIL":

                handleRegistrarChatID(update);
                userStates.put(chatId, "START");

                break;

            case "VISITA":
                String visitaEstado = update.getMessage().getText();
                Visita visita = new Visita();
                visita.setFecha(LocalDateTime.now());

                if (Objects.equals(visitaEstado, "1")) {
                    visita.setEstado(EstadoIncidente.SOLUCIONADO);
                    visitas.put(chatId, visita);

                } else if (Objects.equals(visitaEstado, "2")) {
                    visita.setEstado(EstadoIncidente.SOLUCIONADO);
                    visitas.put(chatId, visita);

                } else {
                    telegramSend(chatId, "Codigo invalido, ingrese nuevamente: \n 1. Arreglado \n 2. Pendiente");
                    telegramSend(chatId, "(Escribe VOLVER para reiniciar)");
                    break;
                }

                userStates.put(chatId, "PREGUNTA_PHOTO");
                telegramSend(chatId, "Vas a ingresar una foto del problema? (SI/NO)");

                break;


            case "PREGUNTA_PHOTO":
                String hayPhoto = update.getMessage().getText().toUpperCase();

                if (Objects.equals(hayPhoto, "SI")) {

                    userStates.put(chatId, "PHOTO");
                    telegramSend(chatId, "Adjunte la foto");

                } else if (Objects.equals(hayPhoto, "NO")) {

                    userStates.put(chatId, "DESCRIPCION");
                    telegramSend(chatId, "Ingrese una descripcion del problema");

                } else {
                    telegramSend(chatId, "Respuesta invalida, ingrese nuevamente");
                    telegramSend(chatId, "Vas a ingresar una foto del problema? (SI/NO)");
                    telegramSend(chatId, "(Escribe VOLVER para reiniciar)");
                    break;

                }
                break;

            case "PHOTO":
                if (update.getMessage().hasPhoto()) {

                    String foto = handlePhoto(update);

                    Visita visita2 = visitas.get(chatId);
                    visita2.setFoto(foto);

                    telegramSend(chatId, "ingrese una descripcion del problema");
                    userStates.put(chatId, "DESCRIPCION");

                } else {
                    telegramSend(chatId, "Por favor, adjunte una foto válida.");
                    telegramSend(chatId, "(Escribe VOLVER para reiniciar)");
                }

                break;

            case "DESCRIPCION":

                String descripcion = update.getMessage().getText();

                Visita visita3 = visitas.get(chatId);
                visita3.setDescripcion(descripcion);

                telegramSend(chatId, "Procesando... Aguarde un momento 😎");
                try {
                    Contacto contacto = repoContacto.buscarPorContactoYTipo(String.valueOf(chatId), TipoDeContacto.TELEGRAM);
                    Tecnico tecnico = contacto.getTecnico();

                    visita3.setTecnico(tecnico);
                    Incidente incidente = repoHeladera.buscarIncidenteTecnico(tecnico.getCuil());

                    visita3.setIncidenteID(incidente);

                    repoHeladera.beginTransaction();
                    if (visita3.getEstado() == EstadoIncidente.SOLUCIONADO) {

                        repoHeladera.modificarEstado(incidente.getHeladera().getId(), Estado.FUNCIONAMIENTO);
                        incidente.setEstado(visita3.getEstado());
                        incidente.setFecha_realizacion(visita3.getFecha());
                        repoHeladera.persist(incidente);
                    }

                    repoHeladera.persist(visita3);
                    repoHeladera.commitTransaction();

                    telegramSend(chatId, "Muchas gracias, esperamos a volver a contactarlo");
                    userStates.put(chatId, "START");
                } catch (Exception e) {
                    e.printStackTrace();
                    telegramSend(chatId, "Problema en el servidor. Intente nuevamente");
                    telegramSend(chatId, "Selecciona la operación: \n 1. Registrar chat \n 2. Registrar visita");
                    userStates.put(chatId, "OPERACION");
                }
                break;

            default:
                // Reiniciar conversación si el estado es desconocido
                telegramSend(chatId, "Problema en el servidor. Intente nuevamente");
                telegramSend(chatId, "Selecciona la operación: \n 1. Registrar chat \n 2. Registrar visita");
                userStates.put(chatId, "OPERACION");

        }

    }

    //we create a SendMessage with the text we want to send to the chat
    private SendMessage generateSendMessage(Long chatId, String mensaje) {
        return new SendMessage(chatId.toString(), mensaje);
    }

    //send the message to Telegram API
    private void sendMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
