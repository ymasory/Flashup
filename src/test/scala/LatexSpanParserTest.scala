package com.yuvimasory.flashcards

import org.scalatest.FunSuite

import FlashcardParser._
import FlashcardParser.ComponentParsers._

import StringUtils._

class LatexSpanParserTest extends FunSuite {

  test("`latexSpan` simple case") {
    parseAll(latexSpan, "````hello world````") match {
      case Success(span, _) => expect(Span("hello world", Latex)) {span}
      case _ => fail("PARSE FAILED")
    }
  }

  test("`latexSpan` preserves whitespace") {
    parseAll(latexSpan, "````  hello world  ````") match {
      case Success(span, _) => expect(Span("  hello world  ", Latex)) {span}
      case _ => fail("PARSE FAILED")
    }
  }

  test("`latexSpan` stays on one line") {
    parse(latexSpan, "````hello\nworld````") match {
      case NoSuccess(_, _) =>
      case Success(_, _) => fail
    }
  }

  test("`latexSpan` accepts tick literals") {
    pending
    parse(latexSpan, """````hello \`world\`````""") match {
      case Success(span, _) => expect(Span("hello `world`", Latex)) {span}
      case _ => fail("PARSE FAILED")
    }
  }
}
