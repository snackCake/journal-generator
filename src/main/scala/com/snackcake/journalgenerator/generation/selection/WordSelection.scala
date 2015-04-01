package com.snackcake.journalgenerator.generation.selection

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
trait WordSelection {
  protected def selectWord(candidateWords: Seq[String], position: Int): String
}
