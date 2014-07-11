import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext
import stories.ListAnnotations
import com.itextpdf.text.pdf.PdfReader
import java.io.FileInputStream


@RunWith(classOf[JUnitRunner])
class AnnotationPDFTest  extends Specification{
	val fn = "/Users/AGlynn/Downloads/vodafone_annual_report_2013.pdf"
	"PDF Reader" should {
	  "test" in {
        ListAnnotations.listAnnotations(new PdfReader(new FileInputStream(fn))).foreach(println _)
        true
	  }
	}
}