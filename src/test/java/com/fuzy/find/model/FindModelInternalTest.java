package com.fuzy.find.model;

import com.fuzy.find.persistence.ConfigurationManager;
import com.fuzy.find.persistence.FindOptions;
import com.intellij.find.FindManager;
import com.intellij.find.FindModel;
import com.intellij.find.findInProject.FindInProjectManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import static com.fuzy.find.TestUtils.assertInitialModelState;

/**
 * Verify the behaviour of internal find model in initial state.
 * This class is supposed to have ONLY one test .
 */
public class FindModelInternalTest extends BasePlatformTestCase {

    public void testResetToInitialState() {
        FindManager findManager = FindManager.getInstance(myFixture.getProject());
        FindModel findInProjectModel = findManager.getFindInProjectModel();
        assertInitialModelState(findInProjectModel);
    }

    public void testComponents() {
        assertNotNull(myFixture);

        ConfigurationManager cm = ConfigurationManager.getInstance(myFixture.getProject());
        assertNotNull(cm);
        FindOptions state = cm.getState();
        assertNull(state);
        Editor editor = myFixture.getEditor();
        assertNull(editor);
        FindManager fm = FindManager.getInstance(myFixture.getProject());
        assertNotNull(fm);
        FindInProjectManager findInProjectManager = FindInProjectManager.getInstance(myFixture.getProject());
        assertNotNull(findInProjectManager);
    }

}
