package com.snackcake.journalgenerator.analysis

import com.snackcake.journalgenerator.analysis.model.{PartOfSpeechFactory, WordAnalysisResult}

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
class CompositeTitleAnalyzer(wordNormalizer: WordNormalizer, stemmer: PorterStemmer, nlpAnalyzer: StanfordNlpAnalyzer) {

  private val partOfSpeechFactory = new PartOfSpeechFactory

  def analyzeTitle(title: String): Seq[WordAnalysisResult] = {
    val originalWords = title.split("\\s+")
    val normalizedWords = originalWords.map(wordNormalizer.normalizeWord)
    val stems = stemmer stem normalizedWords
    val analyses = nlpAnalyzer analyze normalizedWords
    normalizedWords.zipWithIndex.map {
      case (word, index) =>
        val stem = stems(index)._2
        val analysis = analyses(index)
        val partOfSpeech = partOfSpeechFactory.lookupByCode(analysis._2)
        val lemma = analysis._3
        partOfSpeech.map(WordAnalysisResult(word, _, stem, lemma))
    }.flatten.toSeq
  }
}
