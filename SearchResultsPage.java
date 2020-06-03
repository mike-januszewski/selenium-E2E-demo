package seleniumDemo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
//import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
//import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * SearchResultsPage tests the Orbitz web page where flight search results are listed
 * @author mikej
 *
 */
public class SearchResultsPage extends SeleniumUtilities {
	private static final Logger log = LogManager.getLogger(SearchResultsPage.class);

	// Instance variables
	/*private int numberFlightsOnPage=0;  
	private List <WebElement> flightPrices;
	private String sSavedPrice;  // needed for future verification
	private int[] iSavedNumberOfStops;  // needed for future verification
	private String[] sSavedFlightNumbers;  // needed for future verification
	private String parentHandle;*/

/*	private String[] sFlightNumbersLeg1 = new String[2];
	private String[] sFlightNumbersLeg2 = new String[2];
	private String[] sFlightNumbersLeg3 = new String[2];*/
	private ArrayList<String> flightNumbersArray = new ArrayList<String>();
	private String sPrice;
	private int[] numberFlightsPerLeg = new int[3];  // #stops
	private String parentHandle;
	
	
	// locators are gathered here for maintainability
	//private final String selectedPriceField = "//div[@id='sortBar']//select[@name='sort']";

	//private final String allFlightPrices = "//ul[@id='flightModuleList']//div/div[1]//span[@class='full-bold no-wrap']";
	
	// these 2 work for all 3 Leg pages!!
	private final String selectedPriceField = "sortDropdown";  // id
	private final String allFlightPrices = "//span[@class='full-bold no-wrap']";  // xpath
	
// No longer exists?  All results appear on the same page now,
	//private final String nextButtonListing = "//nav[@id='paginationControl']//abbr[contains(text(),'Next')]";

	// in order of departures
	private final String[] sDepartureAirportIDs = {  
		//"//div[@id='flight-info-container']/div[1]//div[@class='col origin']/div[1]//input[@name='departure-locations']",
		//"//div[@id='flight-info-container']/div[2]//div[@class='col origin']/div[1]//input[@name='departure-locations']",
		//"//div[@id='more-trips-pane-content']/div/div[1]//div[@class='col origin']/div[1]//input[@name='departure-locations']"	
		"departure-airport-1",
		"departure-airport-2",
		"departure-airport-3"
	};
	// in order of arrivals
	private final String[] sArrivalAirportIDs = {  
		//"//div[@id='flight-info-container']/div[1]//div[@class='col destination']/div[1]//input[@name='arrival-locations']",
		//"//div[@id='flight-info-container']/div[2]//div[@class='col destination']/div[1]//input[@name='arrival-locations']",
		//"//div[@id='more-trips-pane-content']/div/div[1]//div[@class='col destination']/div[1]//input[@name='arrival-locations']"
		"arrival-airport-1",
		"arrival-airport-2",
		"arrival-airport-3"
	};
	// in order of departing dates
	private final String[] sDepartureDateIDs = {  
		//"//div[@id='flight-info-container']//label/input[@id='departure-date-1']",
		//"//div[@id='flight-info-container']//label/input[@id='departure-date-2']",
		//"//div[@id='flight-info-container']//label/input[@id='departure-date-3']"
		"departure-date-1",
		"departure-date-2",
		"departure-date-3"
	};
	/*
	// The 2 halves of the Airline Number of Stops xpath
	private final String[] sAirlineNumStops = { 
		"//ul[@id='flightModuleList']/li[",
		"]/div[@class='grid-container standard-padding']/div[1]/div[1]//div[2]/div/div[2]/div[1]/span[@class='number-stops']"
	};
	// The 2 halves of the Airline Names xpath
	private final String[] sAirlineNames = { 
		"//ul[@id='flightModuleList']/li[",
		"]/div[@class='grid-container standard-padding']/div[1]/div[1]//div[2]/div/div[1]/div[2]/span"
	};
	// The 2 halves of the Details & Baggage Fees xpath
	private final String[] sDetailsAndBaggageFees = { 
		"//ul[@id='flightModuleList']/li[",
		"]//a[@role='button']/span[@class='show-flight-details']"
	};
	// The 3 parts of the Selected Flight's Number of Stops xpath
	private final String[] sSelectedFlightNumStops = { 
		"//ul[@id='flightModuleList']/li[", 
		"]/div[@class='grid-container standard-padding']/div[1]/div[1]/div[", 
		"]/div[2]/div/div[2]/div[1]/span[@class='number-stops']"
	};
	// The 2 halves of a Trip Number button xpath
	private final String[] sTripNumberButton = { 
		"//div[@id='flight-details-tabs-offer']/ul/li[",
		"]/button[@type='button']"
	};
	// The 2 halves of a Trip's primary flight number xpath
	private final String[] sPrimaryFlightNumber = { 
		"//div[@id='flight-details-tabs-offer']/div/section[",
		"]/div/div[1]/dl/dd[3]"
	};
	// The 2 halves of a Trip's first connecting flight number xpath
	private final String[] sFirstConnectingFlightNumber = { 
		"//div[@id='flight-details-tabs-offer']/div/section[",
		"]/div/div[3]/dl/dd[3]"
	};
	// The 2 halves of a Trip's second connecting flight number xpath
	private final String[] sSecondConnectingFlightNumber = { 
		"//div[@id='flight-details-tabs-offer']/div/section[",
		"]/div/div[5]/dl/dd[3]"
	};
	// The 2 halves of the Select button xpath
	private final String[] sSelectButton = { 
		"//ul[@id='flightModuleList']/li[",
		"]/div/div[1]//button[@type='button']"
	};  */

	
	// constructor
	public SearchResultsPage(WebDriver driver) {
		super(driver);
	}	


