package com.fuzy.find.model;

import com.fuzy.find.TestUtils;
import com.intellij.find.FindManager;
import com.intellij.find.FindModel;
import com.intellij.find.findInProject.FindInProjectManager;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import static com.fuzy.find.TestUtils.*;

/**
 * Tests limitations: no UI test, no persistent context
 */
public class FindModelTest extends BasePlatformTestCase {
    // If you need to set up a multi-module project for your tests, you must write a heavy test.

    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        // When writing a light test, you can specify the project’s requirements that you need to have in your test,
        // such as the module type, the configured SDK, facets, libraries, etc.
        return super.getProjectDescriptor();
    }

    public void testResetToInitialState() {
        FindManager findManager = FindManager.getInstance(myFixture.getProject());
        FindModel findInProjectModel = findManager.getFindInProjectModel();
        FindUtils.resetToDefaults(findInProjectModel); // reset
        TestUtils.assertInitialModelState(findInProjectModel);
    }

    public void testFindByTextModel() {
        FindManager findManager = FindManager.getInstance(myFixture.getProject());
        FindModel model = findManager.getFindInProjectModel();
        FindUtils.resetToDefaults(model);

        //FindUIHelper
        FindInProjectManager findInProjectManager = FindInProjectManager.getInstance(myFixture.getProject());

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
        assertTrue(usedModel.isMultipleFiles());
    }

    public void testFindByTextCaseSensitiveModel() {
        FindManager findManager = FindManager.getInstance(myFixture.getProject());
        FindModel findInProjectModel = findManager.getFindInProjectModel();
        FindUtils.resetToDefaults(findInProjectModel);

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
        assertTrue(model.isCaseSensitive());
        assertFalse(model.isRegularExpressions());
        assertFalse(model.isPreserveCase());
        assertFalse(model.isWholeWordsOnly());
        assertFalse(model.isMultiline());
        assertTestScopeDefaults(usedModel);
        assertNull(usedModel.getCustomScopeName());
        assertFalse(usedModel.isMultipleFiles());
    }

    public void testFindWholeWordsOnlyModel() {
        FindManager findManager = FindManager.getInstance(myFixture.getProject());
        FindModel model = findManager.getFindInProjectModel();
        FindUtils.resetToDefaults(model);

        model.setStringToFind("whole");
        model.setWholeWordsOnly(true);

        assertEquals("whole", model.getStringToFind());

        assertNavigationDefaults(model);
        assertFalse(model.isCaseSensitive());
        assertFalse(model.isRegularExpressions());
        assertFalse(model.isPreserveCase());
        assertTrue(model.isWholeWordsOnly());
        assertTrue(model.isMultiline());
        assertScopeDefaults(model);
        assertTestScopeDefaults(model);
        assertReplaceDefaults(model);
    }

    public void testFindByRegexModel() {

    }

    public void testFileMaskModel() {

    }

    public void testInStringLiteralsModel() {
        FindManager findManager = FindManager.getInstance(myFixture.getProject());
        FindModel model = findManager.getFindInProjectModel();
        FindUtils.resetToDefaults(model);

        model.setStringToFind("literals only");
        model.setSearchContext(FindModel.SearchContext.IN_STRING_LITERALS);

        assertEquals("literals only", model.getStringToFind());

        assertNavigationDefaults(model);
        assertFalse(model.isCaseSensitive());
        assertFalse(model.isRegularExpressions());
        assertFalse(model.isPreserveCase());
        assertFalse(model.isWholeWordsOnly());
        assertTrue(model.isMultiline());
        assertEquals(FindModel.SearchContext.IN_STRING_LITERALS, model.getSearchContext());
        assertNull(model.getCustomScope());
        assertTrue(model.isGlobal());
        assertFalse(model.isCustomScope());
        assertTrue(model.isProjectScope());
        assertTrue(model.isWithSubdirectories());
        assertFalse(model.isSearchInProjectFiles());
        assertFalse(model.isExceptComments());
        assertFalse(model.isInCommentsOnly());
        assertFalse(model.isExceptCommentsAndStringLiterals());
        assertTrue(model.isInStringLiteralsOnly());
        assertFalse(model.isExceptStringLiterals());

        assertReplaceDefaults(model);
    }

    public void testInCommentsModel() {
        FindManager findManager = FindManager.getInstance(myFixture.getProject());
        FindModel model = findManager.getFindInProjectModel();
        FindUtils.resetToDefaults(model);

        model.setStringToFind("comments only");
        model.setSearchContext(FindModel.SearchContext.IN_COMMENTS);

        assertEquals("comments only", model.getStringToFind());

        assertNavigationDefaults(model);
        assertFalse(model.isCaseSensitive());
        assertFalse(model.isRegularExpressions());
        assertFalse(model.isPreserveCase());
        assertFalse(model.isWholeWordsOnly());
        assertTrue(model.isMultiline());
        assertEquals(FindModel.SearchContext.IN_COMMENTS, model.getSearchContext());
        assertNull(model.getCustomScope());
        assertTrue(model.isGlobal());
        assertFalse(model.isCustomScope());
        assertTrue(model.isProjectScope());
        assertTrue(model.isWithSubdirectories());
        assertFalse(model.isSearchInProjectFiles());
        assertFalse(model.isExceptComments());
        assertTrue(model.isInCommentsOnly());
        assertFalse(model.isExceptCommentsAndStringLiterals());
        assertFalse(model.isInStringLiteralsOnly());
        assertFalse(model.isExceptStringLiterals());

        assertReplaceDefaults(model);

    }

    // ANY, EXCEPT_STRING_LITERALS, EXCEPT_COMMENTS, EXCEPT_COMMENTS_AND_STRING_LITERALS

    public void testFindInFileModel() {
        //getFindInFileModel
    }

    //assertNavigationDefaults(model);
    //assertTextDefaults(model);
    //assertScopeDefaults(model);
    //assertTestScopeDefaults(model);
    //assertReplaceDefaults(model);


    // https://jetbrains.org/intellij/sdk/docs/tutorials/custom_language_support_tutorial.html
    // https://jetbrains.org/intellij/sdk/docs/tutorials/writing_tests_for_plugins.html


}
