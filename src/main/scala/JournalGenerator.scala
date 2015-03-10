import scala.collection.mutable
import scala.io.Source

/**
 * A generator that takes in a set of existing journal titles, analyzes them, and generates new titles by combining existing words.
 *
 * @param sourceTitles An iterable of names of existing journals to use as source data
 * @author Josh Klun (jmklun@gmail.com)
 */
class JournalGenerator(val sourceTitles: Iterable[String]) {

  private type IndexFrequencyMap = Seq[Map[String, Int]]
  private type MutableIndexFrequencyMap = mutable.Buffer[mutable.Map[String, Int]]
  private type WordCount = (String, Int)

  /**
   * @return Words that aren't allowed at the end of a generated title.
   */
  private def noEndWords = Seq("the", "and", "in")


  /**
   * @return A map of keys (sets of words) that should all be normalized to their values (single words) when encountered in a title.
   */
  private def normalizeWordRules = Map(
    Set("and", "&") -> "and"
  )

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
   * @param minimumWordFrequency The minimum number of times a word must appear in a source title for it to be considered for an output
   *                             title. Default: 3
   * @param topWordCount The number of top words in a position to select from. Default: 5
   * @return A new, formatted title
   */
  def generateTitle(wordCount: Int = 9, minimumWordFrequency: Int = 3, topWordCount: Int =  5): String = {
    val indexFrequencyMap = buildIndexFrequencyMap
    val nameBuffer = mutable.Buffer[Seq[String]]()

    for (i <- 0 to wordCount - 1) {
      nameBuffer += indexFrequencyMap(i).foldLeft(Seq(("", 0))) {
        case (oldWords: Seq[WordCount], newWordTuple: WordCount) =>
          updateTopWordsInPosition(oldWords, newWordTuple, i, wordCount, topWordCount)
      }.map(_._1)
    }

    selectWords(nameBuffer)
      .map(_.capitalize)
      .mkString(" ")
  }

  /**
   * Given a sequence of current most popular [[WordCount]]s at a given position, decide whether a new [[WordCount]] should be added to the
   * existing sequence of top words, and if so, returns the new sequence.
   *
   * @param positionCount The number of word positions in the output title
   * @param topWordCount The number of top words to select from in this position
   * @param position The position to update top words for
   * @param oldWords The current top words for the position
   * @param newWordTuple The new [[WordCount]] to potentially add to the position's sequence
   * @return A potentially update sequence of word counts for the position
   */
  private def updateTopWordsInPosition(oldWords: Seq[WordCount],
                                       newWordTuple: WordCount,
                                       position: Int,
                                       positionCount: Int,
                                       topWordCount: Int): Seq[WordCount] = {
    val (newWord, newWordCount) = newWordTuple
    val oldWordCounts = oldWords.map(_._2)
    val minWordCount = oldWordCounts.min
    val positionSetNotFull = oldWords.size < topWordCount
    val newWordMoreFrequent = newWordCount > minWordCount
    val validInPosition = isWordValidInPosition(newWord, position, positionCount)
    if (positionSetNotFull && validInPosition) {
      oldWords :+ newWord -> newWordCount
    } else if (newWordMoreFrequent && validInPosition) {
      val oldMinIndex = oldWordCounts.indexOf(minWordCount)
      oldWords.updated(oldMinIndex, newWord -> newWordCount)
    } else {
      oldWords
    }
  }


  /**
   * If the source word matches a normalization rule, return the target word of the rule, otherwise return the source word.
   *
   * @param sourceWord The word to match against the normalization rules
   * @return The normalized word, or the source word if it didn't need to be normalized.
   */
  private def normalizeWord(sourceWord: String): String = {
    val lowerSourceWord = sourceWord.toLowerCase
    val matchingRules = normalizeWordRules.keys.filter(_.contains(lowerSourceWord))
    matchingRules.lastOption.fold(sourceWord) { normalizeWordRules.get(_).get }
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
      (currentWords, potentialWords) =>
        potentialWords.filter(isNewWordUnused(currentWords, _)) match {
          case nonDuplicatePotentialWords if nonDuplicatePotentialWords.length > 0 =>
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

  /**
   * Based on the input titles, build an index frequency map. Each entry in the sequence represents a position in the source titles. Each
   * entry is a map of source title words to counts for how frequent the word is in a given position.
   *
   * @return A new index frequency map for the input titles.
   */
  private def buildIndexFrequencyMap: Seq[Map[String, Int]] = {
    val splitTitles: Iterable[Array[String]] = sourceTitles.map(_.split("\\s+"))
    val indexFrequencyMap = buildEmptyIndexFrequencyMap(splitTitles)
    splitTitles.foreach {
      splitTitle =>
        var wordIndex = 0
        splitTitle.foreach {
          word =>
            val frequencyMap = indexFrequencyMap(wordIndex)
            val normalizedWord = normalizeWord(word)
            val newCount = frequencyMap.get(normalizedWord).fold(1)(_ + 1)
            frequencyMap.put(normalizedWord, newCount)
            wordIndex += 1
        }
    }
    indexFrequencyMap.toSeq.map(_.toMap[String, Int])
  }

  /**
   * @return A new empty structure with enough space for the index frequency map for the given source titles
   */
  private def buildEmptyIndexFrequencyMap(splitTitles: Iterable[Array[String]]): MutableIndexFrequencyMap = {
    val indexFrequencyMap = mutable.Buffer[mutable.Map[String, Int]]()
    val maxTitleWords: Int = splitTitles.foldLeft(0) {
      (oldMax, words) => math.max(oldMax, words.length)
    }
    (0 to maxTitleWords - 1).foreach {
      wordPosition => indexFrequencyMap += mutable.Map[String, Int]()
    }
    indexFrequencyMap
  }
}

object JournalGenerator {

  /**
   * Main method for testing.
   */
  def main(args: Array[String]) {
    val parser = new SourceTitleParser("http://scholarlyoa.com/individual-journals/", "div.entry ul a")
    val generator = new JournalGenerator(parser.parse)
    val title = generator.generateTitle()
    println(title)
  }
}
