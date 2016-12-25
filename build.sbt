import com.typesafe.sbt.SbtScalariform.defaultScalariformSettings
import sbt.Keys._
import sbt._
//import com.sun.tools.classfile.Dependencies._
//import com.typesafe.sbt.pgp.PgpKeys

lazy val commonSettings = defaultScalariformSettings ++ Seq(
  organization := "read-write-web",
  name := "bcwm",
  version := "0.2-SNAPSHOT",
  scalaVersion := "2.12.1",
  javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
  description := "scala version of python cwm tool bases on banana-rdf",
  startYear := Some(2013),
  libraryDependencies ++= Dependencies.all,
  updateOptions := updateOptions.value.withCachedResolution(true) //to speed up dependency resolution
)


//OneJar: https://github.com/sbt/sbt-onejar
//seq(com.github.retronym.SbtOneJar.oneJarSettings: _*)


resolvers += "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"

resolvers += "Sonatype OSS Releases" at "http://oss.sonatype.org/content/repositories/releases/"

resolvers += "spray repo" at "http://repo.spray.io"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"



commonSettings
