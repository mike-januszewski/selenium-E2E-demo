package seleniumDemo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


/**
 * SeleniumDriverUtilities contains common and general purpose methods. Other classes inherit from this class. 
 * @author mikej
 */
public class SeleniumUtilities {
	private static final Logger log = LogManager.getLogger(SeleniumUtilities.class);
	WebDriver driver;  // MJJ- private??? - NO!!!!


	/**
	 *  SeleniumDriverUtilities constructor
	 *  
	 */
	public SeleniumUtilities(WebDriver driver) {
		this.driver = driver;
	}

	
	// OVERLOADING getElement
	
	/**
	 * Find a Selenium element using a locator 
	 * @return WebElement
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
	 * Find a Selenium element using either xpath or id locators by concatenating  
	 * the first part of an xpath with an index into a tag array and then with the second part
	 * of the xpath.
	 * @return WebElement
	 */
	public WebElement getElement(String str1, int i, String str2, String locatorType) throws Exception {
		WebElement element = null;
		String locator = str1 + i + str2;
	
		element = getElement(locator, locatorType);
		
/*		
		if (locatorType.equals("ID"))
			element = driver.findElement(By.id(locator));
		else if (locatorType.equals("XPATH"))
			element = driver.findElement(By.xpath(locator));
		// other locators to be added later
*/
		return element;
	}
	
	/**
	 * Find a Selenium element using either xpath or id locators by concatenating  
	 * the first part of an xpath with an index into a tag array, then with the middle part
	 * of the xpath, then with another index into a tag array and finally with the last part
	 * of the xpath.
	 * @return WebElement
	 */
	public WebElement getElement(String str1, int i, String str2, int j, String str3, String locatorType) throws Exception {
		WebElement element = null;
		String locator = str1 + i + str2 + j + str3;
		
		element = getElement(locator, locatorType);
/*	
		if (locatorType.equals("ID"))
			element = driver.findElement(By.id(locator));
		else if (locatorType.equals("XPATH"))
			element = driver.findElement(By.xpath(locator));
		// other locators to be added later
*/
		return element;
	}
	
	// OVERLOADING getElements
	
	/**
	 * Find a List of Selenium elements using either xpath or id locators 
	 * @return List <WebElement>
	 */
	public List <WebElement> getElements(String locator, String locatorType) {
		List <WebElement> elements = null;
/*
		if (locatorType.equals("ID"))
			elements = driver.findElements(By.id(locator));
		else if (locatorType.equals("XPATH"))
			elements = driver.findElements(By.xpath(locator));
		// other locators to be added later
*/
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
	 * Find a List of Selenium elements using either xpath or id locators by concatenating  
	 * the first part of an xpath with an index into a tag array and then with the second part
	 * of the xpath.
	 * @return List <WebElement>
	 */
	public List <WebElement> getElements(String str1, int i, String str2, String locatorType) {
		List <WebElement> elements = null;
		String locator = str1 + i + str2;
		
		elements = getElements(locator, locatorType);
/*		
		if (locatorType.equals("ID"))
			elements = driver.findElements(By.id(locator));
		else if (locatorType.equals("XPATH"))
			elements = driver.findElements(By.xpath(locator));
		// other locators to be added later
*/
		return elements;
	}
	
	/**
	 * Find a List of Selenium elements using either xpath or id locators by concatenating  
	 * the first part of an xpath with an index into a tag array, then with the middle part
	 * of the xpath, then with another index into a tag array and finally with the last part
	 * of the xpath.
	 * @return List <WebElement>
	 */
	public List <WebElement> getElements(String str1, int i, String str2, int j, String str3, String locatorType) {
		List <WebElement> elements = null;
		String locator = str1 + i + str2 + j + str3;
		
		elements = getElements(locator, locatorType);
		
/*		
		if (locatorType.equals("ID"))
			elements = driver.findElements(By.id(locator));
		else if (locatorType.equals("XPATH"))
			elements = driver.findElements(By.xpath(locator));
		// other locators to be added later
*/
		return elements;
	}
	
	// OVERLOADING click   ********************************************
	
	/**
	 * Click on an element 
	 * 
	 */
	public void clickElement(String locator, String locatorType) throws Exception {
		WebElement element = null;

		element = getElement(locator, locatorType);
		element.click();
	}

	/**
	 * Click on an element by concatenating the first part of an xpath with an index 
	 * into a tag array and then with the second part of the xpath.
	 * 
	 */
	public void clickElement(String str1, int i, String str2, String locatorType) throws Exception {
		WebElement element = null;
		String locator = str1 + i + str2;
		
		element = getElement(locator, locatorType);
		element.click();
	}
	
	
	// *******************************************
	
	/**
	 * Send data to an element
	 * 
	 */
	public void sendKeysElement(String data, String locator, String locatorType) throws Exception {
		WebElement element = null;

		element = getElement(locator, locatorType);
		element.sendKeys(data);
	}

	/**
	 * clear an element
	 * 
	 */
	public void clearElement(String locator, String locatorType) throws Exception {
		WebElement element = null;

		element = getElement(locator, locatorType);
		element.clear();
	}
	
	/**
	 * Determine if an element is in the DOM.
	 * It ultimately calls findElements which returns an empty List if the element is not in the DOM.
	 * I could have used findElement surrounded by a try=catch but I prefer not to handle unnecessary exceptions.
	 * 
	 */
	public boolean isElementPresent(String locator, String type) {
	   List<WebElement> elementList = getElements(locator, type);
		   
	   int size = elementList.size();
		   
	   if (size > 0) {
	      return true;
	   }
	   else {
	      return false;
	   }
	}
	
	
	
	
	
	// This is used by the Review Your Trip and Payment pages to compare their prices with the 
	// price on the Search Results page.
	// The prices on the Review Your Trip and Payment pages are exact prices, meaning they 
	// including cents, while the price on the earlier Search Results page had been rounded up.
	// Orbitz always seems to round up, not round off.  For example, $123.01 becomes $124
	// without a decimal point.  
	// Strategy:  Round up the price, ignore the '$' sign and drop off the decimal point.
	//            Then comparing it to the Search Results price is straightforward.
	/**
	 * Verify whether 2 prices are equivalent.
	 * 
	 */
	public void verifyPrices(String sPriceField, String sSavedPrice) throws Exception {
		NumberFormat format = NumberFormat.getCurrencyInstance(); // handles the $ sign

		WebElement element = getElement(sPriceField, "XPATH");
		Number value = format.parse(element.getText().trim());
		BigDecimal big = new BigDecimal(value.toString());
		big = big.setScale(0, RoundingMode.UP);

		// sSavedPrice (from the Search Results page) still has a $ sign which we need 
		// to ignore, hence the .substring(1)
		if (big.toString().equals(sSavedPrice.substring(1)))
			log.info("verifyPrices()  Prices match.");
		else
			log.info("verifyPrices()  Prices do NOT match");
		
		log.debug("Price on Search Results page = " + sSavedPrice + " Price on the current page = " + big.intValue());
	}
}
