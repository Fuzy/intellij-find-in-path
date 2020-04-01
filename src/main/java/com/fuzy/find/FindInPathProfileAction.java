package com.fuzy.find;

import javax.swing.SwingUtilities;

import org.jetbrains.annotations.NotNull;

import com.intellij.find.FindManager;
import com.intellij.find.FindModel;
import com.intellij.find.FindSettings;
import com.intellij.find.findInProject.FindInProjectManager;
import com.intellij.find.impl.FindInProjectUtil;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowId;


public class FindInPathProfileAction extends AnAction implements DumbAware {

    public static final NotificationGroup NOTIFICATION_GROUP = NotificationGroup.toolWindowGroup("Find in Path (conf)", ToolWindowId.FIND, false);

    private final Project project;
    private final FindModel model;

    public FindInPathProfileAction(Project project, FindModel model, String title) {
        super(title);
        this.project = project;
        this.model = model;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        DataContext dataContext = e.getDataContext();

        try {
            FindSettings settings = FindSettings.getInstance();
            settings.setFileMask(model.getFileFilter());
            settings.initModelBySetings(model);

            SwingUtilities.invokeLater(() -> {
                FindManager findManager = FindManager.getInstance(project);
                findManager.showFindDialog(model, () -> {
                    FindInProjectManager findInProjectManager = FindInProjectManager.getInstance(project);
                    FindInProjectUtil.setDirectoryName(model, dataContext);
                    findInProjectManager.findInPath(model);
                });
            });

        } catch (Throwable ex) {
            NOTIFICATION_GROUP.createNotification(ex.getMessage(), NotificationType.WARNING).notify(project);
        }
    }


}
