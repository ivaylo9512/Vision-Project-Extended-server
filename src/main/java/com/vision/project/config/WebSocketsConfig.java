package com.vision.project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketsConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/message", "/restaurant", "/user");
        config.setApplicationDestinationPrefixes("/api");
        config.setUserDestinationPrefix("/user");
        config.setUserDestinationPrefix("/restaurant");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/api/sockets")
                .setAllowedOrigins("*")
                .addInterceptors(getWebSocketInterceptor());
    }
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(getWebSocketInterceptor());
    }
    private WebSocketInterceptor getWebSocketInterceptor(){
        return new WebSocketInterceptor();
    }
}
