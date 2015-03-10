import scala.collection.mutable
import scala.io.Source

/**
 * @author Josh Klun (jmklun@gmail.com)
 */
class JournalGenerator(val sourceTitles: Iterable[String]) {

  private type IndexFrequencyMap = Seq[Map[String, Int]]
  private type MutableIndexFrequencyMap = mutable.Buffer[mutable.Map[String, Int]]
  private type WordCount = (String, Int)

  private def noEndWords = Seq("the", "and", "in")

  private def normalizeWordRules = Map(
    Set("and", "&") -> "and"
  )

  private def suffixes = Seq("s", "es", "ing", "ed")

  def generateTitle(wordCount: Int = 9, minimumWordFrequency: Int = 3, topWordCount: Int =  5): String = {
    val indexFrequencyMap = buildIndexFrequencyMap
    val nameBuffer = mutable.Buffer[Seq[String]]()

    for (i <- 0 to wordCount - 1) {
      nameBuffer += indexFrequencyMap(i).foldLeft(Seq(("", 0))) {
        case (oldWords: Seq[WordCount], newWordTuple: WordCount) =>
          updateTopWordsInColumn(wordCount, topWordCount, i, oldWords, newWordTuple)
      }.map(_._1)
    }

    selectWords(nameBuffer)
      .map(_.capitalize)
      .mkString(" ")
  }

  private def normalizeWord(sourceWord: String): String = {
    val lowerSourceWord = sourceWord.toLowerCase
    val matchingRules = normalizeWordRules.keys.filter(_.contains(lowerSourceWord))
    matchingRules.lastOption.fold(sourceWord) { normalizeWordRules.get(_).get }
  }

  private def updateTopWordsInColumn(wordCount: Int, topWordCount: Int, column: Int, oldWords: Seq[WordCount], newWordTuple: WordCount): Seq[WordCount] = {
    val (newWord, newWordCount) = newWordTuple
    val oldWordCounts = oldWords.map(_._2)
    val minWordCount = oldWordCounts.min
    val columnNotFull = oldWords.size < topWordCount
    val newWordMoreFrequent = newWordCount > minWordCount
    val validInColumn = isWordValidInColumn(newWord, column, wordCount)
    if (columnNotFull && validInColumn) {
      oldWords :+ newWord -> newWordCount
    } else if (newWordMoreFrequent && validInColumn) {
      val oldMinIndex = oldWordCounts.indexOf(minWordCount)
      oldWords.updated(oldMinIndex, newWord -> newWordCount)
    } else {
      oldWords
    }
  }

  private def selectWords(nameBuffer: Seq[Seq[String]]): Seq[String] =
    nameBuffer.foldLeft(Seq[String]()) {
      (currentWords, potentialWords) =>
        potentialWords.filter(isNewWordUnused(currentWords, _)) match {
          case nonDuplicatePotentialWords if nonDuplicatePotentialWords.length > 0 =>
            currentWords :+ scala.util.Random.shuffle(nonDuplicatePotentialWords).head
          case _ =>
            currentWords
        }
    }

  private def isWordValidInColumn(newWord: String, currentColumn: Int, columnCount: Int): Boolean =
    currentColumn < columnCount - 1 || !noEndWords.contains(newWord)

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

  private def buildEmptyIndexFrequencyMap(splitTitles: Iterable[Array[String]]): MutableIndexFrequencyMap = {
    val indexFrequencyMap = mutable.Buffer[mutable.Map[String, Int]]()
    val maxTitleWords: Int = splitTitles.foldLeft(0) {
      (oldMax, words) => math.max(oldMax, words.length)
    }
    (0 to maxTitleWords - 1).foreach {
      wordColumn => indexFrequencyMap += mutable.Map[String, Int]()
    }
    indexFrequencyMap
  }
}

object JournalGenerator {
  def main(args: Array[String]) {
    val parser = new SourceTitleParser("http://scholarlyoa.com/individual-journals/", "div.entry ul a")
    val generator = new JournalGenerator(parser.parse)
    val title = generator.generateTitle()
    println(title)
  }
}
