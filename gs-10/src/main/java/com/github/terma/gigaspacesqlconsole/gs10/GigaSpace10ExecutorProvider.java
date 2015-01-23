package com.github.terma.gigaspacesqlconsole.gs10;

import com.gigaspaces.client.ChangeResult;
import com.gigaspaces.client.ChangeSet;
import com.gigaspaces.document.SpaceDocument;
import com.github.terma.gigaspacesqlconsole.ExecuteRequest;
import com.github.terma.gigaspacesqlconsole.ExecuteResponse;
import com.github.terma.gigaspacesqlconsole.ExecutorProvider;
import com.github.terma.gigaspacesqlconsole.GigaSpaceUpdateSql;
import com.j_spaces.core.client.SQLQuery;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.UrlSpaceConfigurer;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

public class GigaSpace10ExecutorProvider implements ExecutorProvider {

    public Connection getConnection(final ExecuteRequest request) throws SQLException, ClassNotFoundException {
        Class.forName("com.j_spaces.jdbc.driver.GDriver");
        return DriverManager.getConnection(
                "jdbc:gigaspaces:url:" + request.url, request.user, request.password);
    }

    private static GigaSpace gigaSpaceConnection(ExecuteRequest request) {
        UrlSpaceConfigurer urlSpaceConfigurer = new UrlSpaceConfigurer(request.url);
        urlSpaceConfigurer.userDetails(request.user, request.password);
        return new GigaSpaceConfigurer(urlSpaceConfigurer.create()).create();
    }

    @Override
    public ExecuteResponse handleUpdate(ExecuteRequest request, GigaSpaceUpdateSql updateSql) {
        SQLQuery query = new SQLQuery<SpaceDocument>(updateSql.typeName, updateSql.conditions);
        ChangeSet changeSet = new ChangeSet();

        for (Map.Entry<String, Object> field : updateSql.setFields.entrySet()) {
            changeSet.set(field.getKey(), (Serializable) field.getValue());
        }

        ChangeResult changeResult = gigaSpaceConnection(request).change(query, changeSet);
        ExecuteResponse executeResponse = new ExecuteResponse();
        executeResponse.columns = Arrays.asList("affected_rows");
        executeResponse.data = Arrays.asList(Arrays.asList(Integer.toString(changeResult.getNumberOfChangedEntries())));
        return executeResponse;
    }

}
