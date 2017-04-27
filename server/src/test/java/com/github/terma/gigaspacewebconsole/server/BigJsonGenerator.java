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

package com.github.terma.gigaspacewebconsole.server;

import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BigJsonGenerator {

    public static void main(String[] args) throws IOException {
//        generateFile("big-simple-data.json", new SimpleRowGenerator(new SimpleCellGenerator()));
//        generateFile("big-pair-data.json", new SimpleRowGenerator(new PairCellGenerator()));
//        generateFile("big-row-data.json", new TypeRowGenerator());
        generateFile("big-lastcell-data.json", new LastCellGenerator());
    }

    private static void generateFile(
            String fileName, RowGenerator rowGenerator) throws IOException {
        List<Object> data = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            data.add(rowGenerator.generate(10));
        }

        String json = new Gson().toJson(data);
        new FileWriter(fileName).append(json).close();
    }

    interface CellGenerator {
        Object generate();
    }

    interface RowGenerator {
        Object generate(int size);
    }

    static class SimpleRowGenerator implements RowGenerator {

        private final CellGenerator cellGenerator;

        SimpleRowGenerator(CellGenerator cellGenerator) {
            this.cellGenerator = cellGenerator;
        }

        @Override
        public Object generate(int size) {
            List<Object> row = new ArrayList<>(10);
            for (int j = 0; j < 10; j++) {
                row.add(cellGenerator.generate());
            }
            return row;
        }
    }

    static class SimpleCellGenerator implements CellGenerator {
        public Object generate() {
            return String.valueOf(Math.random());
        }
    }

    static class PairCellGenerator implements CellGenerator {
        public Object generate() {
            Pair pair = new Pair();
            pair.type = String.valueOf(Math.random());
            pair.data = String.valueOf(Math.random());
            return pair;
        }
    }

    static class TypeRowGenerator implements RowGenerator {
        public Object generate(int size) {
            List<String> types = new ArrayList<>();
            List<Object> data = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                types.add(String.valueOf(Math.random()));
                data.add(String.valueOf(Math.random()));
            }
            data.add(types);
            return data;
        }
    }

    static class LastCellGenerator implements RowGenerator {
        public Object generate(int size) {
            Row row = new Row();
            row.types = new ArrayList<>();
            row.data = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                row.types.add(String.valueOf(Math.random()));
                row.data.add(String.valueOf(Math.random()));
            }
            return row;
        }
    }

    static class Row {

        public List<String> types;
        public List<String> data;

    }

    static class Pair {

        public String type;
        public String data;

    }

}
