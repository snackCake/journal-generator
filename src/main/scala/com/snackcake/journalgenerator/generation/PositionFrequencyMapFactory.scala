package com.snackcake.journalgenerator.generation

import scala.collection.immutable.{TreeSet, TreeMap}
import scala.collection.mutable

/**
 * Trait that provides factory functionality for building position frequency map objects, which can be analyzed to create new titles.
 * 
 * @author Josh Klun (jklun@nerdery.com)
 */
trait PositionFrequencyMapFactory extends WordNormalizer {

  private type MutablePositionFrequencyMap = mutable.Buffer[mutable.Map[String, Int]]
  protected type PositionFrequencyMap = Seq[Map[String, Int]]
  protected type WordCount = (String, Int)

  /**
   * Based on the input titles, build a position frequency map. Each entry in the sequence represents a position in the source titles. Each
   * entry is a map of source title words to counts for how frequent the word is in a given position.
   *
   * @param sourceTitles An iterable of source titles to build a position frequency map for
   * @return A new position frequency map for the input titles.
   */
  protected def buildPositionFrequencyMap(sourceTitles: Iterable[String]): Seq[Seq[(String, Int)]] = {
    val splitTitles: Iterable[Array[String]] = sourceTitles.map(_.split("\\s+"))
    val positionFrequencyMap = buildEmptyPositionFrequencyMap(splitTitles)
    splitTitles.foreach {
      splitTitle =>
        var wordPosition = 0
        splitTitle.foreach {
          word =>
            val frequencyMap = positionFrequencyMap(wordPosition)
            val normalizedWord = normalizeWord(word)
            val newCount = frequencyMap.get(normalizedWord).fold(1)(_ + 1)
            frequencyMap.put(normalizedWord, newCount)
            wordPosition += 1
        }
    }
    implicit val countOrder = Ordering.by[(String, Int), Int] { case (word, count) => -count }
    positionFrequencyMap.toSeq.map { wordCounts => TreeSet(wordCounts.toSeq:_*).toSeq }
  }

  /**
   * @return A new empty structure with enough space for the position frequency map for the given source titles
   */
  protected def buildEmptyPositionFrequencyMap(splitTitles: Iterable[Array[String]]): MutablePositionFrequencyMap = {
    val positionFrequencyMap = mutable.Buffer[mutable.Map[String, Int]]()
    val maxTitleWords: Int = splitTitles.foldLeft(0) {
      (oldMax, words) => math.max(oldMax, words.length)
    }
    (0 to maxTitleWords - 1).foreach {
      wordPosition => positionFrequencyMap += mutable.Map[String, Int]()
    }
    positionFrequencyMap
  }
}
