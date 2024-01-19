/*
 * Copyright (c) 2021-2024. caoccao.com Sam Cao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.caoccao.javet.interop;

import javax.management.ListenerNotFoundException;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryNotificationInfo;
import java.util.concurrent.ConcurrentHashMap;

public final class V8Notifier
        /* if not defined ANDROID */
        implements NotificationListener
        /* end if */ {
    private final ConcurrentHashMap<Long, V8Runtime> v8RuntimeMap;

    public V8Notifier(ConcurrentHashMap<Long, V8Runtime> v8RuntimeMap) {
        this.v8RuntimeMap = v8RuntimeMap;
    }

    /* if not defined ANDROID */
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
    /* end if */

    public void registerListeners() {
        /* if not defined ANDROID */
        NotificationEmitter notificationEmitter = (NotificationEmitter) ManagementFactory.getMemoryMXBean();
        try {
            notificationEmitter.removeNotificationListener(this, null, null);
        } catch (ListenerNotFoundException ignored) {
        }
        notificationEmitter.addNotificationListener(this, null, null);
        /* end if */
    }

    public void unregisterListener() {
        /* if not defined ANDROID */
        try {
            NotificationEmitter notificationEmitter = (NotificationEmitter) ManagementFactory.getMemoryMXBean();
            notificationEmitter.removeNotificationListener(this, null, null);
        } catch (ListenerNotFoundException ignored) {
        }
        /* end if */
    }
}
