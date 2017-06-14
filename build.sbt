import sbt.Keys.crossScalaVersions
// import AssemblyKeys._ // put this at the top of the file

name := "yuima.util"

version := "1.0"

scalaVersion := "2.12.2"

crossScalaVersions := Seq("2.11.10")

scalacOptions += "-deprecation"

libraryDependencies += "jline" % "jline" % "2.14.4"

libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "2.16.0"

libraryDependencies += "org.scalatest" % "scalatest_2.12" % "3.0.1" % "test"

libraryDependencies += "org.apache.commons" % "commons-compress" % "1.14"

showSuccess := false

outputStrategy := Some(StdoutOutput)

lazy val util = project in file(".")

// seq(assemblySettings: _*)
