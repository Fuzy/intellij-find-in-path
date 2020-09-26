package com.fuzy.find.model;

import com.intellij.find.FindModel;

public class FindByModelAction {

    private final String name;
    private final String uuid;
    private final FindModel model;

    public FindByModelAction(String uuid, String name, FindModel model) {
        this.uuid = uuid;
        this.name = name;
        this.model = model;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public FindModel getModel() {
        return model;
    }
}
