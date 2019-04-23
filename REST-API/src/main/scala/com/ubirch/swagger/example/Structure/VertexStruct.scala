package com.ubirch.swagger.example.Structure

import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}


case class VertexStruct(label: String, properties: Map[String, String]) {

  def log: Logger = LoggerFactory.getLogger(this.getClass)


  override def toString: String = {
    var s: String = s"Label: ${label}"
    for( (k,v) <- properties) s += s"\n$k: $v"
    s
  }

  def toJson: String ={

    var propertiesReformated: Map[String, String] = Map()
    for((key, value) <- this.properties) propertiesReformated += (key.toString -> value.toString.substring(1, value.length - 1))

    val json = ("label" -> this.label) ~ ("properties" -> propertiesReformated)
    compact(render(json))
  }

}