package com.jyall.config;

import com.jyall.tools.FaizelESTool;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.sniff.ElasticsearchHostsSniffer;
import org.elasticsearch.client.sniff.HostsSniffer;
import org.elasticsearch.client.sniff.SniffOnFailureListener;
import org.elasticsearch.client.sniff.Sniffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * es的rest客户端连接配置
 * @autor: Faizel
 * @date: 2017/5/4
 * @version:1.0.0
 */
@Configuration
@ConditionalOnClass({ RestClientBuilder.class, RestClient.class})
public class ElasticSearchRestConfig {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchRestConfig.class);

    /** es结点地址 **/
    @Value("${faizel.es.clusterNodes}")
    private String esClusterNodes;

    /** socket的请求超时 **/
    @Value("${faizel.es.socketTimeOut}")
    private String socketTimeOut;

    /** 请求es的rest接口超时 **/
    @Value("${faizel.es.connectTimeOut}")
    private String connectTimeOut;

    /** 重试超时时间 **/
    @Value("${faizel.es.maxRetryTimeoutMillis}")
    private String maxRetryTimeoutMillis;

    /** http异步请求线程数 **/
    @Value("${faizel.es.threadCount}")
    private String threadCount;

    /** sniff嗅探超时时间 **/
    @Value("${faizel.es.sniffRequestTimeOut}")
    private String sniffRequestTimeOut;

    /** 使用的协议 **/
    private final static String schema = "http";

    /** sniff更新结点列表时间间隔 **/
    @Value("${faizel.es.sniffIntervalMillis}")
    private String sniffIntervalMillis;

    /** sniff嗅探失败 重试时间间隔**/
    @Value("${faizel.es.sniffAfterFailureDelayMillis}")
    private String sniffAfterFailureDelayMillis;

    @Value("${faizel.es.indexBeansPath}")
    private String indexBeansPath;

    @Value("${faizel.es.user}")
    private String user;

    @Value("${faizel.es.password}")
    private String password;

    @Value("${faizel.es.scanNestedJar}")
    private String scanJars;

    /** 嗅探失败处理器 **/
    private SniffOnFailureListener sniffOnFailureListener;

    /** 初始化es的rest客户端 **/
    @Bean
    public RestClient initRestClient(){

        String[] arrayJars = null;

        /** 将待扫描的jar包拆分为数组 **/
        if(null != scanJars){

            arrayJars = scanJars.split(",");

        }

        String[] arrayPackages = null;

        /** 将待扫描的多个包名拆分为数组 **/
        if(null != indexBeansPath){

            arrayPackages = indexBeansPath.split(",");

        }

        ifNull();

        RestClient restClient = buildClient(parseNodes());

        return restClient;
    }
    /** 初始化es结点嗅探器 **/
    @Bean
    public Sniffer initSniffer(RestClient  restClient ) {

            HostsSniffer hostsSniffer = new ElasticsearchHostsSniffer(restClient,

                    Long.parseLong(sniffRequestTimeOut),

                    ElasticsearchHostsSniffer.Scheme.HTTP);

            Sniffer sniffer = Sniffer.builder(restClient)

                    .setHostsSniffer(hostsSniffer)

                    .setSniffIntervalMillis(Integer.parseInt(sniffIntervalMillis))

                    .setSniffAfterFailureDelayMillis(Integer.parseInt(sniffAfterFailureDelayMillis))

                    .build();

            sniffOnFailureListener.setSniffer(sniffer);

            return sniffer;

    }

    /** 判断es是否为空,这里对于 字符串中应该是数字类型的异常情况没有判断，
     * 在配置是应该多加注意
     * **/
    private void ifNull(){

        if(StringUtils.isEmpty(esClusterNodes)){

            logger.error("es集群结点为空，请确认配置文件已正确配置, 终止程序运行！");

            System.exit(1);

        }

        if(StringUtils.isEmpty(indexBeansPath)){

            logger.info("indexBeansPath 没有配置");

        }

        if(StringUtils.isEmpty(socketTimeOut))

            socketTimeOut = "15000";

        if(StringUtils.isEmpty(connectTimeOut))

            connectTimeOut = "10000";

        if(StringUtils.isEmpty(maxRetryTimeoutMillis))

            maxRetryTimeoutMillis = "15000";

        if(StringUtils.isEmpty(threadCount))

            threadCount = "10";

        if(StringUtils.isEmpty(sniffRequestTimeOut))

            sniffRequestTimeOut = "5000";

        if(StringUtils.isEmpty(sniffIntervalMillis))

            sniffIntervalMillis = "300000";

        if(StringUtils.isEmpty(sniffAfterFailureDelayMillis))

            sniffAfterFailureDelayMillis = "60000";

        if(StringUtils.isEmpty(user))

            user = "elastic";

        if(StringUtils.isEmpty(password))

            password = "changeme";

        logger.info("ESClusterNodes:" + esClusterNodes);

    }

    /** 解析集群结点 **/
    private String[] parseNodes(){

        String[] nodes = null;

        try{

            nodes = esClusterNodes.split(",");

        }catch (Exception e){

            logger.error("解析集群结点异常，终止程序！", e);

            System.exit(1);

        }

        return nodes;
    }
    /** 创建客户端 **/
    private RestClient buildClient(String[] nodes){

        RestClientBuilder  restClientBuilder= FaizelESTool.AutoBuildHttpHost(nodes, schema);

        /** 用户名 密码 **/
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

        credentialsProvider.setCredentials(AuthScope.ANY,

                new UsernamePasswordCredentials(user, password));

        /* * 用于重新请求的超时时间，
         * 默认为30s与 socket timeout一致
         * */
        restClientBuilder.setMaxRetryTimeoutMillis(Integer.parseInt(maxRetryTimeoutMillis));

        /** 设置嗅探失败后的回调 **/
        restClientBuilder.setFailureListener(new RestClient.FailureListener(){

            @Override
            public void onFailure(HttpHost host) {

                logger.info("hostname:" + host.getHostName() +

                        "  ip:" + host.getAddress() + "  port" + host.getPort() +

                        "  的es结点无法连接！ 原因：网络中断或者es服务器故障");

            }
        });

        final int fconnectTimeOut = Integer.parseInt(connectTimeOut);

        final int fsocketTimeout = Integer.parseInt(socketTimeOut);

        final int fthreadCount = Integer.parseInt(threadCount);

        /** 设置rest请求的回调 **/
        restClientBuilder.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback(){

            @Override
            public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {

                requestConfigBuilder

                        .setConnectTimeout(fconnectTimeOut)

                        .setSocketTimeout(fsocketTimeout);

                return requestConfigBuilder;

            }
        });

        /** 设置http客户端配置回调  **/
        restClientBuilder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {

            @Override
            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {

                httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);

                return httpClientBuilder.setDefaultIOReactorConfig(

                        IOReactorConfig.custom().setIoThreadCount(fthreadCount).build());

            }
        });

        sniffOnFailureListener = new SniffOnFailureListener();

        restClientBuilder.setFailureListener(sniffOnFailureListener);

        return restClientBuilder.build();
    }
}
