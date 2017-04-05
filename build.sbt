// import AssemblyKeys._ // put this at the top of the file

name := "yuima.util"

version := "1.0"

scalaVersion := "2.11.8"

scalacOptions += "-deprecation"

libraryDependencies += "jline" % "jline" % "2.14.2"

libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "2.12.0"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test"

libraryDependencies += "org.apache.commons" % "commons-compress" % "1.12"

showSuccess := false

outputStrategy := Some(StdoutOutput)

lazy val util = project in file(".")

// seq(assemblySettings: _*)
