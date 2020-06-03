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
 * SearchPage tests the Orbitz web page where you input desired flight data
 * @author mikej
 *
 */
public class SearchPage extends SeleniumUtilities {
	private static final Logger log = LogManager.getLogger(SearchPage.class);

	// Instance variables
	// array to save the 3 departure dates for future verification   MJJ- [3] here or on line 148 ???
	private String[] sFormattedDepartureDate = new String[3];

	// locators are gathered here for maintainability
	private final String multiCityRadioButton = "flight-type-multi-dest-label-hp-flight";
	private final String addAnotherFlightLink = "add-flight-leg-hp-flight";
	private final String departAirport1 = "flight-origin-hp-flight";
	private final String arrivalAirport1 = "#flight-destination-hp-flight"; // css
	private final String departAirport2  = "flight-2-origin-hp-flight";
	private final String arrivalAirport2 = "#flight-2-destination-hp-flight";
	private final String departAirport3  = "flight-3-origin-hp-flight";
	private final String arrivalAirport3 = "#flight-3-destination-hp-flight";
	private final String numberOfAdults = "//select[@id='flight-adults-hp-flight']";  // xpath
	private final String numberOfChildren = "#flight-children-hp-flight";
	private final String departureDate1 = "flight-departing-single-hp-flight";  // id
	private final String departureDate2 = "flight-2-departing-hp-flight";
	private final String departureDate3 = "#flight-3-departing-hp-flight";
	private final String searchButton = "//form[@id='gcw-flights-form-hp-flight']//button[@type='submit']";  // xpath
	
/*
	private final String addAnotherFlightLink = "//a[contains(@id,'add-flight-leg-')]";
	private final String departAirport1 = "//label/input[starts-with(@id,'flight-origin-')]";
	private final String arrivalAirport1 = "//label/input[starts-with(@id,'flight-destination-')]";
	private final String departAirport2  = "//label/input[starts-with(@id,'flight-2-origin-')]";
	private final String arrivalAirport2 = "//label/input[starts-with(@id,'flight-2-destination-')]";
	private final String departAirport3  = "//label/input[starts-with(@id,'flight-3-origin-')]";
	private final String arrivalAirport3 = "//label/input[starts-with(@id,'flight-3-destination-')]";
	private final String numberOfAdults = "//select[contains(@id,'flight-adults-')]";
	private final String numberOfChildren = "//select[contains(@id,'flight-children-')]";
	private final String departureDate1 = "//input[contains(@id,'flight-departing-single-')]";
	private final String departureDate2 = "//input[contains(@id,'flight-2-departing-')]";
	private final String departureDate3 = "//input[contains(@id,'flight-3-departing-')]";
	*/
	
	// constructor
	public SearchPage(WebDriver driver) {
		super(driver);
	}

	
	// actions
	private void clickMultiCityRadioButton() throws Exception {
		clickElement(multiCityRadioButton, "ID");

		log.info("clickMultiCityRadioButton() was successful");
	}

	private void clickAddAnotherFlight() {
		// Add another set of flight entry boxes
		// It is OK if clicking this link throws an exception. 
		// It seems to mean extra flights have already been added to the web page
		try {
			clickElement(addAnotherFlightLink, "ID");
			log.info("clickAddAnotherFlight() was successful");
		}
		catch(Exception e) {
			log.info("clickAddAnotherFlight() threw an exception but that is OK, extra flights have already been added to this web page.");
		}
	}

	private void enterDepartureAndArrivalCodes(String[] sArrivalAirports, boolean bClearFlightData) throws Exception {
		// If the browser is not changing then clear all airport related fields, otherwise
		// the new airport codes will get appended to the previous ones creating invalid codes. 
		if (bClearFlightData) {
			clearElement(departAirport1, "XPATH");
			clearElement(arrivalAirport1, "XPATH");
			clearElement(departAirport2, "XPATH");
			clearElement(arrivalAirport2, "XPATH");
			clearElement(departAirport3, "XPATH");
			clearElement(arrivalAirport3, "XPATH");
		}
		
		// Trip 1
		sendKeysElement(sArrivalAirports[2], departAirport1, "ID");
		sendKeysElement(sArrivalAirports[0], arrivalAirport1, "CSS");
		// Trip 2
		sendKeysElement(sArrivalAirports[0], departAirport2, "ID");
		sendKeysElement(sArrivalAirports[1], arrivalAirport2, "CSS");
		// Trip 3
		sendKeysElement(sArrivalAirports[1], departAirport3, "ID");
		sendKeysElement(sArrivalAirports[2], arrivalAirport3, "CSS");

		log.info("enterDepartureAndArrivalCodes() was successful");
	}

