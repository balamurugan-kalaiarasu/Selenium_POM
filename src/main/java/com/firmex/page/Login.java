package com.firmex.page;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import com.firmex.base.TestBase;
import com.firmex.helper.Constants;
import com.firmex.helper.Defs;

/**
 * Login page functionalities are written here
 * 
 * @author Bala
 *
 */
public class Login extends TestBase{

	
	@FindBy(xpath = "//img[@class='loginLogo']")
	static WebElement loginLogo;
	@FindBy(xpath = "//h4[text()='Email Address']")
	static WebElement emailLabel;
	@FindBy(xpath = "//input[@name='email']")
	static WebElement emailTextbox;
	@FindBy(xpath = "//input[@value='next']")
	static WebElement next;
	@FindBy(xpath = "//h4[text()='Password']")
	static WebElement passwordLabel;
	@FindBy(xpath = "//input[@name='password']")
	static WebElement passwordTextbox;
	@FindBy(xpath = "//a[text()='Reset/Forgot Password?']")
	static WebElement resetForgotPassword;
	@FindBy(xpath = "//input[@value='back']")
	static WebElement back;
	@FindBy(xpath = "//span[contains(@for, 'access_denied')]")
	static WebElement accessDenied;
	@FindBy(xpath = "//input[@value='Log In']")
	static WebElement loginButton;	
	
	//Dummy  WebElement
	@FindBy(xpath = "TEST_LOGIN_SUCCESS DUMMY ELEMENT")
	static WebElement loginSuccess;	
	
	public Login() {
		PageFactory.initElements(webDriver, this);
	}	
	
	
	/**
	 * Verifies the error message for invalid credentials
	 * 
	 * @param email
	 * @param password
	 * @return
	 */
	public boolean verifyLoginError(String email, String password) {
		
		boolean result = false;		
		try {
			verifyLoginPageAndEnterDetails(email, password);
			result = commonMethods.elementPresenceCheck(accessDenied) && !commonMethods.elementPresenceCheck(loginSuccess);
			if(result) {
				result = StringUtils.contains(commonMethods.getText(accessDenied), Constants.INVALID_CREDENTIALS);
				Defs.log("Login Failed. Reason - "+commonMethods.getText(accessDenied), extentTest, Constants.PASSED);
			}else {
				Defs.log("Login successful. Message - "+commonMethods.getText(loginSuccess), extentTest, Constants.FAILED);
			}
			Defs.takeOneShot(webDriver, "LoginStatus", result ? accessDenied : loginSuccess, result);
		} catch (Exception e) {
 			Defs.logger.info("Exception occurred in verifyLoginError -->>"+e);
		}
		return result;
	}
	
	
	/**
	 * Verifies the fields present in the page and
	 * performs login
	 * 
	 * @param email
	 * @param password
	 */
	public void verifyLoginPageAndEnterDetails(String email, String password) {
		Assert.assertTrue(commonMethods.elementPresenceCheck(new WebElement[] {loginLogo, emailLabel, emailTextbox, next}, true), "Field level validation failed");		
		commonMethods.sendKeys(emailTextbox, email);
		commonMethods.click(next);
		commonMethods.waitUntilElementIsClickable(loginButton);
		Assert.assertTrue(commonMethods.elementPresenceCheck(new WebElement[] {passwordLabel, passwordTextbox, back, loginButton, resetForgotPassword}, true), "Field level validation failed");	
		commonMethods.sendKeys(passwordTextbox, password);
		commonMethods.click(loginButton);		
	}

}
