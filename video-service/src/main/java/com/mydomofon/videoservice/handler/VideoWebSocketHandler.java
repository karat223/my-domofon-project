package com.mydomofon.videoservice.handler;

import com.github.sarxos.webcam.Webcam;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Component
public class VideoWebSocketHandler extends TextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("WebSocket connection established: " + session.getId());

        // Запускаем отправку кадров в отдельном потоке
        new Thread(() -> {
            Webcam webcam = Webcam.getDefault();
            if (webcam == null) {
                System.out.println("No webcam found.");
                return;
            }
            webcam.open();

            while (session.isOpen()) {
                try {
                    // Захватываем кадр
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(webcam.getImage(), "JPG", baos);
                    byte[] imageBytes = baos.toByteArray();

                    // Кодируем в Base64 и отправляем как текстовое сообщение
                    String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                    session.sendMessage(new TextMessage(base64Image));

                    // Контролируем частоту кадров (здесь ~10 кадров в секунду)
                    Thread.sleep(100);

                } catch (Exception e) {
                    System.err.println("Error sending frame: " + e.getMessage());
                    break; // Выходим из цикла при ошибке
                }
            }

            // Закрываем камеру, когда клиент отключается
            webcam.close();
            System.out.println("Webcam closed for session: " + session.getId());
        }).start();
    }
}