	// Dropdown for the number of Adult & Child passengers.
	// Although the defaults are correct for our purposes, it is safer to select the desired numbers.
	// Even though these dropdowns don't have many elements, it is better not to write an xpath for 
	// each one.  I observed that this dropdown happens to use the <select> tag. We can take 
	// advantage of that by using the select class for a more robust solution.
	private void selectAdultAndChildDropdown() throws Exception {
		WebElement element = getElement(numberOfAdults, "XPATH");
		Select selectAdult = new Select(element);
		selectAdult.selectByVisibleText("1");  // select 1 adult

		element = getElement(numberOfChildren, "CSS");
		Select selectChildren = new Select(element);
		selectChildren.selectByVisibleText("0");  // select 0 children

		log.info("selectAdultAndChildDropdown() was successful");
	}

	// Create 3 departure dates: 35, 35+7 and 35+7+7 days from the current day.
	// Insert them into the Search page.
	private void enterDepartureDates(boolean bClearDateFields) throws Exception {
		// if the browser is not changing then clear all flight departure dates, otherwise
		// the new dates will get appended to the previous ones creating invalid dates. 
		if (bClearDateFields) {
			clearElement(departureDate1, "ID");
			clearElement(departureDate2, "ID");
			clearElement(departureDate3, "CSS");
		}
				
		// Get an instance of a calendar.  By default, the calendar instance has the current date and time in it.
		Calendar myCalendar = Calendar.getInstance();  

		// Although Orbitz will accept a date in the M/D/YYYY format when selecting a departure date,
		// it converts it to MM/DD/YYYY on other pages. So for future verification purposes I need to
		// use the MM/DD/YYYY format from the beginning.  To do this I'm using SimpleDateFormat and 
		// and give its constructor a formatting pattern.
		//
		// Date and time formatting patterns are documented here:
		// https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
		//
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

		// instantiate an array to save the 3 dates for future verification
		// sFormattedDepartureDate = new String[3];  // MJJ - or above?????

		// 35 days from today
		myCalendar.add(Calendar.DAY_OF_YEAR, 35);
		Date futureDate = myCalendar.getTime();
		sFormattedDepartureDate[0] = simpleDateFormat.format(futureDate);  // save the date
		sendKeysElement(sFormattedDepartureDate[0], departureDate1, "ID");
		log.debug("sFormattedDepartureDate 35 days from today = " + sFormattedDepartureDate[0]);

		// 35+7 days from today
		myCalendar.add(Calendar.DAY_OF_YEAR, 7);
		futureDate = myCalendar.getTime();
		sFormattedDepartureDate[1] = simpleDateFormat.format(futureDate);
		sendKeysElement(sFormattedDepartureDate[1], departureDate2, "ID");
		log.debug("sFormattedDepartureDate 35+7 = " + sFormattedDepartureDate[1]);

		// 35+7+7 days from today
		myCalendar.add(Calendar.DAY_OF_YEAR, 7);
		futureDate = myCalendar.getTime();
		sFormattedDepartureDate[2] = simpleDateFormat.format(futureDate);
		sendKeysElement(sFormattedDepartureDate[2], departureDate3, "CSS");
		log.debug("sFormattedDepartureDate 35+7+7 = " + sFormattedDepartureDate[2]);

		log.info("enterDepartureDates() was successful");
	}

	// click the Search button
	private void clickSearchButton() throws Exception {
		clickElement(searchButton, "XPATH");

		log.info("clickSearchButton() was successful");
	}


	// Getter methods  (setters are not needed)
	public String[] getFormattedDepartureDate() {
		return sFormattedDepartureDate;
	}


	/**
	 * Process the Search page.  
	 * @return true on success, false otherwise
	 */
	public boolean wholePage(String[] sArrivalAirports, boolean bClearFlightDataAndDates) {
		// process all exceptions here
		try {
			clickMultiCityRadioButton();
			clickAddAnotherFlight();
			enterDepartureAndArrivalCodes(sArrivalAirports, bClearFlightDataAndDates);
			selectAdultAndChildDropdown();
			enterDepartureDates(bClearFlightDataAndDates);
			clickSearchButton();
			return true;
		}
		catch (Exception e) {
			log.error("Search Page failed with this exception: ", e);
			return false;
		}
	}
}
