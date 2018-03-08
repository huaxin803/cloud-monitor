package com.jyall.core.statuschangehandler;

import com.jyall.config.Resolve;
import com.jyall.core.unregistrationhandler.ESService;
import com.jyall.core.unregistrationhandler.HttpService;
import com.jyall.core.unregistrationhandler.Status;
import com.jyall.tools.IndexNameTool;
import com.jyall.tools.ParseJsonString;
import de.codecentric.boot.admin.event.ClientApplicationStatusChangedEvent;
import de.codecentric.boot.admin.model.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.Map;

/**
 * offline事件处理类
 * @autor: Faizel
 * @date: 2017/7/10
 * @version:1.0.0
 */
@Component
public class StatusChangeHandler {

    @Autowired
    private HttpService httpService;

    @Autowired
    private Resolve resolve;

    @Autowired
    private ESService eSService;

    private static final Logger logger = LoggerFactory.getLogger(StatusChangeHandler.class);

    public void handler(ClientApplicationStatusChangedEvent event){

        Map<String, String> map = getHealthInfo(printInfo(event));

        inputES(createObject(map, event));

    }

    /** 打印信息 **/
    private String printInfo(ClientApplicationStatusChangedEvent event){

        Application application = event.getApplication();

        logger.info("name:" + application.getName());

        logger.info("id:" + application.getId());

        logger.info("url:" + application.getHealthUrl());

        logger.info("from:" + event.getFrom().getStatus());

        logger.info("to:" + event.getTo().getStatus());

        return application.getHealthUrl();
    }

    /** 获取健康信息 **/
    private Map<String, String> getHealthInfo(String url){

        String jsonString = httpService.fetchHealthInfo(url);

        return ParseJsonString.parse(jsonString);
    }

    /** 创建存储对象 **/
    private Status createObject(Map<String, String> info, ClientApplicationStatusChangedEvent event){

        Application application = event.getApplication();

        Status status = new Status();

        status.setName(application.getName());

        status.setAppid(application.getId());

        status.setTo(event.getTo().getStatus());

        status.setCreateDate(new Date());

        status.setHostIp(application.getHealthUrl());

        status.setStatus(info);

        status.setFrom(event.getFrom().getStatus());

        return status;

    }
    /** 信息导入es **/
    private void inputES(Status status){

        String index = IndexNameTool.getIndex(resolve.getIndex());

        try {

            eSService.save(index, resolve.getType(), status);

        }catch (Exception e){

            logger.error("索引文档异常", e);
        }
    }
}
