package com.github.terma.gigaspacewebconsole.server;

import junit.framework.Assert;
import org.junit.Test;

public class StringUtilsTest {

    @Test
    public void shouldGenerateValidFileNameAndRemoveUnsupportedSymbols() {
        Assert.assertEquals("select_from_Customer.csv", StringUtils.safeCsvFileName("select * from Customer"));
        Assert.assertEquals("select_a-a_from_Customer.csv", StringUtils.safeCsvFileName("select a-a from Customer"));
    }

    @Test
    public void shouldGenerateValidFileNameAndLimitLength() {
        Assert.assertEquals(
                "select_from_Customer_where_orderId_123_and_name_S.csv",
                StringUtils.safeCsvFileName("select * from Customer where orderId = 123 and name = \"SuperMan\""));
    }

}
