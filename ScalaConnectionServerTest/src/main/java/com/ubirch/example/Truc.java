package com.ubirch.example;

import com.tinkerpop.gremlin.java.GremlinPipeline;
import org.apache.tinkerpop.gremlin.process.traversal.Bindings;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;


public class Truc {

    private static final String LABEL = "label";
    private static final String NAME = "name";
    private static final String ID = "id";
    private static final String OUT_V = "outV";
    private static final String IN_V = "inV";
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
    private static GraphTraversalSource g;
    private static Graph graph;
    private static Bindings b;


    public static void main(String[] args) throws Exception {

        initializeGraphs();

        HashMap<String, String> p1 = new HashMap<>();
        HashMap<String, String> p2 = new HashMap<>();
        HashMap<String, String> p3 = new HashMap<>();
        HashMap<String, String> p4 = new HashMap<>();
        HashMap<String, String> e12 =  new HashMap<>();
        HashMap<String, String> e23 =  new HashMap<>();
        HashMap<String, String> e34 =  new HashMap<>();
        HashMap<String, String> e41 =  new HashMap<>();
        HashMap<String, String> e13 =  new HashMap<>();
        HashMap<String, String> e24 =  new HashMap<>();

        p1.put(NAME, "name1");
        p1.put("RANDOM", "a string");
        for(Map.Entry<String, String> e : p1.entrySet()){
            logger.info(e.getKey() + ": " + e.getValue());
        }
        p2.put(NAME, "name2");
        p3.put(NAME, "name3");
        p4.put(NAME, "name4");

        e12.put(LABEL, "different label");
        e12.put(NAME, "it has a name");
        e12.put("another one", "bite the dust");
        e23.put(LABEL, "linked to");
        e34.put(LABEL, "linked to");
        e41.put(LABEL, "linked to");
        e13.put(LABEL, "linked to");
        e24.put(LABEL, "linked to");



        addTwoVertexes( 1, p1,  2, p2, e12);
        addTwoVertexes( 2, p2, 3, p3, e23);
        addTwoVertexes(3, p3,  4, p4, e34);
        addTwoVertexes(4, p4, 1, p1, e41);
        addTwoVertexes(1, p1,  3, p3, e13);
        addTwoVertexes( 2, p2,  4, p4, e24);

        closeConnection();

    }

    // initialisation and connection to server
    public static void initializeGraphs(){
        graph = EmptyGraph.instance();
        g = getTraversal(graph);
        b = Bindings.instance();
    }

    public static GraphTraversalSource getTraversal(Graph graph){
        GraphTraversalSource g = null;
        try {
            g = graph.traversal().withRemote("configuration/remote-graph.properties");
        } catch (Exception e) {
            logger.error("", e);
        }
        return g;
    }

    public static String addTwoVertexes(int id1, HashMap<String, String> properties1, int id2, HashMap<String, String> properties2, HashMap<String, String> propertiesE) throws Exception {


        VertexStruct v1 = new VertexStruct(id1, g);
        VertexStruct v2 = new VertexStruct(id2, g);
        int howMany = howManyExist(v1, v2);
        logger.info("how many exist?: " + howMany);
        switch (howMany) {
            case 0:
                logger.info("0");
                noneExit(v1, v2, properties1, properties2, propertiesE);
                break;
            case 1:
                logger.info("1");
                oneExist(v1, properties1,v2, properties2, propertiesE);
                break;
            case 2:
                logger.info("2");
                bothExist(v1, v2, propertiesE);
                break;
            default: break;
        }

        logger.info("coucou");

        return "OKI DOKI";
    }


    public static void closeConnection() {
        try {
            g.close();
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    public static int howManyExist(VertexStruct v1, VertexStruct v2) {
        int i = 0;
        if (v1.exist()) {i++;}
        if (v2.exist()) {i++;}
        return i;
    }

    public static void noneExit(VertexStruct v1, VertexStruct v2, HashMap<String, String> p1, HashMap<String, String> p2, HashMap<String, String> propEdge) {
        v1.addVertex(p1, g, b);
        v2.addVertex(p2, g, b);

        createEdge(v1, v2, propEdge);
    }

    public static void oneExist(VertexStruct v1, HashMap<String, String> p1, VertexStruct v2, HashMap<String, String> p2, HashMap<String, String> propEdge) {
        assert (v1.exist() || v2.exist()): "should not happen";
        if(v1.exist()){
            v2.addVertex(p2, g, b);
            createEdge(v1, v2, propEdge);
        } else{
            v1.addVertex(p1, g, b);
            createEdge(v1, v2, propEdge);
        }
    }

    public static void bothExist(VertexStruct v1, VertexStruct v2, HashMap<String, String> propEdge) {
        if(areVertexesLinked(v1, v2)){
            logger.info("V1 and V2 are already linked");
        } else{
            createEdge(v1, v2, propEdge);
        }
    }


    public static void createEdge(VertexStruct vertexFrom, VertexStruct vertexTo, HashMap<String, String> properties) {
        String label = properties.get(LABEL);
        if (label == null){
            label = "linked to";
        } else{
            properties.remove(LABEL);
        }
        g.V(b.of(OUT_V, vertexFrom.getVertex())).as("a").V(b.of(IN_V, vertexTo.getVertex())).addE(b.of(LABEL, label)).from("a").iterate(); // add the label

        // add the other properties
        for(HashMap.Entry<String, String> entry : properties.entrySet()){
            GremlinPipeline pipe = new GremlinPipeline();
            pipe.start(g.V(vertexFrom.getVertex()).outE().as("e").inV().has(ID, vertexTo.getId()).select("e").property(entry.getKey(), b.of(entry.getKey(), entry.getValue())).next());
        }

    }

    public static boolean areVertexesLinked(VertexStruct v1, VertexStruct v2) {
        GraphTraversal oneWay =  g.V(v1.getVertex()).outE().as("e").inV().has(ID, v2.getId()).select("e");
        GraphTraversal otherWay =  g.V(v2.getVertex()).outE().as("e").inV().has(ID, v1.getId()).select("e");
        return !oneWay.toList().isEmpty() || !otherWay.toList().isEmpty();

    }

}