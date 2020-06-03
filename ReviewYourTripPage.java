package seleniumDemo;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * ReviewYourTripPage tests the Orbitz web page where the selected flight data is displayed
 * @author mikej
 */
public class ReviewYourTripPage extends SeleniumUtilities {
	private static final Logger log = LogManager.getLogger(ReviewYourTripPage.class);

	// Instance variables

	 
	// locators are gathered here for maintainability
	private final String continueBookingButton = "bookButton";  // id
	
/*	private final String priceField = "//section[@class='tripSummaryContainer uitk-col']//span[@class='tripTotalPrice visuallyhidden']";
	private final String flightStopFields = "//section[@class='flightSummaryContainer uitk-col']/div[5]//div[@class='flex-content']/div[@class='flex-area-primary']/div[4]/div[@class='durationStops']/span[2]";

	private final String flightDestination3 = "//section[@class='flightSummaryContainer uitk-col']/div[5]/div[3]/div[@class='flex-content']/div[@class='flex-area-primary']/div[1]/ol/li[2]/span[@class='airport type-300']";
	// The 2 halves of the Select button xpath
	private final String[] sFlightDestinations = { 
		"//section[@class='flightSummaryContainer uitk-col']/div[5]/div[",
		"]/div[@class='flex-content']/div[@class='flex-area-primary']/div[1]/ol/li[2]/span[@class='airport type-300']"
	};
	// The 2 halves of the Show Flight and Baggage Fee Details xpath
	private final String[] sShowFlightAndBaggageFeeDetails = { 
		"//section[@class='flightSummaryContainer uitk-col']/div[5]/div[",
		"]/div[@class='flex-content']//div[1]/button[@type='button']"
	};
	// The 2 halves of the Flight Numbers Per Leg xpath
	private final String[] sFlightNumbersPerLeg = { 
			"//section[@class='flightSummaryContainer uitk-col']/div[5]/div[",
			"]/div[@class='flex-content']/div/div[5]/div[2]/div//div[@class='colDetails secondary']/ul[@class='nobullet']//li[@class='flightNumber']/span[2]"
	};
	*/
	
	// constructor
	public ReviewYourTripPage(WebDriver driver) {
		super(driver);
	}	



	// ***actions***
	// Task 7.1
	// Now that focus has been shifted to the new window displaying the Review Your Trip page
	// we are able to find the flight destination elements for verification purposes.
	//
	// This page takes a while to load in the new window so I'm giving it an explicit wait time.
	// I don't want to increase the implicit wait time because that affect ALL elements and I don't want
	// to unnecessarily slow down my program.
	// An explicit wait time is customized to a specific element.
	//
	// Strategy: Determine whether the flight destinations on the current page contain the unique 
	//    airport codes used when originally choosing the flights on the Search page.
	private void verifyAirports(String[] sDepartingAirports, String[] sArrivalAirports) throws Exception {
		// All 3 from-to , total =6
		List <WebElement> allDeparturesAndArrivals = getElements("//div[@class='airport type-300']", "XPATH");
		for (int i = 0, j = 0; j < sArrivalAirports.length; i+=2, j++) {
			if (!allDeparturesAndArrivals.get(i).getText().contains(sDepartingAirports[j])) {
				log.debug("Departure airport mismatch on Leg #" + (j + 1));
			}
			if (!allDeparturesAndArrivals.get(i+1).getText().contains(sArrivalAirports[j])) {
				log.debug("Arrival airport mismatch on Leg #" + (j + 1));
			}
		}
/*			
		WebDriverWait wait = new WebDriverWait(driver, 30);  // wait up to 30 seconds

		// I don't need to use this flight destination field, I just want to know that it has loaded.
		// If this flight destination field has loaded then the others elements have as well.
		@SuppressWarnings("unused")
		WebElement xxx = wait.until(
				ExpectedConditions.visibilityOf(getElement(flightDestination3, "XPATH")));
		
		for (int i=1; i<=3; i++)  {  // there are exactly 3 destinations
			// Check if the flight destination field contain a matching airport code from the Search Results page
			if (getElement(sFlightDestinations[0], i, sFlightDestinations[1], "XPATH").getText().contains(sArrivalAirports[i-1]))
				log.debug("verifyFlightDestinations()  Destination " + i + " is a match.");
			else
				log.debug("verifyFlightDestinations()  Destination " + i + " is NOT a match.");
		}*/

		//log.info("verifyFlightDestinations() was successful");
	}


