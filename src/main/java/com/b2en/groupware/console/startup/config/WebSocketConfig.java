package com.b2en.groupware.console.startup.config;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.converter.ByteArrayMessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.b2en.groupware.console.Const;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
	config.enableSimpleBroker(Const.WEBSOCKET_RESPONSE_CHANNEL);// TOPIC
	config.setApplicationDestinationPrefixes(Const.WEBSOCKET_REQUEST_CHANNEL); // APP

    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
	// WEBSOCKET ENDPOINT
	registry.addEndpoint(Const.WEBSOCKET_SERVICE_NAME).withSockJS().setInterceptors(customHandshakeInterceptor());

	//registry.addEndpoint(Const.WEBSOCKET_SERVICE_NAME).setAllowedOrigins("*");// WEBSOCKET ENDPOINT
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
	messageConverters.add(new StringMessageConverter());
	messageConverters.add(new ByteArrayMessageConverter());
	return true;
    }

    @Bean
    public CustomHandshakeInterceptor customHandshakeInterceptor() {
	return new CustomHandshakeInterceptor();
    }

}

class CustomHandshakeInterceptor implements HandshakeInterceptor {


    private Logger logger = LoggerFactory.getLogger(CustomHandshakeInterceptor.class);
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
	if (request instanceof ServletServerHttpRequest) {
	    ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
	    HttpSession session = servletRequest.getServletRequest().getSession(false);
	    if (session != null) {
		attributes.put("SESSION", session);
	    }
	}
	return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
	logger.debug("afterHandshake");
    }
}