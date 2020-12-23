package com.fuzy.find.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;

import org.jetbrains.annotations.NotNull;

import com.fuzy.find.model.FindUtils;
import com.fuzy.find.persistence.ConfigurationManager;
import com.intellij.find.findInProject.FindInProjectManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Separator;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.popup.PopupFactoryImpl;
import com.intellij.ui.popup.list.ListPopupImpl;
import com.intellij.util.ui.UIUtil;

/**
 * Shows dialog for selection of predefined and persisted configurations.
 */
public class FindInPathChooseConfigAction extends AnAction implements DumbAware {
    private static final Logger LOG = Logger.getInstance(FindInPathChooseConfigAction.class);

    public static final String TITLE = "Choose Configuration";

    public void actionPerformed(@NotNull AnActionEvent e) {

        DataContext dataContext = e.getDataContext();
        Project project = e.getData(CommonDataKeys.PROJECT);
        if (project == null) {
            LOG.warn("Project is null.");
            return;
        }

        FindInProjectManager findInProjectManager = FindInProjectManager.getInstance(project);
        if (!findInProjectManager.isEnabled()) {
            LOG.warn("FindInProjectManager is not ready.");
            return;
        }

        Set<Character> usedMnemonics = new HashSet<>();
        List<FindInPathProfileAction> predefinedActions =
                new FindUtils().createPredefinedActions(project, usedMnemonics);
        List<FindInPathProfileAction> userActions =
                new FindUtils().createUserActions(project, usedMnemonics);

        showPopup(dataContext, predefinedActions, userActions, project);
    }

    private void showPopup(DataContext context, List<FindInPathProfileAction> predefinedActions,
            List<FindInPathProfileAction> userActions, Project project) {

        List<AnAction> applicable = new ArrayList<>(predefinedActions);
        applicable.add(Separator.getInstance());
        applicable.addAll(userActions);

        DefaultActionGroup group = new DefaultActionGroup(applicable.toArray(AnAction.EMPTY_ARRAY));
        ListPopup popup = JBPopupFactory.getInstance().createActionGroupPopup(TITLE, group, context,
                JBPopupFactory.ActionSelectionAid.MNEMONICS, true);
        registerDeleteAction(project, popup);
        popup.showCenteredInCurrentWindow(project);
    }

    private void registerDeleteAction(Project project, ListPopup popup) {
        if (!(popup instanceof ListPopupImpl)) {
            return;
        }

        ListPopupImpl listPopup = (ListPopupImpl) popup;

        AbstractAction deleteOptions = createDeleteAction(project, listPopup);
        listPopup.registerAction("delete", KeyEvent.VK_DELETE, 0, deleteOptions);
    }

    @NotNull
    private AbstractAction createDeleteAction(Project project, ListPopupImpl listPopup) {
        return new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Object selectedValue = listPopup.getList().getSelectedValue();

                FindInPathProfileAction findInPathProfileAction = null;
                if (selectedValue instanceof PopupFactoryImpl.ActionItem) {
                    AnAction action = ((PopupFactoryImpl.ActionItem) selectedValue).getAction();
                    if (action instanceof FindInPathProfileAction) {
                        findInPathProfileAction = (FindInPathProfileAction) action;
                    }
                }

                //hide
                listPopup.dispose();

                if (findInPathProfileAction == null) {
                    LOG.error("Selected option is not instance of FindInPathProfileAction but " + selectedValue);
                    return;
                }

                String uuid = findInPathProfileAction.getUuid();
                if (Arrays.asList(new String[]{FindUtils.LAST_USED, FindUtils.EMPTY}).contains(uuid)) {
                    return;
                }

                askForSaveOptions(findInPathProfileAction, uuid, project);
            }

            private void askForSaveOptions(FindInPathProfileAction findInPathProfileAction, String uuid, Project project) {
                String name = findInPathProfileAction.getName();
                String question = MessageFormat.format("Delete search configuration named  {0}?", name);

                int i = Messages.showYesNoCancelDialog(question, "Delete Configuration", null);
                if (Messages.YES == i) {
                    ConfigurationManager configurationManager = ConfigurationManager.getInstance(project);
                    configurationManager.delete(uuid);
                }
            }

        };
    }

    public static String emphasiseMnemonic(String caption, Set<? super Character> usedMnemonics) {
        if (StringUtil.isEmpty(caption)) {
            return "";
        }

        for (int i = 0; i < caption.length(); i++) {
            char c = caption.charAt(i);

            if (Character.isWhitespace(c)) {
                continue;
            }

            if (usedMnemonics.add(Character.toUpperCase(c))) {
                return UIUtil.MNEMONIC + String.valueOf(c) + ". " + caption;
            }
        }

        return caption;
    }

}