	// Tasks 7.3 and 7.4
	// Verify that all flight numbers, including all connecting flight numbers, are the same 
	// on the Search Results page compared with their counterparts on the Review Your Trip page
	// (which is in the current window).
	// Strategy: Gather the same type of data on the Review Your Trip page that was saved on the 
	//     Search Results page, namely the number of stops for each leg of the trip and all flight numbers.  
	//     Compare these two sets of data, if they are an identical then verification has been accomplished.  
	//     Knowing the number of stops for each of the 3 trips is valuable, it allows us to know exactly 
	//     how many flight numbers to look for on each leg of the journey.
	//private void verifyFlightData(int[] iSavedNumStops, String[] sSavedFlightNumbers) throws Exception {	
	private void verifyFlightNumbers(SearchResultsPage searchResultsPage) throws Exception {	
		//public int[] getSavedNumberFlightsPerLeg() {
		//	return numberFlightsPerLeg;  // #stops
		//}
		//
		// check that number of flights matches
		// SRP
		int iTotalNumberFlightsSearchResultsPage = 0;
		int[] numStops = searchResultsPage.getSavedNumberFlightsPerLeg();  // 3 index array
		for (int i : numStops) {
			iTotalNumberFlightsSearchResultsPage += i;
		}
		// iTotalNumberFlightsSearchResultsPage now has the total
		
		// RYT
		//All 3 Detail links
		//button[@class='btn-text toggle-trigger']
		// try clicking all 3 links in order to make the flight numbers visible
		List <WebElement> allDetailButtons = getElements("//button[@class='btn-text toggle-trigger']", "XPATH");
		for (WebElement z : allDetailButtons) {
			z.click();
		}
		
		//All flight numbers
		List <WebElement> allFlightNumbersRYT = getElements("//li[@class='flightNumber']/span[2]", "XPATH");
		
		
		int iTotalNumberFlightsRYT = allFlightNumbersRYT.size();
		
		// check that number of flights is equivalent
		if (iTotalNumberFlightsSearchResultsPage != iTotalNumberFlightsRYT)
			log.debug("Number of flights on the 2 pages are not equal. FlightsSearchResultsPage = " + iTotalNumberFlightsSearchResultsPage +
					"FlightsRYT = " + iTotalNumberFlightsRYT);
			
		// Now check that the individual flight numbers are the same
		ArrayList<String> fnSRP = searchResultsPage.getSavedFlightNumbers();
		for (int i = 0; i < iTotalNumberFlightsRYT; i++) {
			String x = fnSRP.get(i);
			String y = allFlightNumbersRYT.get(i).getText();  // bad
			if (x.contains(y))
				log.debug("Flight number is a match");	
			else
				log.debug("Flight number is NOT a match");
		}
			
			
		/*	
		public ArrayList<String> getSavedFlightNumbers() {
			return flightNumbersArray;  // ALL the flight numbers
		}
		*/
	
		
	
		
	/*	int[] iNumStops = new int[3];
		int i, index;

		List <WebElement> flightStops = getElements(flightStopFields, "XPATH");
		if (flightStops.size() != 3) {  // safety test
			throw new Exception("Invalid number of flight stops");
		}
		for (i=0; i<3; i++) {
			// Get the number of stops for each of the 3 legs of the trip.
			// These values are either:   ", Nonstop"  or   ", 1 stop"   or   ", 2 stops"
			// If ", 2 stops" is encountered then the condition for Task 6.1 must have occurred.
			String sStops = flightStops.get(i).getText().trim();

			if (sStops.equals(", Nonstop"))
				iNumStops[i] = 0;
			else if (sStops.equals(", 1 stop"))
				iNumStops[i] = 1;
			else if (sStops.equals(", 2 stops")) 
				iNumStops[i] = 2;  // Conditions for Task 6.1 must have occurred
			else 
				log.error("verifyFlightData()  Unexpected: >2 stops");
		}
		if (log.isDebugEnabled()) {
			for (i=0; i<3; i++)
				log.debug("verifyFlightData()  iNumStops[" + i + "] = " +  iNumStops[i]);
		}

		// Save all the flight numbers on the Review Your Trip page (on the current window).
		// Non-existing connection flights are given an empty string value.
		String[] sFlightNumbers = new String[9];  // max is 3x3=9 flight numbers
		for (i=1, index=0; i<=3; i++) {  // legs of the journey
			// click the "Show flight and baggage fee details" button
			clickElement(sShowFlightAndBaggageFeeDetails[0], i, sShowFlightAndBaggageFeeDetails[1], "XPATH");
						
			// Locate all the flight numbers within each leg of a trip
			// These were difficult elements to get unique xpaths for.  Orbitz seems to change the number of div tags in the DOM
			// depending on whether an international flight was chosen.  So I had to bypass that div tag with a relative locator (//)
			// and come up with an xpath that works for both national and international flights.
			List <WebElement> flightNumbers = getElements(sFlightNumbersPerLeg[0], i, sFlightNumbersPerLeg[1], "XPATH");
			
			// each flight has exactly 3 trips 
			// each trip has at least one and a max of 3 flight numbers
			// use the iNumStops[] values to determine how many flights to look for.
			// Primary flight:
			sFlightNumbers[index++] = flightNumbers.get(0).getText().trim();

			// possible first connecting flight number
			if (iNumStops[i-1] > 0) 
				sFlightNumbers[index++] = flightNumbers.get(1).getText().trim();
			else
				sFlightNumbers[index++] = "";  // no first connecting flight number
			
			// possible second connecting flight number
			if (iNumStops[i-1] > 1) 
				sFlightNumbers[index++] = flightNumbers.get(2).getText().trim();
			else
				sFlightNumbers[index++] = "";  // no second connecting flight number
		}
		if (log.isDebugEnabled()) {
			for (i=0; i<9; i++)
				log.debug("verifyFlightData()  sFlightNumbers[" + i + "] = " +  sFlightNumbers[i]);
		}

		// Now that the number of stops for each leg of the trip has been captured as well as
		// all flight numbers, including all connecting flight numbers, we can compare this data
		// to the same type of data saved on the Search Results page.  If they are an identical match
		// then verification has been accomplished.
		for (i=0; i<9; i++)
			if (!(sFlightNumbers[i].equals(sSavedFlightNumbers[i])))
				break;
		if (i>=9)
			log.info("verifyFlightData()  Flight numbers are identical");
		else 
			log.info("verifyFlightData()  Flight numbers are NOT identical");

		for (i=0; i<3; i++)
			if (iNumStops[i] != iSavedNumStops[i])
				break;
		if (i>=3)
			log.info("verifyFlightData()  Flight stops are identical");
		else 
			log.info("verifyFlightData()  Flight stops are NOT identical");
*/
		log.info("verifyFlightData() was successful");
	}


