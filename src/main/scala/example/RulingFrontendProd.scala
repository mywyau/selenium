package example

import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.openqa.selenium.{By, WebDriver}

import scala.annotation.tailrec

case class RulingCaseData(reference: String, endDate: String, page: Int)

case class RulingCaseWithDate(reference: String, endDate: String)

object RulingFrontendProd extends App {

  // set path for chromedriver
  System.setProperty("webdriver.chrome.driver", "/usr/local/Caskroom/chromedriver/124.0.6367.201/chromedriver-mac-x64/chromedriver")

  // Optional: Set Chrome options (e.g., to run headless)

  val options = new ChromeOptions()
  options.addArguments("--remote-allow-origins=*")

  // options.addArguments("--headless") // Uncomment this line to run headless

  // Create a new instance of ChromeDriver
  val webDriver: WebDriver = new ChromeDriver(options)

  val productionService = "https://www.tax.service.gov.uk/search-for-advance-tariff-rulings/search?page=1"

  def rulingCaseRefSelector(i: Int) = s"#search_results-list-$i > h3"

  def rulingCaseExpiraryDateSelector(i: Int) = s"#search_results-list-$i > dl > div:nth-child(3) > dd"

  def findRulingCaseRef(i: Int): String =
    webDriver.findElement(By.cssSelector(rulingCaseRefSelector(i))).getText.replace("Ruling reference ", "")

  def findRulingCaseExpiraryDate(i: Int): String =
    webDriver.findElement(By.cssSelector(rulingCaseExpiraryDateSelector(i))).getText

  def numberOfSearchesOnPage: Int = webDriver.findElements(By.cssSelector("#search_results-list > li")).size()

  def getAllSearchResultsForPage: Seq[RulingCaseWithDate] = {

    @tailrec
    def loop(start: Int, finish: Int, acc: Seq[RulingCaseWithDate]): Seq[RulingCaseWithDate] =

      start match {
        case n if n == finish => acc
        case n =>
          loop(n + 1, finish, acc :+ RulingCaseWithDate(findRulingCaseRef(n), findRulingCaseExpiraryDate(n)))
      }

    val rulingReferences: Seq[RulingCaseWithDate] = loop(0, finish = numberOfSearchesOnPage, acc = Seq())
    println(rulingReferences)
    rulingReferences
  }

  val nextLink = "#search-pagination_bottom-page_next > a"

  def clickNextLink(): Unit = webDriver.findElement(By.cssSelector(nextLink)).click()

  def determinePageIsCorrect(pageNo: Int): Boolean =
    webDriver.getCurrentUrl == s"https://www.tax.service.gov.uk/search-for-advance-tariff-rulings/search?page=$pageNo"

  def loopThroughAllPages(endPageNumber: Option[Int]): Seq[RulingCaseData] = {

    @tailrec
    def loop(pageNumber: Int, endPageNumber: Option[Int], acc: Seq[RulingCaseData]): Seq[RulingCaseData] = {

      pageNumber match {
        case pageNum if endPageNumber.isDefined && pageNum == endPageNumber.getOrElse(0) => acc
        case pageNum if endPageNumber.isEmpty && determinePageIsCorrect(pageNum) =>
          println(
            "\n" + webDriver.getCurrentUrl.replace("https://www.staging.tax.service.gov.uk/search-for-advance-tariff-rulings/search", "")
          )

          val allCasesOnPage = {
            getAllSearchResultsForPage.map { caseData =>
              RulingCaseData(caseData.reference, caseData.endDate, pageNumber)
            }
          }

          if (numberOfSearchesOnPage == 25) {
            clickNextLink()
          }

          loop(pageNumber + 1, endPageNumber, acc ++ allCasesOnPage)
        case _ => acc
      }
    }

    val rulingReferencesWithPageNo = loop(1, endPageNumber, Seq())

    val duplicates: Seq[String] = rulingReferencesWithPageNo.map(_.reference).diff(rulingReferencesWithPageNo.map(_.reference).distinct).distinct

    val duplicatesWithPageNumber: Seq[RulingCaseData] =
      rulingReferencesWithPageNo.collect {
        case caseDataWithPage: RulingCaseData if duplicates.contains(caseDataWithPage.reference) =>
          caseDataWithPage
      }

    println("\nduplicatesWithPageNumber " + duplicatesWithPageNumber)
    println("\nnumber of duplicates  " + duplicates.size)

    //    println(rulingReferencesWithPageNo)  // print all cases with page number
    rulingReferencesWithPageNo
  }

  webDriver.get(productionService)

  println("\nnumberOfSearchesOnPage: " + numberOfSearchesOnPage)

  // loopThroughAllPages(Some(100))  // loops through first 100 pages
  loopThroughAllPages(None) // loops through all pages

  // Close the browser
  webDriver.quit()
}