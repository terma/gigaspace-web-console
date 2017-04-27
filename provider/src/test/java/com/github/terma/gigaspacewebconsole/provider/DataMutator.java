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

package com.github.terma.gigaspacewebconsole.provider;

import com.gigaspaces.document.SpaceDocument;
import com.github.terma.gigaspacewebconsole.provider.driver.GigaSpaceUtils;
import org.openspaces.core.GigaSpace;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class DataMutator {

    private final static Random RANDOM = new Random();

    public static void main(String[] args) throws InterruptedException {
        GigaSpace gigaSpace = GigaSpaceUtils.getGigaSpace("jini:/*/*/gs10?locators=127.0.0.1:4700");

        while (!Thread.currentThread().isInterrupted()) {
            longData(gigaSpace);
            Thread.sleep(TimeUnit.SECONDS.toMillis(5));
        }
    }

    private static void injectAllTypes(SpaceDocument spaceDocument) {
        spaceDocument.setProperty("boolOrNull", Math.random() > 0.5 ? true : null);
        spaceDocument.setProperty("xml", "<node attribute=\"900ff\">\n<shortNode/>\n<value>1&nbsp;12</value>\n<es>\"Escape\"</es>\n</node>");
    }

    private static void longData(GigaSpace gigaSpace) {
        for (int i = 0; i < RANDOM.nextInt(100); i++) {
            SpaceDocument spaceDocument = new SpaceDocument("LongData");
            injectAllTypes(spaceDocument);
            spaceDocument.setProperty("description", "Top books that are recommended by EPAM Solution Architects:\n" +
                    "Software Architecture in Practice (3rd Edition) (available in EPAM Library: Software Architecture in Practice (3rd Edition))\n" +
                    "Microsoft Application Architecture Guide;\n" +
                    "Enterprise Integration Patterns: Designing, Building, and Deploying Messaging Solutions;\n" +
                    "Domain-Driven Design: Tackling Complexity in the Heart of Software;\n" +
                    "Documenting Software Architectures: Views and Beyond\n" +
                    "Evaluating Software Architectures: Methods and Case Studies\n" +
                    "Data Access for Highly-Scalable Solutions: Using SQL, NoSQL, and Polyglot Persistence\n" +
                    "Applying UML and Patterns: An Introduction to Object-Oriented Analysis and Design and Iterative Development\n" +
                    "Domain-Driven Design: Tackling Complexity in the Heart of Software\n" +
                    "Patterns of Enterprise Application Architecture\n" +
                    "APIs: A Strategy Guide\n" +
                    "Object-Oriented Software Construction\n" +
                    "Enterprise Integration Patterns: Designing, Building, and Deploying Messaging Solutions\n" +
                    "Agile Estimating and Planning \n" +
                    "Crystal Clear: A Human-Powered Methodology for Small Teams\n" +
                    "Test Driven Development: By Example\n" +
                    "97 Things Every Software Architect Should Know: Collective Wisdom from the Experts\n" +
                    "97 Things Every Programmer Should Know: Collective Wisdom from the Experts\n" +
                    "Planning and Managing Software Projects\n" +
                    "Class 7: Work Breakdown Structure (WBS)\n" +
                    "Class 14: Risk Management " + Math.random());
            gigaSpace.write(spaceDocument);
        }
    }

}
