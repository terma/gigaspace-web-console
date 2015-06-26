/*
Copyright 2015 Artem Stasiuk

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.github.terma.gigaspacewebconsole.provider.executor;

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


