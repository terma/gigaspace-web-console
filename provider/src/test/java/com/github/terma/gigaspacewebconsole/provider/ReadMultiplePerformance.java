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

package com.github.terma.gigaspacewebconsole.provider;

import com.gigaspaces.client.iterator.IteratorScope;
import com.gigaspaces.document.SpaceDocument;
import com.gigaspaces.query.IdsQuery;
import com.github.terma.gigaspacewebconsole.provider.driver.GigaSpaceUtils;
import com.j_spaces.core.client.SQLQuery;
import org.openspaces.core.GigaSpace;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
400000
25 msec per call < MyIteratorGetter
400000
19 msec per call < GsIteratorGetter
400000
17 msec per call < ReadMultipleGetter
400000
22 msec per call < MyIteratorGetter
400000
19 msec per call < GsIteratorGetter
400000
17 msec per call < ReadMultipleGetter
 */
public class ReadMultiplePerformance {

    public static void main(String[] args) throws InterruptedException {
        GigaSpace gigaSpace = GigaSpaceUtils.getGigaSpace("jini:/*/*/gs10?locators=127.0.0.1:4700");

        // heat
        play(gigaSpace, new GsIteratorGetter(), 400);
        play(gigaSpace, new ReadMultipleGetter(), 400);
        play(gigaSpace, new MyIteratorGetter(), 400);

        // go
        play(gigaSpace, new GsIteratorGetter(), 400);
        play(gigaSpace, new ReadMultipleGetter(), 400);
        play(gigaSpace, new MyIteratorGetter(), 400);
    }

    private static void play(GigaSpace gigaSpace, DataGetter getter, int times) {
        long r = 0;
        long start = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {
            r += getter.get(gigaSpace);
        }
        System.out.println(r);
        System.out.println((System.currentTimeMillis() - start) / times + " msec per call < " + getter.getClass().getSimpleName());
    }

    private interface DataGetter {
        int get(GigaSpace gigaSpace);
    }

    private static class ReadMultipleGetter implements DataGetter {
        @Override
        public int get(GigaSpace gigaSpace) {
            SpaceDocument[] result = gigaSpace.readMultiple(new SQLQuery<SpaceDocument>("LongData", ""));
            return result.length;
        }
    }

    private static class GsIteratorGetter implements DataGetter {
        @Override
        public int get(GigaSpace gigaSpace) {
            Iterator iterator = gigaSpace.iterator()
                    .bufferSize(500)
                    .addTemplate(new SQLQuery<SpaceDocument>("LongData", ""))
                    .iteratorScope(IteratorScope.CURRENT)
                    .create();
            int r = 0;
            while (iterator.hasNext()) {
                iterator.next();
                r++;
            }
            return r;
        }
    }

    private static class MyIteratorGetter implements DataGetter {
        @Override
        public int get(GigaSpace gigaSpace) {
            SpaceDocument[] uids = gigaSpace.readMultiple(new SQLQuery<SpaceDocument>("LongData", "").setProjections("uid"));

            int r = 0;

            int p = 0;
            while (p < uids.length) {
                List<Object> partUids = new ArrayList<>(500);
                for (int i = 0; i < 501 && p + i < uids.length; i++) {
                    partUids.add(uids[p + i].getProperty("id"));
                }
                p+= partUids.size();

                SpaceDocument[] spaceDocuments = gigaSpace.readByIds(new IdsQuery<SpaceDocument>("LongData", partUids.toArray())).getResultsArray();
                r += spaceDocuments.length;
            }

            return r;
        }
    }

}
