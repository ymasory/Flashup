package com.yuvimasory.flashcards

import java.io.{BufferedReader, BufferedWriter, File, FileReader, FileWriter}

import scala.io.Source

import com.martiansoftware.jsap.{Option => MOption, _}
import com.martiansoftware.jsap.stringparsers._

import StringUtils._

object CLI {

  val ProgramName = "flashup"
  val FlashcardExtension = "flashup"

  //short opts
  val shortOpts @ (fronts, backs, all) = ("fronts", "backs", "all")

  //long opts
  val longOpts @ (usi, usl, uss, a4l, a4s, txt) =
    ("usi", "usl", "uss", "a4l", "a4s", "txt")

  lazy val jsap = {
    val jsap = new JSAP()
    shortOpts.productIterator foreach { opt =>
      val switch = new Switch(opt toString)
        .setShortFlag(opt.toString.apply(0))
      jsap registerParameter switch
    }
    longOpts.productIterator foreach { opt =>
      val switch = new Switch(opt toString)
        .setLongFlag(opt.toString)
      jsap registerParameter switch
    }      
    jsap
  }
  
  val input = "input"
  val inputOption = new UnflaggedOption(input)
    .setStringParser(FileStringParser.getParser().setMustBeFile(true).setMustExist(true))
    .setRequired(true)
  jsap registerParameter inputOption


  def usage(config: JSAPResult) = {
    val builder = new java.lang.StringBuilder

    builder append LF
    val iter = config.getErrorMessageIterator()
    while (iter.hasNext()) {
      builder append ("Error: " + iter.next() + LF)
    }
    builder append LF
    builder append ("Usage: java -jar " + ProgramName + ".jar ") + LF
    builder append (jsap.getUsage + LF)
    builder append LF
    builder append ("Short Options:" + LF)
    builder append ("  -" + fronts(0)  + " - generate only the fronts of the flashcards (Default)" + LF)
    builder append ("  -" + backs(0) + " - generate only the backs of the flashcards" + LF)
    builder append ("  -" + all(0) + " - generate both fronts and backs" + LF)
    builder append LF
    builder append ("  Multiple side options can be used." + LF)
    builder append LF
    builder append ("Long Options:" + LF)
    builder append ("  --" + usi + " - output to Index Card (3in x 5in) PDF (Default)" + LF)
    builder append ("  --" + usl + " - output to US Letter (8.5in x 11in) PDF, arranged for long flip" + LF)
    builder append ("  --" + uss + " - output to US Letter (8.5in x 11in) PDF, arranged for short flip" + LF)
    builder append ("  --" + a4l + " - output to A4 PDF (210mm x 297mm) PDF, arranged for long flip" + LF)
    builder append ("  --" + a4s + " - output to A4 PDF (210mm x 297mm) PDF, arranged for short flip" + LF)
    builder append ("  --" + txt + " - output to text format (ignores -b/f, used for debugging)" + LF)
    builder append LF
    builder append ("  Multiple formats can be selected." + LF)
    builder append ("  If no output format is chosen, usi is used." + LF)
    builder append LF
    builder append ("Examples:" + LF)
    builder append ("  java -jar " + ProgramName + ".jar --" + usi + " path/to/input." + FlashcardExtension)
    builder append LF
    
    builder toString
  }

  def main(args: Array[String]) {
    parseArgs(args) match {
      case Some((outType, pages, inFile, outFile)) => {
        val res = FlashcardParser.parseDoc(inFile)
        res match {
          case Some(doc) => {
            try {
              val translator = OutType.outputMap(outType)
              translator.translate(doc, pages, outFile)
            }
            catch {
              case e: Exception => e.printStackTrace()
            }
          }
          case None => {
            Console.err println ("Could not parse: " + inFile.getAbsolutePath)
            exit(1)
          }
        }
      }
      case None => exit(1)
    }
  }

  private[flashcards] def parseArgs(args: Array[String]): Option[(OutType.Value, Pages.Value, File, File)] = {
    val config = jsap.parse(args)
    config.success match {
      case true => {
        var sides: Pages.Value = Pages.Both
        var outType: OutType.Value = OutType.USI
        val inFile = config.getFile(input)
        if (config getBoolean(backs))
          sides = Pages.Backs
        if (config getBoolean(fronts))
          sides = Pages.Fronts
        if (config getBoolean(usi))
          outType = OutType.USI
        if (config getBoolean(txt))
          outType = OutType.TXT

        val outFile = {
          val par = inFile.getParentFile
          val tmp1 = inFile.getName
          val tmp2 = {
            tmp1.split("""\.""") match {
              case a @ Array(_, _*) => a.head.mkString
              case _ => tmp1
            }
          }
          val tmp3 = tmp2 + {
            sides match {
              case Pages.Fronts => "-fronts"
              case Pages.Backs => "-backs"
              case Pages.Both => ""
            }
          }

          val newName = tmp3 + ".pdf"
          new File(newName)
        }

        Some(outType, sides, inFile, outFile)
      }
      case false => {
        Console.err println usage(config)
        None
      }
    }
  }
}
object OutType extends Enumeration {
  val USI, USS, USL, A4S, A4L, TXT = Value
  val outputMap = Map(
    USI -> PdfTranslator,
    USS -> PdfTranslator,
    USL -> PdfTranslator,
    A4S -> PdfTranslator,
    A4L -> PdfTranslator,
    TXT -> TxtTranslator
  )
}

object Pages extends Enumeration {
  val Fronts, Backs, Both = Value
}
