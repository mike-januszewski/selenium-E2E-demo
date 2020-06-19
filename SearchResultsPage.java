package seleniumDemo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * Select a qualifying round trip flight.
 * 
 * @author  Michael Januszewski
 */
public class SearchResultsPage extends SeleniumUtilities {
	private static final Logger log = LogManager.getLogger(SearchResultsPage.class.getName());  // Log4j2

	// Instance variables
	// There are a variable number of flight numbers because each leg of the trip can have either 1 or 2 flights
	// depending on the number of stops.  Therefore using an ArrayList.
	private ArrayList<String> flightNumbersArray = new ArrayList<String>();
	private String sPrice;  // the posted price of the chosen flight 
	private int[] numberFlightsPerLeg = new int[3];  // directly related to the number of stops per leg
	private String parentHandle;  // the current window handle


	// locators are gathered here for maintainability
	private final String allSelectButtons = "//button[@data-test-id='select-button']";
	private final String selectThisFare = "//div[contains(@class,'toggle-pane fade open')]//button[contains(@class,'btn-secondary btn-action t-select-btn')]";
	private final String allAirlineNames = "//span[@data-test-id='airline-name']";
	private final String allStops = "//span[@class='number-stops']";
	private final String allDetailsAndBaggageLinks = "//a[@data-test-id='flight-details-link']";
	private final String allFlightNumbers = "//li[@class='details-utility-item-value segment-info-details-item flight']";
	private final String selectedPriceField = "sortDropdown";
	private final String allFlightPrices = "//span[@class='full-bold no-wrap']";
	// in order of departures
	private final String[] sDepartureAirportIDs = {  
			"departure-airport-1",
			"departure-airport-2",
			"departure-airport-3"
	};
	// in order of arrivals
	private final String[] sArrivalAirportIDs = {  
			"arrival-airport-1",
			"arrival-airport-2",
			"arrival-airport-3"
	};
	// in order of departing dates
	private final String[] sDepartureDateIDs = {  
			"departure-date-1",
			"departure-date-2",
			"departure-date-3"
	};


	/**
	 * Constructor for the Search Results page class.  It calls the base class constructor (SeleniumUtilities).
	 * 
	 * @param  driver   WebDriver object for the browser driver which implements the WebDriver interface
	 */
	public SearchResultsPage(WebDriver driver) {
		super(driver);
	}	

	/**
	 * Make sure that the "Sort by" dropdown menu on the left side of the page has "Price (Lowest)" selected.
	 * If that option is not already selected then click it to filter results that way.
	 * Fulfills Task 4a.
	 * 
	 * @throws  Exception   let the startHere() method catch it
	 */
	private void selectSortByFilterOption() throws Exception {
		WebElement priceMenuElement = getElement(selectedPriceField, "ID");

		// This drop down menu uses a <select> tag so I can use the Select class for a cleaner solution
		Select priceMenuSelectField = new Select(priceMenuElement);
		WebElement firstOption = priceMenuSelectField.getFirstSelectedOption();
		String sCurrentOption = firstOption.getText();
		if (sCurrentOption.equals("Price (Lowest)")) {
			log.info("Filter has already selected \"Price (Lowest)\"");
		}
		else {
			priceMenuSelectField.selectByVisibleText("Price (Lowest)");
			clickElement(priceMenuElement);  // click the "Price (Lowest)" option to sort by that option
			log.info("Clicking the \"Price (Lowest)\" option");
		}
	}

