package com.ubirch.swagger.example;

import com.tinkerpop.gremlin.java.GremlinPipeline;
import com.ubirch.swagger.example.structure.VertexStruct;
import org.apache.tinkerpop.gremlin.process.traversal.Bindings;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Predef;
import scala.collection.JavaConverters;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetVertexes {

    private static GraphTraversalSource g;
    private static Graph graph;
    private static Bindings b;
    private static GremlinServerConnector sCon;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());



    public static void connector(){
        sCon = new GremlinServerConnector();
        graph = sCon.getGraph();
        g = sCon.getTraversal();
        b = sCon.getBindings();
    }

    public static void disconnect(){
        sCon.closeConnection();

    }

    /**
     * return @limit number of random existing vertexes
     * @param limit how much vertexes should be returned
     * @return
     */
    public static VertexStruct[] getAllVertexes(int limit) {

        connector();

        GremlinPipeline pipe = new GremlinPipeline();
        pipe.start(g.V().has("id").limit(limit).valueMap());
        List l = pipe.toList();

        ArrayList<Map<String, String>> list = new ArrayList();

        for (Object o : l){
            Map<Object, Object> hmTmp = (HashMap) o;
            list.add(hmTmp.entrySet().stream().collect(Collectors.toMap(
                    entry -> (String) entry.getKey(),
                    entry ->  entry.getValue().toString()
            )));
        }

        // Create list of vertexes
        int i = 0;
        VertexStruct[] listVertexes = new VertexStruct[l.size()];
        for(Object s: list){
            // Intellij shows an error but it does compile normally
            listVertexes[i] = new com.ubirch.swagger.example.structure.VertexStruct(
                    "transaction",
                    JavaConverters.mapAsScalaMapConverter(list.get(i)).asScala().toMap(Predef.conforms())
                    );
            i++;
        }
        disconnect();
        return listVertexes;
    }

    /**
     * Return a vertex based on its (public) id
     * @param id the public id of the vertex
     * @return a VertexStructDb containing informations about the vertex
     */
    public static VertexStruct getVertexById(int id) {

        connector();

        GremlinPipeline pipe = new GremlinPipeline();
        pipe.start(g.V().has(T.label, "transaction").has("id", id).valueMap());
        List l = pipe.toList();

        if(l.isEmpty()){
            //sCon.closeConnection();
            return null;
        }

        logger.info("l.toString() = " + l.toString());

        Map<Object, Object> propertiesHM = (HashMap) l.get(0);
        Map<String, String> stuff = propertiesHM.entrySet().stream().collect(Collectors.toMap(
                entry -> (String) entry.getKey(),
                entry ->  entry.getValue().toString()));

        VertexStruct v = new com.ubirch.swagger.example.structure.VertexStruct(
                "transaction",
                JavaConverters.mapAsScalaMapConverter(stuff).asScala().toMap(Predef.conforms())
                );

        disconnect();

        return v;
    }

    /**
     * Get a vertex based on their (private) id
     * @param id the (private) id of the vertex
     * @return a VertexStructDb containing informations about the vertex
     */
    private static VertexStruct getVertexByPrivateId(int id) {



        GremlinPipeline pipe = new GremlinPipeline();
        pipe.start(g.V(id).valueMap());
        List l = pipe.toList();

        if(l.isEmpty()){
            //sCon.closeConnection();
            return null;
        }

        logger.info("l.toString() = " + l.toString());

        Map<Object, Object> propertiesHM = (HashMap) l.get(0);
        Map<String, String> stuff = propertiesHM.entrySet().stream().collect(Collectors.toMap(
                entry -> (String) entry.getKey(),
                entry ->  entry.getValue().toString()));

        VertexStruct v = new com.ubirch.swagger.example.structure.VertexStruct(
                "transaction",
                JavaConverters.mapAsScalaMapConverter(stuff).asScala().toMap(Predef.conforms())
        );


        return v;
    }

    /**
     * Get all the vertex linked to one up to a certain depth.
     * Does not take into account the direction of the edge
     * For example, for (A -> B -> C) and (A -> B <- C), A and C are always separated by a distance of 2
     * @param id the (public) id of the vertex
     * @param depth the depth of the link between the starting and ending poitn
     * @return
     */

    public static HashMap<Integer, ArrayList<VertexStruct>> getVertexDepth(int id, int depth){

        connector();
        GremlinPipeline pipe = new GremlinPipeline();
        pipe.start(g.V().has(T.label, "transaction").has("id", 1).id()); //change 1 -> ID
        List l = pipe.toList();
        logger.info("l: " + l.toString());
        // id passed in argument is the "assigned id", we're looking for the database id
        int idDeparture = Integer.parseInt(l.get(0).toString());
        HashMap<Integer, Integer> hashMap = getAllNeighboorsDistance(idDeparture, depth);
        logger.info("hashmap: " + hashMap.toString());
        HashMap<Integer, ArrayList<VertexStruct>> convertedHashMap = convertHashMap(hashMap);
        //logger.info("final hashmap = " + convertedHashMap.toString());

        disconnect();
        return convertedHashMap;
    }

    /**
     * Get a list of vertices assigned id that are neighbors of the vertex passed as an argument, up to the specified depth
     * @param id the vertex id
     * @param depth the depth desired (d = 0 => get just the original vertex)
     * @return a hashmap of the neighbours and their distance to the vertex passed as an argument
     */
    public static HashMap<Integer, ArrayList<Integer>> getVertexIdDepth(int id, int depth){

        connector();
        GremlinPipeline pipe = new GremlinPipeline();
        pipe.start(g.V().has(T.label, "transaction").has("id", id).id()); //change 1 -> ID
        List l = pipe.toList();
        logger.info("l: " + l.toString());
        // id passed in argument is the "assigned id", we're looking for the database id
        int idDeparture = Integer.parseInt(l.get(0).toString());
        HashMap<Integer, Integer> hashMap = getAllNeighboorsDistance(idDeparture, depth);
        HashMap<Integer, Integer> convertedHashMap = getIdNeighborsDistance(hashMap);
        HashMap<Integer, ArrayList<Integer>> finalHashMap = ExchangeKeyValue(convertedHashMap);
        logger.info("finalHashMap: "+ finalHashMap.toString());
        disconnect();
        return finalHashMap;
    }

    private static HashMap<Integer, Integer> getIdNeighborsDistance(HashMap<Integer, Integer> oldMap) {
        HashMap<Integer, Integer> hashMap = (HashMap<Integer, Integer>) oldMap.clone();
        for(Map.Entry<Integer, Integer> entry: oldMap.entrySet()){
            Integer distance = hashMap.remove(entry.getKey());
            Integer idAssigned = getIdAssignedByIdDb(entry.getKey());
            hashMap.put(idAssigned, distance);
        }
        return hashMap;
    }

    /**
     * Convert a hashmap of \<Int vertexId, Int Distance> in <Int Distance, Arraylist(Int Vertex)>
     * @param oldMap the map that will be converted
     * @return converted map
     */
    private static HashMap<Integer, ArrayList<Integer>> ExchangeKeyValue(HashMap<Integer, Integer> oldMap) {
        HashMap<Integer, ArrayList<Integer>> newHashMap = new HashMap<>();
        for(Map.Entry<Integer, Integer> entry: oldMap.entrySet()){
            if(!newHashMap.containsKey(entry.getValue())){
                ArrayList<Integer> listNeighborsAtSameDistance = new ArrayList<>();
                listNeighborsAtSameDistance.add(entry.getKey());
                newHashMap.put(entry.getValue(), listNeighborsAtSameDistance);
            } else {
                ArrayList<Integer> listNeighborsAtSameDistance = newHashMap.get(entry.getValue());
                listNeighborsAtSameDistance.add(entry.getKey());
                newHashMap.replace(entry.getValue(), listNeighborsAtSameDistance);
            }
        }
        return newHashMap;
    }


    private static HashMap<Integer, Integer> getAllNeighboorsDistance(int id, int depth) {
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        hashMap.put(id, 0);
        List<Integer> neighbors;
        for(int d = 1; d < depth + 1; d++) {
            HashMap<Integer, Integer> clone = (HashMap) hashMap.clone();
            for(Map.Entry<Integer, Integer> entry: clone.entrySet()) {
                // get the neighbors
                GremlinPipeline pipe = new GremlinPipeline();
                pipe.start(g.V(entry.getKey()).both());
                List<Vertex> tempList = pipe.toList();
                neighbors = new ArrayList<>();

                // add neighbors to list
                for(Vertex v : tempList){
                    neighbors.add((Integer.parseInt(v.id().toString())));
                }

                // add new neighbors that are not already in the map
                for(Integer neighbor : neighbors){
                    if(!hashMap.containsKey(neighbor)){
                        hashMap.put(neighbor, d);
                    }
                }
            }
        }
        return hashMap;
    }

    /**
     * Convert a HashMap<Integer, Integer> into a HashMap<Integer, ArrayList<VertexStructDb>>
     * get the associated vertex to the id in the original hashmap, link them with the distance to the original one
     * @param originalHashMap
     * @return newHashMap
     */
    private static HashMap<Integer, ArrayList<VertexStruct>> convertHashMap(HashMap<Integer, Integer> originalHashMap){
        HashMap<Integer, ArrayList<VertexStruct>> newHashMap = new HashMap<>();
        for(Map.Entry<Integer, Integer> entry: originalHashMap.entrySet()){
            if(!newHashMap.containsKey(entry.getValue())){
                ArrayList<VertexStruct> aVertexStruct = new ArrayList<>();
                aVertexStruct.add(getVertexByPrivateId(entry.getKey()));
                newHashMap.put(entry.getValue(), aVertexStruct);
            } else {
                ArrayList<VertexStruct> aVertexStruct = newHashMap.get(entry.getValue());
                aVertexStruct.add(getVertexByPrivateId(entry.getKey()));
                newHashMap.replace(entry.getValue(), aVertexStruct);
            }
        }
        logger.info("convertedHashMap = " + newHashMap.toString());
        return newHashMap;
    }

    /**
     * Get the manually assigned ID of a vertex from its database automatically assigned Id
     * @param idDb the database id
     * @return the manually assigned id
     */
    private static Integer getIdAssignedByIdDb(Integer idDb){
        GremlinPipeline pipe = new GremlinPipeline();
        pipe.start(g.V(idDb).values("id"));
        List<Integer> l = pipe.toList();
        return l.get(0);
    }

    public static void main(String[] args) {
        sCon = new GremlinServerConnector();
        graph = sCon.getGraph();
        g = sCon.getTraversal();
        b = sCon.getBindings();
        getVertexDepth(1, 5);
        sCon.closeConnection();
    }
}
