package com.fuzy.find;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fuzy.find.persistence.FindOption;
import com.fuzy.find.persistence.FindOptions;
import com.intellij.find.FindModel;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;

public class FindConstants {

    public List<FindByModelAction> createModels(Project project) {
        List<FindByModelAction> actions = new ArrayList<>();

        ConfigurationManager manager = ServiceManager.getService(project, ConfigurationManager.class);
        FindOptions state = manager.getState();
        if (state != null) {

            List<FindOption> options = state.getOptions();
            actions.addAll(options.stream().map(f ->
                createAction(f.getName(), f.getFileFilter()))
                .collect(Collectors.toList()));
        }

        return actions;
    }

    static List<FindByModelAction> createModelsDeprecated() {
        List<FindByModelAction> models = new ArrayList<>();

        models.add(createAction("Java", "*.java"));
        models.add(createAction("Xml", "*.xml"));
        models.add(createAction("Maven", "pom.xml"));
        models.add(createAction("L10n cz", "*cs.xml"));
        models.add(createAction("L10n others", "lang.*.xml,!lang.*cs.xml"));
        models.add(createAction("V8n", "val*.xml"));
        models.add(createAction("SQL create", "create*.sql"));
        models.add(createAction("SQL upgrade", "upgrade11**.sql"));
        models.add(createAction("SQL other", "*.sql,!create*.sql,!upgrade*.sql"));

        return models;
    }

    private static FindByModelAction createAction(String name, String fileFilter) {
        FindModel model = new FindModel();
        model.setFileFilter(fileFilter);
        model.setProjectScope(true);
        return new FindByModelAction(UUID.randomUUID().toString(), name, model);
    }
}
