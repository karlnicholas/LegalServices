package com.github.karlnicholas.legalservices.opinionrestca;

public class RequestLoggingFilter {}

//@Configuration
//public class RequestLoggingFilter implements WebFilter {
//  @Override
//  public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
//    ServerWebExchangeDecorator decorator =
//        new ServerWebExchangeDecorator(serverWebExchange) {
//          @Override
//          public ServerHttpRequest getRequest() {
//            return new RequestLoggingDecorator(serverWebExchange.getRequest());
//          }
//        };
//
//    return webFilterChain.filter(decorator);
//  }
//}