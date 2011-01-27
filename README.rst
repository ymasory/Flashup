==================================
Flashup - Studying. In plain text.
==================================

*A simple markup language for creating flashcards*

Project homepage <http://ymasory.github.com/Flashup/>

GitHub repository <http://github.com/ymasory/Flashup>

My flashcards <http://yuvimasory.com/flashcards.html>


Run
===
1. Install sbt <http://code.google.com/p/simple-build-tool/>
2. ``cd Flashup``
3. ``sbt update``
4. ``sbt 'run samples/sample.flashup'``
5. ``sbt 'run --anki samples/sample.flashup'``
6. ``sbt 'run --mnemo samples/sample.flashup'``
7. open ``sample.pdf`` in your favorite PDF viewer
8. import ``sample-anki.txt`` in either Anki
9. import ``sample-mnemosyne.txt`` in either Mnemosyne

Standalone
==========
You can also make a standalone jar if you wish, but I'm told this doesn't work on Mac. Works fine for me on Linux.

1. ``cd Flashup``
2. ``sbt update``
3. ``sbt proguard``
4. ``java -jar target/scala_*/flashup-*.min.jar [args]``
