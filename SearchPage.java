package seleniumDemo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * Input flight data into the Search Flights page.
 * 
 * @author Michael Januszewski
 */
public class SearchPage extends SeleniumUtilities {
	private static final Logger log = LogManager.getLogger(SearchPage.class.getName());  // Log4j2

	// Instance variables
	private String[] sFormattedDepartureDate = new String[3];  // save the 3 departure dates for future verification

	/*
	 * Locators are gathered here for maintainability.
	 * They're mostly ID types (which are the fastest) but I included a few Xpaths and CSS Selectors to show 
	 * I can work with various types of locators
	 */
	private final String multiCityRadioButton = "flight-type-multi-dest-label-hp-flight";
	private final String addAnotherFlightLink = "add-flight-leg-hp-flight";
	private final String departAirport1 = "flight-origin-hp-flight";
	private final String arrivalAirport1 = "#flight-destination-hp-flight";
	private final String departAirport2  = "flight-2-origin-hp-flight";
	private final String arrivalAirport2 = "#flight-2-destination-hp-flight";
	private final String departAirport3  = "flight-3-origin-hp-flight";
	private final String arrivalAirport3 = "#flight-3-destination-hp-flight";
	private final String numberOfAdults = "//select[@id='flight-adults-hp-flight']";
	private final String numberOfChildren = "#flight-children-hp-flight";
	private final String departureDate1 = "flight-departing-single-hp-flight";
	private final String departureDate2 = "flight-2-departing-hp-flight";
	private final String departureDate3 = "#flight-3-departing-hp-flight";
	private final String searchButton = "//form[@id='gcw-flights-form-hp-flight']//button[@type='submit']";


	/**
	 * Constructor for the Search page class.  It calls the base class constructor (SeleniumUtilities).
	 * 
	 * @param  driver	WebDriver object for the browser driver which implements the WebDriver interface
	 */
	public SearchPage(WebDriver driver) {
		super(driver);
	}

	/**
	 * Click the Multi-city radio button.
	 * Helps fulfill Task 2.
	 * 
	 * @throws	Exception	let the startHere() method catch it
	 */
	private void clickMultiCityRadioButton() throws Exception {
		clickElement(multiCityRadioButton, "ID");

		log.info("clickMultiCityRadioButton() was successful");
	}

	/**
	 * Click the "Add another flight" link.
	 */
	private void clickAddAnotherFlight() {
		/* 
		 * Add another set of flight entry boxes for Leg 3 of the trip.
		 * It is OK if clicking this link throws an exception. 
		 * It seems to mean that an extra flight was already added to the web page.
		 */
		try {
			clickElement(addAnotherFlightLink, "ID");
			log.info("clickAddAnotherFlight() was successful");
		}
		catch(Exception e) {
			log.debug("clickAddAnotherFlight() threw an exception but that is OK, it means that extra flights have already been added to this page.");
		}
	}

	/**
	 * Input airport codes into the flight search fields.
	 * Determine if clearing these fields is necessary prior to inputting the codes. 
	 * Fulfills Task 3c.
	 * 
	 * @param	sDepartingAirports	string array of departing airport codes from the Search Results page
	 * @param	sArrivalAirports		string array of arriving airport codes from the Search Results page
	 * @param	bClearFlightData		boolean whether to clear the previous airport fields after switching browsers
	 * @throws	Exception			let the startHere() method catch it
	 */
	private void enterDepartureAndArrivalCodes(String[] sDepartingAirports, String[] sArrivalAirports, boolean bClearFlightData) throws Exception {
		/*
		 * If the browser being requested is the same as the previous one then clear all flight 
		 * destination and arrival fields of old airport codes and re-populate them with new codes.
		 * Otherwise the new airport codes will get appended to the previous ones creating invalid codes. 
		 */
		if (bClearFlightData) {
			clearElement(departAirport1, "XPATH");
			clearElement(arrivalAirport1, "XPATH");
			clearElement(departAirport2, "XPATH");
			clearElement(arrivalAirport2, "XPATH");
			clearElement(departAirport3, "XPATH");
			clearElement(arrivalAirport3, "XPATH");
		}

		// Leg 1
		sendKeysElement(sDepartingAirports[0], departAirport1, "ID");
		sendKeysElement(sArrivalAirports[0], arrivalAirport1, "CSS");
		// Leg 2
		sendKeysElement(sDepartingAirports[1], departAirport2, "ID");
		sendKeysElement(sArrivalAirports[1], arrivalAirport2, "CSS");
		// Leg 3
		sendKeysElement(sDepartingAirports[2], departAirport3, "ID");
		sendKeysElement(sArrivalAirports[2], arrivalAirport3, "CSS");

		log.info("enterDepartureAndArrivalCodes() was successful");
	}