	// ***actions***
	// Task 5.1
	// Verify that the "Sort by" dropdown menu on the left side of the page has "Price (Lowest)" selected. 
	// If it doesn't then select and click that option.
	// Do this before checking whether flight prices are actually in lowest priced order
	private void selectPriceDropdownIsLowest() throws Exception {
		WebElement element = getElement(selectedPriceField, "ID");
		Select selectPrice = new Select(element);
		WebElement option = selectPrice.getFirstSelectedOption();
		String defaultPrice = option.getText();
		if (defaultPrice.equals("Price (Lowest)")) {
			log.info("price is already in lowest order");
		}
		else {
			selectPrice.selectByVisibleText("Price (Lowest)");
			element.click();  // click to make sure the "Price (Lowest)" option is chosen
			log.info("click the \"Price (Lowest)\" option");
		}
	}


	// Task 5.2
	// Note:  Because it takes a relatively long time for the Search Results web page to fully 
	// load I will give this an explicit wait time.
	// I don't want to increase the implicit wait time because that affects ALL elements and I don't want
	// to unnecessarily slow down my program.
	// An explicit wait time is customized to a specific element.
	// Objective:  Verify that the flight prices really are in lowest order.
	// Strategy:  Find all the price elements on this page and store them in a List. 
	// A List works well because, unlike a Set, it allows duplicates.  It also retains order.
	// Look at pairs of flight prices from top to bottom and confirm that the 
	// upper value is not greater than the next value. Repeat with another pair that uses
	// the second value from the previous pair and the next value after that.
	// For example, test element+0 with element+1, then element+1 with element+2.
	// Rather than exhaustively testing each flight selection page I'm only testing the
	// first page of search results and assuming that if this page is correctly ordered than the other 
	// pages are as well.
	
	// Check all prices before or after options are set?????
	private void verifyPricesAreInLowestOrder() throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, 30);  // wait up to 30 seconds
log.info("$$$$$$$$$$$ 8a $$$$$$$$$$$$$$");		
		List <WebElement> flightPrices = wait.until(
				ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(allFlightPrices)));
//				.visibilityOfAllElementsLocatedBy(By.xpath(allFlightPrices)));
		//numberFlightsOnPage = flightPrices.size();  // the number of flights listed on the current page - ALL NOW?!
log.info("$$$$$$$$$$$ 8b $$$$$$$$$$$$$$");
		int dollarSignIndex = flightPrices.get(0).getText().lastIndexOf("$");
		String prevPrice = flightPrices.get(0).getText().substring(dollarSignIndex + 1).trim();  // truncate the dollar sign.  $X,XXX.XX becomes X,XXX.XX
				
//		String x = prevPrice.replaceAll(",",  "");
//		String y = x.toString();  // really needed??
		float fPreviousPrice = Float.valueOf(prevPrice.replaceAll(",",  ""));  // delete all commas and convert the string to a float
