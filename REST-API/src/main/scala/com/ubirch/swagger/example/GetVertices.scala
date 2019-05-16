package com.ubirch.swagger.example

import com.ubirch.swagger.example.structure.VertexStruct
import gremlin.scala._
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConverters._

object GetVertices {

  private val gremlinConnector = new GremlinConnector
  private val ID = Key[String]("IdAssigned")

  def log: Logger = LoggerFactory.getLogger(this.getClass)

  /**
    * Returns a fixed amount of (randomly selected) vertices.
    * @param limit number of random vertices to be returned.
    * @return a list of VertexStruct
    */

  def getAllVertices(limit: Int = 1000000): List[VertexStruct] = {
    val listVertexes: List[Vertex] = gremlinConnector.g.V().limit(limit).l() // return scala list of vertex
    log.info(listVertexes.mkString)

    def toVertexStructList(lVertex: List[Vertex], accu: List[VertexStruct]): List[VertexStruct] = {
      lVertex match {
        case Nil => accu
        case x :: xs =>
          val vStruct = toVertexStruct(x)
          toVertexStructList(xs, vStruct :: accu)
      }
    }
    val accuInit: List[VertexStruct] = Nil
    toVertexStructList(listVertexes, accuInit)
  }

  /**
    * Get all the vertex linked to one up to a certain depth.
    * Does not take into account the direction of the edge
    * For example, for (A -> B -> C) and (A -> B <- C), A and C are always separated by a distance of 2
    * @param idAssigned    the (public) id of the vertex
    * @param depth the depth of the link between the starting and ending poitn
    * @return
    */
  def getVertexDepth(idAssigned: String, depth: Int): Map[Int, Iterable[Int]] = {

    def getAllNeighborsDistance(idDb: Int, depth: Int): Map[Int, Int] = {

      def lookForNeighbors(counter: Int, depthMax: Int, accu: Map[Int, Int]): Map[Int, Int] = {
        if (counter > depthMax) {
          accu
        } else {
          // get all neighbors
          val truc = accu map {
            x => neighboorsOfAVertex(x._1, accu, counter)
          }
          def concatenante(accu: Map[Int, Int], toConc: Iterable[Map[Int, Int]]): Map[Int, Int] = {
            toConc match {
              case Nil => accu
              case x :: xs => concatenante(x ++ accu, xs)
            }
          }
          lookForNeighbors(counter + 1, depthMax, concatenante(Map.empty[Int, Int], truc))
        }
      }

      def neighboorsOfAVertex(idDb: Int, mapExistingVertices: Map[Int, Int], distance: Int): Map[Int, Int] = {
        val vertices: List[Vertex] = gremlinConnector.g.V(idDb).both.toList()
        val listIdVertices: List[Int] = vertices map { x => x.id().toString.toInt }
        def addIfNotIn(mapExistingVertices: Map[Int, Int], listIdVertices: List[Int]) : Map[Int, Int]= {
          listIdVertices match {
            case Nil => mapExistingVertices
            case x :: xs =>
              if (mapExistingVertices.contains(x)) {
                addIfNotIn(mapExistingVertices, xs)
              } else addIfNotIn(mapExistingVertices + (x -> distance), xs)
          }
        }
        addIfNotIn(mapExistingVertices, listIdVertices)
      }

      lookForNeighbors(1, depth, Map(idDb -> 0))

    }

    val v: Vertex = gremlinConnector.g.V().has(ID, idAssigned).toList().head
    val idDeparture = v.id.toString
    val map: Map[Int, Int] = getAllNeighborsDistance(idDeparture.toInt, depth)
    map.groupBy(_._2).mapValues(_.keys)
  }



  /**
    * Return a vertex based on its (public) id.
    * @param idAssigned the public id of the vertex.
    * @return a VertexStructDb containing informations about the vertex.
    */
  def getVertexByPublicId(idAssigned: String): VertexStruct = {
    val kv = new KeyValue[String](ID, idAssigned)
    val v: Vertex = gremlinConnector.g.V.has(kv).toList().head
    if(v == null)  null
    toVertexStruct(v)
  }

  /**
    * Get a vertex based on their (private) id.
    * @param idDb the (private) id of the vertex.
    * @return a VertexStructDb containing informations about the vertex.
    */
  def getVertexByDbId(idDb: Int): VertexStruct = {
    val v: Vertex = gremlinConnector.g.V(idDb).toList().head
    if(v == null) null
    toVertexStruct(v)
  }

  /**
    * Covnert a vertex to a vertexStruct.
    * @param v The vertex that will be converted.
    * @return a VertexStruct of the vertex.
    */
  def toVertexStruct(v: Vertex): VertexStruct = {
    val label = gremlinConnector.g.V(v).label().toList().head
    val properties = gremlinConnector.g.V(v).valueMap.toList().head.asScala.toMap
    val propertiesMap = properties map { x => x._1.toString -> x._2.asInstanceOf[java.util.ArrayList[String]].get(0) }
    VertexStruct(label, propertiesMap)
  }
}

