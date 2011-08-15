name := "flashup"

version := "0.2.0"

libraryDependencies ++= Seq (
  "org.scalatest" % "scalatest" % "1.2",
  "com.martiansoftware" % "jsap" % "2.1"
)

seq(ProguardPlugin.proguardSettings :_*)

proguardOptions ++= Seq (
    "-dontshrink -dontoptimize -dontobfuscate -dontpreverify -dontnote " +
    "-ignorewarnings",
    keepAllScala
)

mainClass in (Compile, packageBin) := Some("com.yuvimasory.flashcards.CLI")

mainClass in (Compile, run) := Some("com.yuvimasory.flashcards.CLI")

// artifactID = "flashup"
// extraResources = "README.rst" +++ "LICENSE"
// mainResources = super.mainResources +++ extraResources

