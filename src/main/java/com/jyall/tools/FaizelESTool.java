package com.jyall.tools;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 工具类
 * @autor: Faizel
 * @date: 2017/5/4
 * @version:1.0.0
 */
public class FaizelESTool {

    private static final Logger logger = LoggerFactory.getLogger(FaizelESTool.class);

    /** 根据结点个数自动生成对应的HttpHost,
     * 然后组装并返回RestClientBuilder
     *
     * **/
    public static RestClientBuilder AutoBuildHttpHost(String[] nodes, String SCHEMA){

        if(null == nodes || nodes.length < 1)

            return null;

        /** 解析ip地址和端口号 **/
        List<String[]> list = parseIpPort(nodes);

        HttpHost[] httpHosts = new HttpHost[list.size()];

        for(int i = 0; i < list.size(); i++){

            httpHosts[i] = new HttpHost(list.get(i)[0], Integer.parseInt(list.get(i)[1]), SCHEMA);

        }

        return RestClient.builder(httpHosts);

    }

    /** 将结点标识解析出ip和端口 **/
    private static List<String[]> parseIpPort(String[] nodes){

       List<String[]> ipPorts = new ArrayList<String[]>();

        for(String tem: nodes){

            try{

                if(!check(tem)) {

                    logger.error("cluster-nodes 配置属性有问题，请检测确认. 终止jvm虚拟机！:" + tem);

                    System.exit(1);
                }

                ipPorts.add(tem.split(":"));

            }
            catch (Exception e) {

                logger.error("从结点解析ip和port异常",e);

                System.exit(1);

            }

        }

        return ipPorts;
    }

    /** 检测结点地址是否合法 **/
    private static boolean check(String str){

        String s = new String(":");

        return str.contains(s);

    }

}
