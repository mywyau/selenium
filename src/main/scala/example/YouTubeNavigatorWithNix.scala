package example

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}

object YouTubeNavigatorWithNix {
  def main(args: Array[String]): Unit = {
    // Get the chromedriver path from the PATH environment variable
    val chromedriverPath = sys.env.get("PATH")
      .flatMap(_.split(":").find(_.contains("chromedriver")))

    chromedriverPath match {
      case Some(path) =>
        // Set the system property to use the ChromeDriver path
        System.setProperty("webdriver.chrome.driver", s"$path/chromedriver")

        // Setup Chrome options if needed
        val options = new ChromeOptions()
        options.addArguments("--start-maximized")
        options.addArguments("--remote-allow-origins=*")

        // Initialize ChromeDriver
        val driver: WebDriver = new ChromeDriver(options)

        try {
          // Navigate to YouTube
          driver.get("https://www.youtube.com")

          // Print the title of the page to verify
          println(s"Page title is: ${driver.getTitle}")

          // Optionally, you can wait for a few seconds to see the result
          Thread.sleep(5000)
        } finally {
          // Close the browser
          driver.quit()
        }

      case None =>
        println("ChromeDriver not found in PATH. Make sure ChromeDriver is installed and available in PATH.")
    }
  }
}
