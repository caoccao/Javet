package javax.management;

public interface NotificationListener {
    void handleNotification(Notification notification, Object handback);
}
