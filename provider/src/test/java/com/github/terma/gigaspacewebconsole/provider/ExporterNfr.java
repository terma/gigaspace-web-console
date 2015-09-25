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

package com.github.terma.gigaspacewebconsole.provider;

import com.gigaspaces.document.SpaceDocument;
import com.github.terma.gigaspacewebconsole.provider.driver.GigaSpaceUtils;
import org.openspaces.core.GigaSpace;

public class ExporterNfr {

    public static void main(String[] args) {
        System.out.println(Runtime.getRuntime().maxMemory() / 1024 / 1024);

        final GigaSpace gigaSpace = GigaSpaceUtils.getGigaSpace("/./exporter-nfr");
        GigaSpaceUtils.registerType(gigaSpace, "TestType");

        // generate big amount of data
        final BufferedWriter bufferedWriter = new BufferedWriter(gigaSpace);
        for (int i = 0; i < 10000; i++) {
            SpaceDocument testType = new SpaceDocument("TestType");
            testType.setProperty("A", System.currentTimeMillis());
            testType.setProperty("name", "Text for name" + System.currentTimeMillis());
            bufferedWriter.write(testType);
        }
        bufferedWriter.flush();



        System.out.println("OPA");
    }

}
