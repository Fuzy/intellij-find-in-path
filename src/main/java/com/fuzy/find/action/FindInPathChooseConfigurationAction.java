package com.fuzy.find.action;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import model.FindByModelAction;
import model.FindConstants;
import com.intellij.find.FindManager;
import com.intellij.find.FindModel;
import com.intellij.find.findInProject.FindInProjectManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;

import static com.fuzy.find.action.FindInPathProfileAction.NOTIFICATION_GROUP;

public class FindInPathChooseConfigurationAction extends AnAction implements DumbAware {

    public static final String TITLE = "Choose Configuration";

    public void actionPerformed(@NotNull AnActionEvent e) {

        DataContext dataContext = e.getDataContext();
        Project project = e.getData(CommonDataKeys.PROJECT);
        Editor editor = e.getData(CommonDataKeys.EDITOR);

        try {
            String stringToFind = null;
            if (editor != null) {
                stringToFind = editor.getSelectionModel().getSelectedText();
            }
            if (stringToFind == null) {
                stringToFind = "";
            }

            if (project == null) {
                return;
            }

            FindInProjectManager findInProjectManager = FindInProjectManager.getInstance(project);
            if (!findInProjectManager.isEnabled()) {
                return;
            }

            final FindManager findManager = FindManager.getInstance(project);
            FindModel defaultFindModel = findManager.getFindInProjectModel().clone();

            List<FindByModelAction> findActions = new FindConstants().createModels(project);
            List<AnAction> actions = new ArrayList<>();

            actions.add(new FindInPathProfileAction(project, defaultFindModel, "default", "default"));

            for (FindByModelAction findAction : findActions) {
                FindModel model = findAction.getModel();
                model.setStringToFind(stringToFind);
                FindInPathProfileAction action = new FindInPathProfileAction(project, model, findAction.getName(), findAction.getUuid());
                actions.add(action);
            }

            showPopup(dataContext, actions);
        } catch (Throwable ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String sStackTrace = sw.toString(); // stack trace as a string

            NOTIFICATION_GROUP.createNotification(String.valueOf(sStackTrace), NotificationType.WARNING).notify(project);
        }
    }

    private void showPopup(DataContext context, List<AnAction> applicable) {
        JBPopupFactory.ActionSelectionAid mnemonics = JBPopupFactory.ActionSelectionAid.NUMBERING;
        DefaultActionGroup group = new DefaultActionGroup(applicable.toArray(AnAction.EMPTY_ARRAY));
        JBPopupFactory.getInstance().createActionGroupPopup(TITLE, group, context, mnemonics, true).showInFocusCenter();
    }


}
