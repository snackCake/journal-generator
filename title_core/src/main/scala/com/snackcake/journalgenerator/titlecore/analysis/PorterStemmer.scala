package com.snackcake.journalgenerator.titlecore.analysis

import org.tartarus.Stemmer

/**
 * PorterStemmer is a Scala wrapper for the [[org.tartarus.Stemmer]] class.
 *
 * @author Josh Klun (jklun@nerdery.com)
 */
class PorterStemmer {

  def stem(titleWords: Seq[String]): Seq[(String, String)] = {
    titleWords.map {
      word =>
        val stemmer = new Stemmer
        stemmer.add(word.toCharArray, word.length)
        stemmer.stem()
        (word, stemmer.toString)
    }
  }

}
