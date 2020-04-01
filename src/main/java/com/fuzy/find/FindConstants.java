package com.fuzy.find;

import java.util.ArrayList;
import java.util.List;

import com.intellij.find.FindModel;

//TODO replace by XML
public class FindConstants {

    static List<FindByModelAction> createModels() {
        List<FindByModelAction> models = new ArrayList<>();

        FindModel model1 = new FindModel();
        model1.setFileFilter("pom.xml");
        model1.setProjectScope(true);
        FindByModelAction action1 = new FindByModelAction("Maven", model1);
        models.add(action1);

        FindModel model2 = new FindModel();
        model2.setFileFilter("*.java");
        model2.setProjectScope(true);
        FindByModelAction action2 = new FindByModelAction("Java", model2);
        models.add(action2);

        FindModel model3 = new FindModel();
        model3.setFileFilter("*.xml");
        model3.setProjectScope(true);
        FindByModelAction action3 = new FindByModelAction("Xml", model3);
        models.add(action3);

        //TODO validacni nastaveni
        //TODO lokalizace
        //TODO create
        //TODO upgrade n-5?

        return models;
    }
}
