package com.github.terma.gigaspacesqlconsole.provider;

import java.util.Set;

class CopySql {

    public final String typeName;
    public final Set<String> reset;
    public final String where;

    public CopySql(String typeName, Set<String> reset, String where) {
        this.typeName = typeName;
        this.reset = reset;
        this.where = where;
    }

}
