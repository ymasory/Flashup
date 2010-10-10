package com.yuvimasory.flashcards

import java.io.{BufferedWriter, File, FileWriter}

private[flashcards] trait FlashcardTranslator {
  def translate(instances: List[TranslationInstance]): Unit
}

private[flashcards] object TxtTranslator extends FlashcardTranslator {

  override def translate(instances: List[TranslationInstance]): Unit = {
    // val toWrite = doc.toString
    // val out = new BufferedWriter(new FileWriter(outFile))
    // try {
    //   out.write(toWrite)
    // }
    // finally {
    //   out.flush()
    //   out.close()
    // }
  }
}

case class TranslationInstance(doc: Document, outFile: File, side: Pages.Value)
