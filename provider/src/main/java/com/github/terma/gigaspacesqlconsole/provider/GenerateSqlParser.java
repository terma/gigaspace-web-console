package com.github.terma.gigaspacesqlconsole.provider;

import java_cup.runtime.Symbol;

import java.io.IOException;
import java.io.StringReader;

class GenerateSqlParser {

    private static final String TEMPLATE = "generate count of typeName with fieldName = fieldValue[, fieldName1 = fieldValue1]";

    public static GenerateSql parse(String data) throws IOException {
        final GenerateSqlLexer lexer = new GenerateSqlLexer(new StringReader(data));
        while (true) {

            try {
                Symbol token = lexer.next_token();

                if (token.left == token.right || token.sym == -1) {
                    final GenerateSql sql = lexer.getSql();

                    if (sql.typeName.isEmpty()) {
                        throw new IOException("Expected SQL: " + TEMPLATE + ", but typeName not specified!");
                    }

                    if (sql.fields.isEmpty()) {
                        throw new IOException("Expected SQL: " + TEMPLATE + ", but fields not specified!");
                    }

                    return sql;
                }
            } catch (UnsupportedOperationException e) {
                return null;
            }
        }
    }

}


