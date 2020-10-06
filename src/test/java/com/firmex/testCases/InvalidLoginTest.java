package com.firmex.testCases;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.firmex.base.TestBase;
import com.firmex.helper.Constants;
import com.firmex.helper.Defs;
import com.firmex.page.Login;

/**
 * Test case to validate error message for invalid credentials
 * 
 * @author Bala
 *
 */
public class InvalidLoginTest extends TestBase{

	public InvalidLoginTest() {
		super();
		Defs.testCaseName = this.getClass().getSimpleName();
	}
		
	@Test()
	public void invalidLoginTest() {
		
		Assert.assertTrue(home.launchurl(), "Launching URL failed");
		Assert.assertEquals(home.getTitle(), Constants.TITLE, "Title verification failed");
		Defs.log("Title verified", extentTest, Constants.PASSED);
		Assert.assertTrue(home.verifyPageContents(), "Page content validation failed");
		Defs.log("Page contents are validated", extentTest, Constants.PASSED);
		Login login = home.goToLogin();
		Assert.assertTrue(login.verifyLoginError(data.get("Email"), data.get("Password")), "Login Error validation failed");
		Defs.log("Successfully validated error message for invalid credentials", extentTest, Constants.PASSED);
	}
}
