package com.yuvimasory.flashcards

import java.io._

import StringUtils.LF

class Tex2Im(fontSize: Int, texCode: String) {

  val FileName = "temp"
  val Dvi = ".dvi"
  val Eps = ".eps"
  val Tex = ".tex"

  lazy val texDoc = {
    val builder = new StringBuilder()
    builder append "\\documentclass[" + fontSize.toString + "pt]{article}" + LF
    builder append "\\usepackage{color}" + LF
    builder append "\\usepackage{amsmath}" + LF
    builder append "\\usepackage{amssymb}" + LF
    builder append "\\usepackage[dvips]{graphicx}" + LF
    builder append "\\pagestyle{empty}" + LF
    builder append "\\pagecolor{white}" + LF
    builder append "\\begin{document}" + LF
    builder append "\\color{black}" + LF

    builder append LF
    builder append "$" + texCode + "$" + LF
    builder append LF
 
    builder append "\\end{document}" + LF
    builder.toString
  }


  def makeImage() {

    val out = new BufferedWriter(new FileWriter(FileName + Tex))
    out.write(texDoc)
    out.close()

    Runtime.getRuntime.exec(("latex -interaction=batchmode " + FileName + Tex).split(" "))
    def toEps() = Runtime.getRuntime.exec(("dvips -o " + FileName + Eps + " -E " + FileName + Dvi).split(" "))
    toEps(); toEps();
  }
}

object Tex2Im {

  def main(args: Array[String]) {
    val maker = new Tex2Im(12, """x \in X \forall z \in \mathbb{N}""")
    maker.makeImage()
  }
}
