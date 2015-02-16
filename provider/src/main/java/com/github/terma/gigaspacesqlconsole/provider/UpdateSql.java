package com.github.terma.gigaspacesqlconsole.provider;

import java.util.Map;

public class UpdateSql {

    public final String typeName;
    public final Map<String, Object> setFields;
    public final String conditions;

    public UpdateSql(String typeName, Map<String, Object> setFields, String conditions) {
        this.typeName = typeName;
        this.setFields = setFields;
        this.conditions = conditions;
    }

}
