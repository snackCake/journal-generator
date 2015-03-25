package com.snackcake.journalgenerator

import java.io.File

import com.snackcake.journalgenerator.analysis.{WordNormalizer, CompositeTitleAnalyzer, PorterStemmer, StanfordNlpAnalyzer}
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
    val scraper = new SourceTitleScraper("http://scholarlyoa.com/individual-journals/", "div.entry > ul li a")
    val titles = scraper.scrape

    val modelpath = new File("src/main/resources/english-left3words-distsim.tagger").getAbsolutePath
    val normalizer = new WordNormalizer
    val stemmer = new PorterStemmer
    val analyzer = new StanfordNlpAnalyzer(modelpath)
    val compositeAnalyzer = new CompositeTitleAnalyzer(normalizer, stemmer, analyzer)
    val analysisResults = titles.map(compositeAnalyzer.analyzeTitle)
    println(s"analysis results: ${analysisResults.mkString("\n")}")

    val generator = new PositionFrequencyJournalTitleGenerator(titles, 3, 5)
    val title = generator.generateTitle()
    println(title)
  }
}
