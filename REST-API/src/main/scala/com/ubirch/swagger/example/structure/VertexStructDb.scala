package com.ubirch.swagger.example.structure

import java.util

import gremlin.scala.{Key, KeyValue, TraversalSource}
import org.apache.tinkerpop.gremlin.process.traversal.Bindings
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._

class VertexStructDb(val id: String, val g: TraversalSource) {

  def log: Logger = LoggerFactory.getLogger(this.getClass)

  val Id: Key[String] = Key[String]("IdAssigned")

  var vertex: Vertex = g.V.has(Id, id).headOption() match {
    case Some(x) => x
    case None => null
  } //getOrElse(null)

  def exist: Boolean = if (vertex == null) false else true

  def addVertex(properties: List[KeyValue[String]], label: String, b: Bindings): Unit = {
    if (exist) {
      throw new IllegalStateException("Vertex already exist in the database")
    } else {
      vertex = g.addV(b.of("label", label)).property(Id -> id).l().head //graph + (label, Id -> id)
      for (keyV <- properties) {
        g.V(vertex.id).property(keyV).iterate()
      }
    }
  }

  def getPropertiesMap: Map[Any, List[Any]] = {
    val res = g.V(vertex).valueMap.toList().head.asScala.toMap.asInstanceOf[Map[Any, util.ArrayList[Any]]]
    res map { x => x._1 -> x._2.asScala.toList }
  }


}

