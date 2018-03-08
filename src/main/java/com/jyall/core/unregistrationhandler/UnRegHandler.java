package com.jyall.core.unregistrationhandler;

import com.alibaba.fastjson.JSONArray;
import com.jyall.config.Resolve;
import com.jyall.core.services.*;
import com.jyall.tools.IndexNameTool;
import com.jyall.tools.ParseJsonString;
import com.jyall.tools.ThreadPool;
import de.codecentric.boot.admin.event.ClientApplicationDeregisteredEvent;
import de.codecentric.boot.admin.event.ClientApplicationEvent;
import de.codecentric.boot.admin.event.ClientApplicationRegisteredEvent;
import de.codecentric.boot.admin.model.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 服务节点从eureka注册中心
 * 掉线和注册的处理类
 * @autor: Faizel
 * @date: 2017/7/7
 * @version:1.0.0
 */
@Component
public class UnRegHandler {

    private static final Logger logger = LoggerFactory.getLogger(UnRegHandler.class);

    @Autowired
    private HttpService httpService;

    @Autowired
    private Resolve resolve;

    @Autowired
    private ESService eSService;

    @Autowired
    private FeignService feignService;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private WeiChartService weiChartService;

    /** 处理方法体 **/
    public void handler(ClientApplicationEvent event){

        String url = printLogInfo(event);

        sendWeiChart(event);

        Map<String, String> info = invokeHealth(url);

        Status status = create(info, event);

        String index = IndexNameTool.getIndex(resolve.getIndex());

        try {

            eSService.save(index, resolve.getType(), status);

        }catch (Exception e){

            logger.error("索引文档异常！", e);
        }

    }

    /** 打印日志信息 **/
    private String printLogInfo(ClientApplicationEvent event){

        Application application = event.getApplication();

        String healthUrl = application.getHealthUrl();

        logger.info("name:" + application.getName());

        logger.info("id:" + application.getId());

        logger.info("HealthUrl:" + application.getHealthUrl());

        logger.info("type:" + event.getType());

        return healthUrl;
    }

    /** 调用对应地址的health接口 **/
    private Map<String, String> invokeHealth(String url){

        String jsonString = httpService.fetchHealthInfo(url);

        return ParseJsonString.parse(jsonString);

    }

    /** 创建对象 **/
    private Status create(Map<String, String> info, ClientApplicationEvent event){

        Application application = event.getApplication();

        Status status = new Status();

        status.setName(application.getName());

        status.setAppid(application.getId());

        status.setTo(event.getType());

        status.setCreateDate(new Date());

        status.setHostIp(application.getHealthUrl());

        status.setStatus(info);

        /** 若事件为unregistration事件 **/
        if(event instanceof ClientApplicationDeregisteredEvent) {

            status.setFrom(event.getApplication().getStatusInfo().getStatus());
        }

        /** 若为registration事件 **/
        else if(event instanceof ClientApplicationRegisteredEvent){

            status.setFrom(event.getApplication().getStatusInfo().getStatus());

        }

        return status;
    }

    /** 发送微信 **/
    private void sendWeiChart(ClientApplicationEvent event){

        /** 只处理为deregistered的事件 **/
        if(event instanceof ClientApplicationDeregisteredEvent){

            Application application = event.getApplication();

            RelaxedPropertyResolver propertyResolver = resolve.getProResolver();

            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("warning! ").append("(serverName:")
                    .append(application.getName()).append(" and ip:")
                    .append(this.parseIpFromUrl(application.getHealthUrl()))
                    .append(") ").append("had Deregistered from eureka ")
                    .append(propertyResolver.getProperty("faizel.sms.eurekaSign") + "!");

            SendWeiChartThread sendWeiChartThread = new SendWeiChartThread(weiChartService, stringBuilder.toString());

            /** 启动线程 **/
            ThreadPool.getThreadPool().execute(sendWeiChartThread);
        }
    }

    /** 发送短信 **/
    private void sendSms(ClientApplicationEvent event){

        /** 只处理为deregistered的事件 **/
        if(event instanceof ClientApplicationDeregisteredEvent){

            Application application = event.getApplication();

            String sname = application.getName();

            String ip = this.parseIpFromUrl(application.getHealthUrl());

            RelaxedPropertyResolver propertyResolver = resolve.getProResolver();

            JSONArray jsonArray = new JSONArray();

            jsonArray.add(sname);

            jsonArray.add(ip);

            jsonArray.add(propertyResolver.getProperty("faizel.sms.eurekaSign"));

            String mobiles = propertyResolver.getProperty("faizel.sms.sendMobiles");

            Integer smsModel = Integer.parseInt(propertyResolver.getProperty("faizel.sms.smsModel"));

            /** 调用feign **/
            if("true".equalsIgnoreCase(propertyResolver.getProperty("faizel.sms.autoSmsUrl"))){

                SendMailThread sendMailThread = new SendMailThread(

                        mobiles,

                        smsModel,

                        jsonArray.toString(),

                        feignService,

                        null,

                        null
                );

                /** 启动线程 **/
                ThreadPool.getThreadPool().execute(sendMailThread);

            }

            /** 调用resttemplate **/
            else{

                if(null !=  this.cycleUrl(resolve.getQueue())) {

                    SendMailThread sendMailThread = new SendMailThread(

                            mobiles,

                            smsModel,

                            jsonArray.toString(),

                            null,

                            templateService,

                            this.cycleUrl(resolve.getQueue())
                    );

                    /** 启动线程 **/
                    ThreadPool.getThreadPool().execute(sendMailThread);
                }

                else

                    logger.warn("没有可用的发送短信url， 终止发送");

            }

        }
    }

    /** 从url中解析ip **/
    private String parseIpFromUrl(String url){

        Pattern pattern = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+");

        Matcher matcher = pattern.matcher(url);

        if(matcher.find())

            return matcher.group();

        return "000.000.000.000";

    }

    /** 轮询短信url **/
    private String cycleUrl(BlockingQueue<String> queue){

        try {

            String url = queue.take();

            queue.put(url);

            return url;

        }catch (Exception e){

            logger.error("轮询短信服务url异常", e);
        }

        return null;
    }
}
