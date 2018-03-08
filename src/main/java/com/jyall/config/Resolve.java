package com.jyall.config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 解析配置属性(没有加属性为null判断（使用时请务必检查该配的属性是否在）)
 * @autor: Faizel
 * @date: 2017/7/6
 * @version:1.0.0
 */
@Component
public class Resolve implements EnvironmentAware {

    private static final Logger logger = LoggerFactory.getLogger(Resolve.class);

    private String[] statusArray;/** 监控变更状态 **/

    private long reminderPeriod;/** 委托通知的时间间隔 **/

    private int connetionTimeOut;/** 连接待监控结点超时时间 **/

    private int requestTimeOut;/** 发出请求超时时间 **/

    private int readTimeOut;/** 读取待监控结点健康信息超时时间 **/

    private String index; /** ES索引名前缀 **/

    private String type;/** ES索引类型 **/

    private RelaxedPropertyResolver proResolver;/** 属性解析 **/

    /** 阻塞对列容量为10 用于存储发送短信微服务地址，用于实现轮询**/
    private BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10);

    /** 获取配置属性 **/
    @Override
    public void setEnvironment(Environment environment) {

        RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(environment);

        this.proResolver = propertyResolver;

        String status = propertyResolver.getProperty("faizel.notify.reminderStatuses");

        if(StringUtils.isEmpty(status)) {

            logger.error("faizel.notify.reminderStatuses is null!");

        }

        statusArray = status.split(",");

        /** 清理空格 **/
        for(int i = 0; i < statusArray.length; i++){

            statusArray[i] = statusArray[i].trim();
        }

        connetionTimeOut = Integer.parseInt(propertyResolver.

                getProperty("faizel.restTemplate.connectionTimeOut"));

        requestTimeOut = Integer.parseInt(propertyResolver.

                getProperty("faizel.restTemplate.requestTimeOut"));

        readTimeOut = Integer.parseInt(propertyResolver.

                getProperty("faizel.restTemplate.readTimeOut"));

        reminderPeriod = Long.parseLong(propertyResolver.

                getProperty("faizel.notify.reminderPeriod"));

        index = propertyResolver.getProperty("faizel.es.index");

        type = propertyResolver.getProperty("faizel.es.type");

        /**获取url **/
        String urls = propertyResolver.getProperty("faizel.sms.smsUrl");

        for(String tem: urls.split(",")){

            try {

                this.queue.put(tem.trim());

            }catch (Exception e){

                logger.error("异常", e);
            }
        }
    }

    /** get/set **/
    public String[] getStatusArray() {

        return statusArray;

    }

    public int getConnetionTimeOut() {

        return connetionTimeOut;

    }

    public int getRequestTimeOut() {

        return requestTimeOut;

    }

    public int getReadTimeOut() {

        return readTimeOut;

    }

    public String getIndex() {

        return index;

    }

    public String getType() {

        return type;

    }

    public long getReminderPeriod() {

        return reminderPeriod;

    }

    public RelaxedPropertyResolver getProResolver() {

        return proResolver;

    }

    public BlockingQueue<String> getQueue() {

        return queue;

    }
}
