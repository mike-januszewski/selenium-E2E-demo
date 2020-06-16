package seleniumDemo;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Perform verifications on the Payments web page where the trip's final itinerary and costs are displayed.
 * 
 * @author Michael Januszewski
 */
public class PaymentPage extends SeleniumUtilities {
	private static final Logger log = LogManager.getLogger(PaymentPage.class.getName());  // Log4j2


	// locators are gathered here for maintainability
	private final String allDeparturesAndArrivals = "//div[@class='location-info type-300']";
	private final String pricePayment = "totalPriceForTrip";
	private final String allFlightNumbers = "//div[@class='airlines-and-flight-info']";


	/**
	 * Constructor for the Payment page class.  It calls the base class constructor (SeleniumUtilities).
	 * 
	 * @param  driver	WebDriver object for the browser driver which implements the WebDriver interface
	 */
	public PaymentPage(WebDriver driver) {
		super(driver);
	}	

	/**
	 * Verify that all airport codes on the Payment page are identical to their counterparts 
	 * from the Search Results page.  There are always 3 pairs of departing/arriving airports.
	 * Fulfills Task 7a.
	 * 
	 * @param  sDepartingAirports	string array of departing airport codes from the Search Results page
	 * @param  sArrivalAirports		string array of arriving airport codes from the Search Results page
	 * @throws Exception				let the startHere() method catch it
	 */
	private void verifyAirportCodes(String[] sDepartingAirports, String[] sArrivalAirports) throws Exception {
		String[] sAirportCodes = new String[6];  // 3 pairs of airport codes means a total of 6 codes
		int i = 0, j = 0;

		// Build an array of string objects containing the departure/arrival airport codes for the 3 legs of the trip 
		// as listed on the Payment Page.  Need to perform string manipulation to isolate the airport codes.
		List <WebElement> allDeparturesAndArrivalsList = getElements(allDeparturesAndArrivals, "XPATH");
		for (WebElement legElement : allDeparturesAndArrivalsList) {    
			String sLegDescription = legElement.getText().trim();  // string example: "Boston (BOS) to Miami (MIA)"

			int parenthesis = sLegDescription.indexOf('(');  // index of first open parenthesis
			sAirportCodes[i++] = sLegDescription.substring(parenthesis + 1, parenthesis + 4);  // avoid both parentheses, retrieve the first 3 letter airport code 

			parenthesis = sLegDescription.lastIndexOf('(');  // index of last open parenthesis
			sAirportCodes[i++] = sLegDescription.substring(parenthesis + 1, parenthesis + 4);  // avoid both parentheses, retrieve the second 3 letter airport code
		}

		// Compare the sAirportCodes array with the departure/arrival airport codes from the Search Results Page
		// The i and j variables are used to index into the 3 String arrays to compare the codes.
		for (i = 0, j = 0; j < sArrivalAirports.length; j++) {
			if (!sAirportCodes[i++].equals(sDepartingAirports[j])) {
				log.debug("Departure airport mismatch on Leg #" + (j + 1));
			}
			if (!sAirportCodes[i++].equals(sArrivalAirports[j])) {
				log.debug("Arrival airport mismatch on Leg #" + (j + 1));
			}
		}
		log.info("verifyAirportCodes() completed");
	}

	/**
	 * Verify that the final trip price on the Payment page is identical to what was displayed on  
	 * the Search Results page.
	 * Fulfills Task 7b.
	 * 
	 * @param  searchResultsPage		SearchResultsPage object used to access its getter methods
	 * @throws Exception				let the startHere() method catch it
	 */
	private void verifyPrice(SearchResultsPage searchResultsPage) throws Exception {
		String sPriceSearchResults = searchResultsPage.getSavedPrice();  // the price of the selected flights from the Search Results page
		WebElement pricePaymentElement = getElement(pricePayment, "ID");
		String sPricePayment = pricePaymentElement.getText().trim();  // string will be similar to:  "$###.##"

		if (sPricePayment.equals(sPriceSearchResults)) {
			log.debug("Payment price matches");
		} 
		else {
			log.debug("Payment price MISmatch");
		}
		log.info("verifyPrice() completed");
	}

	/**
	 * Verify that all flight numbers on the Payment page are identical to what was displayed on  
	 * the Search Results pages.
	 * Fulfills Tasks 7c and 7d.
	 * 
	 * @param  searchResultsPage	 SearchResultsPage object used to access its getter methods
	 * @throws Exception			 let the startHere() method catch it
	 */
	private void verifyFlightNumbers(SearchResultsPage searchResultsPage) throws Exception {
		// Determine the total number of flights from the Search Results page
		// Each leg of the trip can either be a non-stop or have 1 stop
		int iTotalNumberFlightsSearchResultsPage = 0;
		int[] iFlightsPerLeg = searchResultsPage.getSavedNumberFlightsPerLeg();
		for (int i : iFlightsPerLeg) {
			iTotalNumberFlightsSearchResultsPage += i;
		}

		// determine the total number of flights from the Payments page	
		List <WebElement> allFlightNumbersList = getElements(allFlightNumbers, "XPATH");    // get all flight number locators
		int iTotalNumberFlightsPaymentPage = allFlightNumbersList.size();

		// check that the Search Results page and the Payments page have the same number of flights
		if (iTotalNumberFlightsPaymentPage != iTotalNumberFlightsSearchResultsPage)
			log.debug("Number of flights on the 2 pages are not equal. FlightsSearchResultsPage = " + iTotalNumberFlightsSearchResultsPage +
					"   FlightsPaymentPage = " + iTotalNumberFlightsPaymentPage);

		// now check that the individual flight numbers are the same
		// SRP = Search Results page     PP = Payments page
		List<String> flightNumbersSRP = searchResultsPage.getSavedFlightNumbers();
		for (int i = 0; i < iTotalNumberFlightsPaymentPage; i++) {
			String sFlightNumberSRP = flightNumbersSRP.get(i);  // a specific SRP flight number
			// each element's original text looks similar to this:  "      American Airlines 1234     "
			String shortenedFlightNumber = allFlightNumbersList.get(i).getText().trim();  // now it looks like:  "American Airlines 1234"
			int lastBlank = shortenedFlightNumber.lastIndexOf(" ");
			String sFlightNumberPP = shortenedFlightNumber.substring(lastBlank + 1);  // flight number begins one character after the last blank
			if (sFlightNumberSRP.equals(sFlightNumberPP))
				log.debug("Flight number is a match");	
			else
				log.debug("Flight number is NOT a match");
		}
		log.info("verifyFlightNumbers() completed");
	}			

	/**
	 * Driver for the Payment page tests.  
	 * 
	 * @param	sDepartingAirports	string array of departing airport codes from the Search Results page
	 * @param	sArrivalAirports		string array of arriving airport codes from the Search Results page
	 * @param	searchResultsPage  	SearchResultsPage object used to access its getter methods
	 * @return						boolean true for success. false indicates an exception was thrown
	 */
	public boolean startHere(String[] sDepartingAirports, String[] sArrivalAirports, SearchResultsPage searchResultsPage) {
		// process all exceptions for this page here
		try {
			verifyAirportCodes(sDepartingAirports, sArrivalAirports);
			verifyPrice(searchResultsPage);
			verifyFlightNumbers(searchResultsPage);
			return true;
		}
		catch (Exception e) {
			log.error("startHere() failed with this exception: ", e);
			return false;
		}
	}
}