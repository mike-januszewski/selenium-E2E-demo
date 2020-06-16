package seleniumDemo;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Base class of frequently used general purpose methods that other classes inherit from.
 * 
 * @author Michael Januszewski
 */
public class SeleniumUtilities {
	private static final Logger log = LogManager.getLogger(SeleniumUtilities.class.getName());  // Log4j2

	// Instance variables
	public WebDriver driver;


	/**
	 * Constructor for the Review Your Trip page class.  It calls the base class constructor (SeleniumUtilities).
	 * 
	 * @param  driver	WebDriver object for the browser driver which implements the WebDriver interface
	 */
	public SeleniumUtilities(WebDriver driver) {
		this.driver = driver;
	}

	/**
	 * Find a Selenium web element using any type of locator.
	 * 
	 * @param  	locator		string containing the locator	
	 * @param  	locatorType	string containing the type of locator
	 * @return 				the WebElement found 
	 * @throws	Exception	let startHere() method catch it
	 */
	public WebElement getElement(String locator, String locatorType) throws Exception {
		WebElement element = null;

		if (locatorType.equals("ID"))
			element = driver.findElement(By.id(locator));
		else if (locatorType.equals("XPATH"))
			element = driver.findElement(By.xpath(locator));
		else if (locatorType.equals("CSS"))
			element = driver.findElement(By.cssSelector(locator));
		else if (locatorType.equals("CLASS"))
			element = driver.findElement(By.className(locator));
		else if (locatorType.equals("NAME"))
			element = driver.findElement(By.name(locator));
		else if (locatorType.equals("LINK-TEXT"))
			element = driver.findElement(By.linkText(locator));
		else if (locatorType.equals("PARTIAL-LINK-TEXT"))
			element = driver.findElement(By.partialLinkText(locator));
		else if (locatorType.equals("TAG"))
			element = driver.findElement(By.tagName(locator));

		return element;
	}

	/**
	 * Find a List of Selenium web elements (plural!) using any type of locator.
	 * 
	 * @param  	locator		string containing the locator	
	 * @param  	locatorType	string containing the type of locator
	 * @return 				the List of WebElements (plural!) found
	 */
	public List <WebElement> getElements(String locator, String locatorType) {
		List <WebElement> elements = null;

		if (locatorType.equals("ID"))
			elements = driver.findElements(By.id(locator));
		else if (locatorType.equals("XPATH"))
			elements = driver.findElements(By.xpath(locator));
		else if (locatorType.equals("CSS"))
			elements = driver.findElements(By.cssSelector(locator));
		else if (locatorType.equals("CLASS"))
			elements = driver.findElements(By.className(locator));
		else if (locatorType.equals("NAME"))
			elements = driver.findElements(By.name(locator));
		else if (locatorType.equals("LINK-TEXT"))
			elements = driver.findElements(By.linkText(locator));
		else if (locatorType.equals("PARTIAL-LINK-TEXT"))
			elements = driver.findElements(By.partialLinkText(locator));
		else if (locatorType.equals("TAG"))
			elements = driver.findElements(By.tagName(locator));		

		return elements;
	}

	/**
	 * Click on an element given its locator.  This method is overloaded. 
	 * 
	 * @param  	locator		string containing the locator	
	 * @param  	locatorType	string containing the type of locator
	 * @throws	Exception	let startHere() method catch it
	 */
	public void clickElement(String locator, String locatorType) throws Exception {
		WebElement element = null;

		element = getElement(locator, locatorType);
		element.click();
	}

	/**
	 * Click on an element given its WebElement.  This method is overloaded. 
	 * 
	 * @param  	element		the WebElement to be clicked
	 * @throws	Exception	let startHere() method catch it
	 */
	public void clickElement(WebElement element) throws Exception {
		element.click();
	}

	/**
	 * Send data to an element.
	 * 
	 * @param  	data			string to be inserted into the web element
	 * @param  	locator		string containing the locator	
	 * @param  	locatorType	string containing the type of locator
	 * @throws	Exception	let startHere() method catch it
	 */
	public void sendKeysElement(String data, String locator, String locatorType) throws Exception {
		WebElement element = null;

		element = getElement(locator, locatorType);
		element.sendKeys(data);
	}

	/**
	 * Clear an element.
	 * 
	 * @param  	locator		string containing the locator	
	 * @param  	locatorType	string containing the type of locator
	 * @throws	Exception	let startHere() method catch it
	 */
	public void clearElement(String locator, String locatorType) throws Exception {
		WebElement element = null;

		element = getElement(locator, locatorType);
		element.clear();
	}

	/**
	 * Determine if an element is in the DOM.  
	 * It ultimately calls findElements which returns an empty List if the element is not in the DOM.
	 * I could have used findElement surrounded by a try-catch but I prefer not to handle unnecessary exceptions.
	 * 
	 * @param  	locator		string containing the locator	
	 * @param  	locatorType	string containing the type of locator
	 * @return				boolean true if the element is in the DOM, false if it is not
	 */
	public boolean isElementPresent(String locator, String locatorType) {
		List<WebElement> elementList = getElements(locator, locatorType);
		int size = elementList.size();

		if (size > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Explicit Wait for a list of WebElements.
	 * 
	 * @param  	locator		a By object containing the locator of the web elements	
	 * @param  	timeout		duration of the explicit wait in seconds
	 * @return 				a List of WebElements if they become present within the alloted time
	 */	
	public List <WebElement> waitForElements(By locator, int timeout) {
		List <WebElement> elements = null;
		try {
			log.info("waitForElements: Waiting for max " + timeout + " seconds for elements to become available");

			// mixing implicit and explicit waits is not a good idea.  The resulting wait time is unpredictable.
			// Better to disable the implicit wait, create an explicit wait (which is customized to a specific element)
			// and then restore the previous implicit wait time.
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);  // disable implicit wait

			WebDriverWait wait = new WebDriverWait(driver, timeout);
			elements = wait.until(
					ExpectedConditions.presenceOfAllElementsLocatedBy(locator));

			driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);  // restore implicit wait
			log.debug("waitForElements: Elements appeared on the web page");
		} catch(Exception e) {
			log.debug("waitForElements: Elements did not appear on the web page");
		}
		return elements;
	}
}