package java.lang.management;

import com.caoccao.javet.interop.V8Notifier;

import java.util.ArrayList;
import java.util.List;

import javax.management.ListenerNotFoundException;
import javax.management.NotificationEmitter;

public class ManagementFactory {
    public static List<MemoryPoolMXBean> getMemoryPoolMXBeans() {
        return new ArrayList<>();
    }

    public static NotificationEmitter getMemoryMXBean() {
        return new NotificationEmitter() {
            @Override
            public void addNotificationListener(V8Notifier v8Notifier, Object o, Object o1) {
            }

            @Override
            public void removeNotificationListener(V8Notifier v8Notifier, Object o, Object o1)
                    throws ListenerNotFoundException {
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
