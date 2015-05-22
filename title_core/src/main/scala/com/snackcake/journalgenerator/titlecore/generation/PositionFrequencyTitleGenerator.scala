package com.snackcake.journalgenerator.titlecore.generation

import com.snackcake.journalgenerator.titlecore.generation.filter.PositionFilter
import com.snackcake.journalgenerator.titlecore.generation.selection.WordSelection

/**
 * A generator that takes in a set of existing journal titles, analyzes them, and generates new titles by combining existing words.
 *
 * @param sourceTitles An iterable of names of existing journals to use as source data
 * @param minimumWordFrequency The minimum number of times a word must appear in a source title for it to be considered for an output
 *                             title. Default: 3
 * @param topWordCandidateCount The number of top words in a position to select from. Default: 5
 * @author Josh Klun (jmklun@gmail.com)
 */
abstract class PositionFrequencyTitleGenerator(val sourceTitles: Iterable[String],
                                               private val filters: Seq[PositionFilter] = Seq(),
                                               private val frequencyMapFactory: PositionFrequencyMapFactory = new PositionFrequencyMapFactory,
                                               val minimumWordFrequency: Int = 3,
                                               val topWordCandidateCount: Int = 5) extends JournalTitleGenerator with WordSelection {

  /**
   * @return Words that aren't allowed at the end of a generated title.
   */
  private def noEndWords = Seq("the", "and", "in")


  /**
   * @return A sequence of suffix strings that may be found on the ends of words that can be used for basic stemming, to avoid duplicate
   *         words or bad word combinations.
   */
  private def suffixes = Seq("s", "es", "ing", "ed")

  /**
   * Generate a new title, based on the configured input data.
   *
   * This implementation looks at the frequency of words in any given position in source titles and chooses from the most popular
   * words for a the current index.
   *
   * @param targetWordCount The target / maximum number of words for the output title. Default: 9
   * @return A new, formatted title
   */
  def generateTitle(targetWordCount: Int = 9): String = {
    val indexFrequencyMap = frequencyMapFactory.buildPositionFrequencyMap(sourceTitles)
    val minimumCountFiltered = filterOnWordCount(indexFrequencyMap)
    val nameBuffer = applyFilters(minimumCountFiltered)
    selectWords(nameBuffer, targetWordCount)
      .map(_.capitalize)
      .mkString(" ")
  }

  private def filterOnWordCount(indexFrequencyMap: Seq[Seq[(String, Int)]]): Seq[Seq[String]] = {
    indexFrequencyMap.map {
      _.filter {
        case (word, count) => count >= minimumWordFrequency
      }.map(_._1)
    }
  }

  private def applyFilters(nameBuffer: Seq[Seq[String]]): Seq[Seq[String]] = {
    nameBuffer.zipWithIndex.map {
      case (positionWords, index) =>
        var filteredWords = positionWords
        filters.foreach {
          filter => filteredWords = filter.filterWords(filteredWords, index)
        }
        filteredWords
    }
  }

  /**
   * Select the words for the title from the given sequence of sequences of top words.
   *
   * @param topWordsPerPosition A sequence, representing the positions in the input titles, containing sequences of top words in those
   *                            positions.
   * @return A sequence of words that can be combined to make a title
   */
  private def selectWords(topWordsPerPosition: Seq[Seq[String]], targetWordCount: Int): Seq[String] =
    topWordsPerPosition.zipWithIndex.foldLeft(Seq[String]()) {
      case (currentWords, (potentialWords, position)) if position < targetWordCount =>
        val positionCandidates = potentialWords.foldLeft(Seq[String]()) {
          // If the current list of candidates is smaller than the max per position…
          case (validWords, word) if validWords.size < topWordCandidateCount &&
            // and the word hasn't been used yet, in some form…
            isNewWordUnused(currentWords, word) &&
            // and the word is legal in this position, accounting for the fact that the planned output may be shorter than requested.
            isWordValidInPosition(word, currentWords.size, topWordsPerPosition.length - (position - currentWords.size)) => validWords :+ word
          case (validWords, _) => validWords
        }
        positionCandidates match {
          case nonDuplicatePotentialWords if nonDuplicatePotentialWords.nonEmpty =>
            currentWords :+ selectWord(nonDuplicatePotentialWords, position)
          case _ =>
            currentWords
        }
      case (currentWords, _) => currentWords
    }

  /**
   * Is the given word a valid candidate for the given position?
   *
   * @param newWord The word to consider adding as a candidate
   * @param currentPosition The position to consider adding the word to
   * @param positionCount The total count of positions in the output title
   * @return true if the word is valid in this position, false if it isn't
   */
  private def isWordValidInPosition(newWord: String, currentPosition: Int, positionCount: Int): Boolean =
    currentPosition < positionCount - 1 || !noEndWords.contains(newWord)

  /**
   * Is the new candidate word, or its stem word unused in the current list of output words?
   *
   * @param currentWords Current output words
   * @param newWord New candidate word
   * @return true if the word is unused, false if already used.
   */
  private def isNewWordUnused(currentWords: Seq[String], newWord: String): Boolean = {
    val newWordStem = stemWord(newWord)
    !currentWords.exists(currentWord => stemWord(currentWord) == newWordStem)
  }

  /**
   * @return The input word's stem, or the word itself, if it can't be stemmed.
   */
  private def stemWord(word: String): String =
    suffixes
      .filter(word.endsWith)
      .map(suffix => word.substring(0, word.length - suffix.length))
      .sortWith(_.length > _.length)
      .headOption
      .getOrElse(word)
}
