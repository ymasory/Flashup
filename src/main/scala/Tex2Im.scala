package com.yuvimasory.flashcards

import java.io._

import com.itextpdf.text.Image

import StringUtils.LF

class Tex2Im(fontSize: Int, texCode: String) {

  val FileName = "temp"
  val DviFileName = FileName + ".dvi"
  val EpsFileName = FileName + ".eps"
  val TexFileName = FileName + ".tex"

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


  def makeImage(): Image = {
    val out = new BufferedWriter(new FileWriter(TexFileName))
    out.write(texDoc)
    out.close()

    Runtime.getRuntime.exec(("latex -interaction=batchmode " + TexFileName).split(" ")).waitFor()
    def toEps() =
      Runtime.getRuntime.exec(("dvips -o " + EpsFileName + " -E " + DviFileName).split(" ")).waitFor()
    toEps(); toEps();

    Image.getInstance(EpsFileName) //EPS unsupported exception
  }
}

object Tex2Im {

  def main(args: Array[String]) {
    val maker = new Tex2Im(12, """x \in X \forall z \in \mathbb{N}""")
    maker.makeImage()
  }
}
