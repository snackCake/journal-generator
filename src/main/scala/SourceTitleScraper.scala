
import org.jsoup.nodes.Document

import scala.io.Source
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import scala.collection.JavaConverters._

/**
 * A scraper that extracts titles from HTML at a configured URL, located at a configured element selector.
 * 
 * @param url The URL to load HTML from
 * @param titleElementSelector The CSS-style selector for elements that should be scraped for titles
 * 
 * @author Josh Klun (jklun@nerdery.com)
 */
class SourceTitleScraper(url: String, titleElementSelector: String) {

  /**
   * Scrape the configured HTML data source for an iterable of titles.
   *
   * @return An iterable of titles
   */
  def scrape: Iterable[String] = {
    val htmlString = Source.fromURL(url).mkString
    val document: Document = Jsoup.parse(htmlString, url)
    val titleElements: Elements = document.select(titleElementSelector)
    titleElements.asScala.map(_.text)
  }
}
