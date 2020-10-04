package com.fuzy.find.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;

import org.jetbrains.annotations.NotNull;

import com.fuzy.find.model.FindByModelAction;
import com.fuzy.find.model.FindConstants;
import com.fuzy.find.persistence.ConfigurationManager;
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
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.ui.popup.list.ListPopupImpl;

import static com.fuzy.find.notification.Notifications.NOTIFICATION_GROUP;

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
            List<FindInPathProfileAction> actions = new ArrayList<>();

            actions.add(new FindInPathProfileAction(project, defaultFindModel, "default", "default"));

            for (FindByModelAction findAction : findActions) {
                FindModel model = findAction.getModel();
                model.setStringToFind(stringToFind);
                FindInPathProfileAction action = new FindInPathProfileAction(project, model, findAction.getUuid(), findAction.getName());
                actions.add(action);
            }

            showPopup(dataContext, actions, project);
        } catch (Throwable ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String sStackTrace = sw.toString(); // stack trace as a string

            NOTIFICATION_GROUP.createNotification(String.valueOf(sStackTrace), NotificationType.WARNING).notify(project);
        }
    }

    private void showPopup(DataContext context, List<FindInPathProfileAction> applicable, Project project) {

        JBPopupFactory.ActionSelectionAid mnemonics = JBPopupFactory.ActionSelectionAid.ALPHA_NUMBERING;
        DefaultActionGroup group = new DefaultActionGroup(applicable.toArray(AnAction.EMPTY_ARRAY));
        ListPopup popup = JBPopupFactory.getInstance().createActionGroupPopup(TITLE, group, context, mnemonics, true);
        if (popup instanceof ListPopupImpl) {
            ListPopupImpl listPopup = (ListPopupImpl) popup;

            AbstractAction deleteOptions = createDeleteAction(applicable, project, listPopup);
            listPopup.registerAction("delete", KeyEvent.VK_DELETE, 0, deleteOptions);

        }
        popup.showInFocusCenter();
    }

    @NotNull
    private AbstractAction createDeleteAction(List<FindInPathProfileAction> applicable, Project project, ListPopupImpl listPopup) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = listPopup.getList().getSelectedIndex();

                //hide
                listPopup.dispose();

                FindInPathProfileAction findInPathProfileAction = applicable.get(selectedIndex);
                if (findInPathProfileAction == null) {
                    return;
                }

                String name = findInPathProfileAction.getName();
                String question = MessageFormat.format("Delete search options named  {0}?",
                    name);

                int i = Messages.showYesNoCancelDialog(question, "Delete Options", null);
                if (Messages.YES == i) {
                    ConfigurationManager configurationManager = ConfigurationManager.getInstance(project);
                    configurationManager.delete(findInPathProfileAction.getUuid());
                }
            }

        };
    }

}
