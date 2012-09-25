name := "flashup"

organization := "com.yuvimasory"

version := "0.2.0"

/* eliminates vexating non-determinism of tests */
fork in Test := true

scalaVersion := "2.9.2"

crossScalaVersions := Seq(
  "2.9.1-1", "2.9.1", "2.9.0-1", "2.9.0",
  "2.8.1", "2.8.0"
)

libraryDependencies += "com.martiansoftware" % "jsap" % "2.1"

libraryDependencies <+= (scalaVersion) {
  case "2.8.0" => "org.scalatest" % "scalatest" % "1.3" % "test"
  case _       => "org.scalatest" %% "scalatest" % "1.8" % "test"
}

mainClass in (Compile, packageBin) := Some("com.yuvimasory.flashcards.CLI")

mainClass in (Compile, run) := Some("com.yuvimasory.flashcards.CLI")

scalacOptions ++= Seq("-deprecation", "-unchecked")

logLevel := Level.Info

traceLevel := 5

publishMavenStyle := true

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT")) Some(
    "snapshots" at nexus + "content/repositories/snapshots"
  )
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>http://ymasory.github.com/Flashup/</url>
  <licenses>
    <license>
      <name>AGPLv3</name>
      <url>http://www.gnu.org/licenses/agpl-3.0.html</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>https://github.com/ymasory/Flashup.git</url>
    <connection>scm:git:https://github.com/ymasory/Flashup.git</connection>
  </scm>
  <developers>
    <developer>
      <id>ymasory</id>
      <name>Yuvi Masory</name>
      <email>ymasory@gmail.com</email>
      <url>http://yuvimasory.com</url>
    </developer>
  </developers>
)
