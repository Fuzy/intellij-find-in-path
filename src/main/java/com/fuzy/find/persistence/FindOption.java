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

    //com.intellij.find.FindModel.SearchContext
    //match case (boolean)
    //words (boolean
    //regex (boolean)

    //TODO scope


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
