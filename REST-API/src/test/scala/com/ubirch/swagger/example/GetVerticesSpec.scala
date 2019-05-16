package com.ubirch.swagger.example

import org.scalatest.{FeatureSpec, Matchers}
import com.ubirch.swagger.example.AddVerticesSpec

class GetVerticesSpec extends FeatureSpec with Matchers {

  feature("get all vertices") {
    scenario("get all") {

      //prepare
      val stuff = new AddVerticesSpec

    }

    scenario("detDepth") {
      GetVertices.getVertexDepth("1", 3)
    }
  }
}
