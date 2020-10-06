package com.firmex.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.assertthat.selenium_shutterbug.core.Shutterbug;
import com.assertthat.selenium_shutterbug.utils.web.ScrollStrategy;
import com.firmex.base.TestBase;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

/**
 * Framework related reusable methods
 * 
 * @author Bala
 *
 */
public class Defs {

	static Workbook book;
	static Sheet sheet;
	
	public static String resultPath = "";
	public static String screenShotPath="";
	public static String resultFlag = "";
	public static String testCaseName="";
	public static Logger logger = Logger.getLogger("logs");
	public static int screenshotIndex = 1;
	
	public static String url = "";
		
	 
	/**
	 * Creates a dynamic result folder for storing screenshots, logs & report
	 */
	public static void createResultFolder() {
		
		DateFormat dateFormat = new SimpleDateFormat(Constants.TIMESTAMP_FORMAT);
		Date date = new Date();

		String path = "", serverName = "";

		try {
			serverName = "" + InetAddress.getLocalHost();

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		path = Constants.RESULT_DIRECTORY_PATH + "\\Execution_Results_" + serverName.substring(0, serverName.indexOf('/')) + "_"
				+ dateFormat.format(date)+"_"+new CommonMethods().generateRandomNumber(3);
		logger.info(path);
		File dir = new File(path);
		boolean success = dir.mkdir();
		if (success) {
			resultPath = path;
			logger.info("Path created successfully--->" + resultPath);
		}
		
		//Screen Shot paths
		path=resultPath+"\\Screenshots";
		dir = new File(path);		
		success = dir.mkdir();
		if (success) {
			screenShotPath = path;
			logger.info("Path created ScreenShot page path--->" + resultPath);
		}
	}

	/**
	 * Logger initialization
	 */
	public static void setUpLog() {

		try {
			String serverDir = Defs.resultPath;
			File directory = new File(serverDir);
			if (!directory.exists()) {
				directory.mkdir();
				serverDir += "\\DBLogs";
				directory = new File(serverDir);
				directory.mkdir();
			}
			String file_name = serverDir + "\\report.log";

			FileHandler fh = new FileHandler(file_name);
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
			logger.info("Logger is inilitized");
		} catch (IOException e) {
			logger.info("IO Exception in initializing" + e);
			System.exit(1);
		} catch (Exception e) {
			logger.info("Exception in initializing" + e);
			System.exit(1);
		}

	}
	
	/**
	 * Extent report log. General method.
	 * 
	 * @param data
	 * @param extentTest
	 */
	public static void log(String data, ExtentTest extentTest) {
		logger.info(data);
		extentTest.log(LogStatus.INFO, data);
	}
	
	/**
	 * Extent report log with status
	 * 
	 * @param data
	 * @param extentTest
	 * @param status
	 */
	public static void log(String data, ExtentTest extentTest,String status) {
		logger.info(data);
		if(StringUtils.equalsIgnoreCase(status,Constants.INFO)) {
			extentTest.log(LogStatus.INFO, data);
		}else if(StringUtils.equalsIgnoreCase(status,Constants.PASSED)) {
			extentTest.log(LogStatus.PASS, data);
		}else if(StringUtils.equalsIgnoreCase(status,Constants.FAILED)) {
			extentTest.log(LogStatus.FAIL, data);
		}
		
	}

	
	/**
	 * Reads test data from test data excel file
	 * 
	 * @param sheetName
	 * @param scriptName
	 * @return HashMap
	 */
	public static Map<String, String> getTestData(String sheetName, String scriptName) {
		FileInputStream file = null;
		Map<String, String> map = new HashMap<String, String>();
		try{
			try {
				file = new FileInputStream(Constants.TEST_DATA_SHEET_PATH);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			try {
				book = WorkbookFactory.create(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
			sheet = book.getSheet(sheetName);	
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {

				if (sheet.getRow(i).getCell(0).toString().equalsIgnoreCase(scriptName)) {				
					for (int k = 1; k < sheet.getRow(i).getLastCellNum(); k++) {					
						String data=sheet.getRow(i ).getCell(k).toString();					
						map.put(data.substring(0,data.indexOf('=')), data.substring(data.indexOf('=')+1));
					}
					break;
				}
			}
			if(map.size()==0){
				logger.info("No data is read from test data");
			}else{
				logger.info("Data read form test data--->"+map.toString());
			}
		}catch(Exception e) {
			logger.info("Exception occurred in TestData read  - "+e);
		}finally {
			if (file != null) {
				try {
					file.close();
				} catch (IOException e) {
					e.printStackTrace();
					logger.info("Exception in closing fileinputstream  - "+e);
				}
			}
		}
		return map;
	}
	
	
	/**
	 * Storing test execution history
	 */
	public static void setResultData() {
		try {
			logger.info("Writing the result to excel-->"+resultPath);
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			Date date = new Date();
			String finalDate = dateFormat.format(date).toString();
			try {
				String line =  "\n"+finalDate + "," + testCaseName   + "," + resultFlag + "," + resultPath;
				FileWriter fw = new FileWriter(Constants.RESULT_DATA, true);
				fw.write(line);
				fw.close();
			
			} catch (IOException ioe) {
				logger.info("Error-->"+ioe);
			}
			
		} catch (Exception e) {
			logger.info("Error-->"+e);
		}
	}
	
	/**
	 * Takes screenshot with error message overlaid on screenshot
	 * 
	 * @param webDriver
	 * @param error
	 * @return
	 */
	public static String takeOneShot(WebDriver webDriver,String error) {
		// createFolder(path);
		logger.info("Inside Screen Shot");
		String path = screenShotPath;
		String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
		String name = "ScreenShot_" + timeStamp;
		try {
			
			if (StringUtils.equalsIgnoreCase(TestBase.browser, Constants.FIREFOX_BROWSER) || StringUtils.equalsIgnoreCase(TestBase.browser, Constants.IE_BROWSER)) {
				Shutterbug.shootPage(webDriver, ScrollStrategy.BOTH_DIRECTIONS).withName(name).withTitle(error).save(path);
			}

			if (StringUtils.equalsIgnoreCase(TestBase.browser, Constants.CHROME_BROWSER)) {
				Shutterbug.shootPage(webDriver, ScrollStrategy.WHOLE_PAGE_CHROME).withName(name).withTitle(error).save(path);
			}

		} catch (Exception e) {
			logger.info("Exception occurred in takeOneShot --->>"+e);
		}
		logger.info(path + "\\" + name);
		return path + "\\" + name + ".png";
	}	

	/**
	 * Takes screenshot for a full page and highlight the 
	 * array of element
	 * 
	 * @param webDriver
	 * @param screenshotName
	 * @param highlights
	 * @param result
	 * @return
	 */
	public static String takeOneShot(WebDriver webDriver,String screenshotName,WebElement[] highlights,Boolean result) {
		String path = screenShotPath;
		// createFolder(path);
		logger.info("Inside Screen Shot");
		String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
		String name = "Screenshot_"+(screenshotIndex++)+"_"+screenshotName+"_"+ timeStamp;
		CommonMethods commonMethods = new CommonMethods();
		
		for(WebElement a : highlights) {	
			
			String highlight = commonMethods.getXpath(a);
			highlightElement(webDriver, highlight, result);	
		}
		
		fullshot(webDriver, name, path);
		
		for(WebElement a : highlights) {	
			
			String highlight = commonMethods.getXpath(a);
			deHighlightElement(webDriver, highlight);
		}	
				
		
		logger.info(path + "\\" + name);
		return name + ".png";
	}
	
	/**
	 * Takes screenshot for a full page and highlight the element of interest
	 * 
	 * @param webDriver
	 * @param screenshotName
	 * @param highlight
	 * @param result
	 * @return
	 */
	public static String takeOneShot(WebDriver webDriver,String screenshotName, WebElement highlight, Boolean result) {
		String path = screenShotPath;
		// createFolder(path);
		logger.info("Inside Screen Shot");
		String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
		String name = "Screenshot_"+(screenshotIndex++)+"_"+screenshotName+"_"+ timeStamp;
		CommonMethods commonMethods = new CommonMethods();	
			
		String highlightXpath = commonMethods.getXpath(highlight);
		
		highlightElement(webDriver, highlightXpath, result);		
		
		fullshot(webDriver, name, path);	
			
		deHighlightElement(webDriver, highlightXpath);
				
		
		logger.info(path + "\\" + name);
		return name + ".png";
	}
	
	/**
	 * Highlights the element with javascript
	 * 
	 * @param webDriver
	 * @param highlight
	 * @param result
	 */
	public static  void highlightElement(WebDriver webDriver, String highlight, Boolean result) {
		try {
			logger.info("Highlight Value --->"+highlight);
			if(!(highlight.isEmpty())) {
			JavascriptExecutor js = (JavascriptExecutor) webDriver;
			
			WebElement element = webDriver.findElement(By.xpath(highlight));
				if(result) {
					js.executeScript("arguments[0].setAttribute('style', 'border: 2px solid "+Constants.COLOR_GREEN+";');",
							new Object[] { element });
				}else {
					js.executeScript("arguments[0].setAttribute('style', 'border: 2px solid "+Constants.COLOR_RED+";');",
						new Object[] { element });
				}
			}
		} catch (Exception e) {
			logger.info("=====Exception Occured in Highlighting the xpath===="+e);
		}
	}
	
	
	/**
	 * Unhighlight the element with javascript
	 * 
	 * @param webDriver
	 * @param highlight
	 */
	public static void deHighlightElement(WebDriver webDriver, String highlight) {		
		// D-highlight
		try {
			if(!(highlight.isEmpty())) {
			JavascriptExecutor js = (JavascriptExecutor) webDriver;
			WebElement element = webDriver.findElement(By.xpath(highlight));
			js.executeScript("arguments[0].setAttribute('style', arguments[1]);", element, "");
			}
		} catch (Exception e) {
			logger.info("====Exception Occured in Highlighting the xpath===="+e);
		}
	}
	
	/**
	 * Takes full page screenshot using shutterbug
	 * 
	 * @param webDriver
	 * @param name
	 * @param path
	 */
	public static void fullshot(WebDriver webDriver, String name, String path) {
		
		try {
			
			logger.info("Browser-->" + TestBase.browser);
			
			if (StringUtils.equalsIgnoreCase(TestBase.browser, Constants.FIREFOX_BROWSER) || StringUtils.equalsIgnoreCase(TestBase.browser, Constants.IE_BROWSER)) {
				Shutterbug.shootPage(webDriver, ScrollStrategy.BOTH_DIRECTIONS).withName(name).save(path);
			}

			if (StringUtils.equalsIgnoreCase(TestBase.browser, Constants.CHROME_BROWSER)) {
				Shutterbug.shootPage(webDriver, ScrollStrategy.WHOLE_PAGE_CHROME).withName(name).save(path);				
			}
			
		} catch (Exception e) {
			logger.info("====Exception Occured in fullshot===="+e);

		}
	}	
	
	/**
	 * Takes normal screenshot with Selenium
	 * 
	 * @param webDriver
	 * @param screenshotName
	 * @param highlight
	 * @param result
	 * @return
	 */
	public static String takeScreenShot(WebDriver webDriver,String screenshotName, WebElement[] highlights,Boolean result) {
		String path = screenShotPath;
		// createFolder(path);
		Defs.logger.info("Inside Screen Shot");
		String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
		String name = "Screenshot_"+(screenshotIndex++)+"_"+screenshotName+"_"+ timeStamp;
		CommonMethods commonMethods = new CommonMethods();
		
		//Highlights the element
		for(WebElement a : highlights) {	
			
			String highlight = commonMethods.getXpath(a);
			highlightElement(webDriver, highlight, result);	
		}
	
		try {
	        File screenshotFile = ((TakesScreenshot)webDriver).getScreenshotAs(OutputType.FILE);
	        FileUtils.copyFile(screenshotFile, new File(path+"\\"+name+".png"));
	     }catch(Exception e) {
	    	 Defs.logger.info("Screenshot exception");	
	     }
		
		// Removing the highlight in elements
		for(WebElement a : highlights) {	
			
			String highlight = commonMethods.getXpath(a);
			deHighlightElement(webDriver, highlight);
		}
			
		Defs.logger.info(path + "\\" + name);
		return name + ".png";
	}
	
	/**
	 * Takes normal screenshot with Selenium with
	 * array of webelement to be highlighted
	 * 
	 * @param webDriver
	 * @param screenshotName
	 * @param highlight
	 * @param result
	 * @return
	 */
	public static String takeScreenShot(WebDriver webDriver,String screenshotName, WebElement highlight,Boolean result) {
		String path = screenShotPath;
		// createFolder(path);
		Defs.logger.info("Inside Screen Shot");
		String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
		String name = "Screenshot_"+(screenshotIndex++)+"_"+screenshotName+"_"+ timeStamp;
		CommonMethods commonMethods = new CommonMethods();
		
		// highlight
		String highlightXpath = commonMethods.getXpath(highlight);
		highlightElement(webDriver, highlightXpath, result);
	
		try {
	        File screenshotFile = ((TakesScreenshot)webDriver).getScreenshotAs(OutputType.FILE);
	        FileUtils.copyFile(screenshotFile, new File(path+"\\"+name+".png"));
	     }catch(Exception e) {
	    	 Defs.logger.info("Screenshot exception");	
	     }
		
		deHighlightElement(webDriver, highlightXpath);
			
		Defs.logger.info(path + "\\" + name);
		return name + ".png";
	}
	
	/**
	 * To get the usual error message and later used to display
	 * error message in screenshot
	 * 
	 * @param data
	 * @param dataError
	 * @return
	 */
	public static String getError(String data , String dataError) {
		String error=data;		
		if(data.contains(Constants.NO_SUCH_ELEMENT_EXCEPTION)) {
			String actualxpath = "Error ";
			try {
				actualxpath = dataError.split("Using=xpath, value=")[1];
			}catch(Exception e) {
				logger.info("====Exception Occured in dataError split===="+e);
			}
			error="Element is not found in the webpage. Xpath : "+actualxpath.substring(0, actualxpath.length()-1);
		}		
		if(data.contains(Constants.ASSERTION_ERROR))
			error=data.substring(data.indexOf(":")+1);
		
		return error;
	}

}
