package com.fuzy.find.action;


import javax.swing.SwingUtilities;

import org.jetbrains.annotations.NotNull;

import com.fuzy.find.notification.Notifications;
import com.fuzy.find.persistence.ConfigurationManager;
import com.intellij.find.FindManager;
import com.intellij.find.FindModel;
import com.intellij.find.findInProject.FindInProjectManager;
import com.intellij.find.replaceInProject.ReplaceInProjectManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
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
        try {
            updateUsagePropertiesIfExists(model);

            SwingUtilities.invokeLater(this::processSearch);

        } catch (Throwable ex) {
            Notifications.notifyError(ex, project);
        }
    }

    private void processSearch() {
        FindManager findManager = FindManager.getInstance(project);
        findManager.showFindDialog(model, () -> {
            if (model.isReplaceState()) {
                ReplaceInProjectManager.getInstance(project).replaceInPath(model);
            } else {
                // Search in project
                FindInProjectManager findInProjectManager = FindInProjectManager.getInstance(project);
                findInProjectManager.findInPath(model);
            }

            // TODO seach in directory - com.intellij.find.impl.FindInProjectUtil.setDirectoryName
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
