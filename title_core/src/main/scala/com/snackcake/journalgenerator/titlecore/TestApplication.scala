package com.snackcake.journalgenerator.titlecore

import java.io.File

import com.snackcake.journalgenerator.titlecore.analysis.CompositeTitleAnalyzer
import com.snackcake.journalgenerator.titlecore.analysis.{CompositeTitleAnalyzer, StanfordNlpAnalyzer, WordNormalizer, PorterStemmer}
import com.snackcake.journalgenerator.titlecore.generation.PositionFrequencyTitleGenerator
import com.snackcake.journalgenerator.titlecore.generation.filter.WeightedPartOfSpeechFilter
import com.snackcake.journalgenerator.titlecore.generation.selection.RandomSelection
import com.snackcake.journalgenerator.titlecore.sourcedata.SourceTitleScraper

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

    val modelPath = new File("src/main/resources/english-left3words-distsim.tagger").getAbsolutePath
    val normalizer = new WordNormalizer
    val stemmer = new PorterStemmer
    val analyzer = new StanfordNlpAnalyzer(modelPath)
    val compositeAnalyzer = new CompositeTitleAnalyzer(normalizer, stemmer, analyzer)
    val analysis = titles.map(compositeAnalyzer.analyzeTitle).toSeq
    println(s"analysis results: ${analysis.mkString("\n")}")

    val generator = new PositionFrequencyTitleGenerator(titles) with RandomSelection
    val posFilter = new WeightedPartOfSpeechFilter(analysis)
    val posGenerator = new PositionFrequencyTitleGenerator(titles, Seq(posFilter)) with RandomSelection
    println(s"position title: ${generator.generateTitle()}")
    println(s"weighted POS title: ${posGenerator.generateTitle()}")
  }
}
