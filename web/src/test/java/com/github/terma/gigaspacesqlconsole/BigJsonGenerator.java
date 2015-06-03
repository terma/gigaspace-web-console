package com.github.terma.gigaspacesqlconsole;

import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BigJsonGenerator {

    public static void main(String[] args) throws IOException {
        generateFile("big-simple-data.json", new SimpleCellGenerator());
        generateFile("big-pair-data.json", new PairCellGenerator());
    }

    private static void generateFile(String fileName, CellGenerator cellGenerator) throws IOException {
        List<Object> data = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            List<Object> row = new ArrayList<>(10);
            data.add(row);
            for (int j = 0; j < 10; j++) {
                row.add(cellGenerator.generate());
            }
        }

        String json = new Gson().toJson(data);
        new FileWriter(fileName).append(json).close();
    }

    interface CellGenerator {
        Object generate();
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


    static class Pair {

        public String type;
        public String data;

    }

}
