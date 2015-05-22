package com.snackcake.journalgenerator.titlecore.generation.selection

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
trait WordSelection {
  protected def selectWord(candidateWords: Seq[String], position: Int): String
}
