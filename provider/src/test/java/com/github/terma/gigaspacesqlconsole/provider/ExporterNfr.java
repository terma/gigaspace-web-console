package com.github.terma.gigaspacesqlconsole.provider;

import com.gigaspaces.document.SpaceDocument;
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
