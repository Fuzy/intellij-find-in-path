package com.fuzy.find.notification;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowId;

public class Notifications {

    public static final String PLUGIN_ID = "com.fuzy.find.in.path";
    public static final String DISPLAY_ID = "com.fuzy.find.notification";

    public static final NotificationGroup NOTIFICATION_GROUP =
        new NotificationGroup(DISPLAY_ID, NotificationDisplayType.BALLOON,
            true, ToolWindowId.FIND, null, "Find in Path (conf)",
            PluginId.getId(PLUGIN_ID));

    public static void notifyError(Throwable ex, Project project) {
        //TODO error if exists, stacktrace if NPE
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String sStackTrace = sw.toString(); // stack trace as a string

        NOTIFICATION_GROUP.createNotification(String.valueOf(sStackTrace), NotificationType.WARNING).notify(project);
    }

}
