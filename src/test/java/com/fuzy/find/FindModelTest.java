package com.fuzy.find;

import com.fuzy.find.persistence.ConfigurationManager;
import com.fuzy.find.persistence.FindOptions;
import com.intellij.find.FindManager;
import com.intellij.find.FindModel;
import com.intellij.find.findInProject.FindInProjectManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class FindModelTest extends BasePlatformTestCase {
    // If you need to set up a multi-module project for your tests, you must write a heavy test.

    // Tests limitations
    // no UI test
    // no persistent context

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        assertTestComponents();
    }

    private void assertTestComponents() {
        assertNotNull(myFixture);
        assertEquals("src/test/testData", myFixture.getTestDataPath());

        ConfigurationManager configurationManager = ConfigurationManager.getInstance(myFixture.getProject());
        assertNotNull(configurationManager);
        FindOptions state = configurationManager.getState();
        assertNull(state);
        Editor editor = myFixture.getEditor();
        assertNull(editor);
        FindManager findManager = FindManager.getInstance(myFixture.getProject());
        assertNotNull(findManager);
        FindInProjectManager findInProjectManager = FindInProjectManager.getInstance(myFixture.getProject());
        assertNotNull(findInProjectManager);
    }

    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        // When writing a light test, you can specify the project’s requirements that you need to have in your test,
        // such as the module type, the configured SDK, facets, libraries, etc.
        return super.getProjectDescriptor();
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    private void assertInitialModelState(FindModel model) {
        assertNotNull(model);
        assertEquals("", model.getStringToFind());//
        assertNull(model.getFileFilter());
        assertEquals("global", model.getCustomScopeName());//
        assertNull(model.getModuleName());
        assertNull(model.getDirectoryName());
        assertEquals(FindModel.SearchContext.ANY, model.getSearchContext());
        assertTextDefaults(model);
        assertScopeDefaults(model);
        assertNavigationDefaults(model);
        assertTrue(model.isMultipleFiles());//
        assertReplaceDefaults(model);
    }

    private void assertNavigationDefaults(FindModel model) {
        // navigation
        assertFalse(model.isFindAllEnabled());
        assertFalse(model.isFindAll());
        assertTrue(model.isForward());
        assertFalse(model.isFromCursor());
    }

    private void assertTextDefaults(FindModel model) {
        // text
        assertFalse(model.isCaseSensitive());
        assertFalse(model.isRegularExpressions());
        assertFalse(model.isPreserveCase());
        assertFalse(model.isWholeWordsOnly());
        assertTrue(model.isMultiline());
    }

    private void assertScopeDefaults(FindModel model) {
        // scope
        assertNull(model.getCustomScope());
        assertTrue(model.isGlobal());
        assertFalse(model.isCustomScope());
        assertTrue(model.isProjectScope());
        assertTrue(model.isWithSubdirectories());
        assertFalse(model.isSearchInProjectFiles());
        assertTestScopeDefaults(model);
    }

    private void assertTestScopeDefaults(FindModel model) {
        // text scope
        assertFalse(model.isExceptComments());
        assertFalse(model.isInCommentsOnly());
        assertFalse(model.isExceptCommentsAndStringLiterals());
        assertFalse(model.isInStringLiteralsOnly());
        assertFalse(model.isExceptStringLiterals());
    }

    private void assertReplaceDefaults(FindModel model) {
        // replace
        assertEquals("", model.getStringToReplace());
        assertTrue(model.isPromptOnReplace());
        assertFalse(model.isReplaceState());
        assertFalse(model.isReplaceAll());
    }

    public void testFindByTextModel() {
        FindManager findManager = FindManager.getInstance(myFixture.getProject());
        FindModel findInProjectModel = findManager.getFindInProjectModel();
        assertInitialModelState(findInProjectModel);

        //FindUIHelper
        FindInProjectManager findInProjectManager = FindInProjectManager.getInstance(myFixture.getProject());

        FindModel model = new FindModel();
        model.setStringToFind("xxx");
        findInProjectManager.findInPath(model);

        FindModel usedModel = findManager.getFindInProjectModel();
        assertEquals("xxx", usedModel.getStringToFind());

        assertNavigationDefaults(usedModel);
        assertReplaceDefaults(usedModel);
        assertScopeDefaults(usedModel);
        assertTextDefaults(usedModel);
        assertTestScopeDefaults(usedModel);
        assertNull(usedModel.getCustomScopeName());
        assertFalse(usedModel.isMultipleFiles());//
    }

    public void testFindByTextCaseSensitiveModel() {
        FindManager findManager = FindManager.getInstance(myFixture.getProject());
        FindModel findInProjectModel = findManager.getFindInProjectModel();
        findInProjectModel.setStringToFind("");
        assertInitialModelState(findInProjectModel);

        //FindUIHelper
        FindInProjectManager findInProjectManager = FindInProjectManager.getInstance(myFixture.getProject());

        FindModel model = new FindModel();
        model.setStringToFind("xxx");
        model.setCaseSensitive(true);
        findInProjectManager.findInPath(model);

        FindModel usedModel = findManager.getFindInProjectModel();
        assertEquals("xxx", usedModel.getStringToFind());

        assertNavigationDefaults(usedModel);
        assertReplaceDefaults(usedModel);
        assertScopeDefaults(usedModel);
        assertTextDefaults(usedModel);
        assertTestScopeDefaults(usedModel);
        assertNull(usedModel.getCustomScopeName());
        assertFalse(usedModel.isMultipleFiles());//
    }

    public void testFindInFileModel() {

    }


}
