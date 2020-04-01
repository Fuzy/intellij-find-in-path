package com.fuzy.find;

import com.intellij.find.FindModel;

public class FindByModelAction {

    private final String name;
    private final FindModel model;

    public FindByModelAction(String name, FindModel model) {
        this.name = name;
        this.model = model;
    }

    public String getName() {
        return name;
    }

    public FindModel getModel() {
        return model;
    }
}
