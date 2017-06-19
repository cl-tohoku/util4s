// import AssemblyKeys._ // put this at the top of the file

name := "yuima.util"

version := "1.0"

scalaVersion := "2.11.11"

crossScalaVersions := Seq("2.11.11", "2.12.2")

scalacOptions += "-deprecation"

libraryDependencies += "org.jline" % "jline" % "3.3.0"

libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "2.16.0"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

libraryDependencies += "org.apache.commons" % "commons-compress" % "1.14"

showSuccess := false

outputStrategy := Some(StdoutOutput)

lazy val util = project in file(".")

// seq(assemblySettings: _*)
