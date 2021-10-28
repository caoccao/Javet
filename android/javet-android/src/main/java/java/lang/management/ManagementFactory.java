package java.lang.management;

import java.util.ArrayList;
import java.util.List;

import javax.management.ListenerNotFoundException;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;

public class ManagementFactory {
    public static List<MemoryPoolMXBean> getMemoryPoolMXBeans() {
        return new ArrayList<>();
    }

    public static NotificationEmitter getMemoryMXBean() {
        return new NotificationEmitter() {
            @Override
            public void addNotificationListener(NotificationListener notificationListener, Object o, Object o1) {
            }

            @Override
            public void removeNotificationListener(NotificationListener notificationListener, Object o, Object o1) throws ListenerNotFoundException {
            }
        };
    }

    public static RuntimeMXBean getRuntimeMXBean() {
        return new RuntimeMXBean() {
            @Override
            public String getInputArguments() {
                return "";
            }

            @Override
            public String getName() {
                return "1";
            }
        };
    }
}
