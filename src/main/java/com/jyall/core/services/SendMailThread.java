package com.jyall.core.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
/**
 * 发送短信线程
 * @autor: Faizel Lannister(褚福影)
 * @date: 2017/8/9 14:33
 * @version:1.0.0
 * @jvm-version: jdk 1.8
 * @Email: chufuying3@163.com
 */
public class SendMailThread implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(SendMailThread.class);

    private String mobiles;

    private Integer templateId;

    private String body;

    private String url;

    private FeignService feignService;

    private TemplateService templateService;

    public SendMailThread(String mobiles, Integer templateId, String body,

                          FeignService feignService, TemplateService templateService,

                          String url) {

        this.mobiles = mobiles;

        this.templateId = templateId;

        this.body = body;

        this.feignService = feignService;

        this.templateService = templateService;

        this.url = url;
    }

    @Override
    public void run() {

        /** 注册到s则使用feign发送 **/
        if(null != feignService){

            try{

                ResponseEntity<String> responseEntity=  feignService.sendMail(mobiles, templateId, body);

                if(responseEntity.getStatusCodeValue() != 200){

                    logger.error(responseEntity.getBody());
                }

                else
                    logger.info("使用feign发送短信成功");

            }catch (Exception e){

                logger.error("使用feign 发送短信异常！", e);

            }

        }
        /** 注册到c则使用resttemplate固定地址发送 **/
        else if(null != templateService){

            templateService.sendSms(url, body, templateId, mobiles);

        }
    }
}
