package com.fuzy.find;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.intellij.find.FindManager;
import com.intellij.find.FindModel;
import com.intellij.find.findInProject.FindInProjectManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;

public class FindInPathChooseConfigurationAction extends AnAction implements DumbAware {

    public static final String TITLE = "Choose Configuration";

    public void actionPerformed(@NotNull AnActionEvent e) {
        DataContext dataContext = e.getDataContext();
        Project project = e.getData(CommonDataKeys.PROJECT);
        Editor editor = e.getData(CommonDataKeys.EDITOR);

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

        List<FindByModelAction> findActions = FindConstants.createModels();
        List<AnAction> actions = new ArrayList<>();

        actions.add(new FindInPathProfileAction(project, defaultFindModel, "default"));

        for (FindByModelAction findAction : findActions) {
            FindModel model = findAction.getModel();
            model.setStringToFind(stringToFind);
            FindInPathProfileAction action = new FindInPathProfileAction(project, model, findAction.getName());
            actions.add(action);
        }

        showPopup(dataContext, actions);
    }

    private void showPopup(DataContext context, List<AnAction> applicable) {
        JBPopupFactory.ActionSelectionAid mnemonics = JBPopupFactory.ActionSelectionAid.NUMBERING;
        DefaultActionGroup group = new DefaultActionGroup(applicable.toArray(AnAction.EMPTY_ARRAY));
        JBPopupFactory.getInstance().createActionGroupPopup(TITLE, group, context, mnemonics, true).showInFocusCenter();
    }


}
