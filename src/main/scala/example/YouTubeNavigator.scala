package example

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}

object YouTubeNavigator {
  def main(args: Array[String]): Unit = {
    // Set the path to the ChromeDriver executable
    System.setProperty("webdriver.chrome.driver", "/usr/local/Caskroom/chromedriver/124.0.6367.201/chromedriver-mac-x64/chromedriver")

    // Setup Chrome options if needed
    val options = new ChromeOptions()
//    options.addArguments("--start-maximized")
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
  }
}
