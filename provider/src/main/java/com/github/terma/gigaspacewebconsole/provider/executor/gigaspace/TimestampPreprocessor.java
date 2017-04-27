/*
Copyright 2015-2017 Artem Stasiuk

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

import com.github.terma.gigaspacewebconsole.provider.executor.ExecutorPreprocessor;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Replace any human timestamp representation on real timestamp
 * Example: $TODAY-1d - start of today by GMT -1 day
 * Or: $NOW+1d - current millis + one day
 */
public class TimestampPreprocessor implements ExecutorPreprocessor {

    private static final Pattern TODAY_PATTERN = Pattern.compile("TODAY(-|\\+)(\\d+)(w|d|h)");
    private static final Pattern NOW_PATTERN = Pattern.compile("NOW(-|\\+)(\\d+)(w|d|h)");

    @Override
    public String preprocess(String sql) {
        if (sql == null) return null;

        sql = replaceTimestamps(TODAY_PATTERN, new DateTime(DateTimeZone.UTC).withTimeAtStartOfDay(), sql);
        sql = replaceTimestamps(NOW_PATTERN, new DateTime(DateTimeZone.UTC), sql);
        return sql;
    }

    private String replaceTimestamps(
            final Pattern pattern, final DateTime timestamp, final String sql) {
        final Matcher matcher = pattern.matcher(sql);
        final StringBuffer updatedSql = new StringBuffer();
        while (matcher.find()) {
            String timestampString = timestampString(matcher, timestamp);
            matcher.appendReplacement(updatedSql, timestampString);
        }
        matcher.appendTail(updatedSql);
        return updatedSql.toString();
    }

    private String timestampString(Matcher matcher, final DateTime timestamp) {
        final int sign = matcher.group(1).equals("-") ? -1 : 1;
        final int quantity = sign * Integer.parseInt(matcher.group(2));
        final String type = matcher.group(3);

        DateTime updatedTimestamp;
        switch (type) {
            case "h":
                updatedTimestamp = timestamp.plusHours(quantity);
                break;
            case "d":
                updatedTimestamp = timestamp.plusDays(quantity);
                break;
            case "w":
                updatedTimestamp = timestamp.plusWeeks(quantity);
                break;
            default:
                throw new IllegalArgumentException();
        }

        return Long.toString(updatedTimestamp.getMillis());
    }

}
