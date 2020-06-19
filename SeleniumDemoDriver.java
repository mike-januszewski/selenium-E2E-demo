package seleniumDemo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * SeleniumDemoDriver drives the Orbitz flight testing program. 
 * Its automation testing framework uses a Page Object Model which is a 
 * design pattern that creates an Object repository for web UI elements.
 * By "Object repository" I mean each web page of the application gets its own page class.
 * Each page class finds the elements and contains the methods that perform actions on 
 * those elements for that specific web page.
 * This design pattern improves code maintenance, promotes cleaner code, reduces redundancy,
 * increases re-usability and helps with organization.  I like it!
 * <p>
 * The startHere methods in each page class allow you to visualize each step of the scenario.
 * In this case there are 5 web pages of the Orbitz web app that will be tested.
 * 
 * @author  Michael Januszewski
 */
public class SeleniumDemoDriver {
	private static final Logger log = LogManager.getLogger(SeleniumDemoDriver.class.getName());  // Log4j2
	private static final String baseUrl = "https://www.orbitz.com";
	private static WebDriver driver;
	private static BufferedReader br = null; 

	/**
	 * main() drives the E2E testing.
	 */
	public static void main(String[] args) throws Exception {
		String[] sArrivalAirports = new String[3];  // there are 3 legs of the trip, therefore 3 arrival airports
		String[] sDepartingAirports = new String[4];  // 3 departure airports + the last string is the browser name
		String sFlightInput;  // holds airport codes and the browser name read from an external file
		String previousBrowserName = "";  // keeps track of the browser name
		String parentHandle = null;  // stores the handle of the new window that opens when we reach the Review Your Trip page
		boolean bFirstTestCase = true;

		try {
			/* 
			 * Open DestinationAirportCodes.txt for reading.
			 * It contains the 3 destination airports for the 3 legs of the multi-city round trip 
			 * as well as which browser to use. 
			 */
			FileInputStream fstream = new FileInputStream("DestinationAirportCodes.txt");
			br = new BufferedReader(new InputStreamReader(fstream));

			// Read the file one line at a time.  Each line contains 3 different airport codes and a browser name
			while ((sFlightInput = br.readLine()) != null) {
				log.info("Departing airports and browser to be tested: " + sFlightInput);

				// airport codes are already in departing airport order
				sDepartingAirports = sFlightInput.split("\\s+");  // the delimiter is any whitespace
				// rearrange these airport codes into arrival airport order
				sArrivalAirports[0] = sDepartingAirports[1];
				sArrivalAirports[1] = sDepartingAirports[2];
				sArrivalAirports[2] = sDepartingAirports[0];

				// get the requested browser name from DestinationAirportCodes.txt
				String requestedBrowserName = sDepartingAirports[3];  
				// determine whether the browser name has changed from the previous test case
				boolean bSameBrowser = requestedBrowserName.equals(previousBrowserName);
				if (!bSameBrowser) {  // helps fulfill Task 8
					/*
					 * Because this test case is requesting a different browser, close all currently open
					 * browser windows and destroy the current driver object (if it exists).
					 * Then open a new browser.
					 */
					if (bFirstTestCase) {
						bFirstTestCase = false;  // Simply reset a flag.  Driver object doesn't exist yet.
					}
					else {
						driver.quit();  // close all currently open browser windows and destroy the current driver object
					}
					openBrowser(requestedBrowserName);  // Open a new browser and instantiate a new driver
				}
				else if (parentHandle != null) {
					/*
					 * The same browser as the previous test is being requested, therefore
					 * close the second open window (the one containing the Payment page) and 
					 * switch focus back to parent handle (the window containing the Search Results page).
					 * There is no need to create a new driver, just reuse the current one.
					 */
					driver.close();
					driver.switchTo().window(parentHandle);
				}
				else {
					log.error("Parent window handle does not exist");
				}
				// Fulfills Task 1
				driver.get(baseUrl);  // browse to the Orbitz home page for each round of testing
				previousBrowserName = requestedBrowserName;  // reset for the next flight test case


				// Test the HomePage page
				HomePage homePage = new HomePage(driver);
				if (homePage.startHere())
					log.info("Home page testing completed, no exceptions thrown");
				else 
					log.info("Home page testing was incomplete, exceptions thrown");

				// Test the Search page
				SearchPage searchPage = new SearchPage(driver);
				if (searchPage.startHere(sDepartingAirports, sArrivalAirports, bSameBrowser))
					log.info("Search page testing completed, no exceptions thrown");
				else
					log.info("Search page testing was incomplete, exceptions thrown");			

				// Test the SearchResults page
				SearchResultsPage searchResultsPage = new SearchResultsPage(driver);
				if (searchResultsPage.startHere(sDepartingAirports, sArrivalAirports, searchPage))
					log.info("SearchResults page testing completed, no exceptions thrown");
				else
					log.info("SearchResults page testing was incomplete, exceptions thrown");

				// Test the ReviewYourTrip page
				ReviewYourTripPage reviewYourTripPage = new ReviewYourTripPage(driver);
				if (reviewYourTripPage.startHere(sDepartingAirports, sArrivalAirports, searchResultsPage))
					log.info("ReviewYourTrip page testing completed, no exceptions thrown");
				else
					log.info("ReviewYourTrip page testing was incomplete, exceptions thrown");

				// Test the Payment page
				PaymentPage paymentPage = new PaymentPage(driver);
				if (paymentPage.startHere(sDepartingAirports, sArrivalAirports, searchResultsPage))
					log.info("Payment page testing completed, no exceptions thrown");
				else
					log.info("Payment page testing was incomplete, exceptions thrown");

				log.info("END OF A TEST CASE");

				// Now that a round trip flight has been fully tested, save the 
				// parentHandle in case the same browser will be used in the next test case
				parentHandle = searchResultsPage.getParentHandle();
			}
		}
		catch(Exception e) {
			log.error("Problem with the browser or reading DestinationAirportCodes.txt", e);
		}
		finally {
			log.info("END OF ALL TEST CASES!");
			br.close();  // close the DestinationAirportCodes.txt file
			// Let any open browser windows remain on the screen
		}
	}

	/**
	 * Instantiate the browser driver, maximize the browser's window and set the implicit wait time.
	 * This method needs to be static so main() can call it.
	 * Fulfills Task 8.
	 * 
	 * @param  requestedBrowserName   string containing a browser name
	 */	
	private static void openBrowser(String requestedBrowserName) {
		// instantiate a new browser driver
		switch (requestedBrowserName) {
		case "Firefox":
			System.setProperty("webdriver.gecko.driver", "/Users/mikej/eclipse-workspace/selenium/selenium_drivers/geckodriver");
			driver = new FirefoxDriver();
			break;
		case "Chrome":
		default:  // let Chrome be the default browser
			System.setProperty("webdriver.chrome.driver", "/Users/mikej/eclipse-workspace/selenium/selenium_drivers/chromedriver");
			driver = new ChromeDriver();
			break;
		}
		// Maximize the browser's window
		driver.manage().window().maximize();
		/*
		 * Set the implicit wait time for all elements.
		 * Normally I have a much shorter implicit wait time (4-6 seconds) but there are many time consuming processes
		 * involved in retrieving and posting flight results.  I'll use explicit waits when necessary.
		 * I also have a relatively slow Internet connection.
		 */
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}
}