	/**
	 * Verify that the flight prices really are in ascending order.
	 * Compare successive numerical prices (comparing strings won't work).
	 * Fulfills Task 4b.
	 * 
	 * @throws  Exception   let the startHere() method catch it
	 */
	private void verifyPricesAreInLowestOrder() throws Exception {
		// explicit wait up to 60 seconds because it takes a relatively long time for the 
		// Search Results page to fully load.
		List <WebElement> allFlightPricesList = waitForElements(By.xpath(allFlightPrices), 60);

		// perform string manipulation to isolate the price, then convert it to a float
		int dollarSignIndex = allFlightPricesList.get(0).getText().lastIndexOf("$");
		String previousPrice = allFlightPricesList.get(0).getText().substring(dollarSignIndex + 1).trim();  // truncate the dollar sign.  $X,XXX.XX becomes X,XXX.XX
		float fPreviousPrice = Float.valueOf(previousPrice.replaceAll(",",  ""));  // delete all commas and convert the string to a float

		boolean bAreSorted = true;
		for (WebElement price : allFlightPricesList) {
			// perform similar string manipulation to isolate the next price
			dollarSignIndex = price.getText().lastIndexOf("$");
			String curPrice = price.getText().substring(dollarSignIndex + 1).trim();  // truncate the dollar sign.  $X,XXX.XX becomes X,XXX.XX
			// delete all commas and convert the string to a float for numerical comparison.  X,XXX.XX becomes XXXX.XX
			float fCurrentPrice = Float.valueOf(curPrice.replaceAll(",",  ""));

			// compare successive float prices
			if (fCurrentPrice < fPreviousPrice) {
				bAreSorted = false;  // not in ascending order so break out of the loop
				break;
			}
			fPreviousPrice = fCurrentPrice;
		}
		if (bAreSorted)
			log.debug("verifyPricesAreInLowestOrder()  All prices really are in lowest order");
		else
			log.debug("verifyPricesAreInLowestOrder()  Prices are NOT in lowest order");
	}

	/**
	 * Verify that the flight information (airport codes and departure dates) on the current page matches 
	 * the flight data input into the Search page.  
	 * Each airport has a unique three letter (all uppercase) identification code.  For example, 
	 * SFO = San Francisco, DEN = Denver.  If the Search Results Page contains the same airport codes given 
	 * to the Search Page then verification is confirmed.
	 * Fulfills Task 4c.
	 * 
	 * @param   sDepartingAirports   string array of departing airport codes from the Search page
	 * @param   sArrivalAirports     string array of arriving airport codes from the Search page
	 * @param   searchPage           SearchPage object used to access its getter methods
	 * @throws  Exception            let the startHere() method catch it
	 */
	private void verifyFlightData(String[] sDepartingAirports, String[] sArrivalAirports, SearchPage searchPage) throws Exception {
		// verify departing airports
		for (int i = 0; i < sDepartureAirportIDs.length; i++) {
			WebElement element = getElement(sDepartureAirportIDs[i], "ID");
			String sDepartAirport = element.getAttribute("value");
			if (sDepartAirport.contains(sDepartingAirports[i]))
				log.debug("verifyFlightData() Flight departure " + i + " is a match.  " + sDepartAirport + "  " + sDepartingAirports[i]);
			else
				log.debug("verifyFlightData() Flight departure " + i + " is NOT a match.  " + sDepartAirport + "  " + sDepartingAirports[i]);
		}

		// verify arrival airports
		for (int i = 0; i < sArrivalAirportIDs.length; i++) {
			WebElement element = getElement(sArrivalAirportIDs[i], "ID");
			String sArrivalAirport = element.getAttribute("value");
			if (sArrivalAirport.contains(sArrivalAirports[i]))
				log.debug("verifyFlightData() Flight arrival " + i + " is a match.  " + sArrivalAirport + "  " + sArrivalAirports[i]);
			else
				log.debug("verifyFlightData() Flight arrival " + i + " is NOT a match.  " + sArrivalAirport + "  " + sArrivalAirports[i]);
		}

		// verify departing dates
		String[] sFormattedDepartureDates = searchPage.getFormattedDepartureDate();
		for (int i = 0; i < sDepartureDateIDs.length; i++) {
			WebElement element = getElement(sDepartureDateIDs[i], "ID");
			String sDate = element.getAttribute("value");  // date is in MM/DD/YYYY format
			if (sDate.equals(sFormattedDepartureDates[i]))
				log.debug("verifyFlightData() Flight date " + i + " is a match.  " + sDate + "  " + sFormattedDepartureDates[i]);
			else
				log.debug("verifyFlightData() Flight date " + i + " is NOT a match.  " + sDate + "  " + sFormattedDepartureDates[i]);	
		}

		log.info("verifyFlightData() completed");
	}

