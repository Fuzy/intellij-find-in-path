package com.fuzy.find.listener;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import com.intellij.notification.NotificationGroupManager;
import org.jetbrains.annotations.NotNull;

import com.fuzy.find.persistence.ConfigurationManager;
import com.fuzy.find.util.StringUtils;
import com.intellij.find.FindManager;
import com.intellij.find.FindModel;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.ex.ToolWindowManagerListener;

import static com.fuzy.find.notification.Notifications.PLUGIN_ID;

/**
 * After Find window is showed, it will evaluate whether to ask to save/update/delete settings.
 * <p>
 * Possible candidate for intercept find event is com.intellij.find.FindManager#FIND_MODEL_TOPIC
 * but is not fired at Find in Files dialog.
 */
public class FindWindowManagerListener implements ToolWindowManagerListener {
    private final Project project;

    public FindWindowManagerListener(Project project) {
        this.project = project;
    }

    @Override
    public void stateChanged(@NotNull ToolWindowManager twm) {

        boolean isFind = Arrays.asList(twm.getActiveToolWindowId(), twm.getLastActiveToolWindowId()).contains(ToolWindowId.FIND);
        if (!isFind) {
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

        if (existsPersistentOption(currentFindModel)) {
            return;
        }

        // if not saved profile, it will save to prevent unwanted prompt to save
        saveAsLastUsed(currentFindModel);

        String question = MessageFormat.format("Do you want to save search configuration used in search for {0}?",
                content);

        List<String> names = collectNames();
        AnAction saveAction = createSaveAction(currentFindModel, createValidator(names), project);

        NotificationGroupManager.getInstance().getNotificationGroup(PLUGIN_ID).createNotification(question, NotificationType.INFORMATION)
                .addAction(saveAction).notify(project);
    }

    @NotNull
    private AnAction createSaveAction(final FindModel currentFindModel, final InputValidator validator, final Project project) {
        return new AnAction("Save") {

            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                String msg = "Save search configuration as";
                String name = StringUtils.trimToNull(Messages.showInputDialog(project, msg,
                        "Save Configuration", null, null, validator));
                if (name != null) {
                    saveFindOption(currentFindModel, name);
                }

            }

            private void saveFindOption(FindModel findModel, String name) {
                ConfigurationManager cm = ConfigurationManager.getInstance(project);
                cm.save(findModel, name);
            }
        };
    }

    private InputValidator createValidator(List<String> names) {
        return new InputValidator() {
            @Override
            public boolean checkInput(String inputString) {
                return inputString != null && inputString.length() > 1;
            }

            @Override
            public boolean canClose(String inputString) {
                return !names.contains(inputString);
            }
        };
    }

    private boolean existsPersistentOption(FindModel findModel) {
        ConfigurationManager cm = ConfigurationManager.getInstance(project);
        return cm.existsPersistentOption(findModel);
    }

    private void saveAsLastUsed(FindModel findModel) {
        ConfigurationManager cm = ConfigurationManager.getInstance(project);
        cm.saveAsLastUsed(findModel);
    }

    private List<String> collectNames() {
        ConfigurationManager cm = ConfigurationManager.getInstance(project);
        return cm.collectNames();
    }
}
