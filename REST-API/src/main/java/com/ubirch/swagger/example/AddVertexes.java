package com.ubirch.swagger.example;

import com.tinkerpop.gremlin.java.GremlinPipeline;
import com.ubirch.swagger.example.StructDB.VertexStructDb;
import org.apache.tinkerpop.gremlin.process.traversal.Bindings;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;


public class AddVertexes {

    private static final String LABEL = "label";
    private static final String NAME = "name";
    private static final String ID = "id";
    private static final String OUT_V = "outV";
    private static final String IN_V = "inV";
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
    private static GraphTraversalSource g;
    private static Graph graph;
    private static Bindings b;
    private static GremlinServerConnector sCon;


    /**
     * Method used to add two vertex to the JanusGraph database
     *
     * @param id1 the (public) id of the first vertex
     * @param p1  a HashMap containing the properties and their values of the first vertex
     * @param id2 the (public) id of the second vertex
     * @param p2  a HashMap containing the properties and their values of the second vertex
     * @param pE  a HashMap containing the properties and their values of the edge
     * @return
     */
    public static String addTwoVertexes(int id1, Map<String, String> p1, int id2, Map<String, String> p2, Map<String, String> pE) {

        sCon = new GremlinServerConnector();
        graph = sCon.getGraph();
        g = sCon.getTraversal();
        b = sCon.getBindings();

        // tmp
        HashMap<String, String> properties1 = new HashMap<>(p1);
        HashMap<String, String> properties2 = new HashMap<>(p2);
        HashMap<String, String> propertiesE = new HashMap<>(pE);

        VertexStructDb v1 = new VertexStructDb(id1, g);
        VertexStructDb v2 = new VertexStructDb(id2, g);
        int howMany = howManyExist(v1, v2);
        logger.info("how many exist?: " + howMany);
        switch (howMany) {
            case 0:
                logger.info("0");
                noneExit(v1, v2, properties1, properties2, propertiesE);
                break;
            case 1:
                logger.info("1");
                oneExist(v1, properties1, v2, properties2, propertiesE);
                break;
            case 2:
                logger.info("2");
                bothExist(v1, v2, propertiesE);
                break;
            default:
                break;
        }

        sCon.closeConnection();

        return "OKI DOKI";
    }


    /**
     * Check how many vertex exist
     *
     * @param v1 the first vertex
     * @param v2 the second vertex
     * @return an integer between 0 and 2 indicating how many vertex exist
     */
    private static int howManyExist(VertexStructDb v1, VertexStructDb v2) {
        int i = 0;
        if (v1.exist()) {
            i++;
        }
        if (v2.exist()) {
            i++;
        }
        return i;
    }

    /**
     * Creates two vertexes and link them
     *
     * @param v1       first vertex
     * @param v2       second vertex
     * @param p1       properties of the first vertex
     * @param p2       properties of the second vertex
     * @param propEdge properties of the edge
     */
    private static void noneExit(VertexStructDb v1, VertexStructDb v2, HashMap<String, String> p1, HashMap<String, String> p2, HashMap<String, String> propEdge) {
        v1.addVertex(p1, g, b);
        v2.addVertex(p2, g, b);

        createEdge(v1, v2, propEdge);
    }

    /**
     * If one out of two vertex, creates the second one, and link them
     *
     * @param v1       first vertex
     * @param v2       second vertex
     * @param p1       properties of the first vertex
     * @param p2       properties of the second vertex
     * @param propEdge properties of the edge
     */
    private static void oneExist(VertexStructDb v1, HashMap<String, String> p1, VertexStructDb v2, HashMap<String, String> p2, HashMap<String, String> propEdge) {
        if (v1.exist()) {
            v2.addVertex(p2, g, b);
            createEdge(v1, v2, propEdge);
        } else {
            v1.addVertex(p1, g, b);
            createEdge(v1, v2, propEdge);
        }
    }

    /**
     * Verify if the two existing vertex are linked, and if not, link them together
     *
     * @param v1       first vertex
     * @param v2       second vertex
     * @param propEdge properties of the edge
     */
    private static void bothExist(VertexStructDb v1, VertexStructDb v2, HashMap<String, String> propEdge) {
        if (areVertexesLinked(v1, v2)) {
            logger.info("V1 and V2 are already linked");
        } else {
            createEdge(v1, v2, propEdge);
        }
    }

    /**
     * Creates an edge between two existing vertex
     *
     * @param vertexFrom Vertex from where the edge will be created
     * @param vertexTo   Vertex to where the edge will be created
     * @param properties Properties of the edge
     */
    private static void createEdge(VertexStructDb vertexFrom, VertexStructDb vertexTo, HashMap<String, String> properties) {
        String label = properties.get(LABEL);
        if (label == null) {
            label = "linked to";
        } else {
            properties.remove(LABEL);
        }
        g.V(b.of(OUT_V, vertexFrom.getVertex())).as("a").V(b.of(IN_V, vertexTo.getVertex())).addE(b.of(LABEL, label)).from("a").iterate(); // add the label

        // add the other properties
        for (HashMap.Entry<String, String> entry : properties.entrySet()) {
            GremlinPipeline pipe = new GremlinPipeline();
            pipe.start(g.V(vertexFrom.getVertex()).outE().as("e").inV().has(ID, vertexTo.getId()).select("e").property(entry.getKey(), b.of(entry.getKey(), entry.getValue())).next());
        }

    }

    /**
     * Verify if two vertex are linked together
     *
     * @param v1 first vertex
     * @param v2 second vertex
     * @return true if they''e already linked. false otherwise
     */
    private static boolean areVertexesLinked(VertexStructDb v1, VertexStructDb v2) {
        GraphTraversal oneWay = g.V(v1.getVertex()).outE().as("e").inV().has(ID, v2.getId()).select("e");
        GraphTraversal otherWay = g.V(v2.getVertex()).outE().as("e").inV().has(ID, v1.getId()).select("e");
        return !oneWay.toList().isEmpty() || !otherWay.toList().isEmpty();

    }


}