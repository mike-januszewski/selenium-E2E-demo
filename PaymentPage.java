package seleniumDemo;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * PaymentPage tests the Orbitz web page where the selected flight's payment request is displayed
 * @author mikej
 *
 */
public class PaymentPage extends SeleniumUtilities {
	private static final Logger log = LogManager.getLogger(PaymentPage.class);

	// Instance variables

/*
	// locators are gathered here for maintainability
	private final String destinationAirports = "//span[@class='arrival-airport-codes']";
	private final String priceField = "//span[@id='totalPriceForTrip']";
	// The 2 halves of the Number of Stops per Leg xpath
	private final String[] sNumberOfStopsPerLeg = { 
		"//article[@id='trip-summary']/div[@class='product_summary_wrapper']/div[",
		"]/div[@class='flight-information']/div[@class='duration-stop-information']/span[@class='stop-information']"
	};
	// The 2 halves of a Trip's primary flight number xpath
	private final String[] sPrimaryFlightNumber = { 
		"//article[@id='trip-summary']/div[@class='product_summary_wrapper']/div[",
		"]/div[@class='flight-information']/div[4]/span[@class='airline-name']"
	};
	// The 2 halves of a Trip's first connecting flight number xpath, if it exists
	private final String[] sFirstConnectingFlightNumber = { 
		"//article[@id='trip-summary']/div[@class='product_summary_wrapper']/div[",
		"]/div[@class='flight-information']/div[5]/span[@class='airline-name']"
	};
	// The 2 halves of a Trip's second connecting flight number xpath, if it exists
	private final String[] sSecondConnectingFlightNumber = { 
		"//article[@id='trip-summary']/div[@class='product_summary_wrapper']/div[",
		"]/div[@class='flight-information']/div[6]/span[@class='airline-name']"
	};
	*/

	// constructor
	public PaymentPage(WebDriver driver) {
		super(driver);
	}	


	// ***actions***
	// TASK 8.1
	// Objective: Verify that the 3 destination airports codes on the Payment page match the original codes
	// used on the Search page.
	// Strategy: Determine whether the flight destinations on the current page contain the unique 
	//    airport codes used when originally choosing the flights on the Search page.
/*	private void verifyFlightDestinations(String[] sArrivalAirports) throws Exception {
		List <WebElement> paymentDestinations = getElements(destinationAirports, "XPATH");
				
		for (int i=0; i<3; i++) { 
			if (paymentDestinations.get(i).getText().trim().equals(sArrivalAirports[i])) 
				log.debug("verifyFlightDestinations()  Destination " + i + " is a match.");
			else 
				log.debug("verifyFlightDestinations()  Destination " + i + " is NOT a match.");
		}

		log.info("verifyFlightDestinations() was successful");
	}
*/

