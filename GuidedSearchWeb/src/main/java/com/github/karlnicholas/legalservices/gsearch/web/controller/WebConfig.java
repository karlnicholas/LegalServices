package com.github.karlnicholas.legalservices.gsearch.web.controller;

public class WebConfig {}
//@Configuration
//public class WebConfig {
//
//  private static final String ALLOWED_HEADERS = "x-requested-with, authorization, Content-Type, Authorization, credential, X-XSRF-TOKEN";
//  private static final String ALLOWED_METHODS = "GET, PUT, POST, DELETE, OPTIONS";
//  private static final String ALLOWED_ORIGIN = "*";
//  private static final String MAX_AGE = "3600";
//
//  @Bean
//  public WebFilter corsFilter() {
//    return (ServerWebExchange ctx, WebFilterChain chain) -> {
//      ServerHttpRequest request = ctx.getRequest();
//      if (CorsUtils.isCorsRequest(request)) {
//        ServerHttpResponse response = ctx.getResponse();
//        HttpHeaders headers = response.getHeaders();
//        headers.add("Access-Control-Allow-Origin", ALLOWED_ORIGIN);
//        headers.add("Access-Control-Allow-Methods", ALLOWED_METHODS);
//        headers.add("Access-Control-Max-Age", MAX_AGE);
//        headers.add("Access-Control-Allow-Headers",ALLOWED_HEADERS);
//        if (request.getMethod() == HttpMethod.OPTIONS) {
//          response.setStatusCode(HttpStatus.OK);
//          return Mono.empty();
//        }
//      }
//      return chain.filter(ctx);
//    };
//  }
//
//}