package com.ubirch.swagger.example

import com.ubirch.swagger.example.structure.VertexStructDb
import gremlin.scala.{Key, KeyValue, ScalaGraph}

object AddVertices {

  private val label = "aLabel"
  private val gremlinConnector = new GremlinConnector
  private val ID: Key[String] = Key[String]("IdAssigned")

  def addTwoVertices(id1: String,
                     p1: List[KeyValue[String]],
                     id2: String,
                     p2: List[KeyValue[String]],
                     pE: List[KeyValue[String]]): String = {
    val v1: VertexStructDb = new VertexStructDb(id1, gremlinConnector.g)
    val v2: VertexStructDb = new VertexStructDb(id2, gremlinConnector.g)
    howMany(v1, v2) match {
      case 0 => noneExist(v1, v2, p1, p2, pE)
      case 1 => oneExist(v1, v2, p1, p2, pE)
      case 2 => twoExist(v1, v2, pE)
    }
    gremlinConnector.closeConnection()
    "OK BB"
  }

  private def noneExist(v1: VertexStructDb, v2: VertexStructDb, p1: List[KeyValue[String]], p2: List[KeyValue[String]], pE: List[KeyValue[String]]): Unit = {
    v1.addVertex(p1, label, gremlinConnector.b)
    v2.addVertex(p2, label, gremlinConnector.b)
    createEdge(v1, v2, pE)
  }

  private def oneExist(v1: VertexStructDb, v2: VertexStructDb, p1: List[KeyValue[String]], p2: List[KeyValue[String]], pE: List[KeyValue[String]]): Unit = {
    if(v1.exist) {
      v2.addVertex(p2, "aLabel", gremlinConnector.b)
      createEdge(v1, v2, pE)
    } else {
      v1.addVertex(p1, "aLabel", gremlinConnector.b)
      createEdge(v1, v2, pE)
    }
  }

  private def twoExist(v1: VertexStructDb, v2: VertexStructDb, pE: List[KeyValue[String]]): Unit = {
    if(!areVertexLinked(v1, v2)) createEdge(v1, v2, pE)
  }

  private def howMany(v1: VertexStructDb, v2: VertexStructDb): Int = {
    if (v1.exist) {
      if(v2.exist) 2 else 1 }
    else if(v2.exist) 1 else 0
  }

  private def createEdge(v1: VertexStructDb, v2: VertexStructDb, pE: List[KeyValue[String]]): Unit = {
    val edge = gremlinConnector.g.V(v1.vertex).as("a").V(v2.vertex).addE("aLabel").from(v1.vertex).toSet().head//v1.vertex --- "alabel" --> v2.vertex.element
    for(keyV <- pE) {
      gremlinConnector.g.E(edge).property(keyV).iterate()
    }
  }

  private def areVertexLinked(v1: VertexStructDb, v2: VertexStructDb): Boolean = {
    val oneWay = gremlinConnector.g.V(v1.vertex).outE().as("e").inV.has(ID, v2.vertex.id.toString).select("e").traversal
    val otherWay = gremlinConnector.g.V(v2.vertex).outE().as("e").inV.has(ID, v1.vertex.id.toString).select("e").traversal
    !oneWay.toList.isEmpty || !otherWay.toList.isEmpty
  }

}
