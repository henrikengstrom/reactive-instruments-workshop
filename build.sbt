name := """reactive-instruments-workshop"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
	"com.typesafe.akka" %% "akka-actor" % "2.3.12",
	"com.typesafe.akka" %% "akka-cluster" % "2.3.12",
	"com.typesafe.akka" %% "akka-contrib" % "2.3.12",
	"com.typesafe.akka" %% "akka-http-core-experimental" % "1.0",
	"com.typesafe.akka" %% "akka-http-experimental" % "1.0",
	"com.typesafe.akka" %% "akka-http-xml-experimental" % "1.0",
	ws
)