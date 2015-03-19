package com.snackcake.journalgenerator.generation

/**
 * A generator that takes in a set of existing journal titles, analyzes them, and generates new titles by combining existing words.
 *
 * @param sourceTitles An iterable of names of existing journals to use as source data
 * @param minimumWordFrequency The minimum number of times a word must appear in a source title for it to be considered for an output
 *                             title. Default: 3
 * @param topWordCandidateCount The number of top words in a position to select from. Default: 5
 * @author Josh Klun (jmklun@gmail.com)
 */
class PositionFrequencyJournalTitleGenerator(val sourceTitles: Iterable[String],
                                             val minimumWordFrequency: Int = 3,
                                             val topWordCandidateCount: Int =  5) extends JournalTitleGenerator with PositionFrequencyMapFactory {

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
   * @param wordCount The target / maximum number of words for the output title. Default: 9
   * @return A new, formatted title
   */
  def generateTitle(wordCount: Int = 9): String = {
    val indexFrequencyMap = buildPositionFrequencyMap(sourceTitles)
    val nameBuffer = indexFrequencyMap.map(_.filter(_._2 >= minimumWordFrequency).map(_._1))
    selectWords(nameBuffer)
      .map(_.capitalize)
      .mkString(" ")
  }

  /**
   * Select the words for the title from the given sequence of sequences of top words.
   *
   * @param topWordsPerPosition A sequence, representing the positions in the input titles, containing sequences of top words in those
   *                            positions.
   * @return A sequence of words that can be combined to make a title
   */
  private def selectWords(topWordsPerPosition: Seq[Seq[String]]): Seq[String] =
    topWordsPerPosition.foldLeft(Seq[String]()) {
      case (currentWords, potentialWords) =>
        val positionCandidates = potentialWords.foldLeft(Seq[String]()) {
          case (validWords, word) if validWords.size < topWordCandidateCount &&
            isNewWordUnused(currentWords, word) &&
            isWordValidInPosition(word, currentWords.size, topWordCandidateCount) => validWords :+ word
          case (validWords, _) => validWords
        }
        positionCandidates match {
          case nonDuplicatePotentialWords if nonDuplicatePotentialWords.nonEmpty =>
            currentWords :+ scala.util.Random.shuffle(nonDuplicatePotentialWords).head
          case _ =>
            currentWords
        }
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

  // TODO: Figure out what this is trying to do. Why the shenanigans with suffixes?
  private def isNewWordUnused(currentWords: Seq[String], newWord: String): Boolean = {
    !currentWords.exists {
      currentWord =>
        val potentialSuffix = currentWord match {
          case word if word.length > newWord.length => Some(word.substring(newWord.length))
          case word if word.length < newWord.length => Some(newWord.substring(word.length))
          case word if word.length == newWord.length => None
        }
        potentialSuffix.fold(currentWords.contains(newWord))(suffixes.contains(_))
    }
  }

}
