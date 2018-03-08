package com.jyall;
import com.jyall.config.HazelCastProperties;
import com.jyall.core.unregistrationhandler.UnRegHandler;
import de.codecentric.boot.admin.config.EnableAdminServer;
import de.codecentric.boot.admin.model.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * 程序入口
 * @autor: Faizel
 * @date: 2017/7/5
 * @version:1.0.0
 */
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@EnableAdminServer
@RestController
public class Main {

    @Autowired
    private UnRegHandler unRegHandler;

    @Autowired
    private HazelCastProperties hazelCastProperties;

    public static void main(String[] args){

        SpringApplication.run(Main.class, args);

    }

    /** 测试用 **/
    @GetMapping("/test")
    public String test(){

        Application.Builder dd= Application.create("test");

        Application application = dd.withId("admin-test").withHealthUrl("http://localhost:8080/health").build();

        MyEvent myEvent = new MyEvent(application, "unre");

        unRegHandler.handler(myEvent);

        System.out.println(hazelCastProperties.getMembers());

        return "ok";
    }
}
