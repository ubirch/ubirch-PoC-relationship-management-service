package com.ubirch.swagger.example.util

import java.util

import com.ubirch.swagger.example.structure.VertexStruct
import org.apache.tinkerpop.gremlin.driver.{Result, ResultSet}
import org.apache.tinkerpop.gremlin.groovy.jsr223.GroovyTranslator
import org.apache.tinkerpop.gremlin.process.traversal.Bytecode
import org.json4s.{DefaultFormats, JsonAST}
import org.json4s.JsonDSL._
import org.json4s.jackson.Serialization
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._


object Util {

  def log: Logger = LoggerFactory.getLogger(this.getClass)

  def createVertex(label: String, listNames: List[String], listValues: List[String]): VertexStruct = {
    var map: Map[String, String] = Map()
    assert(listNames.length == listValues.length)
    for(i <- listNames.indices){
      map += listNames(i) -> listValues(i)
    }
    val vertex: VertexStruct = VertexStruct(label, map)
    vertex
  }

  def listToJson(listString: List[String]): String = {
    val str = listString.toString.substring(5, listString.toString().length - 1)
    str
  }

  def arrayVertexToJson(arrayVertexes: Array[VertexStruct]): String = {

    implicit def vertexes2JValue(v: VertexStruct): JsonAST.JObject = {
      ("label" -> v.label) ~ ("properties" -> v.properties)
    }
    val json = "list of vertexes" -> reformatArrayVertex(arrayVertexes).toList
    implicit val formats: DefaultFormats.type = org.json4s.DefaultFormats

    Serialization.write(json)
  }


  def reformatArrayVertex(arrayVertex: Array[VertexStruct]): Array[VertexStruct] = {
    val arrayVertexReformated: Array[VertexStruct] = new Array(arrayVertex.length)
    var i = 0
    for(v <- arrayVertex){
      val label = v.label
      val properties: Map[String, String] = v.properties
      var propertiesReformated: Map[String, String] = Map()
      for((key, value) <- properties) propertiesReformated += (key.toString -> value.toString.substring(1, value.length - 1))

      val vertexReformated: VertexStruct = VertexStruct(label, propertiesReformated)

      arrayVertexReformated(i) = vertexReformated
      i = i + 1
    }

    arrayVertexReformated.foreach(v => log.info(v.toString))
    arrayVertexReformated
  }

  /**
    * Convert a hashmap <Integer, ArrayList<Integer>> to json
    * @param hashMap the hashmap to be converted
    * @return json string of the map
    */
  def reformatNeighboor(hashMap: util.HashMap[Integer, util.ArrayList[Integer]]): String = {

    implicit val formats: DefaultFormats.type = org.json4s.DefaultFormats
    val scalaMap = hashMap.asScala

    Serialization.write(scalaMap)
  }


  /**
    * Convert a bytecode gremlin query in a String, allowing it to be communicated to CosmosDB
    * @param bytecode the bytecode
    * @return the string corresponding to the bytecode
    */
  def translator(bytecode: Bytecode): String = {
    val translator = GroovyTranslator.of("g")
    translator.translate(bytecode)
  }

  def getResultList(results: ResultSet): List[Result] = {
    val completableFutureResults = results.all
    completableFutureResults.get().asScala.toList
  }

}


