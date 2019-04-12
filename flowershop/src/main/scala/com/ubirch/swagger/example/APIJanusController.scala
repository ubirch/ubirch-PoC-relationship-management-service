package com.ubirch.swagger.example

import com.ubirch.swagger.example.Structure.VertexStruct
import com.ubirch.swagger.example.Utils.Util
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.json.NativeJsonSupport
import org.scalatra.swagger.{ResponseMessage, Swagger, SwaggerSupport}
import org.scalatra.{CorsSupport, ScalatraServlet}
import org.slf4j.{Logger, LoggerFactory}

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
    (apiOperation[VertexStruct]("addToJanusTwoVertexes")
      summary "Add two to JanusGraph"
      schemes "http" // Force swagger ui to use http instead of https, only need to say it once
      description "Still not implemented. Does not work right now as it should now support dynamic properties addition"
      parameters(
      pathParam[Int]("id1").description("id of the first vertex"),
      queryParam[Option[mutable.HashMap[String, String]]]("properties1").description("Properties of the second vertex"),
      pathParam[Int]("id2").description("id of the second vertex"),
      queryParam[Option[java.util.HashMap[String, String]]]("properties2").description("Properties of the second vertex"),
      queryParam[Option[java.util.HashMap[String, String]]]("propertiesEdge").description("Properties of the edge that link the two vertexes")
    )
      )

  post("/addVertexToJG/:id1/:id2", operation(addToJanus)) {
/*    log.info("***** coucou1")
    val truc = new CommunicationJanusgraph
    params.get("properties1") match{
      case Some(stuff) => AddVertexes.addTwoVertexes( params("id1").toInt, params.get("properties1").toMap(String, String), params("id2").toInt, params.get("properties2").get, params("propertiesEdge") )
      //case Some(stuff) => truc.addVertex(params.get("name1").get, params("id1").toInt, params.get("name2").get, params("id2").toInt)
      case None =>
    }*/
    "front end not implemented. To test it, check the corresponding test" // TODO : find a way to pass map as argument
  }


  val getVertexesJanusGraph =
    (apiOperation[List[VertexStruct]]("getVertexesJanusGraph")
      summary "Show the graph database"
      description "see summary"
      parameter queryParam[Option[Int]]("id").description("Id of the vertex we're looking for")
      responseMessage ResponseMessage(404, "404: Can't find edge with the ID: idNumber")
      )

  get("/getVertexesJG", operation(getVertexesJanusGraph)) {
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
    (apiOperation[List[VertexStruct]]("getVertexesWithDepth")
      summary "Get a vertex and the surrounding ones"
      description "see summary"
      parameter queryParam[Int]("id").description("Id of the vertex we're looking for")
      parameter queryParam[Int]("depth").description("Depth of what we're looking for")

      responseMessage ResponseMessage(404, "404: Can't find edge with the ID: idNumber")
      )


  get("/getVertexesDepth", operation(getVertexesWithDepth)) {
    val truc = new CommunicationJanusgraph
    log.info("coucou")


    val vertex = GetVertexes.getVertexDepth(params.get("id").get.toInt, params.get("depth").get.toInt);
    if(vertex == null){
      halt(404,  s"404: Can't find vertex with the provided ID")
    } else{
      vertex.toString
    }


  }

}
