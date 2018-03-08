package com.jyall.core.unregistrationhandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
/**
 * 用于发起http请求
 * @autor: Faizel
 * @date: 2017/7/7
 * @version:1.0.0
 */
@Component
public class HttpService {

    private static final Logger logger = LoggerFactory.getLogger(HttpService.class);

    private static final String ENDPOINT = "/health";

    private static final String EXCEPTION_JSON = "{status:\"DOWN\"}";

    /** 请求空参数数组 **/
    private static final Object[] PARAMS = new Object[]{};

    @Autowired
    private RestTemplate template;

    /** 抓取结点healthInfo信息 **/
    public String fetchHealthInfo(String url){

        String re = null;

        try {

           re = template.getForObject(url, String.class, PARAMS);

        }catch (Exception e){

            System.out.println(e.getClass().getName());

            if(e instanceof ResourceAccessException) {

                logger.info("屏蔽  服务节点无法连接异常（网络故障或结点已死）");

                re = EXCEPTION_JSON;
            }

            else

                logger.error("调用 " + url + " 接口异常", e);
        }

        return  re;
    }

}
