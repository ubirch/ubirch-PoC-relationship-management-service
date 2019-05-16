package com.ubirch.swagger.example.structure

import java.util

import gremlin.scala.{Key, KeyValue, ScalaGraph, ScalaVertex, TraversalSource}
import org.apache.tinkerpop.gremlin.process.traversal.Bindings
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._

class VertexStructDb(val id: String, val g: TraversalSource) {

  def log: Logger = LoggerFactory.getLogger(this.getClass)

  val Id: Key[String] = Key[String]("IdAssigned")

  var vertex: ScalaVertex = g.V.has(Id, id).headOption().getOrElse(null)

  def exist: Boolean = if (vertex == null) false else true

  def addVertex(properties: List[KeyValue[String]], label: String, graph: ScalaGraph, b: Bindings): Unit = {
    if (exist) {
      throw new IllegalStateException("Vertex already exist in the database")
    } else {
      vertex = graph + (label, Id -> id)//g.addV(b.of("label", label)).property(Id -> id).l().head //graph + (label, Id -> id)
      for(keyV <- properties) {
        graph.traversal.V(vertex.id).property(keyV).iterate()
      }
    }
  }

  def getPropertiesMap: Map[Any, util.ArrayList[Any]] = {
    g.V(vertex.id).valueMap.toList().head.asScala.toMap.asInstanceOf[Map[Any, util.ArrayList[Any]]]
  }


}

