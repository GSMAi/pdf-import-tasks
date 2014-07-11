package stories

import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfDictionary

class PdfIterator(reader: PdfReader) extends scala.collection.Iterator[(Integer, PdfDictionary)] {
    var currentPage = 0

    override def hasNext : Boolean = currentPage < reader.getNumberOfPages()
    
    override def next: (Integer, PdfDictionary) = {
      if (currentPage > 0) reader.releasePage(currentPage) // Should probably let consumer release page
      currentPage = currentPage + 1
      (currentPage, reader.getPageN(currentPage))
    }
}
