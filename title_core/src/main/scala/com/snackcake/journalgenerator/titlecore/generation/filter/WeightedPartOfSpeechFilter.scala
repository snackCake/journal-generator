package com.snackcake.journalgenerator.titlecore.generation.filter

import com.snackcake.journalgenerator.titlecore.analysis.model.WordAnalysisResult
import com.snackcake.journalgenerator.titlecore.analysis.model.{PartOfSpeech, WordAnalysisResult}

/**
 * Filter that randomly selects a part of speech to use at a given position, weighted by part of speech frequency, and filters out all
 * words that are not marked as being that part of speech.
 *
 * @author Josh Klun (jklun@nerdery.com)
 */
class WeightedPartOfSpeechFilter(private val analysisResults: Seq[Seq[WordAnalysisResult]]) extends PositionFilter {

  /**
   * Take a sequence of titles, split into sequences of words within a title and "invert" the 2 dimensional sequence, so that it is a
   * sequence of title position-associated sequences of words for that position.
   */
  private def invertAnalysis(inputAnalysis: Seq[Seq[WordAnalysisResult]]): Seq[Seq[WordAnalysisResult]] = {
    (0 to inputAnalysis.maxBy(_.length).length - 1).map {
      index =>
        inputAnalysis.map {
          titleResults =>
            if (index < titleResults.length) {
              Some(titleResults(index))
            } else {
              None
            }
        }.flatten
    }
  }

  private lazy val positionalAnalysis = invertAnalysis(analysisResults)


  override def filterWords(words: Seq[String], position: Int): Seq[String] = {
    val currentPositionAnalysis = positionalAnalysis(position)
    val originalPositionWordCount = currentPositionAnalysis.size
    val weightSelector = scala.util.Random.nextDouble()

    val posCountMap = currentPositionAnalysis.foldLeft(Map[PartOfSpeech, Int]()) {
      (countMap, analysis) => countMap + (analysis.partOfSpeech -> (countMap.getOrElse(analysis.partOfSpeech, 0) + 1))
    }
    val posRatios = posCountMap.map {
      posCount => posCount._1 -> posCount._2.toDouble / originalPositionWordCount
    }.toSeq
    val sortedPosRatios = posRatios.sortBy {
      wordRatio => wordRatio._2
    }
    val selectedPos = sortedPosRatios.foldLeft[(Double, Option[PartOfSpeech])]((0.0, None)) {
      case ((total: Double, selected: Option[PartOfSpeech]), (partOfSpeech: PartOfSpeech, positionRatio: Double)) =>
        val newTotal = total + positionRatio
        if (selected.nonEmpty) {
          (newTotal, selected)
        } else {
          (newTotal, if (newTotal  > weightSelector) Some(partOfSpeech) else None)
        }
    }._2.get

    currentPositionAnalysis.filter {
      analysis => words.contains(analysis.baseWord) && analysis.partOfSpeech == selectedPos
    }.distinct.map {
      analysis => analysis.baseWord
    }
  }
}
