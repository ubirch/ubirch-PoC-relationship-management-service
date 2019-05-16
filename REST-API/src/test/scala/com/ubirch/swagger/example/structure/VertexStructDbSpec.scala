package com.ubirch.swagger.example.structure

import com.ubirch.swagger.example.AddVertices.gremlinConnector
import com.ubirch.swagger.example.Util.extractValue
import gremlin.scala._
import org.apache.tinkerpop.gremlin.process.traversal.Bindings
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.{DateTime, DateTimeZone}
import org.scalatest.{FeatureSpec, Matchers}
import org.slf4j.{Logger, LoggerFactory}

class VertexStructDbSpec extends FeatureSpec with Matchers {

  implicit val graph: ScalaGraph = EmptyGraph.instance.asScala.configure(_.withRemote("configuration/remote-graph.properties"))
  val g: TraversalSource = graph.traversal //graph.traversal.withRemote("configuration/remote-graph.properties")
  private val b: Bindings = Bindings.instance

  private val dateTimeFormat = ISODateTimeFormat.dateTime()

  val Number: Key[String] = Key[String]("number")
  val Name: Key[String] = Key[String]("name")
  val Created: Key[String] = Key[String]("created")
  val test: Key[String] = Key[String]("truc")

  def log: Logger = LoggerFactory.getLogger(this.getClass)

  def deleteDatabase(): Unit = {
    g.V().drop().iterate()
  }


  feature("generate a vertex") {

    scenario("test") {
      deleteDatabase()

      val theId = 1

      val vSDb = new VertexStructDb(theId.toString, g)
      val now = DateTime.now(DateTimeZone.UTC)
      val properties: List[KeyValue[String]] = List(
        new KeyValue[String](Number, "5"),
        new KeyValue[String](Name, "aName"),
        new KeyValue[String](Created, dateTimeFormat.print(now))
      )
      vSDb.addVertex(properties, "aLabel", graph, b)

      val response = vSDb.getPropertiesMap//g.V(vSDb.getVertex.id).valueMap.toList().head.asScala.toMap.asInstanceOf[Map[Any, util.ArrayList[Any]]]
      val label = vSDb.vertex.label
      log.info(response.mkString)
      log.info(label)

      val idGottenBack = extractValue[String](response, "Id")
      val nameGottenBack = extractValue[String](response, "name")
      val createdGottenBack = extractValue[String](response, "created")
      val numberGottenBack = extractValue[String](response, "number")
      val propertiesReceived = List(
        new KeyValue[String](Number, numberGottenBack),
        new KeyValue[String](Name, nameGottenBack),
        new KeyValue[String](Created, createdGottenBack),
      )

      propertiesReceived shouldBe properties
      idGottenBack shouldBe theId
    }

/*    scenario("test") {
      // empty database
      deleteDatabase()

      //prepare
      val vSDb: VertexStructDb = new VertexStructDb(1, g)
      val now = DateTime.now(DateTimeZone.UTC)
      val exampleCC = Transaction(
        Id = 1,
        Name = "aName",
        Created = dateTimeFormat.print(now),
        Number = 10
      )

      vSDb.addVertex[Transaction](exampleCC, graph)

      val response = g.V(vSDb.getVertex.id).valueMap.toList().head.asScala.toMap.asInstanceOf[Map[Any, util.ArrayList[Any]]]



      val idGottenBack = extractValue[Int](response, "Id")
      val nameGottenBack = extractValue[String](response, "Name")
      val createdGottenBack = extractValue[String](response, "Created")
      val numberGottenBack = extractValue[Int](response, "Number")

      val transaction = Transaction(
        idGottenBack,
        nameGottenBack,
        createdGottenBack,
        numberGottenBack
      )

      transaction shouldBe exampleCC


    }


    scenario("test2") {
      deleteDatabase()

      val vStruct = new VertexStructDb(1, g)
      val vCC = Generic(1, 55, "coucou")
      vStruct.addVertex[Generic](vCC, graph)
      val response = g.V(vStruct.getVertex.id).valueMap.toList().head.asScala.asInstanceOf[Map[Any, util.ArrayList[Any]]]
      log.info(response.mkString)
    }*/
  }


}


