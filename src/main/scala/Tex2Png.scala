package com.yuvimasory.flashcards

import java.io._

import com.itextpdf.text.Image

import StringUtils.LF

class Tex2Png(fontSize: Int, texCode: String) {

  val FileName = "temp"
  val DviFileName = FileName + ".dvi"
  val TexFileName = FileName + ".tex"
  val PdfFileName = FileName + ".pdf"
  val PngFileName = FileName + ".png"

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


  private def makePng(): Option[Image] = {
    try {
      val out = new BufferedWriter(new FileWriter(TexFileName))
      out.write(texDoc)
      out.close()
      //latex -> dvi
      Runtime.getRuntime.exec(("latex -interaction=nonstopmode " + TexFileName).split(" ")).waitFor()
      //dvi -> png
      Runtime.getRuntime.exec(("dvipng -D 200 -T tight -o" + PngFileName + " " + DviFileName).split(" ")).waitFor()

      Some(Image.getInstance(PngFileName))
    }
    catch {
      case e => None
    }
  }

  
}

object Tex2Png {

  def main(args: Array[String]) {
    val maker = new Tex2Png(12, """x \in X \forall z \in \mathbb{N}""")
    maker.makePng()
  }
}
