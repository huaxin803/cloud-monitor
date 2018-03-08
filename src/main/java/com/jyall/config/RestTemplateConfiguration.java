package com.jyall.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * 配置restTemplate
 * @autor: Faizel
 * @date: 2017/7/12
 * @version:1.0.0
 */
@Configuration
public class RestTemplateConfiguration {

    @Autowired
    private Resolve resolve;

    /** 实例化RestTemplate **/
    @Bean
    public RestTemplate restTemplate() {

        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();

        httpRequestFactory.setConnectionRequestTimeout(resolve.getRequestTimeOut());

        httpRequestFactory.setConnectTimeout(resolve.getConnetionTimeOut());

        httpRequestFactory.setReadTimeout(resolve.getReadTimeOut());

        return new RestTemplate(httpRequestFactory);

    }
}
