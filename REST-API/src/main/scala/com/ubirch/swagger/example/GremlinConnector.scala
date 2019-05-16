package com.ubirch.swagger.example

import gremlin.scala.{ScalaGraph, TraversalSource}
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph
import gremlin.scala._
import org.apache.tinkerpop.gremlin.process.traversal.Bindings

class GremlinConnector {

  implicit val graph: ScalaGraph = EmptyGraph.instance.asScala.configure(_.withRemote("configuration/remote-graph.properties"))
  val g: TraversalSource = graph.traversal
  val b: Bindings = Bindings.instance

  def closeConnection(): Unit = {
    graph.close()
  }

}
