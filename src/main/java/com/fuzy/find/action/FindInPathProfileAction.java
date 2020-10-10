package com.fuzy.find.action;


import javax.swing.SwingUtilities;

import org.jetbrains.annotations.NotNull;

import com.fuzy.find.notification.Notifications;
import com.fuzy.find.persistence.ConfigurationManager;
import com.intellij.find.FindManager;
import com.intellij.find.FindModel;
import com.intellij.find.findInProject.FindInProjectManager;
import com.intellij.find.impl.FindInProjectUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;

public class FindInPathProfileAction extends AnAction implements DumbAware {

    private final Project project;
    private final FindModel model;
    private final String uuid;
    private final String name;

    public FindInPathProfileAction(Project project, FindModel model, String uuid, String name) {
        super(name);
        this.project = project;
        this.model = model;
        this.uuid = uuid;
        this.name = name;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        DataContext dataContext = e.getDataContext();

        try {
            updateUsagePropertiesIfExists(model);

            SwingUtilities.invokeLater(() -> processSearch(dataContext));

        } catch (Throwable ex) {
            Notifications.notifyError(ex, project);
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

    private void updateUsagePropertiesIfExists(FindModel findModel) {
        ConfigurationManager configurationManager = ConfigurationManager.getInstance(project);
        configurationManager.updateUsagePropertiesIfExists(findModel);
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public FindModel getModel() {
        return model;
    }
}
