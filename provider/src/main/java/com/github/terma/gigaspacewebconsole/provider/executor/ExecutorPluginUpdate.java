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

import com.gigaspaces.client.ChangeResult;
import com.gigaspaces.client.ChangeSet;
import com.github.terma.gigaspacewebconsole.core.ExecuteRequest;
import com.github.terma.gigaspacewebconsole.core.ExecuteResponseStream;
import com.github.terma.gigaspacewebconsole.provider.GigaSpaceUtils;
import com.j_spaces.core.client.SQLQuery;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import static java.util.Arrays.asList;

class ExecutorPluginUpdate implements ExecutorPlugin {

    @Override
    public boolean execute(final ExecuteRequest request, final ExecuteResponseStream responseStream) throws IOException {
        final UpdateSql updateSql = UpdateSqlParser.parse(request.sql);
        if (updateSql == null) return false;

        handleUpdate(request, updateSql, responseStream);
        return true;
    }

    private static void handleUpdate(
            final ExecuteRequest request, UpdateSql updateSql,
            final ExecuteResponseStream responseStream) throws IOException {
        SQLQuery query = new SQLQuery<>(updateSql.typeName, updateSql.conditions);
        ChangeSet changeSet = new ChangeSet();

        for (Map.Entry<String, Object> field : updateSql.setFields.entrySet()) {
            if (field.getValue() == null) {
                changeSet.unset(field.getKey());
            } else {
                changeSet.set(field.getKey(), (Serializable) field.getValue());
            }
        }

        ChangeResult changeResult = GigaSpaceUtils.getGigaSpace(request).change(query, changeSet);
        responseStream.writeHeader(asList("affected_rows"));
        responseStream.writeRow(asList(Integer.toString(changeResult.getNumberOfChangedEntries())));
        responseStream.close();
    }

}
