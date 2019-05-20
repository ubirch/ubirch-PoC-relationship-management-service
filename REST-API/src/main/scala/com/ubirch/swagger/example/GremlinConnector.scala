package com.ubirch.swagger.example

import gremlin.scala.{ScalaGraph, TraversalSource, _}
import org.apache.tinkerpop.gremlin.driver.Cluster
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection
import org.apache.tinkerpop.gremlin.process.traversal.Bindings
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph

class GremlinConnector {

  val cluster: Cluster = Cluster.open("configuration/remote-objects.yaml")

  implicit val graph: ScalaGraph = EmptyGraph.instance.asScala.configure(_.withRemote(DriverRemoteConnection.using(cluster))) //.asScala.configure(_.withRemote("configuration/remote-graph.properties"))
  val g: TraversalSource = graph.traversal //.withRemote(DriverRemoteConnection.using(cluster, "g"))
  val b: Bindings = Bindings.instance

  def closeConnection(): Unit = {
    cluster.close()
  }

}
