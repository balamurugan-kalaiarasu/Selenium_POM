package com.firmex.helper;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.firmex.base.TestBase;

import java.io.*;

/**
 * Selenium common/general methods
 * 
 * @author Bala
 *
 */
public class CommonMethods extends TestBase{

	
	/**
	 * Checks whether element is displayed or not
	 * @param element
	 * @return boolean
	 */
	public boolean elementPresenceCheck(WebElement element) 
	{
		try
		{
			Defs.logger.info("*******Element presence check*********"+element.isDisplayed());
			if(element.isDisplayed()) {
				Defs.logger.info("Element is displayed in UI");
				return true;
			}else {
				Defs.logger.info("Element is not presenet in UI");
				return false;
			}	
		}catch(org.openqa.selenium.NoSuchElementException e){
			Defs.logger.info("No Such Element Exception Occur");
			return false;
		}

	}	
	
	/**
	 * Extracts xpath from WebElement object
	 * @param element
	 * @return String
	 */
	public String getXpath(WebElement element) {

		String[] a = element.toString().split("xpath:");
		Defs.logger.info("Extracted xpath----->"+a[a.length-1].substring(0, a[a.length-1].length()-1)); 
		String xpath = a[a.length-1].substring(0, a[a.length-1].length()-1);
		return xpath;	
	}

	/**
	 * Get text from WebElement
	 * @param element
	 * @return String
	 */
	public String getText(WebElement element) {
		waitUntill(element);
		return element.getText().trim();		
	}	

	/**
	 * Creates a directory
	 * @param newFolder
	 */
	public void createFolder(String newFolder) {
		File theDir = new File(newFolder);

		// if the directory does not exist, create it
		if (!theDir.exists()) {
			Defs.logger.info("creating directory: " + theDir.getName());
			boolean result = false;

			try {
				theDir.mkdir();
				result = true;
			} catch (SecurityException se) {
				// handle it
			}
			if (result) {
				Defs.logger.info("DIR created");
			}
		}

	}	

	/**
	 * Checks element is displayed or not for array
	 * of elements
	 * @param elements - Array of WebElements
	 * @return boolean
	 */
	public boolean elementPresenceCheck(WebElement elements[], boolean isFullPageScreenshot) {
		try
		{
			for(WebElement element:elements)
			{
				Defs.logger.info("*******Element presence check*********" + element.isDisplayed());
				if (element.isDisplayed()) 
				{
					Defs.logger.info("Element is displayed in UI");
				}
				else 
				{
					Defs.logger.info("Element is not present in UI");
					return false;
				}
			}

		}catch (org.openqa.selenium.NoSuchElementException e) {
			Defs.logger.info("No Such Element Exception Occur");
			return false;
		}
		if(isFullPageScreenshot)
			Defs.takeOneShot(webDriver, "elements", elements, true); 
		else
			Defs.takeScreenShot(webDriver, "elements", elements, true);
		return true;
	}

	
	/**
	 * Waits until element is displayed
	 * @param element
	 */
	public void waitUntill(WebElement element) {

		try {
			WebDriverWait driverWait=new WebDriverWait(webDriver, 20);
			driverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(getXpath(element))));
		} catch (Exception e) { 

		}
	}

	/**
	 * Waits until element is clickable
	 * @param element
	 */
	public void waitUntilElementIsClickable(WebElement element) {
		try {
			WebDriverWait wait = new WebDriverWait(webDriver, 20); 
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(getXpath(element))));
		}catch(Exception e) {
			Defs.logger.info("Exception occurred in waitUntilElementIsClickable -->>"+e);
		}
	}

	/**
	 * Enters a value in a WebElement
	 * @param element
	 * @param value to enter
	 */
	public void sendKeys(WebElement element, String value) {
		try {
			waitUntill(element);
			element.clear();
			element.sendKeys(value);
			Defs.takeOneShot(webDriver, "Email", element, true);
		}catch(Exception e) {
			Defs.logger.info("Exception occurred in SendKeys -->>"+e);
		}
	}

	/**
	 * Clicks the WebElement
	 * @param element
	 */
	public void click(WebElement element) {
		try {
			waitUntilElementIsClickable(element);			
			element.click();
		}catch(Exception e) {
			Defs.logger.info("Exception occurred in click -->>"+e);
		}
	}
		
	/**
	 * Generates random numbers
	 * @param size
	 * @return
	 */
	public String generateRandomNumber(int size) {
		String temp="0123456789";
		String name="";
		for(int i=0;i<size;i++) {
			name+=temp.charAt((int)(Math.random()*10));
		}
		return name;
	}

}
