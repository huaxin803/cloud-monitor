package com.jyall.core.services;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import javax.ws.rs.Consumes;
/**
 * feign调用短信接口（依赖eureka注入的短信微服务）
 * @autor: Faizel Lannister(褚福影)
 * @date: 2017/8/9 10:30
 * @version:1.0.0
 * @jvm-version: jdk 1.8
 * @Email: chufuying3@163.com
 */
@Service
@FeignClient("is-common-sms")
public interface FeignService {

    /** 调用发送信息接口 **/
    @Consumes({"application/json"})
    @RequestMapping(value = "/v1/sms/multiple/{mobiles}/{templateID}", method = RequestMethod.POST, consumes = "application/json")
    ResponseEntity<String> sendMail(@PathVariable("mobiles") String mobiles,

                                    @PathVariable("templateID") Integer templateID,

                                    String body);
}
