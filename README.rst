==================================
Flashup - Studying. In plain text.
==================================

*A simple markup language for creating flashcards*

Project homepage <http://ymasory.github.com/Flashup/>

GitHub repository <http://github.com/ymasory/Flashup>

My flashcards <http://yuvimasory.com/flashcards.html>


Run
===
1. Install sbt 0.10+ <https://github.com/harrah/xsbt/wiki/Setup>
2. ``cd Flashup``
3. ``echo 'run samples/sample.flashup' | sbt``
4. ``echo 'run --anki samples/sample.flashup' | sbt``
5. ``echo 'run --mnemo samples/sample.flashup' | sbt``
6. open ``sample.pdf`` in your favorite PDF viewer
7. import ``sample-anki.txt`` in either Anki
8. import ``sample-mnemosyne.txt`` in either Mnemosyne

Standalone
==========
You can also make a standalone jar if you wish, but I'm told this doesn't work on Mac. Works fine for me on Linux.

1. ``cd Flashup``
2. ``sbt proguard``
3. ``java -jar target/scala_*/flashup-*.min.jar [args]``
