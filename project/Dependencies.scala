import sbt._

import scala.collection.immutable.Seq

object Dependencies {
//  val commons = "commons-lang" % "commons-lang" % "2.6"

  val banana = Seq("banana","banana-rdf", "banana-sesame", "banana-jena" ).map("org.w3" %% _  % "0.9.0-SNAPSHOT" excludeAll (ExclusionRule(organization = "org.scala-stm")))

  /**
    * simple scala command line options parsing
    * @see https://github.com/scopt/scopt
    * @see http://repo1.maven.org/maven2/com/github/scopt/
    */
  val scopt = "com.github.scopt" %% "scopt" % "3.5.0"

  /**
    * @see http://akka.io/docs/
    */
  val akka_version = "2.4.16"
  val akka = "com.typesafe.akka" %% "akka-actor" % akka_version


  private val akka_http_version = "10.0.1"
  /**
    * @see http://doc.akka.io/docs/akka-http/current/scala.html
    */
  val akka_http = Seq(
    "com.typesafe.akka" %% "akka-http-core" % akka_http_version,
    "com.typesafe.akka" %% "akka-http" % akka_http_version,
    "com.typesafe.akka" %% "akka-http-testkit" % akka_http_version,
    "com.typesafe.akka" %% "akka-http-spray-json" % akka_http_version,
    "com.typesafe.akka" %% "akka-http-jackson" % akka_http_version,
    "com.typesafe.akka" %% "akka-http-xml" % akka_http_version
  )

  val akka_all = akka +: akka_http

  val all = banana ++ Seq(scopt,akka) ++ akka_all

}