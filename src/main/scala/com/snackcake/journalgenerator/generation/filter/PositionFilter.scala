package com.snackcake.journalgenerator.generation.filter

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
trait PositionFilter {
  def filterWords(words: Seq[String], position: Int): Seq[String]
}
