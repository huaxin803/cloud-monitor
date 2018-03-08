package com.jyall.core.services;

import com.jyall.config.WxCpProperties;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.WxCpMessage;
import me.chanjar.weixin.cp.bean.WxCpMessageSendResult;
import me.chanjar.weixin.cp.bean.messagebuilder.TextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 企业微信号服务类
 * @autor: Faizel Lannister(褚福影)
 * @date: 2017/9/1 10:04
 * @version:1.0.0
 * @jvm-version: jdk 1.8
 * @Email: chufuying3@163.com
 */
@Service
public class WeiChartService {

    @Autowired
    private WxCpService service;

    @Autowired
    private WxCpProperties wxCpProperties;

    /** 发送微信 **/
    public WxCpMessageSendResult sendMessage(String content) throws Exception{

        TextBuilder textBuilder = new TextBuilder();

        textBuilder.content(content);

        WxCpMessage wxCpMessage = textBuilder.build();

        wxCpMessage.setToTag(wxCpProperties.getToTag());

        wxCpMessage.setAgentId(wxCpProperties.getAgentId());

        return service.messageSend(wxCpMessage);
    }
}
