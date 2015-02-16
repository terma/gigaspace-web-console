package com.github.terma.gigaspacesqlconsole.provider;

import java_cup.runtime.Symbol;

import java.io.IOException;
import java.io.StringReader;

class UpdateSqlParser {

    public static UpdateSql parse(String data) throws IOException {
        final UpdateSqlLexer lexer = new UpdateSqlLexer(new StringReader(data));
        while (true) {

            try {
                Symbol token = lexer.next_token();

                if (token.left == token.right || token.sym == -1) {
                    final UpdateSql sql = lexer.getSql();

                    if (sql.typeName.isEmpty()) {
                        throw new IOException("Expected SQL: update <typeName>, but typeName not specified!");
                    }

                    if (sql.setFields.isEmpty()) {
                        throw new IOException("Expected SQL: update <typeName> set <setFields>, but setFields not specified!");
                    }

                    return sql;
                }
            } catch (UnsupportedOperationException e) {
                return null;
            }
        }
    }

}


