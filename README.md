# Flashup - Studying. In plain text. #

[![Build Status](http://ci.yuvimasory.com/job/Flashup/badge/icon)](http://ci.yuvimasory.com/job/Flashup/)

*A simple markup language for creating flashcards*

Project homepage <http://ymasory.github.com/Flashup/>

GitHub repository <http://github.com/ymasory/Flashup>

My flashcards <http://yuvimasory.com/flashcards.html>

## Run ##

- Install sbt 0.12.0 <https://github.com/harrah/xsbt/wiki/Setup>.

```sh
cd Flashup
echo 'run samples/sample.flashup' | sbt
echo 'run --anki samples/sample.flashup' | sbt
echo 'run --mnemo samples/sample.flashup' | sbt
```

- open ``sample.pdf`` in your favorite PDF viewer
- import ``sample-anki.txt`` in either Anki
- import ``sample-mnemosyne.txt`` in either Mnemosyne

## Standalone ##
You can also make a standalone jar if you wish, but I'm told this doesn't work on Mac. Works fine for me on Linux.

```sh
cd Flashup
sbt proguard
java -jar target/scala_*/flashup-*.min.jar [args]
```

## Use as library ##
Flashup is hosted on [Maven Central](http://central.maven.org/maven2/com/yuvimasory/).
It is built for Scala 2.8.0 through 2.9.2.
You can add it as a dependency in your `build.sbt` file.

```scala
libraryDependencies += "com.yuvimasory" %% "flashup" % "1.0.0"
```

Because my repository uses a patched version of iText, you will have to add iText as a dependency to your project as well. Flashup is compatible with unpatched iText as well.

[API Docs (ScalaDocs)](http://ci.yuvimasory.com/job/Flashup/javadoc/?#package)
