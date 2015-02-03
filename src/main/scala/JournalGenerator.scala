import scala.collection.mutable
import scala.io.Source

/**
 * @author Josh Klun (jmklun@gmail.com)
 */
class JournalGenerator(val sourceTitles: Iterable[String]) {
  def generateTitle(wordCount: Int = 5): String = {
    val indexFrequencyMap: Seq[Map[String, Int]] = buildIndexedFrequencyMap
    (0 to wordCount - 1).map {
      index =>
      indexFrequencyMap(index).foldLeft(("", 0)) {
        (previousWordCount: (String, Int), wordCount: (String, Int)) =>
          if (wordCount._2 > previousWordCount._2) wordCount else previousWordCount
      }._1
    }.mkString(" ")
  }

  private def buildIndexedFrequencyMap: Seq[Map[String, Int]] = {
    val indexFrequencyMap: mutable.Buffer[mutable.Map[String, Int]] = mutable.Buffer[mutable.Map[String, Int]]()
    val splitTitles = sourceTitles.map(_.split("\\s+"))
    val maxTitleWords = splitTitles.foldLeft(0)((oldMax: Int, words: Array[String]) => math.max(oldMax, words.length))
    (0 to maxTitleWords - 1).foreach {
      wordColumn =>  indexFrequencyMap += mutable.Map[String, Int]()
    }
    splitTitles.foreach {
      splitTitle =>
        var wordIndex = 0
        splitTitle.foreach {
          word =>
            val frequencyMap = indexFrequencyMap(wordIndex)
            val newCount = frequencyMap.get(word).fold(1)(_ + 1)
            frequencyMap.put(word, newCount)
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
