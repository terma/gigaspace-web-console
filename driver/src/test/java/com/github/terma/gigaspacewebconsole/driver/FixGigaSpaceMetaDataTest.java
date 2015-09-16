package com.github.terma.gigaspacewebconsole.driver;

import com.j_spaces.jdbc.driver.GConnection;
import org.junit.Test;
import org.openspaces.core.GigaSpace;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class FixGigaSpaceMetaDataTest {

    @Test
    public void shouldGetOnlyObjectTypeIfNoRegisteredTypes() throws SQLException, ClassNotFoundException {
        GConnection connection = GigaSpaceUtils.createJdbcConnection("/./meta-data");
        ResultSet tables = new FixGigaSpaceMetaData(connection).getTables(null, null, null, null);
        assertThat(tables.next(), equalTo(true));
        assertThat(tables.getString(1), equalTo(null));
        assertThat(tables.getString(2), equalTo(null));
        assertThat(tables.getString(3), equalTo("java.lang.Object"));
    }

    @Test
    public void shouldGetTableForEachRegisteredType() throws SQLException, ClassNotFoundException {
        GigaSpace gigaSpace = GigaSpaceUtils.getGigaSpace("/./meta-data-non-empty");

        GigaSpaceUtils.registerType(gigaSpace, "A");
        GigaSpaceUtils.registerType(gigaSpace, "Dida");

        GConnection connection = GigaSpaceUtils.createJdbcConnection("/./meta-data-non-empty");
        ResultSet tables = new FixGigaSpaceMetaData(connection).getTables(null, null, null, null);
        assertThat(tables.next(), equalTo(true));
        assertThat(tables.getString(1), equalTo(null));
        assertThat(tables.getString(2), equalTo(null));
        assertThat(tables.getString(3), equalTo("java.lang.Object"));

        assertThat(tables.next(), equalTo(true));
        assertThat(tables.getString(1), equalTo(null));
        assertThat(tables.getString(2), equalTo(null));
        assertThat(tables.getString(3), equalTo("A"));

        assertThat(tables.next(), equalTo(true));
        assertThat(tables.getString(1), equalTo(null));
        assertThat(tables.getString(2), equalTo(null));
        assertThat(tables.getString(3), equalTo("Dida"));
    }

}
