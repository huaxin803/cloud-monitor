package com.jyall.notifier;

import com.jyall.core.statuschangehandler.StatusChangeHandler;
import com.jyall.core.unregistrationhandler.UnRegHandler;
import de.codecentric.boot.admin.event.ClientApplicationDeregisteredEvent;
import de.codecentric.boot.admin.event.ClientApplicationEvent;
import de.codecentric.boot.admin.event.ClientApplicationRegisteredEvent;
import de.codecentric.boot.admin.event.ClientApplicationStatusChangedEvent;
import de.codecentric.boot.admin.notify.AbstractEventNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 自定义的通知，
 * 用于将通知信息存入es中
 * @autor: Faizel
 * @date: 2017/7/6
 * @version:1.0.0
 */
public class ElasticNotifier extends AbstractEventNotifier {

    private static final Logger logger = LoggerFactory.getLogger(ElasticNotifier.class);

    private StatusChangeHandler statusChangeHandler;

    private UnRegHandler unRegHandler;/** unregistration 事件处理类  **/

    public ElasticNotifier(StatusChangeHandler offlineHandler, UnRegHandler unRegHandler){

        this.statusChangeHandler = offlineHandler;

        this.unRegHandler = unRegHandler;

    }

    @Override
    protected void doNotify(ClientApplicationEvent event) throws Exception {

        if(event instanceof ClientApplicationStatusChangedEvent){

            statusChangeHandler.handler((ClientApplicationStatusChangedEvent)event);
        }

        else if(event instanceof ClientApplicationRegisteredEvent){

            logger.info("we get a Registration event!  begain to handling 。 。 。 。 。 。");

            unRegHandler.handler(event);

        }

        else if(event instanceof ClientApplicationDeregisteredEvent){

            logger.info("we get a UnRegistration event!  begain to handling 。 。 。 。 。 。");

            unRegHandler.handler(event);

        }
    }
}
