package oose.euphoria.backend.configuration.websocket;


import oose.euphoria.backend.presentation.WsEventsHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
@EnableWebSocket
public class WebsocketConfiguration implements WebSocketConfigurer {

    /**
     * Returns a new message controller
     *
     * @return MessageController
     */
    @Bean
    public WsEventsHandler messageController() {
        return new WsEventsHandler();
    }

    /**
     * Returns a new serverEndpointExporter
     *
     * @return ServerEndpointExporter
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    /**
     * Does nothing yet, but should be overridden.
     *
     * @param registry
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Function may remain empty until necessary
    }
}