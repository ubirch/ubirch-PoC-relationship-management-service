package com.ubirch.swagger.example

object Util {
  def extractValue[T](map: Map[Any, java.util.ArrayList[Any]], nameValue: String): T = {
    map.get(nameValue) match {
      case Some(x) => x.get(0).asInstanceOf[T]
      case None => throw new IllegalArgumentException("response is null")
    }
  }
}
