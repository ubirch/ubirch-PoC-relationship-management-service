package com.ubirch.swagger.example


import com.ubirch.swagger.example.Util.{extractValue, recompose}
import com.ubirch.swagger.example.structure.VertexStructDb
import gremlin.scala._
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.{DateTime, DateTimeZone}
import org.scalatest.{FeatureSpec, Matchers}
import org.slf4j.{Logger, LoggerFactory}


class AddVerticesSpec extends FeatureSpec with Matchers {

  implicit val gc: GremlinConnector = new GremlinConnector

  private val dateTimeFormat = ISODateTimeFormat.dateTime()

  val Number: Key[String] = Key[String]("number")
  val Name: Key[String] = Key[String]("name")
  val Created: Key[String] = Key[String]("created")
  val IdAssigned: Key[String] = Key[String]("IdAssigned")
  implicit val ordering: (KeyValue[String] => String) => Ordering[KeyValue[String]] = Ordering.by[KeyValue[String], String](_)

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

      // commit
      new AddVertices().addTwoVertices(id1.toString, p1, id2.toString, p2, pE)
      //AddVertices.addTwoVertices(id1.toString, p1, id2.toString, p2, pE)

      // analyse
      val v1Reconstructed = new VertexStructDb(id1.toString, gc.g)
      val v2Reconstructed = new VertexStructDb(id2.toString, gc.g)

      val arrayKeys = Array(IdAssigned, Name, Created, Number)

      val response1 = v1Reconstructed.getPropertiesMap
      val idGottenBack1 = extractValue[String](response1, IdAssigned.name)
      val propertiesReceived1 = recompose(response1, arrayKeys)

      val response2: Map[Any, List[Any]] = v2Reconstructed.getPropertiesMap
      val idGottenBack2 = extractValue[String](response2, IdAssigned.name)
      val propertiesReceived2 = recompose(response2, arrayKeys)

      // verify
      propertiesReceived1.sortBy(x => x.key.name) shouldBe p1.sortBy(x => x.key.name)
      propertiesReceived2.sortBy(x => x.key.name) shouldBe p2.sortBy(x => x.key.name)
      id1.toString shouldBe idGottenBack1
      id2.toString shouldBe idGottenBack2

    }
  }


}
