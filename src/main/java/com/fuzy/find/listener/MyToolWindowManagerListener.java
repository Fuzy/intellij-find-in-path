package com.fuzy.find.listener;

import org.jetbrains.annotations.NotNull;

import com.intellij.find.FindManager;
import com.intellij.find.FindModel;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ex.ToolWindowManagerListener;

import static com.fuzy.find.notification.Notifications.NOTIFICATION_GROUP;

public class MyToolWindowManagerListener implements ToolWindowManagerListener {
    private final Project project;

    public MyToolWindowManagerListener(Project project) {
        this.project = project;
    }

    @Override
    public void stateChanged(@NotNull ToolWindowManager toolWindowManager) {

        if (!ToolWindowId.FIND.equals(toolWindowManager.getActiveToolWindowId())) {
            return;
        }

        final FindManager findManager = FindManager.getInstance(project);
        FindModel defaultFindModel = findManager.getFindInProjectModel().clone();
        String content = defaultFindModel != null ? defaultFindModel.getStringToFind() : "empty";
        String id = toolWindowManager.getActiveToolWindowId();

        NOTIFICATION_GROUP.createNotification(content, NotificationType.INFORMATION).notify(project);
    }

}
