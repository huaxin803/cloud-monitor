package com.jyall;

import de.codecentric.boot.admin.event.ClientApplicationEvent;
import de.codecentric.boot.admin.model.Application;

/**
 * 测试使用
 * @autor: Faizel
 * @date: 2017/7/7
 * @version:1.0.0
 */
public class MyEvent extends ClientApplicationEvent {

    public MyEvent(Application application,String type){

        super(application, type);
    }
}
