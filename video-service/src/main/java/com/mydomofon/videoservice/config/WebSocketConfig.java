package com.mydomofon.videoservice.config;

import com.mydomofon.videoservice.handler.VideoWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final VideoWebSocketHandler videoWebSocketHandler;

    @Autowired
    public WebSocketConfig(VideoWebSocketHandler videoWebSocketHandler) {
        this.videoWebSocketHandler = videoWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Регистрируем наш обработчик по адресу /ws/video/live
        registry.addHandler(videoWebSocketHandler, "/ws/video/live");
    }
}