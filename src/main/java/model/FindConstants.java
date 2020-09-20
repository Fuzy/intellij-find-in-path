package model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fuzy.find.persistence.ConfigurationManager;
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
                createAction(f.getName(), f.getUuid(), f.getFileFilter()))
                .collect(Collectors.toList()));
        }

        return actions;
    }

    private FindByModelAction createAction(String name, String uuid, String fileFilter) {
        FindModel model = new FindModel();
        model.setFileFilter(fileFilter);
        model.setProjectScope(true);
        return new FindByModelAction(uuid, name, model);
    }
}
