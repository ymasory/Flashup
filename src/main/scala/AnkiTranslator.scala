package com.yuvimasory.flashcards

import java.io.{File, PrintWriter}

import StringUtils.LF

private[flashcards] object AnkiTranslator extends FlashcardTranslator {

  val Delimit = "\t"
  val BR = "<br>"

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

    front.frontEls foreach {
      case FrontElement(Stretch(spans)) => {
        spans foreach { span =>
          builder append generateHtmlSpan(span)
        }
        builder append BR //pointless if this is the last
      }
    }

    builder toString
  }

  def generateBack(back: Back): String = {
    val builder = new StringBuilder

    back.backEls foreach {
      case Line(stretch) => {
        stretch.spans foreach { span =>
          builder append generateHtmlSpan(span)
        }
        builder append BR
      }
      case CodeBlock(lines) => {
        lines foreach {
          case Line(stretch) => {
            stretch.spans foreach {
              case Span(text, _) => {
                builder append text
              }
            }
          }
        }
      }
    }

    builder toString
  }

  def generateHtmlSpan(span: Span) = {
    span match {
      case Span(text, dec) => {
        dec match {
          case Plain => text
          case Mono => "<tt>" + text + "</tt>"
          case Italic => "<em>" + text + "</em>"
          case Bold => "<strong>" + text + "</strong>"
        }
      }
    }
  }

}
