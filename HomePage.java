package seleniumDemo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

/**
 * HomePage tests the Orbitz home web page
 * @author mikej
 *
 */
public class HomePage extends SeleniumUtilities {
	private static final Logger log = LogManager.getLogger(HomePage.class);

	// locators are gathered here for maintainability
	private final String flightLink = "tab-flight-tab-hp";

	// constructor
	public HomePage(WebDriver driver) {
		super(driver);
	}

	private void clickFlightsButton() throws Exception {
		clickElement(flightLink, "ID");

		log.info("clickFlightsButton() was successful");
	}

	/**
	 * Process the Home page.  
	 * @return true on success, false otherwise
	 */
	public boolean wholePage() {
		// process all exceptions here
		try {
			clickFlightsButton();
			return true;
		}
		catch (Exception e) {
			log.error("Home page failed: ", e);
			return false;
		}
	}
}
