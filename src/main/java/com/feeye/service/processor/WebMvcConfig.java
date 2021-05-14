package com.feeye.service.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final Logger logger = LoggerFactory.getLogger(WebMvcConfig.class);

    //服务器支持跨域
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Access-Control-Allow-Headers",
                        "Access-Control-Allow-Methods",
                        "Access-Control-Allow-Origin",
                        "Access-Control-Max-Age",
                        "X-Frame-Options")
                .allowCredentials(false)
                .maxAge(3600);
    }


    /**
     * The bean shown in the preceding example registers any @ServerEndpoint
     * annotated beans with the underlying WebSocket container. When deployed to a
     * standalone servlet container, this role is performed by a servlet container
     * initializer, and the ServerEndpointExporter bean is not required.
     *
     * @return
     * 在Spring中可以直接使用Java WebSocket API来提供服务，如果使用内置的web容器，需要做的仅仅是需要在下面添加
     */
    /**
     * 注入ServerEndpointExporter，这个bean会自动注册使用了@ServerEndpoint注解声明的Websocket endpoint 。
     * 要注意，如果使用独立的servlet容器，而不是直接使用springboot的内置容器，就不要注入ServerEndpointExporter，因为它将由容器自己提供和管理。
     */
}