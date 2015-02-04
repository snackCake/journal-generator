import scala.collection.mutable
import scala.io.Source

/**
 * @author Josh Klun (jmklun@gmail.com)
 */
class JournalGenerator(val sourceTitles: Iterable[String]) {

  private def noEndWords = Seq("the", "and", "in")

  private def normalizeWordRules = Map(
    Set("and", "&") -> "and"
  )

  private def normalizeWord(sourceWord: String): String = {
    val lowerSourceWord = sourceWord.toLowerCase
    val matchingRules = normalizeWordRules.keys.filter(_.contains(lowerSourceWord))
    matchingRules.lastOption.fold(sourceWord) { normalizeWordRules.get(_).get }
  }

  def generateTitle(wordCount: Int = 5): String = {
    val indexFrequencyMap = buildIndexedFrequencyMap
    val nameBuffer = mutable.Buffer[String]()
    for (i <- 0 to wordCount - 1) {
      nameBuffer += indexFrequencyMap(i).foldLeft(("", 0)) {
        case ((oldWord, oldWordCount), (newWord, newWordCount)) =>
          if (newWordCount > oldWordCount &&
            !nameBuffer.contains(newWord)
            && (i < newWordCount - 1 || !noEndWords.contains(newWord))
          ) {
            (newWord, newWordCount)
          } else {
            (oldWord, oldWordCount)
          }
      }._1
    }
    nameBuffer.map(_.capitalize).mkString(" ")
  }

  private def buildIndexedFrequencyMap: Seq[Map[String, Int]] = {
    val indexFrequencyMap = mutable.Buffer[mutable.Map[String, Int]]()
    val splitTitles = sourceTitles.map(_.split("\\s+"))
    val maxTitleWords = splitTitles.foldLeft(0)((oldMax, words) => math.max(oldMax, words.length))
    (0 to maxTitleWords - 1).foreach {
      wordColumn => indexFrequencyMap += mutable.Map[String, Int]()
    }
    splitTitles.foreach {
      splitTitle =>
        var wordIndex = 0
        splitTitle.foreach {
          word =>
            val frequencyMap = indexFrequencyMap(wordIndex)
            val newCount = frequencyMap.get(word).fold(1)(_ + 1)
            frequencyMap.put(normalizeWord(word), newCount)
            wordIndex += 1
        }
    }
    indexFrequencyMap.toSeq.map(_.toMap[String, Int])
  }
}

object JournalGenerator {
  def main(args: Array[String]) {
    val sourceData = Source.fromURL("http://scholarlyoa.com/individual-journals/")
    val parser = new SourceTitleParser
    val generator = new JournalGenerator(parser.parse(sourceData))
    val title = generator.generateTitle(9)
    println(title)
  }
}
