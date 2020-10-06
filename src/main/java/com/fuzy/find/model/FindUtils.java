package com.fuzy.find.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fuzy.find.persistence.ConfigurationManager;
import com.fuzy.find.persistence.FindOption;
import com.fuzy.find.persistence.FindOptions;
import com.intellij.find.FindModel;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;

public class FindUtils {

    public List<FindByModelAction> createModels(Project project) {
        List<FindByModelAction> actions = new ArrayList<>();

        ConfigurationManager manager = ServiceManager.getService(project, ConfigurationManager.class);
        FindOptions state = manager.getState();
        if (state != null) {

            List<FindOption> options = state.getOptions();
            actions.addAll(options.stream().map(this::createAction)
                .collect(Collectors.toList()));
        }

        return actions;
    }

    private FindByModelAction createAction(FindOption findOption) {
        FindModel model = new FindModel();
        move(findOption, model);
        return new FindByModelAction(findOption.getUuid(), findOption.getName(), model);
    }

    private void move(FindOption findOption, FindModel model) {
        model.setFileFilter(findOption.getFileFilter());
        model.setCaseSensitive(findOption.isCaseSensitive());
        model.setRegularExpressions(findOption.isRegularExpressions());
        model.setWholeWordsOnly(findOption.isWholeWordsOnly());
//        model.setSearchContext(model.getSearchContext().name());
        model.setModuleName(findOption.getModuleName());
        model.setDirectoryName(findOption.getDirectoryName());
        model.setCustomScope(findOption.isCustomScope());
        model.setProjectScope(findOption.isProjectScope());
        model.setFileFilter(findOption.getFileFilter());
    }
}
