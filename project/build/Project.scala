import sbt._

class Project(info: ProjectInfo) extends DefaultProject(info) with ProguardProject with Exec with extract.BasicSelfExtractingProject {
  
  //project name
  override val artifactID = "flashup"

  //managed dependencies from built-in repositories
  val scalatest = "org.scalatest" % "scalatest" % "1.2"
  val jsap = "com.martiansoftware" % "jsap" % "2.1"
  
  //managed dependencies from external repositories
  // val pdfBox = "org.apache.pdfbox" % "pdfbox" % "1.2.1"
  // val apache = "http://repository.apache.org/snapshots/"


  //files to go in packaged jars
  val extraResources = "README.rst" +++ "LICENSE"
  override val mainResources = super.mainResources +++ extraResources

  //program entry point
  override def mainClass: Option[String] = Some("com.yuvimasory.flashcards.CLI")

  //proguard
  override def proguardOptions = List(
    "-keepclasseswithmembers public class * { public static void main(java.lang.String[]); }",
    "-dontoptimize",
    "-dontobfuscate",
    proguardKeepLimitedSerializability,
    proguardKeepAllScala,
    "-keep interface scala.ScalaObject"
  )
  override def proguardInJars = Path.fromFile(scalaLibraryJar) +++ super.proguardInJars


  //what the executable setup jar will do
//   override def installActions = "update" :: "compile" :: Nil

//   override def compileOptions = ExplainTypes
}
