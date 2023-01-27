package com.korit.library.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.path}")
    private String filePath; //file패스 가져오기

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("*").allowedOrigins("http://127.0.0.1:5500");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        WebMvcConfigurer.super.addResourceHandlers(registry);
        registry.addResourceHandler("/image/**")
                .addResourceLocations("file:///" + filePath) // 파일경로 할때 ///맞음 filePath를 참조하여 file:/// 경로에 넣어줌
                .resourceChain(true) // filter처럼 위의 내용을 상기 상기 시킬거냐? 를 물어보는것
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        resourcePath = URLDecoder.decode(resourcePath, StandardCharsets.UTF_8); // decoding 을 한 다음에 Path로 다시 넣어주겠다는 말
                        return super.getResource(resourcePath, location);
                    }
                }); // 원하는 경로에 mapping을 시킬수가 있음 (Hadler를 /image/**라고 했으니 경로 앞에 /image/만 적어준다면 경로에서 파일을 열수가 있음)

    }
}
