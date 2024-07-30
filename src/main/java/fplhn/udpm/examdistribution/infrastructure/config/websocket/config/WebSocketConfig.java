package fplhn.udpm.examdistribution.infrastructure.config.websocket.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${allowed.origin}")
    public String ALLOWED_ORIGIN;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        List<String> AL = new ArrayList<>();
        if (ALLOWED_ORIGIN.contains(",")) {
            String[] origins = ALLOWED_ORIGIN.split(",");
            Collections.addAll(AL, origins);
        } else {
            AL.add(ALLOWED_ORIGIN);
        }
        registry
                .addEndpoint("/ws")
                .setAllowedOrigins(AL.toArray(new String[0]))
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic");
    }

}
