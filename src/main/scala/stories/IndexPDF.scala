package stories

import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfDictionary
import com.itextpdf.text.pdf.parser.PdfReaderContentParser
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy
import model.Histogram
import model.Group

object IndexPDF {
  private implicit def readerToIterator(reader: PdfReader) = new PdfIterator(reader)
  
  def trim(s: String) = s.trim().replaceAll("\\p{Punct}", "")
  
  private val defaultGroupings = Seq(Group("test", Seq("this", "is", "a", "test")))

  def index(file: String) = {
    var reader = new PdfReader(file)
    var parser = new PdfReaderContentParser(reader)
    for (i <- 1 to reader.getNumberOfPages - 1) yield {
      var histogram = new Histogram(defaultGroupings)
      parser.processContent(i, new SimpleTextExtractionStrategy).getResultantText.split("\\s").map(trim(_)).filter(_ != "").foreach(histogram.increment(_))
      histogram
    } 
  }
  
}