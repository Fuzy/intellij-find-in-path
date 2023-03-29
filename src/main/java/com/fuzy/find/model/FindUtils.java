package com.fuzy.find.model;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import com.fuzy.find.action.FindInPathChooseConfigAction;
import com.fuzy.find.action.FindInPathProfileAction;
import com.fuzy.find.persistence.ConfigurationManager;
import com.fuzy.find.persistence.FindOption;
import com.fuzy.find.persistence.FindOptions;
import com.fuzy.find.util.StringUtils;
import com.intellij.find.FindModel;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.PredefinedSearchScopeProvider;
import com.intellij.psi.search.SearchScope;

public class FindUtils {

    public static final String LAST_USED = "com.fuzy.find.last.used.search";
    public static final String EMPTY = "com.fuzy.find.empty";
    public static final String LAST_USED_NAME = "Recently used";
    public static final String EMPTY_NAME = "Empty";

    public List<FindInPathProfileAction> createPredefinedActions(Project project, Set<Character> usedMnemonics) {
        List<FindInPathProfileAction> actions = new ArrayList<>();

        String emptyName = FindInPathChooseConfigAction.emphasiseMnemonic(EMPTY_NAME, usedMnemonics);
        actions.add(new FindInPathProfileAction(EMPTY, EMPTY_NAME, emptyName));

        ConfigurationManager manager = project.getService(ConfigurationManager.class);
        FindOption byUuid = manager.findByUuid(LAST_USED);
        if (byUuid == null) {
            return actions;
        }

        String lastUsedName = FindInPathChooseConfigAction.emphasiseMnemonic(LAST_USED_NAME, usedMnemonics);
        FindInPathProfileAction last = new FindInPathProfileAction(byUuid.getUuid(),
                LAST_USED_NAME, lastUsedName);
        actions.add(last);
        return actions;
    }

    public List<FindInPathProfileAction> createUserActions(Project project, Set<Character> usedMnemonics) {
        List<FindInPathProfileAction> persistent = initUserActionsOfPersistentState(project, usedMnemonics);
        sortAlphabetically(persistent);
        return new ArrayList<>(persistent);
    }

    private void sortAlphabetically(List<FindInPathProfileAction> actions) {
        actions.sort(Comparator.comparing(FindInPathProfileAction::getName,
                Comparator.nullsLast(Collator.getInstance())));
    }

    private List<FindInPathProfileAction> initUserActionsOfPersistentState(Project project, Set<Character> usedMnemonics) {
        ConfigurationManager manager = project.getService(ConfigurationManager.class);
        FindOptions state = manager.getState();

        if (state != null) {

            List<FindOption> options = state.getOptions();

            Predicate<FindOption> filterByNameUuid = (o) -> StringUtils.trimToNull(o.getName()) != null
                    && !LAST_USED.equals(o.getUuid());

            return options.stream().filter(filterByNameUuid)
                    .map(o -> {
                        String name = FindInPathChooseConfigAction.emphasiseMnemonic(o.getName(), usedMnemonics);
                        return new FindInPathProfileAction(o.getUuid(), o.getName(), name);
                    })
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    public FindModel modelForUuid(String uuid, Project project) {

        if (EMPTY.equals(uuid)) {
            return createEmpty();
        }

        ConfigurationManager manager = project.getService(ConfigurationManager.class);
        FindOption byUuid = manager.findByUuid(uuid);
        if (byUuid == null) {
            return null;
        }

        FindModel model = new FindModel();
        initModel(project, byUuid, model);
        return model;
    }

    /**
     * Before options are applied, internal find model should be reset
     * to it's default state. Then applied setting is always same.
     */
    public static void resetToDefaults(FindModel model) {
        model.setStringToFind("");
        model.setStringToReplace("");
        model.setRegularExpressions(false);
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
            List<? extends SearchScope> predefined = PredefinedSearchScopeProvider.getInstance().getPredefinedScopes(
                    project, null, true, false, false, false, false);
            Optional<? extends SearchScope> first = predefined.stream().filter(scope -> scope.getDisplayName().equals(scopeName)).findFirst();
            first.ifPresent(model::setCustomScope);
        }

        try {
            String searchContext = findOption.getSearchContext();
            if (searchContext != null) {
                model.setSearchContext(FindModel.SearchContext.valueOf(searchContext));
            }
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

}
