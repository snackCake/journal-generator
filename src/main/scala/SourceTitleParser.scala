
import org.jsoup.nodes.Document

import scala.io.Source
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import scala.collection.JavaConverters._

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
class SourceTitleParser(url: String, titleElementSelector: String) {

  def parse: Iterable[String] = {
    val htmlString = Source.fromURL(url).mkString
    val document: Document = Jsoup.parse(htmlString, url)
    val titleElements: Elements = document.select(titleElementSelector)
    titleElements.asScala.map(_.text)
  }
}
