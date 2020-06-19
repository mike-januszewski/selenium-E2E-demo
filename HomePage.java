package seleniumDemo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

/**
 * Enable clicking the Flights tab on the Orbitz home page.
 * 
 * @author Michael Januszewski
 */
public class HomePage extends SeleniumUtilities {
	private static final Logger log = LogManager.getLogger(HomePage.class.getName());  // Log4j2

	// locators are gathered here for maintainability
	private final String flightLink = "tab-flight-tab-hp";


	/**
	 * Constructor for the HomePage class.  It calls the base class constructor (SeleniumUtilities).
	 * 
	 * @param  driver	WebDriver object for the browser driver which implements the WebDriver interface
	 */
	public HomePage(WebDriver driver) {
		super(driver);
	}

	/**
	 * Click the Flights tab on the Home page.  This opens the Search Flights page.
	 * Fulfills Task 2.
	 * 
	 * @throws	Exception	let the startHere() method catch it
	 */	
	private void clickFlightsTab() throws Exception {
		clickElement(flightLink, "ID");

		log.info("clickFlightsTab() completed");
	}

	/**
	 * Driver for the Home page tests.  
	 * 
	 * @return		boolean true for success. false indicates an exception was thrown
	 */
	public boolean startHere() {
		// process all exceptions for this page here
		try {
			clickFlightsTab();
			return true;
		}
		catch (Exception e) {
			log.error("startHere() failed with this exception: ", e);
			return false;
		}
	}
}
