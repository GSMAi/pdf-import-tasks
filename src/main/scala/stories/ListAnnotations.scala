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
  
  private def annotations(dict: PdfDictionary): Iterable[PdfObject] = 
    if (dict.getAsArray(PdfName.ANNOTS) == null) Seq.empty
    else dict.getAsArray(PdfName.ANNOTS)
    
  private def contentAsOption(annotation: PdfDictionary) : Option[PdfObject] = 
	  (annotation.getKeys().contains(PdfName.SUBTYPE) && annotation.get(PdfName.SUBTYPE).asInstanceOf[PdfName].equals(PdfName.HIGHLIGHT)) match {
      case true => Some(PdfReader.getPdfObject(annotation.get(PdfName.CONTENTS)))
      case false => None
  }
  
  private implicit def readerToIterator(reader: PdfReader) = new PdfIterator(reader)

  def listAnnotations(reader: PdfReader) : Iterator[Annotation] = 
	    reader.flatMap { pageTuple =>
	      annotations(pageTuple._2).map { PdfReader.getPdfObject(_).asInstanceOf[PdfDictionary] }.flatMap { annotation =>
	        contentAsOption(annotation).map { content =>
	            val rectangle = annotation.get(PdfName.RECT).asInstanceOf[PdfArray]
	            val strategy = new FilteredTextRenderListener(new LocationTextExtractionStrategy(), new RegionTextRenderFilter(rectangle))
	            Annotation(
	                PdfTextExtractor.getTextFromPage(reader, pageTuple._1, strategy).replaceAll(" ", ""), 
	                content.toString, 
	                pageTuple._1)
	  }
	}
  }
}