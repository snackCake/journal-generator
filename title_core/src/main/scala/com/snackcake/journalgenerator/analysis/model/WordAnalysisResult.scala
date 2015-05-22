package com.snackcake.journalgenerator.analysis.model

/**
 * WordAnalysisResult holds everything that is known about a word that appeared at a given point in a given title.
 *
 * @author Josh Klun (jklun@nerdery.com)
 */
case class WordAnalysisResult(baseWord: String, partOfSpeech: PartOfSpeech, stem: String, lemma: String)