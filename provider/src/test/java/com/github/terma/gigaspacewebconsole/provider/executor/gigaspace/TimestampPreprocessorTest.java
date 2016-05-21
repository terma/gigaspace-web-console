/*
Copyright 2015-2016 Artem Stasiuk

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

package com.github.terma.gigaspacewebconsole.provider.executor.gigaspace;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class TimestampPreprocessorTest {

    private final TimestampPreprocessor timestampPreprocessor = new TimestampPreprocessor();

    @Test
    public void nullStringNullResult() {
        assertThat(timestampPreprocessor.preprocess(null), nullValue());
    }

    @Test
    public void nothingToReplace() {
        assertThat(
                timestampPreprocessor.preprocess("select * from A"),
                is("select * from A"));
    }

    @Test
    public void replaceTodayOnStartOfDayWithModification() {
        long expectedTimestamp = new DateTime(DateTimeZone.UTC).withTimeAtStartOfDay().minusDays(2).getMillis();
        assertThat(
                timestampPreprocessor.preprocess("select * from A where moment = TODAY-2d"),
                is("select * from A where moment = " + expectedTimestamp));
    }

    @Test
    public void replaceTodayOnStartOfDayWithoutModification() {
        long expectedTimestamp = new DateTime(DateTimeZone.UTC).withTimeAtStartOfDay().getMillis();
        assertThat(
                timestampPreprocessor.preprocess("select * from A where moment = TODAY-0d"),
                is("select * from A where moment = " + expectedTimestamp));
    }

    @Test
    public void replaceTodayOnStartOfDayWithWeekModification() {
        long expectedTimestamp = new DateTime(DateTimeZone.UTC).withTimeAtStartOfDay().plusWeeks(1).getMillis();
        assertThat(
                timestampPreprocessor.preprocess("select * from A where moment = TODAY+1w"),
                is("select * from A where moment = " + expectedTimestamp));
    }

    @Test
    public void replaceTodayOnStartOfDayWithHourModification() {
        long expectedTimestamp = new DateTime(DateTimeZone.UTC).withTimeAtStartOfDay().plusHours(3).getMillis();
        assertThat(
                timestampPreprocessor.preprocess("select * from A where moment = TODAY+3h"),
                is("select * from A where moment = " + expectedTimestamp));
    }

    @Ignore // todo fix
    @Test
    public void replaceNowWithWeekModification() {
        long expectedTimestamp = new DateTime(DateTimeZone.UTC).minusWeeks(1).getMillis();
        assertThat(
                timestampPreprocessor.preprocess("select * from A where moment = NOW-1w"),
                is("select * from A where moment = " + expectedTimestamp));
    }

}
