package com.jyall.core.services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * 使用restTemplate调用静态短信接口
 * @autor: Faizel Lannister(褚福影)
 * @date: 2017/8/9 10:31
 * @version:1.0.0
 * @jvm-version: jdk 1.8
 * @Email: chufuying3@163.com
 */
@Service
public class TemplateService {

    private static final Logger logger = LoggerFactory.getLogger(TemplateService.class);

    @Autowired
    private RestTemplate template;

    /** 发送短信 **/
    public String sendSms(String url, String body, Integer templateId, String mobiles){

        String re = null;

        url = url + "{mobiles}/{templateID}";

        try{

            HttpHeaders headers = new HttpHeaders();

            MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");

            headers.setContentType(type);

            headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);

            HttpEntity<String> entity = new HttpEntity<String>(body, headers);

            re = template.postForObject(url, entity, String.class, mobiles, templateId);

            logger.info("使用restTemplate发送短信成功!");

        }catch (Exception e){

            logger.error("调用短信接口异常！", e);

            return null;
        }

        return re;
    }
}