	/**
	 * Select qualifying flights for all 3 legs of the trip.  
	 * Tasks 5 and 11 describe what a qualifying flight is. 
	 * 
	 * @param   sDepartingAirports   string array of departing airport codes from the Search page
	 * @param   sArrivalAirports     string array of arriving airport codes from the Search page
	 * @return                       the index into the flight array for the Leg 3 portion of the trip
	 * @throws  Exception            let the startHere() method catch it
	 */
	private int selectQualifyingFlight(String[] sDepartingAirports, String[] sArrivalAirports) throws Exception {
		int flightIndex;  // array index of the first qualifying flight

		//***************** LEG 1 *****************
		// click the 'Non-stop' and '1 Stop' filter check boxes if they exist
		if (isElementPresent("stopFilter_stops-0", "ID")) {
			clickElement("stopFilter_stops-0", "ID");
		}
		if (isElementPresent("stopFilter_stops-1", "ID")) {
			clickElement("stopFilter_stops-1", "ID");
		}

		// obtain all the airline names on the Leg 1 Results page
		List <WebElement> allAirlineNamesList = getElements(allAirlineNames, "XPATH");

		// if there are no flights in the listing then randomly select 3 other airports
		if (allAirlineNamesList.size() == 0) {
			randomSelectNewAirports(sDepartingAirports, sArrivalAirports); 
		}

		// For some reason Orbitz is sending an interstitial page which seems to be 
		// refreshing the DOM and therefore causing stale elements.  So I need to call 
		// getElements again to get a refreshed list of airline name elements.
		allAirlineNamesList = getElements(allAirlineNames, "XPATH");

		// Determine the index of the first non-United flight
		// Fulfills Task 5a
		for (flightIndex = 0; flightIndex < allAirlineNamesList.size(); flightIndex++) {
			if (!allAirlineNamesList.get(flightIndex).getText().trim().equalsIgnoreCase("United")) {
				break;
			}	
		}

		// Check if there are only United Airline flights
		if (flightIndex == allAirlineNamesList.size()) {  
			// Could not find any non-United flights
			// Fulfills Task 11a
			flightIndex = 0;  // so select the first one because it's the cheapest
		}
		// flightIndex now has the cheapest qualifying flight index on Leg 1

		// Save the airline name even if it is United.
		// Only needed within this method for per-leg verification purposes.
		String sSelectedAirlineName = allAirlineNamesList.get(flightIndex).getText().trim();

		// save the price for verification on other pages
		List <WebElement> allFlightPricesList = getElements(allFlightPrices, "XPATH");
		sPrice = allFlightPricesList.get(flightIndex).getText().trim();  // "$XXX.XX"

		// save the number of stops for this leg of the trip
		List <WebElement> allStopsList = getElements(allStops, "XPATH");
		String sStop = allStopsList.get(flightIndex).getText().trim();
		if (sStop.contains("(Nonstop)"))
			numberFlightsPerLeg[0] = 1;
		else if (sStop.contains("(1 stop)"))
			numberFlightsPerLeg[0] = 2;
		else
			log.error("A Leg 1 flight with more than 1 stop was selected even though it should have been filtered out");

		// click the "Details and Baggage fees" link of the selected flight to get at its flight numbers
		List <WebElement> allDetailsAndBaggageLinksList = getElements(allDetailsAndBaggageLinks, "XPATH");
		clickElement(allDetailsAndBaggageLinksList.get(flightIndex));		

		// there are either 1 or 2 flight numbers
		List <WebElement> allFlightNumbersList = getElements(allFlightNumbers, "XPATH");
		// save flight numbers in a variable length ArrayList
		flightNumbersArray.add(allFlightNumbersList.get(0).getAttribute("data-test-airline-flight-number"));  // there is always at least 1 flight number
		if (numberFlightsPerLeg[0] == 2) {  // is there a second flight number?
			flightNumbersArray.add(allFlightNumbersList.get(1).getAttribute("data-test-airline-flight-number"));  // if so then save it
		}

		// click 1 or 2 buttons to get to the next Leg
		List <WebElement> allSelectButtonsList = getElements(allSelectButtons, "XPATH");  // gather all the Select buttons on the page
		log.debug("Leg 1 allSelectButtonsList size = " + allSelectButtonsList.size());
		clickElement(allSelectButtonsList.get(flightIndex));  // click the Select button for our chosen flight

		// Clicking the Select button may land us on the Leg 2 page OR it may open a 
		// "Rules and restrictions apply" field which contains a "Select this fare" button.
		// If this new field appears then clicking this new button will definitely take us to the Leg 2 page
		if (isElementPresent(selectThisFare, "XPATH")) {
			clickElement(selectThisFare, "XPATH");
		}

		//***************** LEG 2 *****************
		/*
		 * The process of selecting a qualifying Leg 2 flight is very similar to the above process for Leg 1.
		 * However, there are enough differences to make turning this into a method impractical.
		 * The most important difference involves the value of flightIndex which needs to sometimes be manipulated
		 * because the Leg 1 flight is listed at the top of the Leg 2 Results page and this affects indexing into
		 * certain arrays.
		 */

		// click the 'Non-stop' and '1 Stop' filter check boxes if they exist
		if (isElementPresent("stopFilter_stops-0", "ID")) {
			clickElement("stopFilter_stops-0", "ID");
		}
		if (isElementPresent("stopFilter_stops-1", "ID")) {
			clickElement("stopFilter_stops-1", "ID");
		}

		// verify that the prices are listed in ascending order
		selectSortByFilterOption();
		verifyPricesAreInLowestOrder();

		// obtain all the airline names on the Leg 2 Results page
		allAirlineNamesList = getElements(allAirlineNames, "XPATH");

		// Determine the index of the first Leg 2 flight whose airline name is the same as the Leg 1 airline name
		// There is no need to check for United Airlines, that was taken care of during Leg 1
		// Need to start the flightIndex from 1 due to the Leg 1 airline listed at the top of this page
		for (flightIndex = 1; flightIndex < allAirlineNamesList.size(); flightIndex++) {
			if (allAirlineNamesList.get(flightIndex).getText().trim().equalsIgnoreCase(sSelectedAirlineName)) {
				break;
			}	
		}

		// save the number of stops for this leg of the trip
		allStopsList = getElements(allStops, "XPATH");
		sStop = allStopsList.get(flightIndex).getText().trim();
		if (sStop.contains("(Nonstop)"))
			numberFlightsPerLeg[1] = 1;
		else if (sStop.contains("(1 stop)"))
			numberFlightsPerLeg[1] = 2;
		else
			log.debug("A Leg 2 flight with more than 1 stop was selected even though it should have been filtered out");

		// click the "Details and Baggage fees" link of the selected flight to get at its flight numbers
		allDetailsAndBaggageLinksList = getElements(allDetailsAndBaggageLinks, "XPATH");
		clickElement(allDetailsAndBaggageLinksList.get(flightIndex - 1));  // the -1 is due to the Leg 1 flight not having this link

		// there are either 1 or 2 flight numbers
		allFlightNumbersList = getElements(allFlightNumbers, "XPATH");
		// save flight numbers in a variable length ArrayList
		flightNumbersArray.add(allFlightNumbersList.get(0).getAttribute("data-test-airline-flight-number"));  // there is always at least 1 flight number
		if (numberFlightsPerLeg[1] == 2) {  // is there a second flight number?
			flightNumbersArray.add(allFlightNumbersList.get(1).getAttribute("data-test-airline-flight-number"));  // if so then save it
		}

		// click 1 or 2 buttons to get to the next Leg
		allSelectButtonsList = getElements(allSelectButtons, "XPATH");  // gather all the Select buttons on the page
		log.debug("Leg 2 allSelectButtonsList size = " + allSelectButtonsList.size());
		clickElement(allSelectButtonsList.get(flightIndex - 1));  // click the Select button for our chosen flight

		// Clicking the Select button may land us on the Leg 3 page OR it may open a 
		// "Rules and restrictions apply" field which contains a "Select this fare" button.
		// If this new field appears then clicking this new button will definitely take us to the Leg 3 page
		if (isElementPresent(selectThisFare, "XPATH")) {
			clickElement(selectThisFare, "XPATH");
		}

		//***************** LEG 3 *****************
		// the process of selecting a qualifying Leg 3 flight is very similar to the above process for Leg 2

		// click the 'Non-stop' and '1 Stop' filter check boxes if they exist		
		if (isElementPresent("stopFilter_stops-0", "ID")) {
			clickElement("stopFilter_stops-0", "ID");
		}
		if (isElementPresent("stopFilter_stops-1", "ID")) {
			clickElement("stopFilter_stops-1", "ID");
		}

		// verify that the prices are listed in ascending order
		selectSortByFilterOption();
		verifyPricesAreInLowestOrder();

		// obtain all the airline names on the Leg 3 Results page
		allAirlineNamesList = getElements(allAirlineNames, "XPATH");

		// Determine the index of the first Leg 3 flight whose airline name is the same as the Leg 1 airline name
		// Need to start the flightIndex from 2 due to the Leg 1 and Leg 2 airlines listed at the top of this page
		for (flightIndex = 2; flightIndex < allAirlineNamesList.size(); flightIndex++) {
			if (allAirlineNamesList.get(flightIndex).getText().trim().equalsIgnoreCase(sSelectedAirlineName)) {
				break;
			}	
		}

		// save the number of stops for this leg of the trip
		allStopsList = getElements(allStops, "XPATH");
		sStop = allStopsList.get(flightIndex).getText().trim();
		if (sStop.contains("(Nonstop)"))
			numberFlightsPerLeg[2] = 1;
		else if (sStop.contains("(1 stop)"))
			numberFlightsPerLeg[2] = 2;
		else
			log.debug("A Leg 3 flight with more than 1 stop was selected even though it should have been filtered out");

		// click the "Details and Baggage fees" link of the selected flight to get at its flight numbers
		allDetailsAndBaggageLinksList = getElements(allDetailsAndBaggageLinks, "XPATH");
		// the -2 is due to the Leg 1 & 2 flights at the top of the page not having this link
		clickElement(allDetailsAndBaggageLinksList.get(flightIndex-2));

		// there are either 1 or 2 flight numbers
		allFlightNumbersList = getElements(allFlightNumbers, "XPATH");
		// save flight numbers in a variable length ArrayList
		flightNumbersArray.add(allFlightNumbersList.get(0).getAttribute("data-test-airline-flight-number"));  // there is always at least 1 flight number
		if (numberFlightsPerLeg[2] == 2) {  // is there a second flight number?
			flightNumbersArray.add(allFlightNumbersList.get(1).getAttribute("data-test-airline-flight-number"));  // if so then save it
		}

		log.info("Nearly Completed with Leg 3");
		log.info("selectQualifyingFlight() was successful, about to click the Select button which will open the \"Review your trip\" "
				+ "page in a new window but need to save the parent (current) window handler first");
		// the -2 is due to the Leg 1 & 2 flights at the top of the page not having Select buttons
		return flightIndex - 2;  // index to the correct Select button on the Leg 3 Results page
	}

