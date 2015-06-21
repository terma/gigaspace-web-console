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

package com.github.terma.gigaspacesqlconsole.provider.executor;

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


