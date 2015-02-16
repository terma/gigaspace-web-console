package com.github.terma.gigaspacesqlconsole.provider;

import java.util.Map;

class GenerateSql {

    public final String typeName;
    public final int count;
    public final Map<String, Object> fields;

    public GenerateSql(String typeName, int count, Map<String, Object> fields) {
        this.typeName = typeName;
        this.count = count;
        this.fields = fields;
    }

}
