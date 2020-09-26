package com.fuzy.find.action;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.swing.SwingUtilities;

import org.jetbrains.annotations.NotNull;

import com.fuzy.find.persistence.ConfigurationManager;
import com.fuzy.find.persistence.FindOption;
import com.fuzy.find.persistence.FindOptions;
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
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindowId;

import static com.fuzy.find.notification.Notifications.NOTIFICATION_GROUP;

public class FindInPathProfileAction extends AnAction implements DumbAware {

    private final Project project;
    private final FindModel model;
    private final String uuid;

    public FindInPathProfileAction(Project project, FindModel model, String name, String uuid) {
        super(name);
        this.uuid = uuid;
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

            ConfigurationManager configurationManager = ConfigurationManager.getInstance(project);
            FindOptions findOptions = configurationManager.getState();

            if (findOptions == null) {
                findOptions = new FindOptions();
            }
            configurationManager.setState(findOptions);

            FindOption findOption = configurationManager.findOrInitNew(uuid);
            if (findOption.getUuid() == null) {
                updateFindOptionByModel(findOption, model);
                findOptions.getOptions().add(findOption);
            }
            updateFindOptionByModel(findOption, model);


            NOTIFICATION_GROUP.createNotification(String.valueOf(findOptions), NotificationType.INFORMATION).notify(project);

            SwingUtilities.invokeLater(() -> {
                FindManager findManager = FindManager.getInstance(project);
                findManager.showFindDialog(model, () -> {
                    FindInProjectManager findInProjectManager = FindInProjectManager.getInstance(project);
                    FindInProjectUtil.setDirectoryName(model, dataContext);
                    findInProjectManager.findInPath(model);

                    //TODO pouzit odkaz na dialog v notifikacnim okne
                    //TODO pokud neulozi nazev znamena to ze to ulozit nechce
                    //TODO pokud se nastaveni zmenilo od ulozeneho zeptat se novy/prepsat - ale az v notifikace
                    String s = Messages.showInputDialog(project, "Save search options", "Save Options", null);
                    if (s != null) {
                        updateFindOptionByModel(findOption, s);
                    }
                });
            });


        } catch (Throwable ex) {
            NOTIFICATION_GROUP.createNotification(String.valueOf(ex.getMessage()), NotificationType.WARNING).notify(project);
        }
    }

    private void updateFindOptionByModel(FindOption findOption, FindModel model) {
        if (findOption.getUuid() == null || findOption.getUuid().isBlank()) {
            findOption.setUuid(UUID.randomUUID().toString());
        }

        findOption.setLastUsed(LocalDateTime.now());
        findOption.setCntUsed(findOption.getCntUsed() + 1);
        findOption.setFileFilter(model.getFileFilter());
        findOption.setCaseSensitive(model.isCaseSensitive());
        findOption.setRegularExpressions(model.isRegularExpressions());
        findOption.setWholeWordsOnly(model.isWholeWordsOnly());
        findOption.setSearchContext(model.getSearchContext().name());
    }

    private void updateFindOptionByModel(FindOption findOption, String name) {
        findOption.setName(name);
    }

}
