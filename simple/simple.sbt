import AssemblyKeys._

name := "Simple-Project"

version := "1.0"

scalaVersion := "2.10.6"

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.1.0"

libraryDependencies += "org.apache.spark" %% "spark-streaming" % "2.1.0"

libraryDependencies += "org.apache.spark" % "spark-streaming-kafka_2.10" % "1.6.3"

libraryDependencies += "org.apache.spark" % "spark-mllib_2.10" % "2.1.0"

libraryDependencies += "org.apache.hadoop" % "hadoop-client" % "2.7.3"

libraryDependencies += "redis.clients" % "jedis" % "2.9.0"

libraryDependencies += "org.apache.commons" % "commons-pool2" % "2.2"

libraryDependencies += "net.sf.json-lib" % "json-lib" % "2.4" from "http://repo1.maven.org/maven2/net/sf/json-lib/json-lib/2.4/json-lib-2.4-jdk15.jar"

retrieveManaged := true

assemblySettings

mergeStrategy in assembly := {
   case m if m.toLowerCase.endsWith("manifest.mf")          => MergeStrategy.discard
   case m if m.toLowerCase.matches("meta-inf.*\\.sf$")      => MergeStrategy.discard
   case "log4j.properties"                                  => MergeStrategy.discard
   case m if m.toLowerCase.startsWith("meta-inf/services/") => MergeStrategy.filterDistinctLines
   case "reference.conf"                                    => MergeStrategy.concat
   case _                                                   => MergeStrategy.first
}

