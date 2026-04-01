package com.steve.gateway.filter;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println("Request path: " + exchange.getRequest().getPath());
        return chain.filter(exchange)
                .then(Mono.fromRunnable(() ->
                        System.out.println("Response status code: " + exchange.getResponse().getStatusCode())
                ));
    }

    @Override
    public int getOrder() {

        return -1; // Execute before other filters
    }
}