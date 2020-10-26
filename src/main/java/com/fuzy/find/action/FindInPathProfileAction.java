package com.fuzy.find.action;


import javax.swing.SwingUtilities;

import org.jetbrains.annotations.NotNull;

import com.fuzy.find.model.FindUtils;
import com.fuzy.find.notification.Notifications;
import com.fuzy.find.persistence.ConfigurationManager;
import com.intellij.find.FindManager;
import com.intellij.find.FindModel;
import com.intellij.find.FindSettings;
import com.intellij.find.findInProject.FindInProjectManager;
import com.intellij.find.replaceInProject.ReplaceInProjectManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;

public class FindInPathProfileAction extends AnAction implements DumbAware {

    private final Project project;
    private final String uuid;
    private final String name;
    private final String stringToFind;

    public FindInPathProfileAction(Project project, String uuid, String name, String stringToFind) {
        super(name);
        this.project = project;
        this.uuid = uuid;
        this.name = name;
        this.stringToFind = stringToFind;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        try {
            FindModel model = new FindUtils().modelForUuid(uuid, project);
            if (model == null) {
                String msg = "Persistent state of options not found in FindInPathConfiguration.xml";
                Notifications.notifyError(msg, project);
                return;
            }

            updateUsagePropertiesIfExists(model);
            model.setStringToFind(stringToFind);

            SwingUtilities.invokeLater(() -> processSearch(model));

        } catch (Throwable ex) {
            Notifications.notifyError(ex, project);
        }
    }

    private void processSearch(FindModel model) {
        FindManager findManager = FindManager.getInstance(project);

        // Dropdown must contain this option
        FindSettings.getInstance().setFileMask(model.getFileFilter());

        findManager.showFindDialog(model, () -> {
            if (model.isReplaceState()) {
                ReplaceInProjectManager.getInstance(project).replaceInPath(model);
            } else {
                // Search in project
                FindInProjectManager findInProjectManager = FindInProjectManager.getInstance(project);
                findInProjectManager.findInPath(model);
            }

            // TODO search in directory - com.intellij.find.impl.FindInProjectUtil.setDirectoryName
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

}
