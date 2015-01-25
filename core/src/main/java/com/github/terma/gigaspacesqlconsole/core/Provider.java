package com.github.terma.gigaspacesqlconsole.core;

import com.github.terma.gigaspacesqlconsole.core.*;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by terma on 1/24/15.
 */
public interface Provider {
    CountsResponse counts(CountsRequest request);

    ExecuteResponse handleUpdate(ExecuteRequest request, GigaSpaceUpdateSql updateSql);

    Connection getConnection(ExecuteRequest request) throws SQLException, ClassNotFoundException;
}