log.info("$$$$$$$$$$$ 8c $$$$$$$$$$$$$$");		
		boolean bAreSorted = true;
		
		// PREVENT STALE - REFRESHING
		//flightPrices = wait.until(
		//		ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(allFlightPrices)));
		
		for (WebElement curEl : flightPrices) {
log.info("$$$$$$$$$$$ 8d $$$$$$$$$$$$$$");			
			dollarSignIndex = curEl.getText().lastIndexOf("$");
			String curPrice = curEl.getText().substring(dollarSignIndex + 1).trim();  // truncate the dollar sign.  $X,XXX.XX becomes X,XXX.XX
					
log.info("$$$$$$$$$$$ 8e $$$$$$$$$$$$$$   curPrice = " + curPrice);
//			String curPrice = curEl.getText().trim().substring(1);
//			x = curPrice.replaceAll(",",  "");
//			y = x.toString();  // really needed??
			float fCurrentPrice = Float.valueOf(curPrice.replaceAll(",",  ""));  // delete all commas and convert the string to a float
//			float fCurrentPrice = Float.valueOf(y);  // can this handle a decimal point?
log.info("$$$$$$$$$$$ 8f $$$$$$$$$$$$$$   fCurrentPrice = " + fCurrentPrice);
			
			if (fCurrentPrice < fPreviousPrice) {
				bAreSorted = false;
				break;
			}
			
			fPreviousPrice = fCurrentPrice;
		}
		if (bAreSorted)
			log.info("verifyPricesAreInLowestOrder()  All prices are really in lowest order");
		else
			log.info("verifyPricesAreInLowestOrder()  Prices are NOT in lowest order");
	}


	// Task 5.3
	// Objective: Verify that the flight information on the current page matches the flight data
	// input into the Search page.
	// Strategy: I am taking advantage of the fact that each airport has a unique three letter
	// (all uppercase) identification code.  For example, SFO = San Francisco, DEN = Denver, etc...
	// If the Search Results Page contains the same airport code given to the Search Page then 
	// verification is confirmed.
	// Note: I don't have to click on "Show more trips" to reach Flight-3 elements
	private void verifyFlightData(String[] sDepartingAirports, String[] sArrivalAirports, String[] sFormattedDepartureDates) throws Exception {
		// verify departing airports
		for (int i=0; i < sDepartureAirportIDs.length; i++) {
			WebElement element = getElement(sDepartureAirportIDs[i], "ID");
			String sDepartAirport = element.getAttribute("value");
			if (sDepartAirport.contains(sDepartingAirports[i]))
				log.debug("verifyFlightData() Flight departure " + i + " is a match.  " + sDepartAirport + "  " + sDepartingAirports[i]);
			else
				log.debug("verifyFlightData() Flight departure " + i + " is NOT a match.  " + sDepartAirport + "  " + sDepartingAirports[i]);
		}

		// verify arrival airports
		for (int i=0; i < sArrivalAirportIDs.length; i++) {
			WebElement element = getElement(sArrivalAirportIDs[i], "ID");
			String sArrivalAirport = element.getAttribute("value");
			if (sArrivalAirport.contains(sArrivalAirports[i]))
				log.debug("verifyFlightData() Flight arrival " + i + " is a match.  " + sArrivalAirport + "  " + sArrivalAirports[i]);
			else
				log.debug("verifyFlightData() Flight arrival " + i + " is NOT a match.  " + sArrivalAirport + "  " + sArrivalAirports[i]);
		}

		// verify departing dates
		for (int i=0; i < sDepartureDateIDs.length; i++) {
			WebElement element = getElement(sDepartureDateIDs[i], "ID");
			String sDate = element.getAttribute("value");  // date in MM/DD/YYYY format
			if (sDate.equals(sFormattedDepartureDates[i]))
				log.debug("verifyFlightData() Flight date " + i + " is a match.  " + sDate + "  " + sFormattedDepartureDates[i]);
			else
				log.debug("verifyFlightData() Flight date " + i + " is NOT a match.  " + sDate + "  " + sFormattedDepartureDates[i]);	
		}

		log.info("verifyFlightData() was successful");
	}


	// Tasks 6 and 6.1
	// Note: Task 6.1 describes a condition where a leg of a trip with more than one stop can be selected.
	// Strategy:  Having already confirmed that the flights are listed in lowest-to-highest
	// priced order, examine flights in order by first looking at their number of stops and then
	// by comparing their airline names while being on the lookout for "United".  If the task 6.1 condition occurs
	// then disable the check for the number of stops.
	
	
	
	/*
	 * Have already confirmed that the flights are listed in lowest-to-highest pricing order via the Sort menu.
	 * Click the 'Non-stop' and '1 Stop' filter check boxes if they exist.
	 * Examine the first flight (the cheapest) and observe its airline name.
	 * If it is 'United' then go to the next flight.  Repeat this until a flight by an airline other than United is found.
	 * If there are only United flights in the list then choose the first, and therefore the cheapest, one.
	 * Click the appropriate flight's Select button which brings up the second leg flight selections.
	 * Verify that the prices for Leg 2 are in ascending order
	 * Select the first flight.  (verifying airline name and number of stops will be done later)
	 * Do the same on the Leg 3 page as was done on the Leg 2 page.  This lands us on the "Review your trip" page.
	 * 
	 * N.B. - "Review your trip" page - as long as all 3 airlines are the same name we are golden, even if United.
	 * The ONLY way it could be United is if there were only United on Leg 1.  Last resort...
	 */
	//private int selectQualifyingFlight() throws Exception {
	private int selectQualifyingFlight() throws Exception {
		int flightIndex;

log.info("$$$$$$$$$$$Entering selectQualifyingFlight()$$$$$$$$$$$$$$");
		
//driver.manage().timeouts().implicitlyWait(45, TimeUnit.SECONDS);  // I have a relatively slow Internet connection

		if (isElementPresent("stopFilter_stops-0", "ID")) {
			clickElement("stopFilter_stops-0", "ID");
		}
		if (isElementPresent("stopFilter_stops-1", "ID")) {
			clickElement("stopFilter_stops-1", "ID");
		}
log.info("$$$$$$$$$$$ 1 $$$$$$$$$$$$$$");
		
		List <WebElement> sAllAirlineNames = getElements("//span[@data-test-id='airline-name']", "XPATH");  // this locator works for all 3 legs
		// check if there are no flights in the listing
		if (sAllAirlineNames.size() == 0) {
			log.debug("No flights were returned!  Future: retry with randomly changed destinations");	
		}
log.info("$$$$$$$$$$$ 2a $$$$$$$$$$$$$$" + "    sAllAirlineNames.size() = " + sAllAirlineNames.size());
// TESTING STALE - DOM is refreshing????  need to re-get sAllAirlineNames	
Thread.sleep(10000);	 // needed!
sAllAirlineNames = getElements("//span[@data-test-id='airline-name']", "XPATH");  // this locator works for all 3 legs
//driver.manage().timeouts().implicitlyWait(25, TimeUnit.SECONDS);  // I have a relatively slow Internet connection



		for (flightIndex = 0; flightIndex < sAllAirlineNames.size(); flightIndex++) {
log.info("$$$$$$$$$$$ 2b $$$$$$$$$$$$$$");
			if (!sAllAirlineNames.get(flightIndex).getText().trim().equalsIgnoreCase("United")) {
				break;
			}	
		}
log.info("$$$$$$$$$$$ 2c $$$$$$$$$$$$$$");		
		if (flightIndex == sAllAirlineNames.size()) {  // could not find any non-United flights
			flightIndex = 0;  // The first flight must have been a United flight so choose it because it's the cheapest
		}
		// flightIndex now has the cheapest qualifying flight index on Leg 1
		
		
log.info("$$$$$$$$$$$ 3 $$$$$$$$$$$$$$");			
		// get airline name even if it is United.  Only needed within this method for verification per Leg
		String sSelectedAirlineName = sAllAirlineNames.get(flightIndex).getText().trim();
log.info("$$$$$$$$$$$ 4 $$$$$$$$$$$$$$");							
		// save price for future verification - one time only
		List <WebElement> sAllPrices = getElements("//span[@class='full-bold no-wrap']", "XPATH");
		sPrice = sAllPrices.get(flightIndex).getText().trim();  // $XXX.XX
log.info("$$$$$$$$$$$ 5 $$$$$$$$$$$$$$");		
		// get #stops.  Fulfills a verification!
		List <WebElement> sAllStops = getElements("//span[@class='number-stops']", "XPATH");
		String sStop = sAllStops.get(flightIndex).getText().trim();
		if (sStop.contains("(Nonstop)"))
			numberFlightsPerLeg[0] = 1;
		else if (sStop.contains("(1 stop)"))
			numberFlightsPerLeg[0] = 2;
		else
			log.debug("A Leg 1 flight with more than 1 stop was selected even though it should have been filtered out");	
log.info("$$$$$$$$$$$ 6 $$$$$$$$$$$$$$");			
		// SAVE Flight Numbers
		// click open "Details and Baggage fees" to get at flight numbers.  Use #stops array to know how many flight numbers to look for.
		List <WebElement> sAllDetailsLinks = getElements("//a[@data-test-id='flight-details-link']", "XPATH");

		log.info("$$$$$$$$$$$ 6 $$$$$$$$$$$$$$  numberFlightsPerLeg[0] = " + numberFlightsPerLeg[0]);	
		log.info("$$$$$$$$$$$ 6 $$$$$$$$$$$$$$  flightIndex = " + flightIndex);	
		log.info("$$$$$$$$$$$ 6 $$$$$$$$$$$$$$  sAllDetailsLinks.size() = " + sAllDetailsLinks.size());	

		
		
		sAllDetailsLinks.get(flightIndex).click();
		List <WebElement> sAllFlightNumbers = getElements("//li[@class='details-utility-item-value segment-info-details-item flight']", "XPATH");  // either 1 or 2 flight numbers

		flightNumbersArray.add(sAllFlightNumbers.get(0).getAttribute("data-test-airline-flight-number"));
//		sFlightNumbersLeg1[0] = sAllFlightNumbers.get(0).getAttribute("data-test-airline-flight-number");  // there is always at least 1 flight number
		if (numberFlightsPerLeg[0] == 2) { // true = there is a second flight number
			//sFlightNumbersLeg1[1] = sAllFlightNumbers.get(1).getAttribute("data-test-airline-flight-number");
			flightNumbersArray.add(sAllFlightNumbers.get(1).getAttribute("data-test-airline-flight-number"));
		}
log.info("$$$$$$$$$$$ 7 $$$$$$$$$$$$$$");		
log.info("$$$$$$$$$$$ 7 $$$$$$$$$$$$$$    flightNumbersArray[0] = " + flightNumbersArray.get(0));
log.info("$$$$$$$$$$$ 7 $$$$$$$$$$$$$$    flightNumbersArray[1] = " + flightNumbersArray.get(1));  // may be empty

/*
Select button, top
/html[1]/body[1]/div[2]/div[13]/section[1]/div[1]/div[11]/ul[1]/li[1]/div[1]/div[1]/div[2]/div[1]/div[2]/button[1]

inner select
/html[1]/body[1]/div[2]/div[13]/section[1]/div[1]/div[11]/ul[1]/li[1]/div[2]/div[1]/div[1]/div[1]/div[1]/button[1]

All Selects and Select This Fare
//button[@class='btn-secondary btn-action t-select-btn']

*/



		// click 1 or 2 buttons to get to the next Leg
		//List <WebElement> allSelectButtons = getElements("//div[1]/div[2]/div[2]/div[1]/div[2]/button[1]", "XPATH");  // only gets the 1 and done Selects
		//List <WebElement> allSelectButtons = getElements("//button[@class='btn-secondary btn-action t-select-btn']", "XPATH"); // ALL both Selects
		List <WebElement> allSelectButtons = getElements("//button[@data-test-id='select-button']", "XPATH"); // ONLY the Select buttons
		//for (WebElement z : allSelectButtons) {
		//	log.info("Select buttons text = " + z.getText());
		//allSelectButtons
		log.info("allSelectButtons size = " + allSelectButtons.size());
		
		allSelectButtons.get(flightIndex).click();		
		// Clicking the Select button may land us on the Leg 2 page OR it
		// may open a "Rules and restrictions apply" field which contains a "Select this fare" button.
		// If this new field appears then clicking this new button will take us to the Leg 2 page
		if (isElementPresent("//div[contains(@class,'toggle-pane fade open')]//button[contains(@class,'btn-secondary btn-action t-select-btn')]", "XPATH")) {
			clickElement("//div[contains(@class,'toggle-pane fade open')]//button[contains(@class,'btn-secondary btn-action t-select-btn')]", "XPATH");
		}
		
		log.info("$$$$$$$$$$$   Completed Leg 1   $$$$$$$$$$$$$$");

		//*****************LEG 2
//Thread.sleep(15000);	
		if (isElementPresent("stopFilter_stops-0", "ID")) {
			clickElement("stopFilter_stops-0", "ID");
		}
		if (isElementPresent("stopFilter_stops-1", "ID")) {
			clickElement("stopFilter_stops-1", "ID");
		}
log.info("$$$$$$$$$$$ 8 $$$$$$$$$$$$$$");
		// verifyPricesAreInLowestOrder() has an explicit wait
		selectPriceDropdownIsLowest();
		verifyPricesAreInLowestOrder();
log.info("$$$$$$$$$$$ 9 $$$$$$$$$$$$$$");


		// get name and confirm it's the same as Leg 1.     Fulfills a verification!
		sAllAirlineNames = getElements("//span[@data-test-id='airline-name']", "XPATH");  // this locator works for all 3 legs
		// Verify we're selecting a flight with the same airline name
		
		// NEED TO START AT INDEX 1 TO AVOID LEG 1 ENTRY AT THE TOP!!!!!!!
		for (flightIndex = 1; flightIndex < sAllAirlineNames.size(); flightIndex++) {
			if (sAllAirlineNames.get(flightIndex).getText().trim().equalsIgnoreCase(sSelectedAirlineName)) {
				break;
			}	
		}
		// flightIndex takes the Leg 1 entry into consideration
		
		
		// save #stops.    Fulfills a verification!
		sAllStops = getElements("//span[@class='number-stops']", "XPATH");
		sStop = sAllStops.get(flightIndex).getText().trim();
		if (sStop.contains("(Nonstop)"))
			numberFlightsPerLeg[1] = 1;
		else if (sStop.contains("(1 stop)"))
			numberFlightsPerLeg[1] = 2;
		else
			log.debug("A Leg 2 flight with more than 1 stop was selected even though it should have been filtered out");
		
		
		log.info("$$$$$$$$$$$ 10 $$$$$$$$$$$$$$  numberFlightsPerLeg[1] = " + numberFlightsPerLeg[1]);	
		log.info("$$$$$$$$$$$ 10 $$$$$$$$$$$$$$  flightIndex = " + flightIndex);	
		
		
		
		// SAVE Flight Numbers
		// click open "Details and Baggage fees" to get at flight numbers.  Use #stops array to know how many flight numbers to look for.
		sAllDetailsLinks = getElements("//a[@data-test-id='flight-details-link']", "XPATH");
		
log.info("$$$$$$$$$$$ 10 $$$$$$$$$$$$$$  sAllDetailsLinks.size() = " + sAllDetailsLinks.size());			
		// -1 due to there is no Details link for the Leg 1 flight at top of page
		sAllDetailsLinks.get(flightIndex - 1).click();
//Thread.sleep(6000);	
		
		
		sAllFlightNumbers = getElements("//li[@class='details-utility-item-value segment-info-details-item flight']", "XPATH");  // either 1 or 2 flight numbers
		
log.info("$$$$$$$$$$$ 10 $$$$$$$$$$$$$$  sAllFlightNumbers.size() = " + sAllFlightNumbers.size());	
		
		
		
	    //sFlightNumbersLeg2[0] = sAllFlightNumbers.get(0).getAttribute("data-test-airline-flight-number");  // there is always at least 1 flight number
		flightNumbersArray.add(sAllFlightNumbers.get(0).getAttribute("data-test-airline-flight-number"));
		if (numberFlightsPerLeg[1] == 2) { // there might be a second flight number
			//sFlightNumbersLeg2[1] = sAllFlightNumbers.get(1).getAttribute("data-test-airline-flight-number");
			flightNumbersArray.add(sAllFlightNumbers.get(1).getAttribute("data-test-airline-flight-number"));
		}
		
		log.info("$$$$$$$$$$$ 11 $$$$$$$$$$$$$$    flightNumbersArray[2] = " + flightNumbersArray.get(2));  // may be empty
		log.info("$$$$$$$$$$$ 11 $$$$$$$$$$$$$$    flightNumbersArray[3] = " + flightNumbersArray.get(3));  // may be empty

		
		// click 1 or 2 buttons to get to the next Leg
		//allSelectButtons = getElements("//div[1]/div[2]/div[2]/div[1]/div[2]/button[1]", "XPATH");
		allSelectButtons = getElements("//button[@data-test-id='select-button']", "XPATH"); // ONLY the Select buttons
		
		// -1 due to there is no Select button for the Leg 1 flight at top of page
		allSelectButtons.get(flightIndex - 1).click();		
		// Clicking the Select button may land us on the Leg 3 page OR it
		// may open a "Rules and restrictions apply" field which contains a "Select this fare" button.
		// If this new field appears then clicking this new button will take us to the Leg 3 page
		if (isElementPresent("//div[contains(@class,'toggle-pane fade open')]//button[contains(@class,'btn-secondary btn-action t-select-btn')]", "XPATH")) {
			clickElement("//div[contains(@class,'toggle-pane fade open')]//button[contains(@class,'btn-secondary btn-action t-select-btn')]", "XPATH");
		}
		
/*		
		// click the first flight's Select button since we always want the least expensive flight
		clickElement("//li[1]//div[1]//div[2]//div[2]//div[1]//div[2]//button[1]", "XPATH");
		// possibly click again
		if (isElementPresent("//div[contains(@class,'toggle-pane fade open')]//button[contains(@class,'btn-secondary btn-action t-select-btn')]", "XPATH")) {
			clickElement("//div[contains(@class,'toggle-pane fade open')]//button[contains(@class,'btn-secondary btn-action t-select-btn')]", "XPATH");
		}*/
		
		log.info("$$$$$$$$$$$   Completed Leg 2   $$$$$$$$$$$$$$");
		
		
		//*****************LEG 3
		//Thread.sleep(15000);	
		
		if (isElementPresent("stopFilter_stops-0", "ID")) {
			clickElement("stopFilter_stops-0", "ID");
		}
		if (isElementPresent("stopFilter_stops-1", "ID")) {
			clickElement("stopFilter_stops-1", "ID");
		}
		
		// verifyPricesAreInLowestOrder() has an explicit wait
		selectPriceDropdownIsLowest();
		verifyPricesAreInLowestOrder();
		
		// get name and confirm it's the same as Leg 1.     Fulfills a verification!
		sAllAirlineNames = getElements("//span[@data-test-id='airline-name']", "XPATH");  // this locator works for all 3 legs
		
		
		// BEWARE OF LEG 1 & 2 NAMES AT THE TOP!!!!!
		// Verify we're selecting a flight with the same airline name
		for (flightIndex = 2; flightIndex < sAllAirlineNames.size(); flightIndex++) {
			if (sAllAirlineNames.get(flightIndex).getText().trim().equalsIgnoreCase(sSelectedAirlineName)) {
				break;
			}	
		}
				
		// save #stops.    Fulfills a verification!
		sAllStops = getElements("//span[@class='number-stops']", "XPATH");
		sStop = sAllStops.get(flightIndex).getText().trim();
		if (sStop.contains("(Nonstop)"))
			numberFlightsPerLeg[2] = 1;
		else if (sStop.contains("(1 stop)"))
			numberFlightsPerLeg[2] = 2;
		else
			log.debug("A Leg 3 flight with more than 1 stop was selected even though it should have been filtered out");
		
		// SAVE Flight Numbers
		// click open "Details and Baggage fees" to get at flight numbers.  Use #stops array to know how many flight numbers to look for.
		sAllDetailsLinks = getElements("//a[@data-test-id='flight-details-link']", "XPATH");
		//sAllDetailsLinks = getElements("//span[@class='show-flight-details']", "XPATH");
		sAllDetailsLinks.get(flightIndex-2).click();
		
		sAllFlightNumbers = getElements("//li[@class='details-utility-item-value segment-info-details-item flight']", "XPATH");  // either 1 or 2 flight numbers
		//sFlightNumbersLeg3[0] = sAllFlightNumbers.get(0).getAttribute("data-test-airline-flight-number");  // there is always at least 1 flight number
		flightNumbersArray.add(sAllFlightNumbers.get(0).getAttribute("data-test-airline-flight-number"));
		
		if (numberFlightsPerLeg[2] == 2) { // there might be a second flight number
			//sFlightNumbersLeg3[1] = sAllFlightNumbers.get(1).getAttribute("data-test-airline-flight-number");
			flightNumbersArray.add(sAllFlightNumbers.get(1).getAttribute("data-test-airline-flight-number"));
		}
		log.info("$$$$$$$$$$$   Nearly Completed Leg 3   $$$$$$$$$$$$$$");  // Names and #Stops for all 3 legs have been verified
		log.info("selectQualifyingFlight() was successful, about to click the Select button which will open the \"Review your trip\" "
				+ "page in a new window but need to save parent (current) window handler first");
		
		return flightIndex - 2;  // -2 due to there is no Select button for the Leg 1&2 flights at the top


		// THE END
		
		
/*		
		// for Leg 3 - hopefully no timing issues here while loading
		selectPriceDropdownIsLowest();
		verifyPricesAreInLowestOrder();
		
		// get name and confirm it's the same as Leg 1
		
		// get #stops
		
		// click open "Details and Baggage fees" to get at flight numbers.  Use #stops to know how many flight number to look for.  SAVE Stop# and flight#
		
		// click the first flight's Select button
		clickElement("//li[1]//div[1]//div[2]//div[2]//div[1]//div[2]//button[1]", "XPATH");
		// possibly click again
		if (isElementPresent("//div[contains(@class,'toggle-pane fade open')]//button[contains(@class,'btn-secondary btn-action t-select-btn')]", "XPATH")) {
			clickElement("//div[contains(@class,'toggle-pane fade open')]//button[contains(@class,'btn-secondary btn-action t-select-btn')]", "XPATH");
		}
		*/

		
		
		/*
		stopFilter_stops-0

		stopFilter_stops-1


		******leg 1 top SKIP
		    //li[1]//div[1]//div[2]//div[1]//div[1]//div[1]//div[1]//div[1]//div[2]//span[1]
		Leg 1 secondary
		    //li[2]//div[1]//div[2]//div[1]//div[1]//div[1]//div[1]//div[1]//div[2]//span[1]
		    //li[3]//div[1]//div[2]//div[1]//div[1]//div[1]//div[1]//div[1]//div[2]//span[1]

		USE THIS
		****All Leg 1 names:  Use find elements and then iterate and look for United
		//div[contains(@class,'secondary-content overflow-ellipsis inline-children')]//span[@data-test-id='airline-name']



		SELECT - skip
		//li[1]/div[1]/div[2]/div[2]/div[1]/div[2]/button[1]
		//li[2]/div[1]/div[2]/div[2]/div[1]/div[2]/button[1]
		//li[3]/div[1]/div[2]/div[2]/div[1]/div[2]/button[1]

		USE THIS
		All Select buttons
		   //div[1]/div[2]/div[2]/div[1]/div[2]/button[1]



		leg 1 "Select this Fare". ALL ARE THE SAME! if only one is visible at a time which is true for automation
		//div[contains(@class,'toggle-pane fade open')]//button[contains(@class,'btn-secondary btn-action t-select-btn')]


		=====================

		Leg 2 top name - who cares???
		//li[1]//div[1]//div[1]//div[1]//div[1]//div[1]//div[1]//div[1]//div[2]//span[1]
		//li[2]//div[1]//div[1]//div[1]//div[1]//div[1]//div[1]//div[1]//div[2]//span[1]
		//li[3]//div[1]//div[1]//div[1]//div[1]//div[1]//div[1]//div[1]//div[2]//span[1]

		Select:
		USE:
		//li[1]//div[1]//div[2]//div[2]//div[1]//div[2]//button[1]

		    Select
		    //li[1]//div[1]//div[1]//div[2]//div[1]//div[2]//button[1]
		    //li[2]//div[1]//div[1]//div[2]//div[1]//div[2]//button[1]
		    //li[3]//div[1]//div[1]//div[2]//div[1]//div[2]//button[1]

		Select this Fare - same if only one select is open, which is fine
		                //div[@class='toggle-pane fade open']//button[@class='btn-secondary btn-action t-select-btn']
		USE:
		//div[contains(@class,'toggle-pane fade open')]//button[contains(@class,'btn-secondary btn-action t-select-btn')]








		LEG 3
		names - who cares?????
		//li[1]//div[1]//div[2]//div[1]//div[1]//div[1]//div[1]//div[1]//div[2]//span[1]
		//li[2]//div[1]//div[2]//div[1]//div[1]//div[1]//div[1]//div[1]//div[2]//span[1]
		//li[3]//div[1]//div[2]//div[1]//div[1]//div[1]//div[1]//div[1]//div[2]//span[1]



		    Direct Select - BAD
		    //li[1]//div[1]//div[2]//div[1]//div[1]//div[1]//div[1]//div[1]//div[2]//span[1]
		    //li[2]//div[1]//div[2]//div[1]//div[1]//div[1]//div[1]//div[1]//div[2]//span[1]
		    //li[3]//div[1]//div[2]//div[1]//div[1]//div[1]//div[1]//div[1]//div[2]//span[1]
		    All direct Selects
		    //div[1]//div[2]//div[1]//div[1]//div[1]//div[1]//div[1]//div[2]//span[1]

		USE THESE:
		***Indirect Select first one
		//li[1]//div[1]//div[2]//div[2]//div[1]//div[2]//button[1]


		***Direct Select - first one == same as above
		//li[1]//div[1]//div[2]//div[2]//div[1]//div[2]//button[1]

		***
		If this element is displayed then click it
		Select This Fare after an indirect
		//div[contains(@class,'toggle-pane fade open')]//button[contains(@class,'btn-secondary btn-action t-select-btn')]

*/
		
		
/*		
		int selectedFlightIndex = 0;  // index into flight selections 
		boolean bIgnoreNumStops = false;  // this helps with Task 6.1

		// iterate through each flight possibility
		for (selectedFlightIndex=1; selectedFlightIndex <= numberFlightsOnPage; selectedFlightIndex++) {
			// Create a list of xpaths for the flight under examination's group of 3 "number of stops"
			List <WebElement> flightStops = getElements(sAirlineNumStops[0], selectedFlightIndex, sAirlineNumStops[1], "XPATH");
			if (flightStops.size() != 3)  {  // safety test
				throw new Exception("Invalid number of flight stops");
			}
			String sStop1 = flightStops.get(0).getText().trim();
			String sStop2 = flightStops.get(1).getText().trim();
			String sStop3 = flightStops.get(2).getText().trim();

			if (bIgnoreNumStops ||
					(sStop1.equals("(Nonstop)") || sStop1.equals("(1 stop)")) &&
					(sStop2.equals("(Nonstop)") || sStop2.equals("(1 stop)")) &&	
					(sStop3.equals("(Nonstop)") || sStop3.equals("(1 stop)")`))
			{
				// The number of stops looks good, now check the airline names

				// This round trip's 3 airline names
				List <WebElement> flightNames = getElements(sAirlineNames[0], selectedFlightIndex, sAirlineNames[1], "XPATH");
				if (flightNames.size() != 3)  { // safety test
					throw new Exception("Invalid number of airline names");
				}
				String airlineName1 = flightNames.get(0).getText().trim();
				String airlineName2 = flightNames.get(1).getText().trim();
				String airlineName3 = flightNames.get(2).getText().trim();

				if (airlineName1.equals(airlineName2) && airlineName2.equals(airlineName3)) {
					// All 3 airline names are the same.
					// Ignoring the upper/lower case of "United" just in case it changes in the future
					// Now determine if they are all "United"
					if (!airlineName1.equalsIgnoreCase("United")) {
						// Success! The airline names are not all United so this is our flight!
						log.info("selectQualifyingFlight()  We found our flight, it's number " + selectedFlightIndex + " on the flight list!");
						break;
					}
					else {  // this helps accomplish Task 6.1
						// All 3 airline names are United so loop back around while ignoring the number of stops from now on
						bIgnoreNumStops = true;
					}
				}
			} 
			
			// No more Next Button, everything is now on one page
			// May need to throw my line 295
			
			// Determine if we need to click the Next button to look at more flight possibilities
			if (selectedFlightIndex == numberFlightsOnPage) {  
				// just processed the last flight entry on the page
				try {
					WebElement nextButton = getElement(nextButtonListing, "XPATH");
					if (nextButton.isEnabled()) { 
						nextButton.click();

						Thread.sleep(3000);	// this seems to help
						// reset selectedFlightIndex and numberFlightsOnPage for the new page of flight selections
						selectedFlightIndex = 0;  // will get incremented right away at the top of the for-loop
						// the number of flights listed on the current page
						numberFlightsOnPage = getElements(allFlightPrices, "XPATH").size();  
					}
				}
				catch (NoSuchElementException e) {  // only occurs if all flight selection pages are exhausted
					throw new Exception("No flight on any page in Search Results qualifies, throwing my own exception");	
				}
			}
		}  // end for-loop for flights 

		log.info("selectQualifyingFlight() was successful");

		// Return the qualified flight index value, this is our flight!
		return selectedFlightIndex;
		
*/
	}

