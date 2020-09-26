package com.fuzy.find.persistence;

import java.time.LocalDateTime;

import com.fuzy.find.converter.LocalDateTimeConverter;
import com.intellij.util.xmlb.annotations.OptionTag;

public class FindOption {
    public String uuid;
    public String name;
    public String fileFilter;

    @OptionTag(converter = LocalDateTimeConverter.class)
    public LocalDateTime lastUsed;

    public int cntUsed;

    public String searchContext;

    public boolean caseSensitive;

    public boolean wholeWordsOnly;

    public boolean regularExpressions;

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

    @Override
    public String toString() {
        return "FindOption{" +
            "name='" + name + '\'' +
            ", fileFilter='" + fileFilter + '\'' +
            ", lastUsed=" + lastUsed +
            ", cntUsed=" + cntUsed +
            '}';
    }
}
