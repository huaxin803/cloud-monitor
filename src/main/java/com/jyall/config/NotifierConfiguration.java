package com.jyall.config;
import com.jyall.core.statuschangehandler.StatusChangeHandler;
import com.jyall.core.unregistrationhandler.UnRegHandler;
import com.jyall.notifier.ElasticNotifier;
import com.jyall.notifier.FaizelRemindingNotifier;
import de.codecentric.boot.admin.notify.filter.FilteringNotifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import java.util.concurrent.TimeUnit;
/**
 * 服务节点状态变更通知类
 * @autor: Faizel
 * @date: 2017/7/6
 * @version:1.0.0
 */
@Configuration
@EnableScheduling
public class NotifierConfiguration {

    @Autowired
    private Resolve resolve;

    @Autowired
    private UnRegHandler unRegHandler;

    @Autowired
    private StatusChangeHandler offlineHandler;

    @Bean/**这个暂时没使用（用于过滤请求） **/
    public FilteringNotifier filteringNotifier() {

        return new FilteringNotifier(elasticNotifier());

    }

    @Bean
    @Primary
    public FaizelRemindingNotifier remindingNotifier() {

        FaizelRemindingNotifier remindingNotifier = new FaizelRemindingNotifier(elasticNotifier());

        remindingNotifier.setReminderPeriod(TimeUnit.MINUTES.toMillis(resolve.getReminderPeriod()));

        remindingNotifier.setReminderStatuses(resolve.getStatusArray());

        return remindingNotifier;
    }
    /* *
     * 用于在指定时间间隔内调用通知另一个notifier方法，
     * 通知能够被通过需要ReminderPeriod配合(暂时不用)
     * */
//    @Scheduled(fixedRate = 5_000L)
//    public void remind() {
//
//        remindingNotifier().sendReminders();
//
//    }

    @Bean
    public ElasticNotifier elasticNotifier(){

        return new ElasticNotifier(offlineHandler, unRegHandler);
    }
}
