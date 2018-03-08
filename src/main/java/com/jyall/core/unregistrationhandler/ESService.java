package com.jyall.core.unregistrationhandler;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.lang.reflect.Field;
import java.util.Collections;

/**
 * 存储数据到es
 * @autor: Faizel
 * @date: 2017/7/7
 * @version:1.0.0
 */
@Component
public class ESService {

    private static final Logger logger = LoggerFactory.getLogger(ESService.class);

    @Autowired
    private RestClient restClient;

    public <T> int save(String index, String type, T obj) throws Exception {

        this.stringIsNull(index);

        this.stringIsNull(type);

        if(null == obj)

            throw new Exception(obj.getClass().getName() + "  不能为null");

        String reqBody = JSONObject.toJSONString(obj);

        HttpEntity entity = new NStringEntity(reqBody, ContentType.APPLICATION_JSON);

        Response response = sendSave(index, type, entity, null);

        return response.getStatusLine().getStatusCode();
    }

    /** 判断字符串参数是否为空 **/
    private boolean stringIsNull(String str) throws Exception{

        if(StringUtils.isEmpty(str)){

            throw new Exception("字符串不能为null或空");

        }
        else

            return false;
    }

    /** 发送索引文档请求
     * @param object  id
     * **/
    private Response sendSave(String index, String type, HttpEntity entity, Object object) throws Exception{

        Response response = null;

        if(null != object) {

            response = restClient.performRequest("PUT", "/" + index.toLowerCase() +

                            "/" + type.toLowerCase() + "/" + object,

                    Collections.emptyMap(), entity);
        }

        else{

            response = restClient.performRequest("POST", "/" + index.toLowerCase() + "/" + type.toLowerCase(),

                    Collections.emptyMap(), entity);
        }

        return response;
    }
}
