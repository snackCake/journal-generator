package com.snackcake.journalgenerator

import com.snackcake.journalgenerator.generation.JournalTitleGenerator
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
    val generator = new JournalTitleGenerator(scraper.scrape)
    val title = generator.generateTitle()
    println(title)
  }
}