/*
	// Preparation for Tasks 7.3 and 7.4
	// Saving the following values for future verifications:
	// -- all 3 number-of-stops for each leg of the selected flight.
	// -- all the flight numbers of the selected flight including all its connecting flights.
	// -- the price of our selected flight
	// Getter methods will be provided to allow other classes to access these values.
	// Saving these values needs to be done BEFORE switching to the upcoming new window
	private void preparationForReviewYourTripPage(int selectedFlightIndex) throws Exception {
		// Click on the "Details & baggage fees" link for the selected flight
		clickElement(sDetailsAndBaggageFees[0], selectedFlightIndex, sDetailsAndBaggageFees[1], "XPATH");
		
		// Saving all 3 number-of-stops for each leg of the selected flight in the
		// iSavedNumberOfStops[] array.  
		// Values can either be (Nonstop) or (1 stop) or (2 stops)
		// If (2 stops) is encountered then Task 6.1 must have occurred.
		iSavedNumberOfStops = new int[3];  // for each of the 3 trips for the selected flight
		for (int i=1; i<=3; i++) {
			String sStops = getElement(sSelectedFlightNumStops[0], selectedFlightIndex, sSelectedFlightNumStops[1], i, sSelectedFlightNumStops[2], "XPATH").getText().trim();
						
			if (sStops.equals("(Nonstop)"))
				iSavedNumberOfStops[i-1] = 0;
			else if (sStops.equals("(1 stop)"))
				iSavedNumberOfStops[i-1] = 1;
			else if (sStops.equals("(2 stops)"))  // Conditions for Task 6.1 must have occurred
				iSavedNumberOfStops[i-1] = 2;
			else
				log.error("preparationForReviewYourTripPage()  Unexpected: >2 stops");
		}
		for (int i=0; i<3; i++)
			log.debug("preparationForReviewYourTripPage()  iSavedNumberOfStops[" + i + "] = " +  iSavedNumberOfStops[i]);


		// Save all the flight numbers of the selected flight from the Search Results page.
		// Non-existing connection flights are given a null string value.
		// In this case it will be a little easier to work with a fixed size array rather than a variable 
		// one like ArrayList even though there are a variable number of flight numbers.
		sSavedFlightNumbers = new String[9];  // max is 3x3=9 flight numbers per flight
		for (int i=1, index=0; i<=3; i++) {  // iterate through trips
			// click a Trip button
			clickElement(sTripNumberButton[0], i, sTripNumberButton[1], "XPATH");
			
			// Each flight has exactly 3 trips. 
			// Each trip has at least one and a max of 3 flight numbers.
			// This is the primary flight number:
			sSavedFlightNumbers[index++] = getElement(sPrimaryFlightNumber[0], i, sPrimaryFlightNumber[1], "XPATH").getAttribute("data-test-airline-flight-number");
							
			// possible first connecting flight number
			if (iSavedNumberOfStops[i-1] > 0)
				sSavedFlightNumbers[index++] = getElement(sFirstConnectingFlightNumber[0], i, sFirstConnectingFlightNumber[1], "XPATH").getAttribute("data-test-airline-flight-number");
			else
				sSavedFlightNumbers[index++] = "";
			
			// possible second connecting flight number
			if (iSavedNumberOfStops[i-1] > 1)
				sSavedFlightNumbers[index++] = getElement(sSecondConnectingFlightNumber[0], i, sSecondConnectingFlightNumber[1], "XPATH").getAttribute("data-test-airline-flight-number");				
			else
				sSavedFlightNumbers[index++] = "";
		}
		for (int i=0; i<9; i++)
			log.debug("preparationForReviewYourTripPage()  sSavedFlightNumbers[" + i + "] = " +  sSavedFlightNumbers[i]);

		// Need to save the price of the selected flight before clicking the Select button
		// (before switching window focus)
		sSavedPrice = flightPrices.get(selectedFlightIndex-1).getText().trim();  // needed for future verification
		log.debug("preparationForReviewYourTripPage()  sSavedPrice = " +  sSavedPrice);

		log.info("preparationForReviewYourTripPage() was successful");
	}

*/
	// Clicking 'Select' will open a new window.  Selenium needs to shift its focus to this new window.
	// This is similar to dealing with IFrames. 
	private void clickSelectAndOpenNewWindow(int leg3flightIndex) throws Exception {	
		// Prepare to switch window focus
		// Save the current handle
		parentHandle = driver.getWindowHandle();
	
		// click 1 or 2 buttons to get to the "Review your trip" page
		//List <WebElement> allSelectButtons = getElements("//div[1]/div[2]/div[2]/div[1]/div[2]/button[1]", "XPATH");
		List <WebElement> allSelectButtons = getElements("//button[@data-test-id='select-button']", "XPATH"); // ONLY the Select buttons
		
		allSelectButtons.get(leg3flightIndex).click();		
		// Clicking the Select button may land us on the Leg 3 page OR it
		// may open a "Rules and restrictions apply" field which contains a "Select this fare" button.
		// If this new field appears then clicking this new button will take us to the Leg 3 page
		if (isElementPresent("//div[contains(@class,'toggle-pane fade open')]//button[contains(@class,'btn-secondary btn-action t-select-btn')]", "XPATH")) {
			clickElement("//div[contains(@class,'toggle-pane fade open')]//button[contains(@class,'btn-secondary btn-action t-select-btn')]", "XPATH");
		}
		// opens the "Review your trip" page in a new window
		
		
		
		// Click the Select button for our chosen flight.
		// This will open a new window displaying the Review Your Trip page.
		//clickElement(sSelectButton[0], selectedFlightIndex, sSelectButton[1], "XPATH");
	
		// Each window has its own handle, get them all.
		Set<String> handles = driver.getWindowHandles();  // There are no duplicates in a Set
		
		// Switch between handles
		for (String handle: handles) {
			if (!handle.equals(parentHandle)) {
				driver.switchTo().window(handle);
				break;
			}
		}

		log.info("clickSelectAndOpenNewWindow() was successful");
	}


	// Getter methods   (setters are not needed)
	public String getSavedPrice() {
		return sPrice;
	}
	public int[] getSavedNumberFlightsPerLeg() {
		return numberFlightsPerLeg;  // #stops
	}
	public ArrayList<String> getSavedFlightNumbers() {
		return flightNumbersArray;  // ALL the flight numbers
	}
	public String getParentHandle() {
		return parentHandle;
	}

		
	/**
	 * Process the Search Results page.  
	 * @return true on success, false otherwise
	 */
	public boolean wholePage(String[] sDepartingAirports, String[] sArrivalAirports, String[] sFormattedDepartureDates) {
		// process all exceptions here
		try {
			selectPriceDropdownIsLowest();
			verifyPricesAreInLowestOrder();  // has an explicit wait
			verifyFlightData(sDepartingAirports, sArrivalAirports, sFormattedDepartureDates);
//			selectQualifyingFlight();
			int selectedFlightIndex = selectQualifyingFlight();
/*			preparationForReviewYourTripPage(selectedFlightIndex); */
			clickSelectAndOpenNewWindow(selectedFlightIndex);
			return true;
		}
		catch (Exception e) {
			log.error("Search Results page failed with this exception: ", e);
			return false;
		}
	}
}