	/**
	 * Randomly obtain new 3 airport codes if the previous ones did not yield any flight results.
	 * Enter the new codes into the airport fields and try another search.  Repeat until flight results are obtained.
	 * Fulfills Task 11b.
	 * 
	 * @param   sDepartingAirports   string array of departing airport codes from the Search Results page
	 * @param   sArrivalAirports     string array of arriving airport codes from the Search Results page
	 * @throws  Exception            let the startHere() method catch it
	 */
	private void randomSelectNewAirports(String[] sDepartingAirports, String[] sArrivalAirports) throws Exception {
		List <WebElement> allAirlineNamesList = null;
		do {
			String sAirportCodes = "MEM BIL PDX ICT RIC BOI MKE SAT";  // 8 airport codes
			String[] arrayOfCodes = sAirportCodes.split("\\s+");  // any whitespace is a delimiter

			int i = 0;
			int j = 0;
			int iNewAirports = sArrivalAirports.length;
			while (i < iNewAirports) {
				int index = (int)(Math.random() * arrayOfCodes.length);  // random index 0-7
				String sNewAirport = arrayOfCodes[index];

				// Checking that 3 *different* airport codes are chosen.
				// Avoiding duplicates; could have used a Set Collections Framework which 
				// does not allow duplicates
				while (j < iNewAirports) {
					if (sArrivalAirports[j++].equals(sNewAirport)) {
						break;  // found a duplicate so don't store it
					}
				}
				if (j == iNewAirports) {  // no duplicate found
					sArrivalAirports[i++] = sNewAirport;  // so store it and increment i
				}
				// else there was a duplicate so get another random index and try again
			}
			// now have a new set of sArrivalAirports

			// re-do departures which will be passed along to the next 2 pages
			sDepartingAirports[1] = sArrivalAirports[0];
			sDepartingAirports[2] = sArrivalAirports[1];
			sDepartingAirports[0] = sArrivalAirports[2];

			// clear and insert new airport codes at the top of Search Results page
			for (j=0; j < 3; j++) {
				clearElement(sDepartureAirportIDs[j], "ID");
				clearElement(sArrivalAirportIDs[j], "ID");
				sendKeysElement(sDepartingAirports[j], sDepartureAirportIDs[j], "ID");
				sendKeysElement(sArrivalAirports[j], sArrivalAirportIDs[j], "ID");
			}

			// click the Search button
			clickElement("flight-wizard-search-button", "ID");

			// get the airline names from the newly generated Results listing
			allAirlineNamesList = getElements(allAirlineNames, "XPATH");
		} while (allAirlineNamesList.size() == 0);  // loop back up and try again if there are still no flight results
		log.info("randomSelectNewAirports() was successful");
	}

