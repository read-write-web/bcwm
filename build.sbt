organization := "bcwm"

name := "bcwm"

version := "0.1-SNAPSHOT"

resolvers += "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"

resolvers += "Sonatype OSS Releases" at "http://oss.sonatype.org/content/repositories/releases/"

resolvers += "spray repo" at "http://repo.spray.io"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq("banana","banana-rdf", "banana-sesame", "banana-jena" )
        .map("org.w3" %% _  % "0.4" excludeAll (ExclusionRule(organization = "org.scala-stm")))

libraryDependencies += "com.github.scopt" %% "scopt" % "3.2.0"

libraryDependencies += "io.spray" % "spray-http" % "1.2.0"

libraryDependencies += "io.spray" % "spray-httpx" % "1.2.0"

libraryDependencies += "io.spray" % "spray-client" % "1.2.0"

libraryDependencies += "io.spray" % "spray-io" % "1.2.0"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.2.3"
