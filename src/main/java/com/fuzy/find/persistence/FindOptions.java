package com.fuzy.find.persistence;

import java.util.ArrayList;
import java.util.List;

public class FindOptions {

    public List<FindOption> options;

    public FindOptions() {
        this.options = new ArrayList<FindOption>();
    }

    public List<FindOption> getOptions() {
        return options;
    }

    public void setOptions(List<FindOption> options) {
        this.options = options;
    }
}
