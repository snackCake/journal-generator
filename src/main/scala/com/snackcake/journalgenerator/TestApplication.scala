package com.snackcake.journalgenerator

import com.snackcake.journalgenerator.generation.PositionFrequencyJournalTitleGenerator
import com.snackcake.journalgenerator.sourcedata.SourceTitleScraper

/**
 * Simple CLI application for testing the generator.
 *
 * @author Josh Klun (jklun@nerdery.com)
 */
object TestApplication {

  /**
   * Main method for testing.
   */
  def main(args: Array[String]) {
    val scraper = new SourceTitleScraper("http://scholarlyoa.com/individual-journals/", "div.entry ul a")
    val generator = new PositionFrequencyJournalTitleGenerator(scraper.scrape, 3, 5)
    val title = generator.generateTitle()
    println(title)
  }
}
