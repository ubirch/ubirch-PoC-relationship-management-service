package com.ubirch.swagger.example.StructDB;

import com.tinkerpop.gremlin.java.GremlinPipeline;
import org.apache.tinkerpop.gremlin.process.traversal.Bindings;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;

public class VertexStructDb {

    private Vertex vertex;
    private int id;
    private boolean exist = false;

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());


    public VertexStructDb(int id, GraphTraversalSource g) {
        this.id = id;
        this.vertex = getVertexAssociatedToId(id, g);
        if (this.vertex != null) this.exist = true;
    }

    public int getId() {
        return id;
    }


    public boolean exist() {
        return exist;
    }


    // TODO: make this more versatile: replace ID with unique identifier
    private static Vertex getVertexAssociatedToId(int id, GraphTraversalSource g) {
        String ID = "id";
        Vertex truc = null;
        try {
            truc = g.V().hasLabel("benoit").has(ID, id).next();
        } catch (Exception osef) {
            logger.info("vertex does not exist");
            return null;
        }

        logger.info("The vertex with the label ID:" + Integer.toString(id) + " has the dabtabase id:" + truc.id().toString());
        return truc;
    }

    public void addVertex(HashMap<String, String> properties, GraphTraversalSource g, Bindings b) {
        if (exist) {
            throw new InstantiationError("Vertex already exist in the database");
        }
        this.vertex = g.addV(b.of("label", "benoit")).property("id", b.of("id", id)).next();

        for (Map.Entry<String, String> entry : properties.entrySet()) {
            GremlinPipeline pipe = new GremlinPipeline();
            pipe.start(g.V(this.vertex.id()).property(entry.getKey(), b.of(entry.getKey(), entry.getValue())).next());
        }

    }

    public Vertex getVertex() {
        return this.vertex;
    }

}
