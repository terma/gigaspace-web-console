package com.github.terma.gigaspacesqlconsole.provider;

import com.github.terma.gigaspacesqlconsole.core.GigaSpaceUpdateSql;
import java_cup.runtime.Symbol;

import java.io.IOException;
import java.io.StringReader;

public class GigaSpaceUpdateSqlParser {

    public static GigaSpaceUpdateSql parse(String data) throws IOException {
        final Lexer lexer = new Lexer(new StringReader(data));
        while (true) {

            try {
                Symbol token = lexer.next_token();

                if (token.left == token.right || token.sym == -1) {
                    final GigaSpaceUpdateSql sql = lexer.getSql();

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


