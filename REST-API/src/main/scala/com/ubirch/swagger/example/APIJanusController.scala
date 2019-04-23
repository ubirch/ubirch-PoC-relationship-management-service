package com.ubirch.swagger.example

import com.ubirch.swagger.example.Structure.VertexStruct
import com.ubirch.swagger.example.Utils.Util
import org.json4s.JsonAST.JNothing
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json.NativeJsonSupport
import org.scalatra.swagger.{ResponseMessage, Swagger, SwaggerSupport}
import org.scalatra.{CorsSupport, MultiParams, ScalatraServlet}
import org.slf4j.{Logger, LoggerFactory}
import scala.collection.JavaConverters._

import scala.collection.immutable.HashMap
import scala.collection.mutable

class APIJanusController(implicit val swagger: Swagger) extends ScalatraServlet
  with NativeJsonSupport with SwaggerSupport with CorsSupport {

  def log : Logger = LoggerFactory.getLogger(this.getClass)
  def ut : Util = new Util

  // Allows CORS support to display the swagger UI when using the same network
  options("/*") {
    response.setHeader(
      "Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers")
    )
  }


  // Stops the APIJanusController from being abstract
  protected val applicationDescription = "The API working with JanusGraph, allows add / display of vertexes/edges"

  // Sets up automatic case class to JSON output serialization
  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  // Before every action runs, set the content type to be in JSON format.
  before() {
    contentType = formats("json")
  }


  val addToJanus =
    (apiOperation[addVertex]("addToJanusTwoVertexes")
      summary "Add two to JanusGraph"
      schemes "http" // Force swagger ui to use http instead of https, only need to say it once
      description "Still not implemented. Does not work right now as it should now support dynamic properties addition"
      parameters(
      pathParam[Int]("id1").description("id of the first vertex"),
      queryParam[Option[Map[String, String]]]("properties1").description("Properties of the second vertex"),
      pathParam[Int]("id2").description("id of the second vertex"),
      queryParam[Option[Map[String, String]]]("properties2").description("Properties of the second vertex"),
      queryParam[Option[Map[String, String]]]("propertiesEdge").description("Properties of the edge that link the two vertexes")
    )
      )

  post("/addVertexToJG/:id1/:id2", operation(addToJanus)) {
    log.info("***** coucou1")
    println(params.get("properties1"))
    var prop1: String = null
    params.get("properties1") match {
      case Some(stuff) => prop1 = stuff
      case None => prop1= ""
    }
    println("prop1: " + prop1)

    val jValue1 = parse(prop1)
    println("jvalue: " + jValue1.toString)
    var truc1: Map[String, String] = null
    if(jValue1 == JNothing){
      truc1 = Map.empty[String, String]
    } else {
      truc1 = jValue1.extract[Map[String, String]]
    }
    val hashmap1 = truc1
    println(truc1)



    var prop2: String = null
    params.get("properties2") match {
      case Some(stuff) => prop2 = stuff
      case None => prop2= ""
    }
    println("prop2: " + prop2)

    val jValue2 = parse(prop2)
    println("jvalue: " + jValue2.toString)
    var truc2: Map[String, String] = null
    if(jValue2 == JNothing){
      truc2 = Map.empty[String, String]
    } else {
      truc2 = jValue2.extract[Map[String, String]]
    }
    val hashmap2 = truc2
    println(truc2)




    var prop3: String = null
    params.get("propertiesEdge") match {
      case Some(stuff) => prop3 = stuff
      case None => prop3= ""
    }
    println("prop3: " + prop3)

    val jValue3 = parse(prop3)
    println("jvalue: " + jValue3.toString)
    var truc3: Map[String, String] = null
    if(jValue3 == JNothing){
      truc3 = Map.empty[String, String]
    } else {
      truc3 = jValue3.extract[Map[String, String]]
    }
    val hashmap3 = truc3
    println(truc3)



    AddVertexes.addTwoVertexes( params("id1").toInt, truc1.asJava, params("id2").toInt, truc2.asJava, truc3.asJava )

    /*    val truc = new CommunicationJanusgraph
        params.get("properties1") match{
          case Some(stuff) => AddVertexes.addTwoVertexes( params("id1").toInt, params.get("properties1").toMap(String, String), params("id2").toInt, params.get("properties2").get.toMap(String, String), params("propertiesEdge") )
          //case Some(stuff) => truc.addVertex(params.get("name1").get, params("id1").toInt, params.get("name2").get, params("id2").toInt)
          case None =>
        }*/
    "front end not implemented. To test it, check the corresponding test" // TODO : find a way to pass map as argument
  }


  val getVertexesJanusGraph =
    (apiOperation[List[VertexStruct]]("getVertexesJanusGraph")
      summary "Display informations about a Vertex"
      description "Display informations about a Vertex (ID and properties)." +
                  "Not providing an ID will display the entire database"
      parameter queryParam[Option[Int]]("id").description("Id of the vertex we're looking for")
      responseMessage ResponseMessage(404, "404: Can't find edge with the given ID")
      )

  get("/getVertexe", operation(getVertexesJanusGraph)) {
    val truc = new CommunicationJanusgraph
    log.info("coucou")
    params.get("id") match {
      case Some(id) =>{
        val vertex = GetVertexes.getVertexById(id.toInt);
        if(vertex == null){
          halt(404,  s"404: Can't find vertex with the ID: $id")
        } else{
          vertex.toJson
        }
      }
      case None =>
        val listVertexes = GetVertexes.getAllVertexes(100);
        ut.arrayVertexToJson(listVertexes)
    }
  }


  val getVertexesWithDepth =
    (apiOperation[List[vertexWithDepth]]("getVertexesWithDepth")
      summary "Get a vertex and the surrounding ones"
      description "see summary"
      parameter queryParam[Int]("id").description("Id of the vertex we're looking for")
      parameter queryParam[Int]("depth").description("Depth of what we're looking for")

      responseMessage ResponseMessage(404, "404: Can't find edge with the ID: idNumber")
      )


  get("/getVertexesDepth", operation(getVertexesWithDepth)) {
    val truc = new CommunicationJanusgraph
    log.info("coucou")


    val neighbors = GetVertexes.getVertexIdDepth(params.get("id").get.toInt, params.get("depth").get.toInt);
    if(neighbors == null){
      halt(404,  s"404: Can't find vertex with the provided ID")
    } else{
      ut.reformatNeighboor(neighbors)
    }


  }


}

case class properties(map: Map[String, String])
case class vertexWithDepth(distance: Array[Integer])
case class neighbor(neighborId: Integer)
case class addVertex(id1: Int, properties1: Map[String, String], id2: Integer, properties2: Map[String, String], propertiesEdge: Map[String, String])