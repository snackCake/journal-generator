package com.snackcake.journalgenerator.generation.filter

/**
 * Interface for a filter that removes words that aren't valid at the given position in a title.
 *
 * @author Josh Klun (jklun@nerdery.com)
 */
trait PositionFilter {

  /**
   * Filter the given list of words that appear at a given position.
   *
   * @param words Sequence of words at a position in a list of titles
   * @param position The position in a title that the words appear
   * @return The input sequence of words, filtered to remove invalid values.
   */
  def filterWords(words: Seq[String], position: Int): Seq[String]
}
