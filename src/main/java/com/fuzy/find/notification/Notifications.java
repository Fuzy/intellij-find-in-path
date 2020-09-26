package com.fuzy.find.notification;

import com.intellij.notification.NotificationGroup;
import com.intellij.openapi.wm.ToolWindowId;

public class Notifications {

    public static final NotificationGroup NOTIFICATION_GROUP = NotificationGroup.toolWindowGroup("Find in Path (conf)", ToolWindowId.FIND, false);

}
