package com.github.terma.gigaspacesqlconsole.core;

import java.util.Map;

public class GigaSpaceUpdateSql {

    public final String typeName;
    public final Map<String, Object> setFields;
    public final String conditions;

    public GigaSpaceUpdateSql(String typeName, Map<String, Object> setFields, String conditions) {
        this.typeName = typeName;
        this.setFields = setFields;
        this.conditions = conditions;
    }

}
