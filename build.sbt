name := "journal-generator"

version := "1.0"

scalaVersion := "2.11.5"

libraryDependencies ++= Seq(
  "org.jsoup" % "jsoup" % "1.8.1",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.5.1",
  "org.scaldi" %% "scaldi" % "0.5.4"
)

