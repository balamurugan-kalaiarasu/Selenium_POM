package com.firmex.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import com.firmex.base.TestBase;
import com.firmex.helper.Defs;

/**
 * Functionalities for the home page written in this class
 * 
 * @author Bala
 *
 */
public class Home extends TestBase {

	@FindBy(xpath = "//a[@class='button login']")
	static WebElement login;
	@FindBy(xpath = "//div[contains(@class, 'survey-point')]")
	static WebElement surveyCheck;
	@FindBy(xpath = "//button[@aria-label='close']")
	static WebElement closeSurvey;
	@FindBy(xpath = "//a[@class='custom-logo-link']")
	static WebElement logoLink;
	@FindBy(xpath = "//ul[@id='primary-menu']//li//a[contains(text(),'Virtual Data Room')]")
	static WebElement menuVirtualDataRoom;
	@FindBy(xpath = "//ul[@id='primary-menu']//li//a[contains(text(),'Solutions')]")
	static WebElement menuSolutions;
	@FindBy(xpath = "//ul[@id='primary-menu']//li//a[contains(text(),'Customers')]")
	static WebElement menuCustomers;
	@FindBy(xpath = "//ul[@id='primary-menu']//li//a[contains(text(),'Support')]")
	static WebElement menuSupport;
	@FindBy(xpath = "//ul[@id='primary-menu']//li//a[contains(text(),'Resources')]")
	static WebElement menuResources;
	@FindBy(xpath = "//a[@class='button login']/following::a[contains(text(),'Book A Demo')][1]")
	static WebElement bookADemo;

	public Home() {
		PageFactory.initElements(webDriver, this);
	}

	/**
	 * Launches the URL
	 * 
	 * @return
	 */
	public boolean launchurl() {
		boolean result = true;

		try {
			String url = prop.getProperty("firmex_url");
			Defs.url = url;
			webDriver.get(Defs.url);
			Defs.log("Successfully lauched the URL - " + url, extentTest, "info");
		} catch (Exception e) {
			Defs.logger.info("Exception occurred in Launch URL --->>" + e);
		}
		return result;
	}

	/**
	 * Returns the title of the page
	 * 
	 * @return title
	 */
	public String getTitle() {
		Defs.logger.info("Title - " + webDriver.getTitle().trim());
		return webDriver.getTitle().trim();
	}

	public boolean verifyPageContents() {
		return commonMethods.elementPresenceCheck(new WebElement[] { logoLink, menuVirtualDataRoom, menuSolutions,
				menuCustomers, menuSupport, menuResources, bookADemo }, false);
	}

	/**
	 * Navigating to login page
	 * 
	 * @return Login
	 */
	public Login goToLogin() {
		// Closing the survey popup if appeared
		if (commonMethods.elementPresenceCheck(surveyCheck))
			commonMethods.click(closeSurvey);

		if (commonMethods.elementPresenceCheck(login)) {
			commonMethods.click(login);
		} else {
			Assert.assertTrue(false, "Login button is not present");
		}
		return new Login();
	}

}
