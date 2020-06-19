package seleniumDemo;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Perform verifications on the Review Your Trip web page where the trip's itinerary and cost are displayed.
 * 
 * @author  Michael Januszewski
 */
public class ReviewYourTripPage extends SeleniumUtilities {
	private static final Logger log = LogManager.getLogger(ReviewYourTripPage.class.getName());  // Log4j2


	// locators are gathered here for maintainability
	private final String continueBookingButton = "bookButton";
	private final String allDeparturesAndArrivals = "//div[@class='airport type-300']";
	private final String priceRYT = "//section[contains(@class,'tripSummaryContainer desktopView')]//span[@class='packagePriceTotal']";
	private final String allFlightNumbersRYT = "//li[@class='flightNumber']/span[2]";
	private final String allDetailButtons = "//button[@class='btn-text toggle-trigger']";


	/**
	 * Constructor for the Review Your Trip page class.  It calls the base class constructor (SeleniumUtilities).
	 * 
	 * @param  driver   WebDriver object for the browser driver which implements the WebDriver interface
	 */
	public ReviewYourTripPage(WebDriver driver) {
		super(driver);
	}	

	/**
	 * Verify that all airport codes on the Review Your Trip page are identical to their counterparts 
	 * from the Search Results page.  Now that focus has been shifted to the new window displaying the 
	 * Review Your Trip page we are able to find the flight elements for verification purposes.
	 * <p>
	 * This page takes a while to load in the new window so I'm giving it an explicit wait time.
	 * I don't want to increase the implicit wait time because that affects ALL elements and I don't want
	 * to unnecessarily slow down my program.  An explicit wait time is customized to a specific element.
	 * Fulfills Task 6a.
	 * 
	 * @param   sDepartingAirports   string array of departing airport codes from the Search Results page
	 * @param   sArrivalAirports     string array of arriving airport codes from the Search Results page
	 * @throws  Exception            let the startHere() method catch it
	 */
	private void verifyAirportCodes(String[] sDepartingAirports, String[] sArrivalAirports) throws Exception {
		// explicitly wait up to 60 seconds for all departure and arrival flight code elements to become present
		List <WebElement> allDeparturesAndArrivalsList = waitForElements(By.xpath(allDeparturesAndArrivals), 60);

		// Compare the Review Your Trip airport codes with the departure/arrival airport codes from the Search Results Page
		// The i and j variables are used to index into the 3 String arrays to compare the codes.
		for (int i = 0, j = 0; j < sArrivalAirports.length; i+=2, j++) {
			if (!allDeparturesAndArrivalsList.get(i).getText().contains(sDepartingAirports[j])) {
				log.debug("Departure airport mismatch on Leg #" + (j + 1));
			}
			if (!allDeparturesAndArrivalsList.get(i+1).getText().contains(sArrivalAirports[j])) {
				log.debug("Arrival airport mismatch on Leg #" + (j + 1));
			}
		}
		log.info("verifyAirportsCodes() completed");
	}

	/**
	 * Verify that the trip price on the Review Your Trip page is identical to what was displayed on  
	 * the Search Results page.
	 * Fulfills Task 6b.
	 * 
	 * @param   searchResultsPage   SearchResultsPage object used to access its getter methods
	 * @throws  Exception           let the startHere() method catch it
	 */
	private void verifyPrice(SearchResultsPage searchResultsPage) throws Exception {
		String sPriceSearchResults = searchResultsPage.getSavedPrice();  // the price of the selected flights from the Search Results page
		WebElement priceRYTelement = getElement(priceRYT, "XPATH");
		String sPriceRYT = priceRYTelement.getText().trim();  // the price from the current page (the Review Your Trip page)

		if (sPriceRYT.equals(sPriceSearchResults)) {
			log.debug("RYT price matches!");
		} 
		else {
			log.debug("RYT price mismatch");
		}
		log.info("verifyPrice() completed");
	}

	/**
	 * Verify that all flight numbers on the Review Your Trip page are identical to what was displayed on  
	 * the Search Results pages.
	 * Fulfills Tasks 6c and 6d.
	 * 
	 * @param   searchResultsPage   SearchResultsPage object used to access its getter methods
	 * @throws  Exception           let the startHere() method catch it
	 */
	private void verifyFlightNumbers(SearchResultsPage searchResultsPage) throws Exception {
		// Determine the total number of flights from the Search Results page
		// Each leg of the trip can either be a non-stop or have 1 stop
		int iTotalNumberFlightsSearchResultsPage = 0;
		int[] iFlightsPerLeg = searchResultsPage.getSavedNumberFlightsPerLeg();
		for (int i : iFlightsPerLeg) {
			iTotalNumberFlightsSearchResultsPage += i;
		}

		// click all 3 "Show flight and baggage fee details" buttons in order to make the flight numbers visible
		List <WebElement> allDetailButtonsList = getElements(allDetailButtons, "XPATH");
		for (WebElement button : allDetailButtonsList) {
			clickElement(button);
		}

		// determine the total number of flights from the RYT page		
		List <WebElement> allFlightNumbersRYTlist = getElements(allFlightNumbersRYT, "XPATH");  // get all flight number locators
		int iTotalNumberFlightsRYT = allFlightNumbersRYTlist.size();

		// check that the Search Results page and the RYT page have the same number of flights
		if (iTotalNumberFlightsSearchResultsPage != iTotalNumberFlightsRYT)
			log.debug("Number of flights on the 2 pages are not equal. FlightsSearchResultsPage = " + iTotalNumberFlightsSearchResultsPage +
					"   FlightsRYT = " + iTotalNumberFlightsRYT);

		// now check that the individual flight numbers are the same
		// SRP = Search Results page     RYT = Review Your Trip page
		List<String> flightNumbersSRP = searchResultsPage.getSavedFlightNumbers();
		for (int i = 0; i < iTotalNumberFlightsRYT; i++) {
			String sFlightNumberSRP = flightNumbersSRP.get(i);  // a specific SRP flight number
			String sFlightNumberRYT = allFlightNumbersRYTlist.get(i).getText();
			if (sFlightNumberRYT.equals(sFlightNumberSRP))
				log.debug("Flight number is a match");	
			else
				log.debug("Flight number is NOT a match");
		}
		log.info("verifyFlightNumbers() completed");
	}

	/**
	 * Click the "Continue Booking" button.
	 * 
	 * @throws  Exception   let the startHere() method catch it
	 */
	private void clickContinueBookingButton() throws Exception {	
		clickElement(continueBookingButton, "ID");

		log.info("clickContinueBookingButton() was successful");
	}

	/**
	 * Driver for the Review Your Trip page tests.  
	 * 
	 * @param  sDepartingAirports   string array of departing airport codes from the Search Results page
	 * @param  sArrivalAirports     string array of arriving airport codes from the Search Results page
	 * @param  searchResultsPage    SearchResultsPage object used to access its getter methods
	 * @return                      boolean true for success. false indicates an exception was thrown
	 */
	public boolean startHere(String[] sDepartingAirports, String[] sArrivalAirports, SearchResultsPage searchResultsPage) {
		// process all exceptions for this page here
		try {
			verifyAirportCodes(sDepartingAirports, sArrivalAirports);
			verifyPrice(searchResultsPage);
			verifyFlightNumbers(searchResultsPage);
			clickContinueBookingButton();
			return true;
		}
		catch (Exception e) {
			log.error("startHere() failed with this exception: ", e);
			return false;
		}
	}
}
