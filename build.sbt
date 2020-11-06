
// import AssemblyKeys._ // put this at the top of the file

name := "yuima.util"

version := "1.0"

scalaVersion := "2.13.2"

resolvers := {
  val localMaven = "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository"
  localMaven +: resolvers.value
}

libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.0"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.0" % "test"

libraryDependencies += "org.jline" % "jline" % "3.17.1"

libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "2.24.0"


libraryDependencies += "org.apache.commons" % "commons-compress" % "1.20"

showSuccess := false

outputStrategy := Some(StdoutOutput)

lazy val util = project in file(".")

// seq(assemblySettings: _*)
