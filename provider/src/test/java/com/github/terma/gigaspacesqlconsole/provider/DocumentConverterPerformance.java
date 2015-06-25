package com.github.terma.gigaspacesqlconsole.provider;

import com.gigaspaces.document.SpaceDocument;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DocumentConverterPerformance {

    private static final int ITERATIONS = 5;
    private static final int COUNT = 1000;

    @Test
    public void showGoodPerformance() throws InterruptedException {
        performInputTest("10 plain", plain10());
        performInputTest("10 plain and 2 documents", plain10document2());
        performInputTest("10 plain and 2 documents nested list 10 of documents", plain10document2nestedList10Document());
    }

    private void performInputTest(String name, List<SpaceDocument> input) throws InterruptedException {
        System.gc();

        long trash = 0;
        for (int i = 0; i < ITERATIONS; i++) trash += performOneTest(input);
        System.gc();

        Thread.sleep(1000);

//        System.out.println(name + " example -> " + DocumentConverter.convert(input.get(0)));

        // test
        long start = System.currentTimeMillis();
        for (int i = 0; i < ITERATIONS; i++) trash += performOneTest(input);
        long time = System.currentTimeMillis() - start;

        System.out.println(name + " avg for 1k is " + time / ITERATIONS + " msec, trash " + trash);
    }

    private long performOneTest(List<SpaceDocument> input) {
        long trash = 0;
        for (SpaceDocument document : input) trash += DocumentConverter.convert(document).length();
        return trash;
    }

    private List<SpaceDocument> plain10() {
        List<SpaceDocument> input = new ArrayList<>();
        for (int i = 0; i < COUNT; i++) {
            SpaceDocument spaceDocument = documentWith10Plain();
            Assert.assertThat(spaceDocument.getProperties().size(), CoreMatchers.is(10));
            input.add(spaceDocument);
        }
        return input;
    }

    private SpaceDocument documentWith10Plain() {
        SpaceDocument spaceDocument = new SpaceDocument();
        injectPlainProperties(spaceDocument);
        return spaceDocument;
    }

    private void injectPlainProperties(SpaceDocument spaceDocument) {
        spaceDocument.setProperty("a1", 1);
        spaceDocument.setProperty("a3", 13);
        spaceDocument.setProperty("a4", 1234);
        spaceDocument.setProperty("a5", "super dup" + System.currentTimeMillis());
        spaceDocument.setProperty("a6", 1);
        spaceDocument.setProperty("a7", -90);
        spaceDocument.setProperty("a8", Math.random());
        spaceDocument.setProperty("a9", "wer");
        spaceDocument.setProperty("a10", 12);
        spaceDocument.setProperty("a11", System.currentTimeMillis());
    }

    private List<SpaceDocument> plain10document2() {
        List<SpaceDocument> input = plain10();
        for (SpaceDocument spaceDocument : input) {
            SpaceDocument embedded1 = new SpaceDocument("embedded1");
            injectPlainProperties(embedded1);
            spaceDocument.setProperty("embedded1", embedded1);
            SpaceDocument embedded2 = new SpaceDocument("embedded2");
            injectPlainProperties(embedded2);
            spaceDocument.setProperty("embedded2", embedded2);
            Assert.assertThat(spaceDocument.getProperties().size(), CoreMatchers.is(12));
        }
        return input;
    }

    private List<SpaceDocument> plain10document2nestedList10Document() {
        List<SpaceDocument> input = plain10();
        for (SpaceDocument spaceDocument : input) {
            SpaceDocument embedded1 = new SpaceDocument("embedded1");
            injectList10Documents(embedded1);
            injectPlainProperties(embedded1);
            spaceDocument.setProperty("embedded1", embedded1);
            SpaceDocument embedded2 = new SpaceDocument("embedded2");
            injectList10Documents(embedded2);
            injectPlainProperties(embedded2);
            spaceDocument.setProperty("embedded2", embedded2);
            Assert.assertThat(spaceDocument.getProperties().size(), CoreMatchers.is(12));
        }
        return input;
    }

    private void injectList10Documents(SpaceDocument embedded2) {
        List<SpaceDocument> emList2 = new ArrayList<>();
        for (int i = 0; i < 10; i++) emList2.add(documentWith10Plain());
        embedded2.setProperty("list", emList2);
    }

}
