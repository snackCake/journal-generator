
import scala.io.Source
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import scala.collection.JavaConverters._

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
class SourceTitleParser {
  def parse(input: Source): Iterable[String] = {
    val document = Jsoup.parse(input.mkString, "http://scholarlyoa.com/individual-journals/")
    val journalAnchors: Elements = document.select("div.entry ul a")
    journalAnchors.asScala.map(_.text)
  }
}
