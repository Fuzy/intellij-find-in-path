package com.fuzy.find.action;


import javax.swing.SwingUtilities;

import org.jetbrains.annotations.NotNull;

import com.fuzy.find.model.FindUtils;
import com.fuzy.find.notification.Notifications;
import com.fuzy.find.persistence.ConfigurationManager;
import com.intellij.find.FindModel;
import com.intellij.find.FindSettings;
import com.intellij.find.findInProject.FindInProjectManager;
import com.intellij.find.impl.FindInProjectUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;

/**
 * Opens 'Find in path' dialog with predefined options.
 */
public class FindInPathProfileAction extends AnAction implements DumbAware {
    private static final Logger LOG = Logger.getInstance(FindInPathProfileAction.class);

    private final String uuid;
    private final String name;

    public FindInPathProfileAction(String uuid, String name, String presentationName) {
        super(presentationName);
        this.uuid = uuid;
        this.name = name;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getData(CommonDataKeys.PROJECT);
        if (project == null) {
            LOG.warn("Project is null.");
            return;
        }

        DataContext dataContext = e.getDataContext();

        FindModel model = new FindUtils().modelForUuid(uuid, project);
        if (model == null) {
            String msg = "Persistent state of configurations not found in FindInPathConfiguration.xml";
            Notifications.notifyError(msg, project);
            return;
        }

        updateUsagePropertiesIfExists(model, project);

        SwingUtilities.invokeLater(() -> processSearch(dataContext, model, project));
    }

    private void processSearch(DataContext dataContext, FindModel model, Project project) {
        FindInProjectManager findManager = FindInProjectManager.getInstance(project);

        if (!findManager.isEnabled()) {
            showNotAvailableMessage(project);
            return;
        }

        // Dropdown must contain this option
        FindSettings.getInstance().setFileMask(model.getFileFilter());
        FindInProjectUtil.initStringToFindFromDataContext(model, dataContext);

        findManager.findInProject(dataContext, model);
    }

    private void updateUsagePropertiesIfExists(FindModel findModel, Project project) {
        ConfigurationManager configurationManager = ConfigurationManager.getInstance(project);
        configurationManager.updateUsagePropertiesIfExists(findModel);
    }

    private void showNotAvailableMessage(Project project) {
        final String message = "'Find in Files' is not available while search is in progress";
        Notifications.notifyWarning(message, project);
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

}
