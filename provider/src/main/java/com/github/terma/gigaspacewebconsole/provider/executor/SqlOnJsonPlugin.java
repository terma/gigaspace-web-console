/*
Copyright 2015-2017 Artem Stasiuk

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

import com.github.terma.gigaspacewebconsole.core.ExecuteRequest;
import com.github.terma.gigaspacewebconsole.core.ExecuteResponseStream;
import com.github.terma.gigaspacewebconsole.provider.ConverterHelper;
import com.github.terma.gigaspacewebconsole.provider.RealSqlResult;
import com.github.terma.sqlonjson.SqlOnJson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * sql_on_json(select STATE from AUDITS where id = (select max(id) from AUDITS))) select * from thresholds t where t.limit > 10
 * sql_on_json(select STATE from AUDIT where rownum < 2) select * from thresholds t where t.limit > 10
 */
public class SqlOnJsonPlugin implements ExecutorPlugin {

    private static final String PREFIX = "sql_on_json(";

    private final ConnectionFactory connectionFactory;
    private final ConverterHelper converterHelper;

    public SqlOnJsonPlugin(ConnectionFactory connectionFactory, ConverterHelper converterHelper) {
        this.connectionFactory = connectionFactory;
        this.converterHelper = converterHelper;
    }

    @Override
    public boolean execute(ExecuteRequest request, ExecuteResponseStream responseStream) throws Exception {
        if (request.sql.startsWith(PREFIX)) {

            int delimiter = PREFIX.length();
            int inc = 0;
            while (delimiter < request.sql.length()) {
                char c = request.sql.charAt(delimiter);
                if (c == '(') inc++;
                else if (c == ')') inc--;

                if (inc < 0) break;
                delimiter++;
            }

            String sql = request.sql.substring(PREFIX.length(), delimiter);
            String jsonSql = request.sql.substring(delimiter + 1);

            try (
                    final Connection connection = connectionFactory.get(request);
                    final PreparedStatement ps = connection.prepareStatement(sql);
                    final ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new IllegalArgumentException("No row with JSON for " + request.sql);

                final String json = rs.getString(1);
                if (json == null) throw new IllegalArgumentException("Null JSON for " + request.sql);

                try (
                        final Connection jsonConnection = SqlOnJson.convertPlain(json);
                        final PreparedStatement jsonPs = jsonConnection.prepareStatement(jsonSql);
                        final ResultSet jsonRs = jsonPs.executeQuery()
                ) {
                    ResponseUtil.sqlResultToResponseStream(
                            new RealSqlResult(jsonRs, request.sql, converterHelper), responseStream);
                }
            }
            return true;
        }
        return false;
    }

}