	private void verifyPrice(SearchResultsPage searchResultsPage) throws Exception {
		String priceSR = searchResultsPage.getSavedPrice();
		WebElement priceRYTelement = getElement("//section[contains(@class,'tripSummaryContainer desktopView')]//span[@class='packagePriceTotal']", "XPATH");
		//WebElement priceDollarsRYT = getElement("//span[@class='packagePriceTotal']", "XPATH");  // $XXX
		//WebElement priceCentsRYT = getElement("//span[@class='packagePriceTotal']/sup", "XPATH");  // .XX
		//String s1 = priceDollarsRYT.getText().trim();
		//String s2 = priceCentsRYT.getText().trim();
		//String combinedPriceRYT = s1.concat(s2);
		String priceRYT = priceRYTelement.getText().trim();
		
		//section[contains(@class,'tripSummaryContainer desktopView')]//span[@class='packagePriceTotal']
		
		//String combinedPriceRYT = priceDollarsRYT.getText().trim().concat(priceCentsRYT.getText().trim());
		// if (combinedPriceRYT.contains(priceSR)) {			
		if (priceRYT.equals(priceSR)) {
			log.debug("Price matches!");
		} 
		else {
			log.debug("Price mismatch");
		}
	}
	
	
	// click the "Continue Booking" button
	private void clickContinueBookingButton() throws Exception {	
		clickElement(continueBookingButton, "ID");

		log.info("clickContinueBookingButton() was successful");
	}


	// Getter & Setter methods
	
	
/*	
	All 3 from-to , total =6
	//div[@class='airport type-300']
	 
	price(s)
	//span[@class='packagePriceTotal']/sup

	All 3 Detail links
	//button[@class='btn-text toggle-trigger']

	All flight number WITHOUT opening the Detail links!
	//li[@class='flightNumber']/span[2]
*/

	/**
	 * Process the Review Your Trip page.  
	 * @return true on success, false otherwise
	 */
	//public boolean wholePage(String[] sArrivalAirports, int[] iSavedNumStops, String[] sSavedFlightNumbers, String savedPrice) {
	public boolean wholePage(String[] sDepartingAirports, String[] sArrivalAirports, SearchResultsPage searchResultsPage) {
		// process all exceptions here
		try {
			verifyAirports(sDepartingAirports, sArrivalAirports);
			verifyPrice(searchResultsPage);
			verifyFlightNumbers(searchResultsPage);
			
			//verifyPrices(priceField, savedPrice);  //Task 7.2   This method is inherited from SeleniumDriverUtilities
			//verifyFlightData(iSavedNumStops, sSavedFlightNumbers);
			
			clickContinueBookingButton();
			return true;
		}
		catch (Exception e) {
			log.error("failed with this exception: ", e);
			return false;
		}
	}
}