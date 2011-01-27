package com.yuvimasory.flashcards

import java.io.{BufferedReader, BufferedWriter, File, FileReader, FileWriter}

import scala.io.Source

import com.martiansoftware.jsap.{Option => MOption, _}
import com.martiansoftware.jsap.stringparsers._

import StringUtils._

object CLI {

  val ProgramName = "flashup"
  val FlashcardExtension = "flashup"

  val (backs, fronts, pdf, anki, mnemo, debug, input, all) =
    ("backs", "fronts", "pdf", "anki", "mnemo", "debug", "input", "all")
  lazy val jsap = {
    val jsap = new JSAP()
    val backsSwitch = new Switch(backs)
      .setShortFlag('b')
      .setLongFlag(backs)
    jsap registerParameter backsSwitch
    val frontsSwitch = new Switch(fronts)
      .setShortFlag('f')
      .setLongFlag(fronts)
    jsap registerParameter frontsSwitch
    val pdfSwitch = new Switch(pdf)
      .setLongFlag(pdf)
    jsap registerParameter pdfSwitch
    val ankiSwitch = new Switch(anki)
      .setLongFlag(anki)
    jsap registerParameter ankiSwitch
    val mnemoSwitch = new Switch(mnemo)
      .setLongFlag(mnemo)
    jsap registerParameter mnemoSwitch
    val debugSwitch = new Switch(debug)
      .setLongFlag(debug)
    jsap registerParameter debugSwitch
    val allSwitch = new Switch(all)
      .setLongFlag(all)
    jsap registerParameter allSwitch
    val inputOption = new UnflaggedOption(input)
      .setStringParser(FileStringParser.getParser().setMustBeFile(true).setMustExist(true))
      .setRequired(true).setGreedy(true)
    jsap registerParameter inputOption
      
    jsap
  }
  
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
    builder append ("Options:" + LF)
    builder append ("  -f/--fronts - only generate the fronts of the flashcards (Optional)" + LF)
    builder append ("  -b/--backs  - only generate the backs of the flashcards (Optional)" + LF)
    builder append ("  --pdf       - output to PDF format" + LF)
    builder append ("  --anki      - output to format importable by Anki" + LF)
    builder append ("  --mnemo     - output to format importable by Mnemosyne" + LF)
    builder append ("  --debug     - output to debugging format" + LF)
    builder append ("  --all       - generate all formats except debugging" + LF)
    builder append ("Only pdf mode respects the front/back flags.")
    builder append LF
    builder append ("Examples:" + LF)
    builder append ("  java -jar " + ProgramName + ".jar --pdf path/to/input." + FlashcardExtension)
    builder append LF
    
    builder toString
  }

  def main(args: Array[String]) {

    parseArgs(args) match {
      case Nil => exit(1)
      case fileReqs if (fileReqs.contains(Nil)) => exit(1)
      case fileReqs => {
        fileReqs.map {
          case reqs => {
            val inFile = reqs(0).inFile
            val res = FlashcardParser.parseDoc(inFile)
            res match {
              case Some(doc) => {
                reqs.map {
                  case Request(outType, pages, inFile, outFile) => {
                    try {
                      val translator = OutType.outputMap(outType)
                      translator.translate(doc, pages, outFile)
                    }
                    catch {
                      case e: Exception => e.printStackTrace()
                    }
                  }
                }
              }
              case None => {
                Console.err println ("Could not parse: " + inFile.getAbsolutePath)
                exit(1)
              }
            }
          }
        }
      }
    }
  }

  private def makeRequest(inFile: File, naiveOutFile: File, sides: Pages.Value, outType: OutType.Value): Request = {
    val outFile = naiveOutFile match {
      case f: File => f
      case _ => {
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
            case Pages.All => ""
          }
        }

        val newName = tmp3 + OutType.extensionMap(outType)
        new File(newName)
      }
    }

    Request(outType, sides, inFile, outFile)
  }

  private[flashcards] def parseArgs(args: Array[String]): List[List[Request]] = {
    val config = jsap.parse(args)
    config.success match {
      case true => {
        val naiveOutFile = null
        val inFiles = config.getFileArray(input)

        List.empty ++ inFiles.map {

          case inFile => {

            //there has got to be a way to eliminate this tedious duplication
            if (config getBoolean(all)) {
              List (
                makeRequest(inFile, naiveOutFile, Pages.Backs, OutType.Pdf),
                makeRequest(inFile, naiveOutFile, Pages.Fronts, OutType.Pdf),
                makeRequest(inFile, naiveOutFile, Pages.All, OutType.Pdf),
                makeRequest(inFile, naiveOutFile, Pages.All, OutType.Anki),
                makeRequest(inFile, naiveOutFile, Pages.All, OutType.Mnemo)
              )
            }
            else if (config getBoolean(pdf)) {
              if (config getBoolean(backs))
                List(makeRequest(inFile, naiveOutFile, Pages.Backs, OutType.Pdf))
              else if (config getBoolean(fronts))
                List(makeRequest(inFile, naiveOutFile, Pages.Fronts, OutType.Pdf))
              else
                List(makeRequest(inFile, naiveOutFile, Pages.All, OutType.Pdf))
            }
            else if (config getBoolean(debug))
              List(makeRequest(inFile, naiveOutFile, Pages.All, OutType.Debug))
            else if (config getBoolean(anki))
              List(makeRequest(inFile, naiveOutFile, Pages.All, OutType.Anki))
            else if (config getBoolean(mnemo))
              List(makeRequest(inFile, naiveOutFile, Pages.All, OutType.Mnemo))
            else {
              Console.err println "unexpected CLI case"
              Nil
            }
          }
        }
      }
      case false => {
        Console.err println usage(config)
        Nil
      }
    }
  }
}

case class Request(outType: OutType.Value, sides: Pages.Value, inFile: File, outFile: File)

object OutType extends Enumeration {
  val Pdf, Debug, Anki, Mnemo, All = Value
  val outputMap = Map(
    Pdf -> PdfTranslator,
    Debug -> TxtTranslator,
    Anki -> AnkiTranslator,
    Mnemo -> MnemosyneTranslator
  )
  val extensionMap = Map(
    Pdf -> ".pdf",
    Debug -> ".debug",
    Anki -> "-anki.txt",
    Mnemo -> "-mnemosyne.txt"
  )
}

object Pages extends Enumeration {
  val Fronts, Backs, All = Value
}
