package com.ubirch.swagger.example

import gremlin.scala.{Key, KeyValue}
import org.slf4j.{Logger, LoggerFactory}

object Util {

  def log: Logger = LoggerFactory.getLogger(this.getClass)

  /**
    * Get the value associated to a map<<String>, List<T>> based on the parameter.
    * @param map The map.
    * @param nameValue the name on which we want the value.
    * @tparam T Type of value we're looking for.
    * @return value of type T.
    */
  def extractValue[T](map: Map[Any, List[Any]], nameValue: String): T = {
    map.get(nameValue) match {
      case Some(x) => x.head.asInstanceOf[T]
      case None => throw new IllegalArgumentException("response is null")
    }
  }

  /**
    * Converts a Map<<String>, List<String>> into a List<KeyValues<String>>.
    * @param theMap the map containing the data.
    * @param keys array of <Key> contained in the map.
    * @return a List<KeyValues<String>>.
    */
  def recompose(theMap: Map[Any, List[Any]], keys: Array[Key[String]]): List[KeyValue[String]] = {
    val resWithId = theMap map {
      x =>
        val pos = keys.indexOf(Key[String](x._1.asInstanceOf[String]))
        keys(pos) -> KeyValue(keys(pos), extractValue(theMap, keys(pos).name))
    }
    (resWithId - Key[String]("IdAssigned")).values.toList
  }

}
