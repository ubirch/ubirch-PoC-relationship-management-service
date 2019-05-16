val ScalatraVersion = "2.6.5"

organization := "ubirch"

name := "swagger-scalatra-REST-API"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.12.8"

resolvers += Classpaths.typesafeReleases
resolvers += Resolver.sonatypeRepo("releases")  // Or "snapshots"
resolvers += Resolver.sonatypeRepo("snapshots")  // Or "snapshots"

javaOptions ++= Seq(
  "-Xdebug",
  "-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
)

libraryDependencies ++= Seq(
  "org.scalatra" %% "scalatra" % ScalatraVersion,
  "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
  "org.scalactic" %% "scalactic" % "3.0.5",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "ch.qos.logback" % "logback-classic" % "1.2.3" % "runtime",
  "org.eclipse.jetty" % "jetty-webapp" % "9.4.15.v20190215" % "container",
  "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",
  "org.json4s"   %% "json4s-native" % "3.5.2",
  "org.json4s" %% "json4s-jackson" % "3.5.2",
  "org.scalatra" %% "scalatra-swagger"  % "2.6.5",
  "com.michaelpollmeier" %% "gremlin-scala" % "3.4.1.1",
  "org.janusgraph" % "janusgraph-core" % "0.3.1",
  "org.janusgraph" % "janusgraph-cql" % "0.3.1",
  "org.janusgraph" % "janusgraph-es" % "0.3.1",
  "org.apache.tinkerpop" % "gremlin-driver" % "3.3.3",
  "com.tinkerpop.gremlin" % "gremlin-java" % "2.6.0",
  "com.ubirch" % "event-log-kafka" % "1.2.3-SNAPSHOT"
)

enablePlugins(SbtTwirl)
enablePlugins(ScalatraPlugin)