	// Tasks 8.3 and 8.4
	// Verify that all flight numbers, including all connecting flight numbers, are the same 
	// on the Search Results page compared with their counterparts on the Payment page
	// (which is on the current window).
	// It has been my observation that the Payment page does not always display flight number information.
	// Sometimes it does and sometimes it does not.
	// I have a try/catch to deal with this.
	// Strategy: Gather the same type of data on the Payment page that was saved on the 
	//     Search Results page, namely the number of stops for each leg of the trip and all flight numbers.  
	//     Compare these two sets of data, if they are an identical then verification has been accomplished.  
	//     Knowing the number of stops for each of the 3 trips is valuable, it allows us to know exactly 
	//     how many flight numbers to look for on each leg of the journey.
	// Note: although this method is very similar to ReviewYourTrip.verifyFlightData() it is different
	// enough so that combining the two methods into one is not worth it.
/*	private void verifyFlightData(int[] iSavedNumStops, String[] sSavedFlightNumbers) throws Exception {	
		int[] iNumStopsPayment = new int[3];  // gather all 3 number-of-stops for each leg of the flight 
		int i, index, tripIndex;

		for (i=0, index=5; i<3; i++, index++) {
			// Get the number of stops for each leg of the trip.
			// These are either: "1 stop:"  or  "2 stops:"  or   "Nonstop"  
			String sStops = getElement(sNumberOfStopsPerLeg[0], index, sNumberOfStopsPerLeg[1], "XPATH").getText().trim();
							
			if (sStops.equals("Nonstop"))
				iNumStopsPayment[i] = 0;
			else if (sStops.equals("1 stop:"))
				iNumStopsPayment[i] = 1;
			else if (sStops.equals("2 stops:"))  // Conditions for Task 6.1 must have occurred
				iNumStopsPayment[i] = 2;
			else
				log.error("verifyFlightData()  Unexpected: >2 stops");
		}
		if (log.isDebugEnabled()) {
			for (i=0; i<3; i++)
				log.debug("verifyFlightData()  iNumStopsPayment[" + i + "] = " +  iNumStopsPayment[i]);
		}
		// Now that the number of stops for each leg of the trip has been captured
		// we can compare this to the same type of data saved from the Search Results page.  
		// If they are an identical match then verification has been accomplished.
		for (i=0; i<3; i++)
			if (iNumStopsPayment[i] != iSavedNumStops[i])
				break;
		if (i>=3)
			log.info("verifyFlightData()  Flight stops are identical");
		else 
			log.info("verifyFlightData()  Flight stops are NOT identical");


		// Save all the flight numbers on the Payment page if they are visible.
		// Non-existing connection flights are given an empty string value.
		String[] sFlightNumbersPayment = new String[9];  // max is 3x3=9 flight numbers
		try {
			for (i=0, index=0, tripIndex=5; i<3; i++, tripIndex++) {  // 3 legs of the trip
				// each flight has exactly 3 trips 
				// each trip has at least one and a max of 3 flight numbers
				// use the iNumStopsPayment[] values to determine how many flights to look for
				
				// there is always at least 1 flight per trip
				sFlightNumbersPayment[index++] = getElement(sPrimaryFlightNumber[0], tripIndex, sPrimaryFlightNumber[1], "XPATH").getText().trim();
						
				// possible first connecting flight number
				if (iNumStopsPayment[i] > 0) 
					sFlightNumbersPayment[index++] = getElement(sFirstConnectingFlightNumber[0], tripIndex, sFirstConnectingFlightNumber[1], "XPATH").getText().trim();
				else
					sFlightNumbersPayment[index++] = "";  // no first connecting flight number

				// possible second connecting flight number
				if (iNumStopsPayment[i] > 1) 
					sFlightNumbersPayment[index++] = getElement(sSecondConnectingFlightNumber[0], tripIndex, sSecondConnectingFlightNumber[1], "XPATH").getText().trim();
				else
					sFlightNumbersPayment[index++] = "";  // no second connecting flight number
			}
			if (log.isDebugEnabled()) {
				for (i=0; i<9; i++)
					log.debug("verifyFlightData()  sFlightNumbersPayment[" + i + "] = " +  sFlightNumbersPayment[i]);
			}

			// Now that all flight numbers, including all connecting flight numbers, for each leg of the 
			// trip has been captured we can compare it to the same type of data saved from the Search Results page.  
			// If they are an identical match then verification has been accomplished.
			for (i=0; i<9; i++)
				if (!(sFlightNumbersPayment[i].contains(sSavedFlightNumbers[i])))
					break;
			if (i>=9)
				log.info("verifyFlightData()  Flight numbers are identical");
			else 
				log.info("verifyFlightData()  Flight numbers are NOT identical");
		}
		catch(Exception e) {
			log.info("verifyFlightData()  Payment page is not displaying any flight number information.  Sometimes this happens, it is not an error");
		}
	}
*/

