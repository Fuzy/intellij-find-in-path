package com.fuzy.find;

import java.util.ArrayList;
import java.util.List;

import com.intellij.find.FindModel;

//TODO replace by XML
public class FindConstants {

    static List<FindByModelAction> createModels() {
        List<FindByModelAction> models = new ArrayList<>();

        models.add(createAction("Java", "*.java"));
        models.add(createAction("Xml", "*.xml"));
        models.add(createAction("Maven", "pom.xml"));
        models.add(createAction("L10n cz", "*cs.xml"));
        models.add(createAction("L10n others", "lang.*.xml,!lang.*cs.xml"));
        models.add(createAction("V8n", "val*.xml"));
        models.add(createAction("SQL create", "create*.sql"));
        models.add(createAction("SQL upgrade", "upgrade11**.sql"));

        return models;
    }

    private static FindByModelAction createAction(String name, String fileFilter) {
        FindModel model2 = new FindModel();
        model2.setFileFilter(fileFilter);
        model2.setProjectScope(true);
        return new FindByModelAction(name, model2);
    }
}
