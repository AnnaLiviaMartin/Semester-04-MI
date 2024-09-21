package de.hsrm.mi.web.projekt.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class StompWebMessageBrokerConfiguration implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // SimpleBroker soll auf alle zugehörigen Destinations mit Präfix /topic reagieren
        registry.enableSimpleBroker("/topic"); // Quasi wie so ein Nachrichtenkanal
        // registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stompbroker").setAllowedOrigins("*"); // Endpunkt mit dem man sich verbindet
        // registry.addEndpoint("/stompbroker").withSockJS();
    }
}
