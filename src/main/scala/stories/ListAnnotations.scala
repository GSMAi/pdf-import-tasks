package stories

import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfName
import scala.collection.JavaConversions._
import com.itextpdf.text.pdf.PdfDictionary
import com.itextpdf.text.pdf.PdfArray
import com.itextpdf.text.pdf.parser.RegionTextRenderFilter
import com.itextpdf.text.pdf.parser.FilteredTextRenderListener
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import com.itextpdf.text.pdf.PdfNumber
import com.itextpdf.text.Rectangle
import model.Annotation
import com.itextpdf.text.pdf.PdfObject

object ListAnnotations {
  
  private implicit def pdfNumberToFloat(pdfNumber: PdfNumber) : Float = pdfNumber.floatValue()
  
  private implicit def pdfArrayToRectangle(pdfArray: PdfArray): Rectangle = new Rectangle(pdfArray.getAsNumber(0), pdfArray.getAsNumber(1), pdfArray.getAsNumber(2), pdfArray.getAsNumber(3))
  
  class PdfIterator(reader: PdfReader) extends scala.collection.Iterator[(Integer, PdfDictionary)] {
    var currentPage = 0
    override def hasNext : Boolean = currentPage < reader.getNumberOfPages()
    
    override def next: (Integer, PdfDictionary) = {
      // Should probably let consumer release page
      if (currentPage > 0) reader.releasePage(currentPage)
      currentPage = currentPage + 1
      (currentPage, reader.getPageN(currentPage))
    }
  }
  
  private def annotations(dict: PdfDictionary): Iterable[PdfObject] = 
    if (dict.getAsArray(PdfName.ANNOTS) == null) Seq.empty
    else dict.getAsArray(PdfName.ANNOTS)
    
  private def contentAsOption(annotation: PdfDictionary) : Option[PdfObject] = 
	  if (annotation.getKeys().contains(PdfName.SUBTYPE) && annotation.get(PdfName.SUBTYPE).asInstanceOf[PdfName].equals(PdfName.HIGHLIGHT)) {
	    Some(PdfReader.getPdfObject(annotation.get(PdfName.CONTENTS)))
	  } else {
	    None
	  }
  
  private implicit def readerToIterator(reader: PdfReader) = new PdfIterator(reader)

	def listAnnotations(reader: PdfReader) : Iterator[Annotation] = 
	    reader.flatMap { pageTuple =>
	      annotations(pageTuple._2).map { PdfReader.getPdfObject(_).asInstanceOf[PdfDictionary] }.flatMap { annotation =>
	        contentAsOption(annotation).map { content =>
	            val rectangle = annotation.get(PdfName.RECT).asInstanceOf[PdfArray]
	            val filter = new RegionTextRenderFilter(rectangle)
	            val strategy = new FilteredTextRenderListener(new LocationTextExtractionStrategy(), filter)
	            Annotation(PdfTextExtractor.getTextFromPage(reader, pageTuple._1, strategy).replaceAll(" ", ""), content.toString, pageTuple._1)
	        }
	      }
	    }
}