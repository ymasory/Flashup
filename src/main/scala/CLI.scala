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
      case Some((outTypes, sides, inFile)) => {
        val res = FlashcardParser.parseDoc(inFile)
        res match {
          case Some(doc) => {
            try {
              for (outType <- outTypes) {
                for (side <- sides) {
                  val translator = OutType.outputMap(outType)
                  translator.translate(doc, side, null)
                }
              }
            }
            catch {
              case e: Exception => e.printStackTrace()
            }
          }
          case None => {
            Console.err println ("Could not parse: " + inFile)
            exit(1)
          }
        }
      }
      case None => exit(1)
    }
  }

  private def outputName(inFile: File, side: Pages.Value, outType: OutType.Value): String = {
    val inName = inFile getName
    val baseName = {
      inName.split("""\.""") match {
        case a @ Array(_, _*) => a.head.mkString
        case _ => inName
      }
    }
    ""
  }

  private[flashcards] def parseArgs(args: Array[String]): Option[(List[OutType.Value], List[Pages.Value], File)] = {
    val config = jsap.parse(args)
    config.success match {
      case true => {
        val sides = for (opt <- shortOpts.productIterator) yield {
          if (config getBoolean opt.toString)
            opt
        }
        val outTypes = for (opt <- longOpts.productIterator) yield {
          if (config getBoolean opt.toString)
            opt
        }

        Some(Nil, Nil, config getFile input)
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
