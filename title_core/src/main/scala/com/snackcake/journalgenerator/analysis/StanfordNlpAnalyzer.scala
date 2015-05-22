package com.snackcake.journalgenerator.analysis

import java.io.File
import java.util.Properties

import edu.stanford.nlp.ling.CoreAnnotations._
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}

import scala.collection.JavaConverters._

/**
 *
 * Translated from Java code found here: http://stackoverflow.com/questions/1578062/lemmatization-java
 *
 * @author Josh Klun (jklun@nerdery.com)
 */
class StanfordNlpAnalyzer(modelPath: String) {

  private val props = new Properties()
  props.put("pos.model", new File(modelPath).getAbsolutePath)
  props.put("annotators", "tokenize, ssplit, pos, lemma")

  protected val pipeline = new StanfordCoreNLP(props)

  def analyze(titleWords: Seq[String]): Seq[(String, String, String)] = {
    val document = new Annotation(titleWords.mkString(" "))
    pipeline.annotate(document)
    for (sentence <- document.get(classOf[SentencesAnnotation]).asScala; token <- sentence.get(classOf[TokensAnnotation]).asScala)
      yield (token.get(classOf[ValueAnnotation]), token.get(classOf[PartOfSpeechAnnotation]), token.get(classOf[LemmaAnnotation]))
  }
}
