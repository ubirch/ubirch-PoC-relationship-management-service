package com.ubirch.swagger.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;

import static com.ubirch.swagger.example.AddVertexes.addTwoVertexes;



public class AddVertexesTest {

    private static final String LABEL = "label";
    private static final String NAME = "name";
    private static final String ID = "id";
    private static final String OUT_V = "outV";
    private static final String IN_V = "inV";
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());



    public static void main(String[] args) {
        populateDb();
    }

    /**
     * Creates some vertex, and link them
     */
    private static void populateDb(){
        HashMap<String, String> p1 = new HashMap<>();
        HashMap<String, String> p2 = new HashMap<>();
        HashMap<String, String> p3 = new HashMap<>();
        HashMap<String, String> p4 = new HashMap<>();
        HashMap<String, String> p5 = new HashMap<>();
        HashMap<String, String> p6 = new HashMap<>();
        HashMap<String, String> p7 = new HashMap<>();
        HashMap<String, String> e12 =  new HashMap<>();
        HashMap<String, String> e23 =  new HashMap<>();
        HashMap<String, String> e34 =  new HashMap<>();
        HashMap<String, String> e41 =  new HashMap<>();
        HashMap<String, String> e13 =  new HashMap<>();
        HashMap<String, String> e24 =  new HashMap<>();
        HashMap<String, String> e45 =  new HashMap<>();
        HashMap<String, String> e56 =  new HashMap<>();
        HashMap<String, String> e67 =  new HashMap<>();

        p1.put(NAME, "name1");
        p1.put("RANDOM", "a string");
        for(Map.Entry<String, String> e : p1.entrySet()){
            logger.info(e.getKey() + ": " + e.getValue());
        }
        p2.put(NAME, "name2");
        p3.put(NAME, "name3");
        p4.put(NAME, "name4");
        p5.put(NAME, "name5");
        p6.put(NAME, "name6");
        p7.put(NAME, "name7");

        e12.put(LABEL, "different label");
        e12.put(NAME, "it has a name");
        e12.put("another one", "bite the dust");
        e23.put(LABEL, "linked to");
        e34.put(LABEL, "linked to");
        e41.put(LABEL, "linked to");
        e13.put(LABEL, "linked to");
        e24.put(LABEL, "linked to");
        e45.put(LABEL, "linked to");
        e56.put(LABEL, "linked to");
        e67.put(LABEL, "linked to");



        addTwoVertexes( 1, p1,  2, p2, e12);
        addTwoVertexes( 2, p2, 3, p3, e23);
        addTwoVertexes(3, p3,  4, p4, e34);
        addTwoVertexes(4, p4, 1, p1, e41);
        addTwoVertexes(1, p1,  3, p3, e13);
        addTwoVertexes( 2, p2,  4, p4, e24);
        addTwoVertexes( 4, p4,  5, p5, e45);
        addTwoVertexes( 5, p5,  6, p6, e56);
        addTwoVertexes( 6, p6,  7, p7, e67);
    }


}
