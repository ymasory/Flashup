package com.yuvimasory.flashcards

import java.io._

import StringUtils.LF

class Tex2Im(fontSize: Int, texCode: String, headers: List[String]) {

  val FileName = "temp"
  val Dvi = ".dvi"
  val Eps = ".eps"
  val Tex = ".tex"

  lazy val texDoc = {
    val builder = new StringBuilder()
    builder append "\\documentclass[" + fontSize.toString + "pt]{article}"
    builder append "\\usepackage{color}" + LF
    builder append "\\usepackage[dvips]{graphicx}" + LF
    builder append "\\pagestyle{empty}" + LF

    builder append LF
    headers foreach {h => builder append (h + LF)}
    builder append LF

    builder append "\\pagecolor{white}" + LF
    builder append "\\begin{document}" + LF
    builder append "{\\color{black}" + LF
    builder append "\\begin{eqnarray*}" + LF

    builder append LF
    builder append texCode + LF
    builder append LF

    builder append "\\end{eqnarray*}}" + LF
    builder append "\\end{document}" + LF

    builder.toString
  }


  def makeImage {

    val out = new BufferedWriter(new FileWriter(FileName + Tex))
    out.write(texDoc)
    out.close()

    Runtime.getRuntime.exec(("latex -interaction=batchmode " + FileName + Tex).split(" "))
    Runtime.getRuntime.exec(("dvips -o " + FileName + Eps + " -E " + FileName + Dvi).split(" "))
  }
}