	/** 
	 * Select the number of adults and children making this trip.
	 * Although the default values happen to be correct for my purposes, it is safer to 
	 * explicitly select the desired number of passengers.  This dropdown menu uses the <select> tag. 
	 * I can take advantage of that by using the select class for a more robust solution.
	 * Fulfills Task 3a.
	 * 
	 * @throws	Exception	let the startHere() method catch it
	 */
	private void selectAdultAndChildDropdown() throws Exception {
		WebElement element = getElement(numberOfAdults, "XPATH");
		Select selectAdult = new Select(element);
		selectAdult.selectByVisibleText("1");  // select 1 adult

		element = getElement(numberOfChildren, "CSS");
		Select selectChildren = new Select(element);
		selectChildren.selectByVisibleText("0");  // select 0 children

		log.info("selectAdultAndChildDropdown() was successful");
	}

	/**
	 * Create 3 departure dates: 35, 35+7 and 35+7+7 days from the current day.
	 * Insert them into the Search page.
	 * Fulfills Task 3b and 3d.
	 * 
	 * @param	bClearDateFields		boolean whether to clear the previous date fields after switching browsers
	 * @throws	Exception			let the startHere() method catch it
	 */	
	private void enterDepartureDates(boolean bClearDateFields) throws Exception {
		// if the browser is not changing then clear all departure dates, otherwise
		// the new dates will get appended to the previous ones creating invalid dates. 
		if (bClearDateFields) {
			clearElement(departureDate1, "ID");
			clearElement(departureDate2, "ID");
			clearElement(departureDate3, "CSS");
		}

		// Get an instance of a calendar.  By default, the calendar instance has the current date and time in it.
		Calendar myCalendar = Calendar.getInstance();  

		/* Although Orbitz will accept a date in the M/D/YYYY format when selecting a departure date,
		 * it converts it to MM/DD/YYYY on other pages. So for future verification purposes I need to
		 * use the MM/DD/YYYY format from the beginning.  To do this I'm using SimpleDateFormat and 
		 * and give its constructor a formatting pattern.
		 * 
		 * Date and time formatting patterns are documented here:
		 * https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html
		 */
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

		// 35 days from today
		myCalendar.add(Calendar.DAY_OF_YEAR, 35);
		Date futureDate = myCalendar.getTime();
		sFormattedDepartureDate[0] = simpleDateFormat.format(futureDate);  // save the date
		sendKeysElement(sFormattedDepartureDate[0], departureDate1, "ID"); // enter date into the Leg 1 departure date field
		log.debug("sFormattedDepartureDate 35 days from today = " + sFormattedDepartureDate[0]);

		// 35+7 days from today
		myCalendar.add(Calendar.DAY_OF_YEAR, 7);
		futureDate = myCalendar.getTime();
		sFormattedDepartureDate[1] = simpleDateFormat.format(futureDate);  // save the date
		sendKeysElement(sFormattedDepartureDate[1], departureDate2, "ID"); // enter date into the Leg 2 departure date field
		log.debug("sFormattedDepartureDate 35+7 = " + sFormattedDepartureDate[1]);

		// 35+7+7 days from today
		myCalendar.add(Calendar.DAY_OF_YEAR, 7);
		futureDate = myCalendar.getTime();
		sFormattedDepartureDate[2] = simpleDateFormat.format(futureDate);   // save the date
		sendKeysElement(sFormattedDepartureDate[2], departureDate3, "CSS"); // enter date into the Leg 3 departure date field
		log.debug("sFormattedDepartureDate 35+7+7 = " + sFormattedDepartureDate[2]);

		log.info("enterDepartureDates() completed");
	}

	/**
	 * Click the Search button.
	 * Land on the Search Results page.
	 * Helps fulfill Task 4.
	 * 
	 * @throws	Exception	let the startHere() method catch it
	 */	
	private void clickSearchButton() throws Exception {
		clickElement(searchButton, "XPATH");

		log.info("clickSearchButton() was successful");
	}

	/**
	 * Getter method for departure dates (setters are not needed).
	 * 
	 * @return		string array of departure dates
	 */
	public String[] getFormattedDepartureDate() {
		return sFormattedDepartureDate;
	}

	/**
	 * Driver for the Search Flights page tests.  
	 * 
	 * @param	sDepartingAirports			string array of departing airport codes from the Search Results page
	 * @param	sArrivalAirports				string array of arriving airport codes from the Search Results page
	 * @param	bClearFlightDataAndDates		boolean whether to clear the previous airport and date fields after switching browsers
	 * @return								boolean true for success. false indicates an exception was thrown
	 */
	public boolean startHere(String[] sDepartingAirports, String[] sArrivalAirports, boolean bClearFlightDataAndDates) {
		// process all exceptions for this page here
		try {
			clickMultiCityRadioButton();
			clickAddAnotherFlight();
			enterDepartureAndArrivalCodes(sDepartingAirports, sArrivalAirports, bClearFlightDataAndDates);
			selectAdultAndChildDropdown();
			enterDepartureDates(bClearFlightDataAndDates);
			clickSearchButton();
			return true;
		}
		catch (Exception e) {
			log.error("startHere() failed with this exception: ", e);
			return false;
		}
	}
}