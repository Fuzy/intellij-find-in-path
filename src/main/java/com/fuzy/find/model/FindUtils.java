package com.fuzy.find.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import com.fuzy.find.action.FindInPathProfileAction;
import com.fuzy.find.persistence.ConfigurationManager;
import com.fuzy.find.persistence.FindOption;
import com.fuzy.find.persistence.FindOptions;
import com.intellij.find.FindModel;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.PredefinedSearchScopeProvider;
import com.intellij.psi.search.SearchScope;

public class FindUtils {

    public static final String LAST_USED = "com.fuzy.find.last.used.search";

    public List<FindInPathProfileAction> createActions(Project project, String stringToFind) {

        List<FindInPathProfileAction> actions = new ArrayList<>();
        actions.add(new FindInPathProfileAction(project, createEmpty(), "empty", "Empty"));
        List<FindInPathProfileAction> persistent = initActionsOfPersistentState(project);
        sortAlphabetically(persistent);
        actions.addAll(persistent);
        actions.forEach(a -> a.getModel().setStringToFind(stringToFind));
        return actions;
    }

    private void sortAlphabetically(List<FindInPathProfileAction> actions) {
        actions.sort(Comparator.comparing(FindInPathProfileAction::getName,
            Comparator.nullsLast(Comparator.naturalOrder())));
    }

    private List<FindInPathProfileAction> initActionsOfPersistentState(Project project) {
        ConfigurationManager manager = ServiceManager.getService(project, ConfigurationManager.class);
        FindOptions state = manager.getState();

        if (state != null) {

            List<FindOption> options = state.getOptions();

            return options.stream()
                .map(o -> {
                    FindModel model = new FindModel();
                    initModel(project, o, model);
                    return new FindInPathProfileAction(project, model, o.getUuid(), o.getName());
                }).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    /**
     * Before options are applied, internal find model should be reset
     * to it's default state. Then applied setting is always same.
     */
    public static void resetToDefaults(FindModel model) {
        model.setStringToFind("");
        model.setStringToReplace("");
        model.setFileFilter(null);
        // com.intellij.find.impl.FindSettingsImpl.FIND_SCOPE_GLOBAL
//        model.setCustomScopeName("global");
        model.setCustomScopeName(null);
        model.setModuleName(null);
        model.setDirectoryName(null);
        model.setSearchContext(FindModel.SearchContext.ANY);
        model.setCaseSensitive(false);
        model.setPreserveCase(false);
        model.setWholeWordsOnly(false);
        model.setMultiline(true);
        model.setCustomScope(null);
        model.setCustomScope(false);
        model.setGlobal(true);
        model.setProjectScope(true);
        model.setWithSubdirectories(true);
        model.setSearchInProjectFiles(false);
        model.setFindAllEnabled(false);
        model.setFindAll(false);
        model.setForward(true);
        model.setFromCursor(false); // for search in project
        model.setMultipleFiles(true);
        model.setPromptOnReplace(true);
        model.setReplaceState(false);
        model.setReplaceAll(false);
    }

    private void initModel(Project project, FindOption findOption, FindModel model) {
        resetToDefaults(model);

        model.setCaseSensitive(findOption.isCaseSensitive());
        model.setRegularExpressions(findOption.isRegularExpressions());
        model.setWholeWordsOnly(findOption.isWholeWordsOnly());
        model.setModuleName(findOption.getModuleName());
        model.setDirectoryName(findOption.getDirectoryName());
        model.setCustomScope(findOption.isCustomScope());
        model.setProjectScope(findOption.isProjectScope());
        model.setFileFilter(findOption.getFileFilter());

        //NamedScopeManager.getInstance(project); workspace.xml
        String scopeName = findOption.getScope();
        if (scopeName != null) {
            List<SearchScope> predefined = PredefinedSearchScopeProvider.getInstance().getPredefinedScopes(
                project, null, true, false, false, false, false);
            Optional<SearchScope> first = predefined.stream().filter(scope -> scope.getDisplayName().equals(scopeName)).findFirst();
            first.ifPresent(model::setCustomScope);
        }

        try {
            model.setSearchContext(FindModel.SearchContext.valueOf(findOption.getSearchContext()));
        } catch (IllegalArgumentException e) {
            // ignore
        }
    }

    @NotNull
    private FindModel createEmpty() {
        FindModel model = new FindModel();
        model.setCaseSensitive(false);
        model.setFileFilter(null);
        model.setRegularExpressions(false);
        model.setWholeWordsOnly(false);
        model.setModuleName(null);
        model.setDirectoryName(null);
        model.setCustomScope(false);
        model.setProjectScope(true);
        model.setCustomScope(null);
        model.setSearchContext(FindModel.SearchContext.ANY);
        return model;
    }

    public static String trimToEmpty(String s) {
        if (s == null) {
            return "";
        }

        return s.trim();
    }
}
