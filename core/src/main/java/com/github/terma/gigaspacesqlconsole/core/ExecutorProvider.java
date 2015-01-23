package com.github.terma.gigaspacesqlconsole.core;

import java.sql.Connection;
import java.sql.SQLException;

public interface ExecutorProvider {

    Connection getConnection(final ExecuteRequest request) throws SQLException, ClassNotFoundException;

    ExecuteResponse handleUpdate(ExecuteRequest request, GigaSpaceUpdateSql updateSql);

}
