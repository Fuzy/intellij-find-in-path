package com.fuzy.find.listener;

import java.text.MessageFormat;

import org.jetbrains.annotations.NotNull;

import com.fuzy.find.persistence.ConfigurationManager;
import com.intellij.find.FindManager;
import com.intellij.find.FindModel;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ex.ToolWindowManagerListener;

import static com.fuzy.find.notification.Notifications.NOTIFICATION_GROUP;

/**
 * After Find window is showed, it will evaluate whether to ask to save/update/delete settings.
 */
public class FindWindowManagerListener implements ToolWindowManagerListener {
    private final Project project;

    public FindWindowManagerListener(Project project) {
        this.project = project;
    }

    @Override
    public void stateChanged(@NotNull ToolWindowManager toolWindowManager) {

        if (!ToolWindowId.FIND.equals(toolWindowManager.getActiveToolWindowId())) {
            return;
        }

        // last used search
        final FindManager findManager = FindManager.getInstance(project);
        FindModel currentFindModel = findManager.getFindInProjectModel().clone();
        if (currentFindModel == null) {
            return;
        }

        // no previous search
        String content = currentFindModel.getStringToFind();
        if (content.isEmpty()) {
            return;
        }
        String question = MessageFormat.format("Do you want to save search options used in search for {0}?",
            content);

        //TODO 1. existuje ulozene nastaveni se stejnymi parametry - zadny dialog
        //TODO 2. pouzil jsem hledani s novym nastavenim - dotaz zda ulozit

        AnAction saveAction = createSaveAction(currentFindModel);

        NOTIFICATION_GROUP.createNotification(question, NotificationType.INFORMATION)
            .addAction(saveAction).notify(project);
    }

    @NotNull
    private AnAction createSaveAction(FindModel currentFindModel) {
        return new AnAction("Save") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                String name = Messages.showInputDialog(project, "Save search options as", "Save Options", null);
                if (name != null) {
                    saveFindOption(currentFindModel, name);
                }
            }
        };
    }

    private void saveFindOption(FindModel currentFindModel, String name) {
        ConfigurationManager configurationManager = ConfigurationManager.getInstance(project);
        configurationManager.save(currentFindModel, name);
    }

    //com.intellij.psi.search.PredefinedSearchScopeProvider.getPredefinedScopes
    //NamedScopeManager.getInstance(project); workspace.xml
}
