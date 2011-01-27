package com.yuvimasory.flashcards

import org.scalatest.FunSuite

import FlashcardParser._
import FlashcardParser.ComponentParsers._

import StringUtils._

class LatexBlockParserTest extends FunSuite {

  test("`latexBlock` simple case") {
    val str =
"""
````
hello
````
""" substring(1)
    parseAll(latexBlock, str) match {
      case Success(
        LatexBlock(
          List(
            Line(
              Stretch(
                List(
                  Span(
                    hello, Plain)))))), _) => expect("hello") {hello}
      case _ => fail("PARSE ERROR")
    }
  }

  test("`latexBlock` preserves empty lines") {
    val str =
"""
````

hello

world

````
""" substring(1)
    parseAll(latexBlock, str) match {
      case Success(LatexBlock(List(one, two, three, four, five)), _) => 
      case _ => fail("PARSE ERROR")
    }
  }

  test("`latexBlock` doesn't overshoot") {
    val str =
"""
````
hello
world
````

````
goodbye
world
````
""" substring(1)
    parse(latexBlock, str) match {
      case Success(LatexBlock(List(one, two)), _) =>
      case _ => fail("PARSE ERROR")
    }
  }

  test("`latexBlock` insists tics are on their own line") {
    val str1 =
"""
````f
hello
````
""" substring(1)
    val str2 =
"""
````
hello
````f
""" substring(1)
    parseAll(latexBlock, str1) match {
      case NoSuccess(_, _) =>
      case _ => fail
    }
    parseAll(latexBlock, str2) match {
      case NoSuccess(_, _) =>
      case _ => fail
    }
  }
}
