package com.jyall.tools;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
/**
 * 解析 healthInfo 信息
 * @autor: Faizel
 * @date: 2017/7/7
 * @version:1.0.0
 */
public class ParseJsonString {

    private static final Logger logger = LoggerFactory.getLogger(ParseJsonString.class);

    /** 关注的key值 **/
    private static final String FOCUS_KEY = "status";

    /** 解析方法体 这里向下解析一层，故无需使用递归 **/
    public static Map<String, String> parse(String jsonString) {

        if(StringUtils.isEmpty(jsonString)) {

            logger.warn("待解析的json字符串为null ！");

            return null;
        }

        /** 用于存储保存的状态信息 **/
        Map<String, String> info = new HashMap<String, String>();

        JSONObject jsonObject = JSONObject.parseObject(jsonString);

        Set<Map.Entry<String, Object>> status = jsonObject.entrySet();

        for(Map.Entry<String, Object> tem: status){

            Object ob = tem.getValue();

            String key = tem.getKey();

            /** 若值为一个JsonObject对象，继续解析 **/
            if(ob instanceof JSONObject){

                parseInfo((JSONObject)ob, info, key);
            }

            else if( (ob instanceof String) && FOCUS_KEY.equalsIgnoreCase(key)){

                info.put("all", (String)ob);

            }
        }

        return info;
    }

    /** 从信息体中提取需要的信息 **/
    private static void parseInfo(JSONObject jsonObject, Map<String, String> info, String keyName){

        Set<Map.Entry<String, Object>> status = jsonObject.entrySet();

        for(Map.Entry<String, Object> tem: status){

            Object ob = tem.getValue();

            String key = tem.getKey();

            if( (ob instanceof String) && FOCUS_KEY.equalsIgnoreCase(key)){

                info.put(keyName, (String)ob);

            }
        }
    }
}
