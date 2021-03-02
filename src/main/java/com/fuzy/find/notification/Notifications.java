package com.fuzy.find.notification;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowId;

/**
 * Shows error and warning notifications.
 * <p>
 * In case of of unprocessed error, 'IDE Fatal Errors' will show.
 */
public class Notifications {

    public static final String PLUGIN_ID = "com.fuzy.find.in.path";


    public static void notifyError(String msg, Project project) {
        NotificationGroupManager.getInstance().getNotificationGroup(PLUGIN_ID)
                .createNotification(msg, NotificationType.ERROR)
                .notify(project);
    }

    public static void notifyWarning(String msg, Project project) {
        NotificationGroupManager.getInstance().getNotificationGroup(PLUGIN_ID)
                .createNotification(msg, NotificationType.ERROR)
                .notify(project);
    }


}
