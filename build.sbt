ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.11.12"

organization := "com.jeffharwell"
name := "WarcParserBenchmark"
version := "0.0.1"

libraryDependencies += "com.jeffharwell" %% "warcparser" % "0.0.37"
libraryDependencies += "org.apache.commons" % "commons-math3" % "3.6.1"
libraryDependencies += "org.netpreserve" % "jwarc" % "0.20.0"
libraryDependencies += "org.jwat" % "jwat-warc" % "1.1.3"


lazy val root = (project in file("."))
  .settings(
    name := "WarcParserBenchmark"
  )
