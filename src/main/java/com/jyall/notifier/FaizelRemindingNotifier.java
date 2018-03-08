package com.jyall.notifier;

import de.codecentric.boot.admin.event.ClientApplicationDeregisteredEvent;
import de.codecentric.boot.admin.event.ClientApplicationEvent;
import de.codecentric.boot.admin.event.ClientApplicationStatusChangedEvent;
import de.codecentric.boot.admin.notify.AbstractEventNotifier;
import de.codecentric.boot.admin.notify.Notifier;
import org.springframework.util.Assert;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 自定义的RemindingNotifier类
 * 主要修正了无法通知unregistration事件
 * @autor: Faizel
 * @date: 2017/7/7
 * @version:1.0.0
 */
public class FaizelRemindingNotifier extends AbstractEventNotifier {

        private final ConcurrentHashMap<String, Reminder> reminders = new ConcurrentHashMap<>();

        private long reminderPeriod = TimeUnit.MINUTES.toMillis(10L);

        private String[] reminderStatuses = { "DOWN", "OFFLINE" };

        private final Notifier delegate;

        public FaizelRemindingNotifier(Notifier delegate) {

            Assert.notNull(delegate, "'delegate' must not be null!");

            this.delegate = delegate;
        }

        @Override
        public void doNotify(ClientApplicationEvent event) {

            delegate.notify(event);

            if (shouldEndReminder(event)) {

                reminders.remove(event.getApplication().getId());

            } else if (shouldStartReminder(event)) {

                reminders.putIfAbsent(event.getApplication().getId(), new Reminder(event));

            }
        }

        public void sendReminders() {

            long now = System.currentTimeMillis();

            for (Reminder reminder : new ArrayList<>(reminders.values())) {

                if (now - reminder.getLastNotification() > reminderPeriod) {

                    reminder.setLastNotification(now);

                    delegate.notify(reminder.getEvent());

                }
            }
        }

        protected boolean shouldStartReminder(ClientApplicationEvent event) {

            if (event instanceof ClientApplicationStatusChangedEvent) {

                return Arrays.binarySearch(reminderStatuses,

                        event.getApplication().getStatusInfo().getStatus()) >= 0;
            }

            return false;
        }

        protected boolean shouldEndReminder(ClientApplicationEvent event) {

            if (event instanceof ClientApplicationDeregisteredEvent) {

                return true;

            }
            if (event instanceof ClientApplicationStatusChangedEvent) {

                return Arrays.binarySearch(reminderStatuses,

                        event.getApplication().getStatusInfo().getStatus()) < 0;

            }

            return false;
        }

        public void setReminderPeriod(long reminderPeriod) {

            this.reminderPeriod = reminderPeriod;

        }

        public void setReminderStatuses(String[] reminderStatuses) {

            String[] copy = Arrays.copyOf(reminderStatuses, reminderStatuses.length);

            Arrays.sort(copy);

            this.reminderStatuses = copy;
        }

    private static class Reminder {

        private final ClientApplicationEvent event;

        private long lastNotification;

        private Reminder(ClientApplicationEvent event) {

            this.event = event;

            this.lastNotification = event.getTimestamp();

        }

        public void setLastNotification(long lastNotification) {

            this.lastNotification = lastNotification;

        }

        public long getLastNotification() {

            return lastNotification;

        }

        public ClientApplicationEvent getEvent() {

            return event;

        }
    }
}
