package com.fuzy.find.action;


import javax.swing.SwingUtilities;

import org.jetbrains.annotations.NotNull;

import com.fuzy.find.model.FindUtils;
import com.fuzy.find.notification.Notifications;
import com.fuzy.find.persistence.ConfigurationManager;
import com.intellij.find.FindModel;
import com.intellij.find.FindSettings;
import com.intellij.find.findInProject.FindInProjectManager;
import com.intellij.ide.lightEdit.LightEdit;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectoryContainer;
import com.intellij.psi.PsiElement;

/**
 * Opens 'Find in path' dialog with predefined options.
 */
public class FindInPathProfileAction extends AnAction implements DumbAware {
    private static final Logger LOG = Logger.getInstance(FindInPathProfileAction.class);

    private final String uuid;
    private final String name;
    private final String stringToFind;// TODO FindInProjectUtil.initStringToFindFromDataContext(findModel, dataContext);

    public FindInPathProfileAction(String uuid, String name, String presentationName, String stringToFind) {
        super(presentationName);
        this.uuid = uuid;
        this.name = name;
        this.stringToFind = stringToFind;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getData(CommonDataKeys.PROJECT);
        if (project == null) {
            LOG.warn("Project is null.");
            return;
        }

        DataContext dataContext = e.getDataContext();

        try {
            FindModel model = new FindUtils().modelForUuid(uuid, project);
            if (model == null) {
                String msg = "Persistent state of options not found in FindInPathConfiguration.xml";
                Notifications.notifyError(msg, project);
                return;
            }

            updateUsagePropertiesIfExists(model, project);
//            model.setStringToFind(stringToFind);

            SwingUtilities.invokeLater(() -> processSearch(dataContext, model, project));

        } catch (Throwable ex) {
            Notifications.notifyError(ex, project);
        }
    }

    private void processSearch(DataContext dataContext, FindModel model, Project project) {
        FindInProjectManager findManager = FindInProjectManager.getInstance(project);

        if (!findManager.isEnabled()) {
            showNotAvailableMessage(project);
            return;
        }

        // Dropdown must contain this option
        FindSettings.getInstance().setFileMask(model.getFileFilter());


        findManager.findInProject(dataContext, model);
    }

    private void updateUsagePropertiesIfExists(FindModel findModel, Project project) {
        ConfigurationManager configurationManager = ConfigurationManager.getInstance(project);
        configurationManager.updateUsagePropertiesIfExists(findModel);
    }

    private void showNotAvailableMessage(Project project) {
        final String message = "'Find in path' is not available while search is in progress";
        Notifications.notifyWarning(message, project);
    }

    //region Copy of com.intellij.find.actions.FindInPathAction
    @Override
    public void update(@NotNull AnActionEvent e){
        doUpdate(e);
    }

    private void doUpdate(@NotNull AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        Project project = e.getData(CommonDataKeys.PROJECT);
        presentation.setEnabled(project != null && !LightEdit.owns(project));
        if (ActionPlaces.isPopupPlace(e.getPlace())) {
            presentation.setVisible(isValidSearchScope(e));
        }
    }

    private boolean isValidSearchScope(@NotNull AnActionEvent e) {
        final PsiElement[] elements = e.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        if (elements != null && elements.length == 1 && elements[0] instanceof PsiDirectoryContainer) {
            return true;
        }
        final VirtualFile[] virtualFiles = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
        return virtualFiles != null && virtualFiles.length == 1 && virtualFiles[0].isDirectory();
    }
    //endregion

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

}
