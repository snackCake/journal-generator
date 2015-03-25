package com.snackcake.journalgenerator.analysis

import org.tartarus.Stemmer

/**
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
