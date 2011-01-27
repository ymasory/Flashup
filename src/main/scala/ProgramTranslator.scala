package com.yuvimasory.flashcards

import java.io.{File, PrintWriter}

import StringUtils.LF

private[flashcards] abstract class ProgramTranslator extends FlashcardTranslator {

  val Delimit = "\t"
  val BR = "<br>"
  val PreOpen = "<pre>"
  val PreClose = "</pre>"

  override def translate(flashDoc: Document, sides: Pages.Value, outFile: File): Unit = {
    require(sides == Pages.All)
    val out = new PrintWriter(outFile)
    try {
      (0 until flashDoc.cards.length) foreach { i =>
        val card = flashDoc.cards(i)
        out print generateFront(card.front)
        out print Delimit
        out print (generateBack(card.back))
        out print LF
      }
    }
    finally {
      out flush()
      out close()
    }
  }

  def generateFront(front: Front): String = {
    val builder = new StringBuilder

    for (i <- 0 until front.frontEls.length) {
      front.frontEls(i) match {
        case FrontElement(Stretch(spans)) => {
          spans foreach {builder append generateHtmlSpan(_)}
          if (i < front.frontEls.length - 1) builder append (BR + BR)
        }
      }
    }

    builder toString
  }

  def generateBack(back: Back): String = {
    val builder = new StringBuilder

    for (i <- 0 until back.backEls.length) {
      back.backEls(i) match {
        case Line(stretch) =>
          stretch.spans foreach {builder append generateHtmlSpan(_)}
        case CodeBlock(lines) => {
          builder append PreOpen
          for (j <- 0 until lines.length) {
            builder append lines(j).extractText
            if (j < lines.length - 1) builder append BR
          }
          builder append PreClose
        }
        case LatexBlock(lines) => builder append handleLatex(lines.foldLeft("")(_ + _.extractText), true)
      }
      if (i < back.backEls.length - 1) builder append BR
    }

    builder toString
  }

  def generateHtmlSpan(span: Span) = {
    span match {
      case Span(text, dec) => {
        dec match {
          case Plain  => text
          case Mono   => "<tt>" + text + "</tt>"
          case Italic => "<em>" + text + "</em>"
          case Bold   => "<strong>" + text + "</strong>"
          case Latex  => handleLatex(text, false)
        }
      }
    }
  }

  def handleLatex(text: String, isBlock: Boolean): String
}

private[flashcards] object AnkiTranslator extends ProgramTranslator() {
  override def handleLatex(text: String, isBlock: Boolean) = "[latex]" + text + "[/latex]"
}
private[flashcards] object MnemosyneTranslator extends ProgramTranslator() {
  override def handleLatex(text: String, isBlock: Boolean) = 
    if (isBlock) "<$>" + text + "</$>"
    else "<$$>" + text + "</$$>"
}
