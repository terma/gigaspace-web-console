package com.github.terma.gigaspacesqlconsole.provider;

import com.gigaspaces.client.ChangeResult;
import com.gigaspaces.client.ChangeSet;
import com.github.terma.gigaspacesqlconsole.core.ExecuteRequest;
import com.github.terma.gigaspacesqlconsole.core.ExecuteResponse;
import com.github.terma.gigaspacesqlconsole.core.ExecutorProvider;
import com.github.terma.gigaspacesqlconsole.core.GigaSpaceUpdateSql;
import com.j_spaces.core.client.SQLQuery;
import com.j_spaces.jdbc.driver.GConnection;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.GigaSpaceConfigurer;
import org.openspaces.core.space.UrlSpaceConfigurer;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

public class ExecutorProviderImpl implements ExecutorProvider {

    public Connection getConnection(final ExecuteRequest request) throws SQLException, ClassNotFoundException {
        Thread.currentThread().setContextClassLoader(ExecutorProviderImpl.class.getClassLoader());

        java.util.Properties info = new java.util.Properties();

        if (request.user != null) {
            info.put("user", request.user);
        }
        if (request.password != null) {
            info.put("password", request.password);
        }

        return new GConnection(GConnection.JDBC_GIGASPACES_URL + request.url, info);
    }

    @SuppressWarnings("deprecation")
    private static GigaSpace gigaSpaceConnection(ExecuteRequest request) {
        UrlSpaceConfigurer urlSpaceConfigurer = new UrlSpaceConfigurer(request.url);
        urlSpaceConfigurer.userDetails(request.user, request.password);
        return new GigaSpaceConfigurer(urlSpaceConfigurer.create()).create();
    }

    @Override
    public ExecuteResponse handleUpdate(ExecuteRequest request, GigaSpaceUpdateSql updateSql) {
        SQLQuery query = new SQLQuery<>(updateSql.typeName, updateSql.conditions);
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
