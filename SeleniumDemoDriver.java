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
 * @author mikej
 *
 */
public class SeleniumDemoDriver {
	private static final Logger log = LogManager.getLogger(SeleniumDemoDriver.class);
	private static final String baseUrl = "https://www.orbitz.com";
	private static WebDriver driver;
	private static BufferedReader br = null; 
	
	// main() drives the testing of each Orbitz web page.
	// Each web page has its own test class
	public static void main(String[] args) throws Exception {
		String[] sDepartingAirports = new String[4];  // the last string is the browser name
		String[] sArrivalAirports = new String[3];
		String sFlightInput;
		String previousBrowserName = "";
		String parentHandle = null;
		boolean bNotFirstTestCase = false;

		
		try {
			// Open the DestinationAirportCodes.txt for reading.
			// This contains the 3 destination airports for our round trip as well as 
			// which browser to use.
			FileInputStream fstream = new FileInputStream("DestinationAirportCodes.txt");
			br = new BufferedReader(new InputStreamReader(fstream));
			
			// Read file line by line
			while ((sFlightInput = br.readLine()) != null) {
				log.debug("Departing airports and browser to be tested: " + sFlightInput);
	
				// airport codes are already in departing airport order
				sDepartingAirports = sFlightInput.split("\\s+");  // the delimiter is any whitespace
				// rearrange these airport codes into arrival airport order
				sArrivalAirports[0] = sDepartingAirports[1];
				sArrivalAirports[1] = sDepartingAirports[2];
				sArrivalAirports[2] = sDepartingAirports[0];

				// get the requested browser name from DestinationAirportCodes.txt
				String requestedBrowserName = sDepartingAirports[3];  
				// see if the browser name has changed from the previous test
				boolean bSameBrowser = requestedBrowserName.equals(previousBrowserName);
				if (!bSameBrowser) {
					// Because this flight test is requesting a different browser *all* open 
					// browser windows need to be closed and the current driver object destroyed.
					// Then open a new browser.
					if (bNotFirstTestCase) {
						driver.quit();  // don't call this for test case #1 because driver doesn't exist yet.
					}
					else {
						bNotFirstTestCase = true;  // flag for the first test case
					}
					openBrowser(requestedBrowserName);
				}
				else if (parentHandle != null) {
					// The same browser as the previous test is being requested, therefore
					// close the second open window (the one containing the Payment page) and 
					// switch focus back to parent handle (containing the Search window)
					driver.close();
					driver.switchTo().window(parentHandle);
				}
				// always do this
				driver.get(baseUrl);  // browse to the Orbitz home page for each round of testing
				previousBrowserName = requestedBrowserName;  // reset for the next flight test
								
				
				// Test the HomePage page
				HomePage homePage = new HomePage(driver);
				if (homePage.wholePage())
					log.info("***Home page testing completed, no exceptions thrown***");
				else 
					log.info("***Home page testing was incomplete, exceptions thrown***");
				
				// Test the Search page
				SearchPage searchPage = new SearchPage(driver);
				// if the browser being requested is the same as the previous one then clear flight 
				// destination and arrival fields of old airport codes on the Search page and 
				// re-populate them with new codes.  Do the same with old departure dates.
				if (searchPage.wholePage(sArrivalAirports, bSameBrowser))
					log.info("***Search page testing completed, no exceptions thrown***");
				else
					log.info("***Search page testing was incomplete, exceptions thrown***");
				

				// Test the SearchResults page
				SearchResultsPage searchResultsPage = new SearchResultsPage(driver);
				String[] dates = searchPage.getFormattedDepartureDate();  // get saved departure dates
				if (searchResultsPage.wholePage(sDepartingAirports, sArrivalAirports, dates))
					log.info("***SearchResults page testing completed, no exceptions thrown***");
				else
					log.info("***SearchResults page testing was incomplete, exceptions thrown***");

				// Test the ReviewYourTrip page
				ReviewYourTripPage reviewYourTripPage = new ReviewYourTripPage(driver);
				//int[] stops = searchResultsPage.getSavedNumberOfStops();  // get saved number of stops
				//String[] flightNumbers = searchResultsPage.getSavedFlightNumbers();  // get saved flight numbers
				//String price = searchResultsPage.getSavedPrice();  // get saved price
				//if (reviewYourTripPage.wholePage(sArrivalAirports, stops, flightNumbers, price))
				if (reviewYourTripPage.wholePage(sDepartingAirports, sArrivalAirports, searchResultsPage))
					log.info("***ReviewYourTrip page testing completed, no exceptions thrown***");
				else
					log.info("***ReviewYourTrip page testing was incomplete, exceptions thrown***");


				// Test the Payment page
				PaymentPage paymentPage = new PaymentPage(driver);
				if (paymentPage.wholePage(sDepartingAirports, sArrivalAirports, searchResultsPage))
					log.info("***Payment page testing completed, no exceptions thrown***");
				else
					log.info("***Payment page testing was incomplete, exceptions thrown***");

				log.info("***END OF A TEST CASE***");

				// Now that a flight round trip has been fully tested, I need to save the 
				// parentHandle in case we are not changing browsers for the next test case
				parentHandle = searchResultsPage.getParentHandle();
			}
		}
		catch(Exception e) {
			log.error("***Unable to open browser or read DestinationAirportCodes.txt***", e);
			//System.exit(1);
		}
		finally {
			log.info("END OF ALL TEST CASES!!!");
			br.close();  // close the DestinationAirportCodes.txt file
			// leave any open browser windows on the screen
		}
	}
	
	private static void openBrowser(String requestedBrowserName) {
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
		// Do this for all browsers
		// Maximize the browser's window
		driver.manage().window().maximize();
		// set the implicit wait time for all elements
		driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);  // I have a relatively slow Internet connection
	}
}