	/**
	 * Open the "Review Your Trip" page in a new window.  Selenium needs to shift its focus to this new window.
	 * This is similar to dealing with IFrames.  Precede this by finish choosing the qualifying Leg 3 flight.
	 * 
	 * @param   leg3flightIndex   the index to the Select button on the Leg 3 Results page
	 * @throws  Exception         let the startHere() method catch it
	 */
	private void clickAndOpenReviewPageInNewWindow(int leg3flightIndex) throws Exception {	
		// Prepare to switch window focus
		// Save the current handle
		parentHandle = driver.getWindowHandle();

		// click 1 or 2 buttons to get to the "Review your trip" page
		// while still on the Leg 3 Results page, gather all the Select buttons on the page
		List <WebElement> allSelectButtonsList = getElements(allSelectButtons, "XPATH");
		clickElement(allSelectButtonsList.get(leg3flightIndex));  // click the appropriate Select button	

		// Clicking the Select button may land us on the "Review your trip" page OR it
		// may open a "Rules and restrictions apply" field which contains a "Select this fare" button.
		// If this new field appears then clicking this new button will definitely take us to the "Review your trip" page.
		if (isElementPresent(selectThisFare, "XPATH")) {
			clickElement(selectThisFare, "XPATH");
		}

		// The "Review your trip" page is now open in a new window.
		// Each window has its own handle, get them all.
		Set<String> handles = driver.getWindowHandles();  // There are no duplicates in a Set

		// Switch between handles to change Selenium's focus
		for (String handle: handles) {
			if (!handle.equals(parentHandle)) {
				driver.switchTo().window(handle);
				break;
			}
		}
		log.info("clickSelectAndOpenNewWindow() was successful");
	}

