package com.fuzy.find.notification;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.wm.ToolWindowId;

public class Notifications {

    public static final String PLUGIN_ID = "com.fuzy.find.in.path";
    public static final String DISPLAY_ID = "com.fuzy.find.notification";

    public static final NotificationGroup NOTIFICATION_GROUP =
        new NotificationGroup(DISPLAY_ID, NotificationDisplayType.BALLOON,
            true, ToolWindowId.FIND, null, "Find in Path (conf)",
            PluginId.getId(PLUGIN_ID));

}
