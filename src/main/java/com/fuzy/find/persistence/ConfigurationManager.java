package com.fuzy.find.persistence;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;

import com.fuzy.find.model.FindUtils;
import com.intellij.find.FindModel;
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

    public void saveAsLastUsed(FindModel findModel) {
        save(findModel, "Last used", FindUtils.LAST_USED);//TODO duplicita
    }

    public void save(FindModel findModel, String name) {
        save(findModel, name, UUID.randomUUID().toString());
    }

    private void save(FindModel findModel, String name, String uuid) {
        FindOptions findOptions = getState();

        if (findOptions == null) {
            findOptions = new FindOptions();
        }
        setState(findOptions);

        FindOption findOption = new FindOption();
        findOption.setUuid(uuid);
        updateFindOptionByModel(findOption, findModel);
        updateFindOptionByName(findOption, name);
        findOptions.getOptions().add(findOption);
    }

    private void updateFindOptionByModel(FindOption findOption, FindModel model) {
        findOption.setFileFilter(model.getFileFilter());
        findOption.setCaseSensitive(model.isCaseSensitive());
        findOption.setRegularExpressions(model.isRegularExpressions());
        findOption.setWholeWordsOnly(model.isWholeWordsOnly());
        findOption.setSearchContext(model.getSearchContext().name());
        findOption.setModuleName(model.getModuleName());
        findOption.setDirectoryName(model.getDirectoryName());
        findOption.setCustomScope(model.isCustomScope());
        findOption.setProjectScope(model.isProjectScope());

        updateUsageProperties(findOption);
    }

    private void updateFindOptionByName(FindOption findOption, String name) {
        findOption.setName(name);
    }

    public void updateUsagePropertiesIfExists(FindModel findModel) {

        Optional<FindOption> findEquals = find(findModel);
        if (findEquals.isEmpty()) {
            return;
        }

        FindOption saved = findEquals.get();
        updateUsageProperties(saved);
    }

    public boolean existsPersistentOption(FindModel findModel) {
        return find(findModel).isPresent();
    }

    private Optional<FindOption> find(FindModel findModel) {

        FindOptions findOptions = getState();
        if (findOptions == null) {
            return Optional.empty();
        }

        FindOption findOption = new FindOption();
        updateFindOptionByModel(findOption, findModel);

        return findOptions.getOptions().stream().filter(findOption::equals).findFirst();
    }

    private void updateUsageProperties(FindOption findOption) {
        findOption.setLastUsed(LocalDateTime.now());
        findOption.setCntUsed(findOption.getCntUsed() + 1);
    }

    public void delete(String uuid) {
        if (uuid == null) {
            return;
        }

        FindOptions findOptions = getState();
        if (findOptions == null) {
            return;
        }

        Predicate<FindOption> isUuid = f -> uuid.equals(f.getUuid());
        findOptions.getOptions().removeIf(isUuid);
    }

}
