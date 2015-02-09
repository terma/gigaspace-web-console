package com.github.terma.gigaspacesqlconsole.provider;

import java_cup.runtime.Symbol;

import java.io.IOException;
import java.io.StringReader;

public class CopySqlParser {

    public static CopySql parse(String sql) throws IOException {
        final CopySqlLexer lexer = new CopySqlLexer(new StringReader(sql));
        while (true) {

            try {
                Symbol token = lexer.next_token();

                if (token.left == token.right || token.sym == -1) {
                    final CopySql copySql = lexer.getSql();

                    if (copySql.typeName.isEmpty()) {
                        throw new IOException("Expected SQL: update <typeName>, but typeName not specified!");
                    }
                    return copySql;
                }
            } catch (UnsupportedOperationException e) {
                return null;
            }
        }
    }

}


