package com.ubirch.swagger.example;

import java.io.BufferedReader;

import static com.ubirch.swagger.example.FastImport.fastImport;

public class FastImportTest {

    static String csvFile = "db7.csv";
    static BufferedReader br = null;
    static String cvsSplitBy = ";";


    public static void main(String[] args) {
        fastImport(csvFile, cvsSplitBy);
    }

}
