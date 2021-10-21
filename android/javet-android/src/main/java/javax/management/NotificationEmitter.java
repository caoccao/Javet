package javax.management;

import com.caoccao.javet.interop.V8Notifier;

public interface NotificationEmitter {
    void addNotificationListener(V8Notifier v8Notifier, Object o, Object o1);

    void removeNotificationListener(V8Notifier v8Notifier, Object o, Object o1)
            throws ListenerNotFoundException;
}