	// Getter & Setter methods
	
/*	
	totalPriceForTrip.   <-- id   $XXX.XX  needs trim first

	All 3 pairs of airport codes - each of the 3 pairs is in the form:  "Boston (BOS) to Miami (MIA)"
	//div[@class='location-info type-300']

	ALL flight numbers
	//div[@class='airlines-and-flight-info']       now do a .getText() to get  "      American Airlines 1234     "
	need to string manipulate to just get the number

	List.size() gives us the number of flight numbers on the payment page 

	*/
	private void verifyAirports(String[] sDepartingAirports, String[] sArrivalAirports) throws Exception {
		// Always 3 from-to pairs
		String[] toFrom = new String[6];
		//int parenthesis = 0;
		int i = 0, j = 0;
		
		// Build an array of string objects containing the departure/arrival airport codes for the 3 legs of the trip on the Payment Page
		List <WebElement> allDeparturesAndArrivals = getElements("//div[@class='location-info type-300']", "XPATH");  // .size() = 3
		for (WebElement w : allDeparturesAndArrivals) {  // loop 3 times.     "Boston (BOS) to Miami (MIA)"
			String zzz = w.getText().trim();
			int parenthesis = zzz.indexOf('(');  // first open paren
			toFrom[i++] = zzz.substring(parenthesis + 1, parenthesis + 4);  // avoid both parentheses, only retrieve the 3 letter airport code 
			
			parenthesis = zzz.lastIndexOf('(');  // last open paren
			toFrom[i++] = zzz.substring(parenthesis + 1, parenthesis + 4);  // avoid both parentheses, only retrieve the 3 letter airport code
		}
		
		// Compare the above array with the departure/arrival airport codes from the Search Page
		for (i = 0, j = 0; j < sArrivalAirports.length; j++) {  // 3 loops
			if (!toFrom[i++].equals(sDepartingAirports[j])) {
				log.debug("Departure airport mismatch on Leg #" + (j + 1));
			}
			if (!toFrom[i++].equals(sArrivalAirports[j])) {
				log.debug("Arrival airport mismatch on Leg #" + (j + 1));
			}
		}
		log.info("Payments Page:verifyFlightData() completed");
	}
	
	
	private void verifyPrice(SearchResultsPage searchResultsPage) throws Exception {
		String priceSR = searchResultsPage.getSavedPrice();
		WebElement pricePaymentElement = getElement("totalPriceForTrip", "ID");  // $XXX.XX
		String pricePayment = pricePaymentElement.getText().trim();
		
		if (pricePayment.equals(priceSR)) {
			log.debug("Payment price matches!");
		} 
		else {
			log.debug("Payment price mismatch");
		}
		log.info("Payments Page: verifyPrice() completed");
	}
	
	
	private void verifyFlightNumbers(SearchResultsPage searchResultsPage) throws Exception {	
		int iTotalNumberFlightsSearchResultsPage = 0;
		int[] numStops = searchResultsPage.getSavedNumberFlightsPerLeg();  // 3 index array
		for (int i : numStops) {
			iTotalNumberFlightsSearchResultsPage += i;
		}
		// iTotalNumberFlightsSearchResultsPage now has the total
		
		// All flight numbers
		List <WebElement> allFlightNumbersPP = getElements("//div[@class='airlines-and-flight-info']", "XPATH");  // "      American Airlines 1234     "

		int iTotalNumberFlightsPP = allFlightNumbersPP.size();
		
		// check that number of flights is equivalent
		if (iTotalNumberFlightsSearchResultsPage != iTotalNumberFlightsPP)
			log.debug("Number of flights on the 2 pages are not equal. FlightsSearchResultsPage = " + iTotalNumberFlightsSearchResultsPage +
					"FlightsPP = " + iTotalNumberFlightsPP);
	
		// Now check that the individual flight numbers are the same
		ArrayList<String> fnSRP = searchResultsPage.getSavedFlightNumbers();
		for (int i = 0; i < iTotalNumberFlightsPP; i++) {
			String x = fnSRP.get(i);
			String shortenedFlightNumber = allFlightNumbersPP.get(i).getText().trim();
			int lastBlank = shortenedFlightNumber.lastIndexOf(" ");
			String y = shortenedFlightNumber.substring(lastBlank + 1);  // flight number begins one character beyond the last blank
			if (x.equals(y))
				log.debug("Flight number is a match");	
			else
				log.debug("Flight number is NOT a match");
		}
		log.info("Payments Page:verifyFlightData() completed");
	}			

	
	/**
	 * 
	 * Process the Payment page.  
	 * @return true on success, false otherwise
	 */
	public boolean wholePage(String[] sDepartingAirports, String[] sArrivalAirports, SearchResultsPage searchResultsPage) {
		// process all exceptions here
		try {
			// From RYT
			verifyAirports(sDepartingAirports, sArrivalAirports);
			verifyPrice(searchResultsPage);
			verifyFlightNumbers(searchResultsPage);
			return true;
			
			
			
/*			
			verifyFlightDestinations(sArrivalAirports);
			verifyPrices(priceField, price);  //Task 8.2   This method is inherited from SeleniumDriverUtilities
			verifyFlightData(stops, flightNumbers);
			return true;
			*/
		}
		catch (Exception e) {
			log.error("Payment page failed with this exception: ", e);
			return false;
		}
	}
}

