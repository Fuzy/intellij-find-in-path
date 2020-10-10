package com.fuzy.find.persistence;

import java.time.LocalDateTime;
import java.util.Objects;

import com.fuzy.find.converter.LocalDateTimeConverter;
import com.intellij.util.xmlb.annotations.OptionTag;

public class FindOption {
    private String uuid;
    private String name;
    private String fileFilter;

    @OptionTag(converter = LocalDateTimeConverter.class)
    private LocalDateTime lastUsed;

    private int cntUsed;

    private String searchContext;

    private boolean caseSensitive;

    private boolean wholeWordsOnly;

    private boolean regularExpressions;
    private String moduleName;
    private String directoryName;
    private boolean customScope;
    private boolean projectScope;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileFilter() {
        return fileFilter;
    }

    public void setFileFilter(String fileFilter) {
        this.fileFilter = fileFilter;
    }

    public LocalDateTime getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(LocalDateTime lastUsed) {
        this.lastUsed = lastUsed;
    }

    public int getCntUsed() {
        return cntUsed;
    }

    public void setCntUsed(int cntUsed) {
        this.cntUsed = cntUsed;
    }

    public String getSearchContext() {
        return searchContext;
    }

    public void setSearchContext(String searchContext) {
        this.searchContext = searchContext;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public boolean isWholeWordsOnly() {
        return wholeWordsOnly;
    }

    public void setWholeWordsOnly(boolean wholeWordsOnly) {
        this.wholeWordsOnly = wholeWordsOnly;
    }

    public boolean isRegularExpressions() {
        return regularExpressions;
    }

    public void setRegularExpressions(boolean regularExpressions) {
        this.regularExpressions = regularExpressions;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void setCustomScope(boolean customScope) {
        this.customScope = customScope;
    }

    public boolean isCustomScope() {
        return customScope;
    }

    public void setProjectScope(boolean projectScope) {
        this.projectScope = projectScope;
    }

    public boolean isProjectScope() {
        return projectScope;
    }

    //TODO pokud pozdeji pridam nejake nastaveni tak nebudou shodne - v persistentnim zaznamu budou property chybet, co s tim?
    //TODO SearchScope
    //com.intellij.psi.search.PredefinedSearchScopeProvider.getPredefinedScopes
    //NamedScopeManager.getInstance(project); workspace.xml

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FindOption that = (FindOption) o;
        return caseSensitive == that.caseSensitive &&
            wholeWordsOnly == that.wholeWordsOnly &&
            regularExpressions == that.regularExpressions &&
            customScope == that.customScope &&
            projectScope == that.projectScope &&
            Objects.equals(fileFilter, that.fileFilter) &&
            Objects.equals(searchContext, that.searchContext) &&
            Objects.equals(moduleName, that.moduleName) &&
            Objects.equals(directoryName, that.directoryName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileFilter, searchContext, caseSensitive, wholeWordsOnly, regularExpressions, moduleName, directoryName, customScope, projectScope);
    }

    @Override
    public String toString() {
        return "FindOption{" +
            "uuid='" + uuid + '\'' +
            ", name='" + name + '\'' +
            ", fileFilter='" + fileFilter + '\'' +
            ", lastUsed=" + lastUsed +
            ", cntUsed=" + cntUsed +
            ", searchContext='" + searchContext + '\'' +
            ", caseSensitive=" + caseSensitive +
            ", wholeWordsOnly=" + wholeWordsOnly +
            ", regularExpressions=" + regularExpressions +
            ", moduleName='" + moduleName + '\'' +
            ", directoryName='" + directoryName + '\'' +
            ", customScope=" + customScope +
            ", projectScope=" + projectScope +
            '}';
    }
}
