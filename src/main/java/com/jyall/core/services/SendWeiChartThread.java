package com.jyall.core.services;

import me.chanjar.weixin.cp.bean.WxCpMessageSendResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 线程类，用于发送微信
 * @autor: Faizel Lannister(褚福影)
 * @date: 2017/9/4 11:07
 * @version:1.0.0
 * @jvm-version: jdk 1.8
 * @Email: chufuying3@163.com
 */
public class SendWeiChartThread implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(SendWeiChartThread.class);

    private WeiChartService weiChartService;

    private String content;

    public SendWeiChartThread(WeiChartService weiChartService, String content){

        this.weiChartService = weiChartService;

        this.content = content;
    }

    @Override
    public void run() {

        try{

            WxCpMessageSendResult wxCpMessageSendResult = weiChartService.sendMessage(content);

            logger.info(wxCpMessageSendResult.toString());

        }catch (Exception e){

            logger.error("发送微信失败！", e);
        }
    }
}
