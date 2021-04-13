package com.caoccao.javet.interop;

import javax.management.ListenerNotFoundException;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryNotificationInfo;
import java.util.concurrent.ConcurrentHashMap;

public final class V8Notifier implements NotificationListener {
    private ConcurrentHashMap<Long, V8Runtime> v8RuntimeMap;

    public V8Notifier(ConcurrentHashMap<Long, V8Runtime> v8RuntimeMap) {
        this.v8RuntimeMap = v8RuntimeMap;
    }

    @Override
    public void handleNotification(Notification notification, Object handback) {
        String notificationType = notification.getType();
        if (MemoryNotificationInfo.MEMORY_THRESHOLD_EXCEEDED.equals(notificationType)
                || MemoryNotificationInfo.MEMORY_COLLECTION_THRESHOLD_EXCEEDED.equals(notificationType)) {
            // This is thread-safe and async operation.
            for (V8Runtime v8Runtime : v8RuntimeMap.values()) {
                v8Runtime.setGCScheduled(true);
            }
        }
    }

    public void registerListeners() {
        NotificationEmitter notificationEmitter = (NotificationEmitter) ManagementFactory.getMemoryMXBean();
        try {
            notificationEmitter.removeNotificationListener(this, null, null);
        } catch (ListenerNotFoundException e) {
        }
        notificationEmitter.addNotificationListener(this, null, null);
    }

    public void unregisterListener() {
        try {
            NotificationEmitter notificationEmitter = (NotificationEmitter) ManagementFactory.getMemoryMXBean();
            notificationEmitter.removeNotificationListener(this, null, null);
        } catch (ListenerNotFoundException e) {
        }
    }
}
