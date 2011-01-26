package com.yuvimasory.flashcards

import java.io.File

private[flashcards] trait FlashcardTranslator {
  def translate(doc: Document, sides: Pages.Value, outFile: File): Unit
}
