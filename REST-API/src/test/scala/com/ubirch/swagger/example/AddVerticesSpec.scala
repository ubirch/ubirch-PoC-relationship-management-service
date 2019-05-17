package com.ubirch.swagger.example

import com.ubirch.swagger.example.Util.extractValue
import com.ubirch.swagger.example.structure.VertexStructDb
import gremlin.scala._
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.{DateTime, DateTimeZone}
import org.scalatest.{FeatureSpec, Matchers}
import org.slf4j.{Logger, LoggerFactory}


class AddVerticesSpec extends FeatureSpec with Matchers {

  val gc: GremlinConnector = new GremlinConnector
  private val dateTimeFormat = ISODateTimeFormat.dateTime()

  val Number: Key[String] = Key[String]("number")
  val Name: Key[String] = Key[String]("name")
  val Created: Key[String] = Key[String]("created")
  val IdAssigned: Key[String] = Key[String]("IdAssigned")

  def log: Logger = LoggerFactory.getLogger(this.getClass)

  def deleteDatabase(): Unit = {
    gc.g.V().drop().iterate()
  }

  feature("add vertices") {
    scenario("add two unlinked vertex") {
      // clean database
      deleteDatabase()

      // prepare
      val id1 = 1
      val id2 = 2

      val now1 = DateTime.now(DateTimeZone.UTC)
      val p1: List[KeyValue[String]] = List(
        new KeyValue[String](Number, "5"),
        new KeyValue[String](Name, "aName1"),
        new KeyValue[String](Created, dateTimeFormat.print(now1))
      )
      val now2 = DateTime.now(DateTimeZone.UTC)
      val p2: List[KeyValue[String]] = List(
        new KeyValue[String](Number, "6"),
        new KeyValue[String](Name, "aName2"),
        new KeyValue[String](Created, dateTimeFormat.print(now2))
      )
      val pE: List[KeyValue[String]] = List(
        new KeyValue[String](Name, "edge")
      )

      //commit
      val t0 = System.nanoTime()

      AddVertices.addTwoVertices(id1.toString, p1, id2.toString, p2, pE)

      // analyse
      val v1Reconstructed = new VertexStructDb(id1.toString, gc.g)
      val v2Reconstructed = new VertexStructDb(id2.toString, gc.g)

      val response1 = v1Reconstructed.getPropertiesMap
      val idGottenBack1 = extractValue[String](response1, IdAssigned.name)
      val nameGottenBack1 = extractValue[String](response1, Name.name)
      val createdGottenBack1 = extractValue[String](response1, Created.name)
      val numberGottenBack1 = extractValue[String](response1, Number.name)
      val propertiesReceived1 = List(
      new KeyValue[String](Number, numberGottenBack1),
      new KeyValue[String](Name, nameGottenBack1),
      new KeyValue[String](Created, createdGottenBack1),
      )

      val response2 = v2Reconstructed.getPropertiesMap
      val idGottenBack2 = extractValue[String](response2, IdAssigned.name)
      val nameGottenBack2 = extractValue[String](response2, Name.name)
      val createdGottenBack2 = extractValue[String](response2, Created.name)
      val numberGottenBack2 = extractValue[String](response2, Number.name)
      val propertiesReceived2 = List(
      new KeyValue[String](Number, numberGottenBack2),
      new KeyValue[String](Name, nameGottenBack2),
      new KeyValue[String](Created, createdGottenBack2),
      )

      propertiesReceived1 shouldBe p1
      propertiesReceived2 shouldBe p2
      id1.toString shouldBe idGottenBack1
      id2.toString shouldBe idGottenBack2
      val t1 = System.nanoTime()
      log.info("time elapsed= " + (t1/1000000 - t0/1000000).toString + "ms")



    }
  }


}
