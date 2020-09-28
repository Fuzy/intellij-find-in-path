package com.fuzy.find.action;


import javax.swing.SwingUtilities;

import org.jetbrains.annotations.NotNull;

import com.intellij.find.FindManager;
import com.intellij.find.FindModel;
import com.intellij.find.FindSettings;
import com.intellij.find.findInProject.FindInProjectManager;
import com.intellij.find.impl.FindInProjectUtil;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;

import static com.fuzy.find.notification.Notifications.NOTIFICATION_GROUP;

public class FindInPathProfileAction extends AnAction implements DumbAware {

    private final Project project;
    private final FindModel model;

    public FindInPathProfileAction(Project project, FindModel model, String name) {
        super(name);
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

            SwingUtilities.invokeLater(() -> processSearch(dataContext));

        } catch (Throwable ex) {
            NOTIFICATION_GROUP.createNotification(String.valueOf(ex.getMessage()), NotificationType.WARNING).notify(project);
        }
    }

    private void processSearch(DataContext dataContext) {
        FindManager findManager = FindManager.getInstance(project);
        findManager.showFindDialog(model, () -> {
            FindInProjectManager findInProjectManager = FindInProjectManager.getInstance(project);
            FindInProjectUtil.setDirectoryName(model, dataContext);
            findInProjectManager.findInPath(model);
        });
    }

}
