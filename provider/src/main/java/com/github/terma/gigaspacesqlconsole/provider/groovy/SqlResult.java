package com.github.terma.gigaspacesqlconsole.provider.groovy;

import java.sql.SQLException;
import java.util.List;

public interface SqlResult extends AutoCloseable {

    boolean next() throws SQLException;

    List<String> getColumns() throws SQLException;

    List<String> getRow() throws SQLException;

    void close() throws SQLException;

}
