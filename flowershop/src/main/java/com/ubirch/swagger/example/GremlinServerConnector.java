package com.ubirch.swagger.example;

import org.apache.tinkerpop.gremlin.process.traversal.Bindings;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class GremlinServerConnector {

    private GraphTraversalSource g;
    private Graph graph;
    private Bindings b;

    private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());


    public GremlinServerConnector(){
        graph = EmptyGraph.instance();
        g = getTraversal(graph);
        b = Bindings.instance();
    }

    // initialisation and connection to server

    public void closeConnection() {
        try {
            g.close();
        } catch (Exception e) {
            logger.error("", e);
        }
        graph = null;
        g = null;
        b = null;
    }

    public GraphTraversalSource getTraversal(Graph graph){
        GraphTraversalSource g = null;
        try {
            g = graph.traversal().withRemote("configuration/remote-graph.properties");
        } catch (Exception e) {
            logger.error("", e);
        }
        return g;
    }

    public Graph getGraph(){
        return this.graph;
    }

    public GraphTraversalSource getTraversal(){
        return this.g;
    }

    public Bindings getBindings() {
        return b;
    }
}
