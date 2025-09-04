package com.mydomofon.apigateway.filter;

import com.mydomofon.apigateway.util.JwtTokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final JwtTokenUtils jwtTokenUtils;

    @Autowired
    public AuthenticationFilter(JwtTokenUtils jwtTokenUtils) {
        super(Config.class);
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // Пропускаем все запросы к auth-service без проверки
            if (request.getURI().getPath().startsWith("/auth")) {
                return chain.filter(exchange);
            }

            // Проверяем наличие заголовка Authorization
            if (!request.getHeaders().containsKey("Authorization")) {
                return this.onError(exchange, "Authorization header is missing", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = request.getHeaders().get("Authorization").get(0);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return this.onError(exchange, "Authorization header is invalid", HttpStatus.UNAUTHORIZED);
            }

            String jwt = authHeader.substring(7);
            try {
                // Проверяем токен
                jwtTokenUtils.getUsername(jwt);
            } catch (ExpiredJwtException e) {
                return this.onError(exchange, "Token is expired", HttpStatus.UNAUTHORIZED);
            } catch (Exception e) {
                return this.onError(exchange, "Token is invalid", HttpStatus.UNAUTHORIZED);
            }

            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    public static class Config {
    }
}