package com.fuzy.find.model;

import java.util.List;

import com.fuzy.find.TestUtils;
import com.intellij.find.FindInProjectSettings;
import com.intellij.find.FindManager;
import com.intellij.find.FindModel;
import com.intellij.find.FindSettings;
import com.intellij.find.findInProject.FindInProjectManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.SearchScope;
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
        FindManager findManager = FindManager.getInstance(myFixture.getProject());
        FindModel model = findManager.getFindInProjectModel();
        FindUtils.resetToDefaults(model);

        model.setStringToFind("\\d+");
        model.setRegularExpressions(true);

        assertEquals("\\d+", model.getStringToFind());

        assertNavigationDefaults(model);
        assertFalse(model.isCaseSensitive());
        assertTrue(model.isRegularExpressions());
        assertFalse(model.isPreserveCase());
        assertFalse(model.isWholeWordsOnly());
        assertTrue(model.isMultiline());
        assertEquals(FindModel.SearchContext.ANY, model.getSearchContext());
        assertNull(model.getCustomScope());
        assertTrue(model.isGlobal());
        assertFalse(model.isCustomScope());
        assertTrue(model.isProjectScope());
        assertTrue(model.isWithSubdirectories());
        assertFalse(model.isSearchInProjectFiles());
        assertFalse(model.isExceptComments());
        assertFalse(model.isInCommentsOnly());
        assertFalse(model.isExceptCommentsAndStringLiterals());
        assertFalse(model.isInStringLiteralsOnly());
        assertFalse(model.isExceptStringLiterals());

        assertReplaceDefaults(model);
    }

    public void testFileMaskModel() {
        FindManager findManager = FindManager.getInstance(myFixture.getProject());
        FindModel model = findManager.getFindInProjectModel();
        FindUtils.resetToDefaults(model);

        model.setStringToFind("any");
        model.setFileFilter("*.java");

        assertEquals("any", model.getStringToFind());
        assertEquals("*.java", model.getFileFilter());

        assertNavigationDefaults(model);
        assertFalse(model.isCaseSensitive());
        assertFalse(model.isRegularExpressions());
        assertFalse(model.isPreserveCase());
        assertFalse(model.isWholeWordsOnly());
        assertTrue(model.isMultiline());
        assertEquals(FindModel.SearchContext.ANY, model.getSearchContext());
        assertNull(model.getCustomScope());
        assertTrue(model.isGlobal());
        assertFalse(model.isCustomScope());
        assertTrue(model.isProjectScope());
        assertTrue(model.isWithSubdirectories());
        assertFalse(model.isSearchInProjectFiles());
        assertFalse(model.isExceptComments());
        assertFalse(model.isInCommentsOnly());
        assertFalse(model.isExceptCommentsAndStringLiterals());
        assertFalse(model.isInStringLiteralsOnly());
        assertFalse(model.isExceptStringLiterals());

        assertReplaceDefaults(model);
    }

    public void testProjectFilesModel() {
        FindManager findManager = FindManager.getInstance(myFixture.getProject());
        FindModel model = findManager.getFindInProjectModel();
        FindUtils.resetToDefaults(model);

        model.setStringToFind("any");
        model.setFileFilter("*.iml");

        assertEquals("any", model.getStringToFind());
        assertEquals("*.iml", model.getFileFilter());

        assertNavigationDefaults(model);
        assertFalse(model.isCaseSensitive());
        assertFalse(model.isRegularExpressions());
        assertFalse(model.isPreserveCase());
        assertFalse(model.isWholeWordsOnly());
        assertTrue(model.isMultiline());
        assertEquals(FindModel.SearchContext.ANY, model.getSearchContext());
        assertNull(model.getCustomScope());
        assertTrue(model.isGlobal());
        assertFalse(model.isCustomScope());
        assertTrue(model.isProjectScope());
        assertTrue(model.isWithSubdirectories());
        assertTrue(model.isSearchInProjectFiles());
        assertFalse(model.isExceptComments());
        assertFalse(model.isInCommentsOnly());
        assertFalse(model.isExceptCommentsAndStringLiterals());
        assertFalse(model.isInStringLiteralsOnly());
        assertFalse(model.isExceptStringLiterals());

        assertReplaceDefaults(model);
    }

    public void testAnyContextModel() {
        FindManager findManager = FindManager.getInstance(myFixture.getProject());
        FindModel model = findManager.getFindInProjectModel();
        FindUtils.resetToDefaults(model);

        model.setStringToFind("any");
        model.setSearchContext(FindModel.SearchContext.ANY);

        assertEquals("any", model.getStringToFind());

        assertNavigationDefaults(model);
        assertFalse(model.isCaseSensitive());
        assertFalse(model.isRegularExpressions());
        assertFalse(model.isPreserveCase());
        assertFalse(model.isWholeWordsOnly());
        assertTrue(model.isMultiline());
        assertEquals(FindModel.SearchContext.ANY, model.getSearchContext());
        assertNull(model.getCustomScope());
        assertTrue(model.isGlobal());
        assertFalse(model.isCustomScope());
        assertTrue(model.isProjectScope());
        assertTrue(model.isWithSubdirectories());
        assertFalse(model.isSearchInProjectFiles());
        assertFalse(model.isExceptComments());
        assertFalse(model.isInCommentsOnly());
        assertFalse(model.isExceptCommentsAndStringLiterals());
        assertFalse(model.isInStringLiteralsOnly());
        assertFalse(model.isExceptStringLiterals());

        assertReplaceDefaults(model);
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

    public void testExceptStringLiteralsModel() {
        FindManager findManager = FindManager.getInstance(myFixture.getProject());
        FindModel model = findManager.getFindInProjectModel();
        FindUtils.resetToDefaults(model);

        model.setStringToFind("except string literals");
        model.setSearchContext(FindModel.SearchContext.EXCEPT_STRING_LITERALS);

        assertEquals("except string literals", model.getStringToFind());

        assertNavigationDefaults(model);
        assertFalse(model.isCaseSensitive());
        assertFalse(model.isRegularExpressions());
        assertFalse(model.isPreserveCase());
        assertFalse(model.isWholeWordsOnly());
        assertTrue(model.isMultiline());
        assertEquals(FindModel.SearchContext.EXCEPT_STRING_LITERALS, model.getSearchContext());
        assertNull(model.getCustomScope());
        assertTrue(model.isGlobal());
        assertFalse(model.isCustomScope());
        assertTrue(model.isProjectScope());
        assertTrue(model.isWithSubdirectories());
        assertFalse(model.isSearchInProjectFiles());
        assertFalse(model.isExceptComments());
        assertFalse(model.isInCommentsOnly());
        assertFalse(model.isExceptCommentsAndStringLiterals());
        assertFalse(model.isInStringLiteralsOnly());
        assertTrue(model.isExceptStringLiterals());

        assertReplaceDefaults(model);
    }

    public void testExceptCommentsModel() {
        FindManager findManager = FindManager.getInstance(myFixture.getProject());
        FindModel model = findManager.getFindInProjectModel();
        FindUtils.resetToDefaults(model);

        model.setStringToFind("except comments");
        model.setSearchContext(FindModel.SearchContext.EXCEPT_COMMENTS);

        assertEquals("except comments", model.getStringToFind());

        assertNavigationDefaults(model);
        assertFalse(model.isCaseSensitive());
        assertFalse(model.isRegularExpressions());
        assertFalse(model.isPreserveCase());
        assertFalse(model.isWholeWordsOnly());
        assertTrue(model.isMultiline());
        assertEquals(FindModel.SearchContext.EXCEPT_COMMENTS, model.getSearchContext());
        assertNull(model.getCustomScope());
        assertTrue(model.isGlobal());
        assertFalse(model.isCustomScope());
        assertTrue(model.isProjectScope());
        assertTrue(model.isWithSubdirectories());
        assertFalse(model.isSearchInProjectFiles());
        assertTrue(model.isExceptComments());
        assertFalse(model.isInCommentsOnly());
        assertFalse(model.isExceptCommentsAndStringLiterals());
        assertFalse(model.isInStringLiteralsOnly());
        assertFalse(model.isExceptStringLiterals());

        assertReplaceDefaults(model);
    }

    public void testExceptCommentsStringLiteralsModel() {
        FindManager findManager = FindManager.getInstance(myFixture.getProject());
        FindModel model = findManager.getFindInProjectModel();
        FindUtils.resetToDefaults(model);

        model.setStringToFind("except comments/string literals");
        model.setSearchContext(FindModel.SearchContext.EXCEPT_COMMENTS_AND_STRING_LITERALS);

        assertEquals("except comments/string literals", model.getStringToFind());

        assertNavigationDefaults(model);
        assertFalse(model.isCaseSensitive());
        assertFalse(model.isRegularExpressions());
        assertFalse(model.isPreserveCase());
        assertFalse(model.isWholeWordsOnly());
        assertTrue(model.isMultiline());
        assertEquals(FindModel.SearchContext.EXCEPT_COMMENTS_AND_STRING_LITERALS, model.getSearchContext());
        assertNull(model.getCustomScope());
        assertTrue(model.isGlobal());
        assertFalse(model.isCustomScope());
        assertTrue(model.isProjectScope());
        assertTrue(model.isWithSubdirectories());
        assertFalse(model.isSearchInProjectFiles());
        assertFalse(model.isExceptComments());
        assertFalse(model.isInCommentsOnly());
        assertTrue(model.isExceptCommentsAndStringLiterals());
        assertFalse(model.isInStringLiteralsOnly());
        assertFalse(model.isExceptStringLiterals());

        assertReplaceDefaults(model);
    }

    public void testFindInFileModel() {
        FindManager findManager = FindManager.getInstance(myFixture.getProject());
        FindModel model = findManager.getFindInFileModel();
        FindUtils.resetToDefaults(model);

        model.setStringToFind("any");
        assertEquals("any", model.getStringToFind());

        assertNavigationDefaults(model);
        assertFalse(model.isCaseSensitive());
        assertFalse(model.isRegularExpressions());
        assertFalse(model.isPreserveCase());
        assertFalse(model.isWholeWordsOnly());
        assertTrue(model.isMultiline());
        assertEquals(FindModel.SearchContext.ANY, model.getSearchContext());
        assertNull(model.getCustomScope());
        assertTrue(model.isGlobal());
        assertFalse(model.isCustomScope());
        assertTrue(model.isProjectScope());
        assertTrue(model.isWithSubdirectories());
        assertFalse(model.isSearchInProjectFiles());
        assertFalse(model.isExceptComments());
        assertFalse(model.isInCommentsOnly());
        assertFalse(model.isExceptCommentsAndStringLiterals());
        assertFalse(model.isInStringLiteralsOnly());
        assertFalse(model.isExceptStringLiterals());

        assertReplaceDefaults(model);
    }

    public void testModuleScopeModel() {
        Project project = myFixture.getProject();
        FindManager findManager = FindManager.getInstance(project);
        FindModel model = findManager.getFindInFileModel();
        FindUtils.resetToDefaults(model);

        model.setStringToFind("any");

        ProjectFileIndex projectFileIndex = ProjectFileIndex.getInstance(project);
        assertNotNull(projectFileIndex);

        VirtualFile[] vFiles = ProjectRootManager.getInstance(project).getContentRoots();
        assertFalse(vFiles.length == 0);

        Module moduleForFile = projectFileIndex.getModuleForFile(vFiles[0]);
        assertNotNull(moduleForFile);

        model.setCustomScope(true);
        model.setCustomScope(moduleForFile.getModuleScope());
        assertEquals("any", model.getStringToFind());

        assertNavigationDefaults(model);
        assertFalse(model.isCaseSensitive());
        assertFalse(model.isRegularExpressions());
        assertFalse(model.isPreserveCase());
        assertFalse(model.isWholeWordsOnly());
        assertTrue(model.isMultiline());
        assertEquals(FindModel.SearchContext.ANY, model.getSearchContext());
        SearchScope customScope = model.getCustomScope();
        assertNotNull(customScope);
        assertEquals("Module 'light_idea_test_case'", customScope.getDisplayName());
        assertTrue(model.isGlobal());
        assertTrue(model.isCustomScope());
        assertTrue(model.isProjectScope());
        assertTrue(model.isWithSubdirectories());
        assertFalse(model.isSearchInProjectFiles());
        assertFalse(model.isExceptComments());
        assertFalse(model.isInCommentsOnly());
        assertFalse(model.isExceptCommentsAndStringLiterals());
        assertFalse(model.isInStringLiteralsOnly());
        assertFalse(model.isExceptStringLiterals());

        assertReplaceDefaults(model);
    }

    public void testDirectoryScopeModel() {
        Project project = myFixture.getProject();
        FindManager findManager = FindManager.getInstance(project);
        FindModel model = findManager.getFindInFileModel();
        FindUtils.resetToDefaults(model);

        model.setStringToFind("any");
        model.setDirectoryName(".");
        assertEquals("any", model.getStringToFind());

        FindInProjectSettings findSettings = FindInProjectSettings.getInstance(project);
        assertNotNull(findSettings);

        assertNavigationDefaults(model);
        assertFalse(model.isCaseSensitive());
        assertFalse(model.isRegularExpressions());
        assertFalse(model.isPreserveCase());
        assertFalse(model.isWholeWordsOnly());
        assertTrue(model.isMultiline());
        assertEquals(FindModel.SearchContext.ANY, model.getSearchContext());
        assertNull(model.getCustomScope());
        assertTrue(model.isGlobal());
        assertFalse(model.isCustomScope());
        assertTrue(model.isProjectScope());
        assertTrue(model.isWithSubdirectories());
        assertFalse(model.isSearchInProjectFiles());
        assertFalse(model.isExceptComments());
        assertFalse(model.isInCommentsOnly());
        assertFalse(model.isExceptCommentsAndStringLiterals());
        assertFalse(model.isInStringLiteralsOnly());
        assertFalse(model.isExceptStringLiterals());

        assertReplaceDefaults(model);
    }

    //assertNavigationDefaults(model);
    //assertTextDefaults(model);
    //assertScopeDefaults(model);
    //assertTestScopeDefaults(model);
    //assertReplaceDefaults(model);


    // https://jetbrains.org/intellij/sdk/docs/tutorials/custom_language_support_tutorial.html
    // https://jetbrains.org/intellij/sdk/docs/tutorials/writing_tests_for_plugins.html


}
