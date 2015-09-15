val akkaVersion = "2.4-SNAPSHOT"

name := "akka-test"
version := "1.0"
scalaVersion := "2.11.6"

crossPaths := false

lazy val referrals = Project(id = "akka-test", base = file("."))
  //.configs(MultiJvm, IntegrationTest)
  //.enablePlugins(SbtWeb)
  //.enablePlugins(PlayScala)

//SbtMultiJvm.multiJvmSettings
//Defaults.itSettings


resolvers += "Typesafe Snapshots" at "http://repo.akka.io/snapshots/"

libraryDependencies ++= Seq(
  "net.logstash.logback" % "logstash-logback-encoder" % "4.2",
  "ch.qos.logback" % "logback-classic" % "1.1.3",
  "ch.qos.logback" % "logback-core" % "1.1.3",
  "ch.qos.logback" % "logback-access" % "1.1.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",

  "com.typesafe.play" %% "play-json" % "2.4.0-RC3",

  "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence-query-experimental" % akkaVersion,
  
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.scalatestplus" %% "play" % "1.4.0-M2" % "test",
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion
)

// This will show the hidden compiler warnings from feature
scalacOptions ++= Seq("-feature") //, "-Xfatal-warnings")

// This makes sbt bootstrap faster by using cached dependency resolution
updateOptions := updateOptions.value.withCachedResolution(cachedResoluton = true)

// --- Configure some test options to show full stack traces and test duratinos
// ---
testOptions in referrals += Tests.Argument("-oD") //F")

