package com.fuzy.find;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.fuzy.find.persistence.FindOption;
import com.fuzy.find.persistence.FindOptions;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;

@State(name = "FindInPathConfiguration", storages = @Storage("FindInPathConfiguration.xml"))
public class ConfigurationManager implements PersistentStateComponent<FindOptions> {

    FindOptions findOptions;

    @Override
    public FindOptions getState() {
        return findOptions;
    }

    public void setState(FindOptions findOption) {
        this.findOptions = findOption;
    }

    @Override
    public void loadState(@NotNull FindOptions findOption) {
        this.findOptions = findOption;
    }

    public static ConfigurationManager getInstance(Project project) {
        return ServiceManager.getService(project, ConfigurationManager.class);
    }

    public FindOption findOrInitNew(String uuid) {
        Optional<FindOption> first = findOptions.getOptions().stream().filter(f -> uuid.equals(f.getUuid())).findFirst();
        return first.orElseGet(FindOption::new);
    }

}