	// Setters are not needed
	/**
	 * Getter method for the cost of the trip.
	 * 
	 * @return	string containing the price
	 */
	public String getSavedPrice() {
		return sPrice;
	}

	/**
	 * Getter method for the number of flights per leg.
	 * 
	 * @return	array with the number of flights per leg.  This is directly related to the number of stops per leg.
	 */
	public int[] getSavedNumberFlightsPerLeg() {
		return numberFlightsPerLeg;
	}

	/**
	 * Getter method for the flight numbers.
	 * 
	 * @return	arrayList containing the flight numbers
	 */	
	public ArrayList<String> getSavedFlightNumbers() {
		return flightNumbersArray;
	}

	/**
	 * Getter method for the parent window handle.  Used when switching focus back to a previous window.
	 * 
	 * @return	string containing the parent window handle
	 */		
	public String getParentHandle() {
		return parentHandle;
	}

	/**
	 * Driver for the Search Results page tests.  
	 * 
	 * @param  sDepartingAirports   string array of departing airport codes from the Search page
	 * @param  sArrivalAirports     string array of arriving airport codes from the Search page
	 * @param  searchPage           SearchPage object used to access its getter methods
	 * @return                      boolean true for success. false indicates an exception was thrown
	 */
	public boolean startHere(String[] sDepartingAirports, String[] sArrivalAirports, SearchPage searchPage) {
		// process all exceptions for this page here
		try {
			selectSortByFilterOption();
			verifyPricesAreInLowestOrder();
			verifyFlightData(sDepartingAirports, sArrivalAirports, searchPage);
			int leg3FlightIndex = selectQualifyingFlight(sDepartingAirports, sArrivalAirports);
			clickAndOpenReviewPageInNewWindow(leg3FlightIndex);
			return true;
		}
		catch (Exception e) {
			log.error("startHere() failed with this exception: ", e);
			return false;
		}
	}
}
