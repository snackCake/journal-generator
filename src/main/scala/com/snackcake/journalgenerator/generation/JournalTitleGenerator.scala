package com.snackcake.journalgenerator.generation

/**
 * Trait that defines the basic title generator interface.
 *
 * @author Josh Klun (jklun@nerdery.com)
 */
trait JournalTitleGenerator {

  /**
   * Generate a title with the given word count, based on the current configuration.
   *
   * @param wordCount The target number of words for the generated title.
   * @return A new title
   */
  def generateTitle(wordCount: Int): String
}
