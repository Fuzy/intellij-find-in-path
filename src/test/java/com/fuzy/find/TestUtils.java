package com.fuzy.find;

import com.intellij.find.FindModel;

import static org.junit.Assert.*;

public class TestUtils {

    public static void assertInitialModelState(FindModel model) {
        assertNotNull(model);
        assertEquals("", model.getStringToFind());
        assertNull(model.getFileFilter());
//        assertEquals("global", model.getCustomScopeName());
        assertNull(model.getCustomScopeName());
        assertNull(model.getModuleName());
        assertNull(model.getDirectoryName());
        assertEquals(FindModel.SearchContext.ANY, model.getSearchContext());
        assertTextDefaults(model);
        assertScopeDefaults(model);
        assertNavigationDefaults(model);
        assertTrue(model.isMultipleFiles());
        assertReplaceDefaults(model);
    }

    public static void assertNavigationDefaults(FindModel model) {
        // navigation
        assertFalse(model.isFindAllEnabled());
        assertFalse(model.isFindAll());
        assertTrue(model.isForward());
        assertFalse(model.isFromCursor());
    }

    public static void assertTextDefaults(FindModel model) {
        // text
        assertFalse(model.isCaseSensitive());
        assertFalse(model.isRegularExpressions());
        assertFalse(model.isPreserveCase());
        assertFalse(model.isWholeWordsOnly());
        assertTrue(model.isMultiline());
    }

    public static void assertScopeDefaults(FindModel model) {
        // scope
        assertEquals(FindModel.SearchContext.ANY, model.getSearchContext());
        assertNull(model.getCustomScope());
        assertTrue(model.isGlobal());
        assertFalse(model.isCustomScope());
        assertTrue(model.isProjectScope());
        assertTrue(model.isWithSubdirectories());
        assertFalse(model.isSearchInProjectFiles());
    }

    public static void assertTestScopeDefaults(FindModel model) {
        // text scope
        assertFalse(model.isExceptComments());
        assertFalse(model.isInCommentsOnly());
        assertFalse(model.isExceptCommentsAndStringLiterals());
        assertFalse(model.isInStringLiteralsOnly());
        assertFalse(model.isExceptStringLiterals());
    }

    public static void assertReplaceDefaults(FindModel model) {
        // replace
        assertEquals("", model.getStringToReplace());
        assertTrue(model.isPromptOnReplace());
        assertFalse(model.isReplaceState());
        assertFalse(model.isReplaceAll());
    }

}
