package com.snackcake.journalgenerator.generation.selection

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
trait RandomSelection extends WordSelection {

  protected def selectWord(candidateWords: Seq[String], position: Int): String = scala.util.Random.shuffle(candidateWords).head

}
