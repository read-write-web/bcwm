resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.3.0")

/**
  * sbt-updates
  *
  * for easier dependency updates monitoring
  * @see https://github.com/rtimush/sbt-updates
  */
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.3.0")

//addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0-SNAPSHOT")
//
//addSbtPlugin("org.scala-sbt.plugins" % "sbt-onejar" % "0.8")

