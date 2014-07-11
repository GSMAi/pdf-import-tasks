package stories

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext


@RunWith(classOf[JUnitRunner])
class IndexPDFTest extends Specification{

  "Read PDF and check contents " should {
    "A file" in {
      IndexPDF.index("/Users/AGlynn/Downloads/vodafone_annual_report_2013.pdf")
      true
    }
  }
  
}