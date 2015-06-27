/*
Copyright 2015 Artem Stasiuk

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

package com.github.terma.gigaspacewebconsole.provider.executor;

import com.gigaspaces.async.AsyncResult;
import com.gigaspaces.document.SpaceDocument;
import com.github.terma.gigaspacewebconsole.core.ExecuteRequest;
import com.github.terma.gigaspacewebconsole.core.ExecuteResponseStream;
import com.github.terma.gigaspacewebconsole.provider.GigaSpaceUtils;
import com.j_spaces.core.client.ExternalEntry;
import com.j_spaces.core.client.SQLQuery;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.executor.DistributedTask;
import org.openspaces.core.executor.TaskGigaSpace;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// select * from my_data where property 'SUPER'
// select * from my_data where property '%PER%'
// select * from my_data where property 'S%R' -> 'S[A-Z]*R'
public class PropertySelectExecutorPlugin implements ExecutorPlugin {

    private static final String PROPERTY_CHAR_PATTERN = "[-.A-Za-z_0-9%]*";

    private static final Pattern TYPE_NAME_PATTERN = Pattern.compile("(from|update) ([A-Za-z_][-.A-Za-z_0-9]*)");
    private static final Pattern PROPERTY_PATTERN = Pattern.compile("property '(" + PROPERTY_CHAR_PATTERN + ")'");

    @Override
    public boolean execute(
            final ExecuteRequest request, final ExecuteResponseStream responseStream) throws Exception {
        final ArrayList<Pattern> property = convertToPatterns(findPropertyConditions(request));

        if (property.size() > 0) {
            final String typeName = findTypeName(request);

            final GigaSpace gigaSpace = GigaSpaceUtils.getGigaSpace(request);

            final List<PropertyReplacement> replacements;
            try {
                replacements = gigaSpace.execute(new TT(typeName, property)).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new IOException(e);
            }

            if (isEmpty(replacements)) {
                responseStream.writeHeader(Arrays.asList("id"));
                responseStream.close();
                return true;
            }

            request.sql = buildRealSql(request.sql, replacements);
            Executor.execute(request, responseStream);
            return true;
        }
        return false;
    }

    private static String findTypeName(ExecuteRequest request) {
        final Matcher typeNameMatcher = TYPE_NAME_PATTERN.matcher(request.sql);
        if (!typeNameMatcher.find()) throw new IllegalArgumentException("Can't find typeName in " + request.sql);
        return typeNameMatcher.group(2);
    }

    private static ArrayList<Pattern> convertToPatterns(final List<String> properties) {
        final ArrayList<Pattern> patterns = new ArrayList<>();
        for (final String property : properties) {
            patterns.add(Pattern.compile("^" + property.replaceAll("%", PROPERTY_CHAR_PATTERN) + "$"));
        }
        return patterns;
    }

    private static boolean isEmpty(List<PropertyReplacement> realPropertyPerProperty) {
        boolean empty = realPropertyPerProperty.isEmpty();
        for (PropertyReplacement replacement : realPropertyPerProperty)
            if (replacement.real.isEmpty()) empty = true;
        return empty;
    }

    private static String buildRealSql(String sql, List<PropertyReplacement> realPropertyPerProperty) {
        final Matcher matcher = PROPERTY_PATTERN.matcher(sql);

        final StringBuffer realSql = new StringBuffer();
        for (final PropertyReplacement replacement : realPropertyPerProperty) {
            final String replacementSql = notNullOr(replacement.real);

            matcher.find();
            matcher.appendReplacement(realSql, replacementSql);
        }
        matcher.appendTail(realSql);
        return realSql.toString();
    }

    private static String notNullOr(final Set<String> columns) {
        StringBuilder sq = new StringBuilder().append("(");
        for (String realProperty : columns) {
            if (sq.length() > 1) sq.append(" or ");
            sq.append(realProperty).append(" is not null");
        }
        sq.append(")");
        return sq.toString();
    }

    private ArrayList<String> findPropertyConditions(ExecuteRequest request) {
        ArrayList<String> property = new ArrayList<>();
        Matcher matcher = PROPERTY_PATTERN.matcher(request.sql);
        while (matcher.find()) {
            property.add(matcher.group(1));
        }
        return property;
    }

    private static class PropertyReplacement implements Serializable {

        private final Pattern property;
        public final HashSet<String> real;

        public PropertyReplacement(final Pattern property) {
            this.property = property;
            this.real = new HashSet<>();
        }

        public boolean accept(String real) {
            return property.matcher(real).find();
        }

    }

    private static class TT implements DistributedTask<ArrayList<PropertyReplacement>, List<PropertyReplacement>> {

        private final String typeName;
        private final ArrayList<Pattern> patterns;

        @TaskGigaSpace
        public transient GigaSpace gigaSpace;

        private TT(final String typeName, final ArrayList<Pattern> patterns) {
            this.typeName = typeName;
            this.patterns = patterns;
        }

        @Override
        public List<PropertyReplacement> reduce(
                final List<AsyncResult<ArrayList<PropertyReplacement>>> list) throws Exception {
            final List<PropertyReplacement> result = emptyReplacements();

            for (final AsyncResult<ArrayList<PropertyReplacement>> partialResult : list) {
                if (partialResult.getException() != null) throw partialResult.getException();
                final ArrayList<PropertyReplacement> partialReplacements = partialResult.getResult();

                for (int i = 0; i < result.size(); i++) {
                    result.get(i).real.addAll(partialReplacements.get(i).real);
                }
            }

            return result;
        }

        @Override
        public ArrayList<PropertyReplacement> execute() throws Exception {
            final ArrayList<PropertyReplacement> replacements = emptyReplacements();

            Object[] spaceDocuments = gigaSpace.readMultiple(new SQLQuery<>(typeName, ""));
            for (Object spaceDocument : spaceDocuments) {
                Collection<String> keys;
                if (spaceDocument instanceof ExternalEntry) {
                    keys = Arrays.asList(((ExternalEntry) spaceDocument).getFieldsNames());
                } else {
                    keys = ((SpaceDocument) spaceDocument).getProperties().keySet();
                }

                for (String property : keys) {
                    for (final PropertyReplacement replacement : replacements) {
                        if (replacement.accept(property)) replacement.real.add(property);
                    }
                }
            }

            return replacements;
        }

        private ArrayList<PropertyReplacement> emptyReplacements() {
            final ArrayList<PropertyReplacement> replacements = new ArrayList<>();
            for (final Pattern pattern : patterns) replacements.add(new PropertyReplacement(pattern));
            return replacements;
        }

    }

}
