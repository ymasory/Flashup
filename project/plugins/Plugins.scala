import sbt._

class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
  val extract = "org.scala-tools.sbt" % "installer-plugin" % "0.3.0"
  val proguard = "org.scala-tools.sbt" % "sbt-proguard-plugin" % "0.0.4"
}
