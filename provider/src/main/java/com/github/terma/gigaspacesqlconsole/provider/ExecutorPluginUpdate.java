package com.github.terma.gigaspacesqlconsole.provider;

import com.gigaspaces.client.ChangeResult;
import com.gigaspaces.client.ChangeSet;
import com.gigaspaces.query.ISpaceQuery;
import com.github.terma.gigaspacesqlconsole.core.ExecuteRequest;
import com.github.terma.gigaspacesqlconsole.core.ExecuteResponseStream;
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
