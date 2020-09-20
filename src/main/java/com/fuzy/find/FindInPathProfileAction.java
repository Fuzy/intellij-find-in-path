package com.fuzy.find;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.swing.SwingUtilities;

import org.jetbrains.annotations.NotNull;

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
import com.intellij.openapi.wm.ToolWindowId;

public class FindInPathProfileAction extends AnAction implements DumbAware {

    public static final NotificationGroup NOTIFICATION_GROUP = NotificationGroup.toolWindowGroup("Find in Path (conf)", ToolWindowId.FIND, false);

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
                });
            });

            //TODO ask for name

        } catch (Throwable ex) {
            NOTIFICATION_GROUP.createNotification(String.valueOf(ex.getMessage()), NotificationType.WARNING).notify(project);
        }
    }

    private void updateFindOptionByModel(FindOption findOption, FindModel model) {
        if (findOption.getUuid() == null || findOption.getUuid().isBlank()) {
            findOption.setUuid(UUID.randomUUID().toString());
        }
        findOption.setName("");//TODO
        findOption.setLastUsed(LocalDateTime.now());
        findOption.setCntUsed(findOption.getCntUsed() + 1);
        findOption.setFileFilter(model.getFileFilter());
    }

}
