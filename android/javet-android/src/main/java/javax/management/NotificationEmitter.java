package javax.management;

public interface NotificationEmitter {
    void addNotificationListener(NotificationListener notificationListener, Object o, Object o1);

    void removeNotificationListener(NotificationListener notificationListener, Object o, Object o1)
            throws ListenerNotFoundException;
}
