package com.firmex.base;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.firmex.helper.CommonMethods;
import com.firmex.helper.Constants;
import com.firmex.helper.Defs;
import com.firmex.listener.WebEventListener;
import com.firmex.page.Home;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Base class for the framework
 * 
 * @author Bala
 *
 */
public class TestBase {
	public static WebDriver webDriver;
	public static Properties prop;
	public  static EventFiringWebDriver e_driver;
	public static WebEventListener eventListener;
	public static CommonMethods commonMethods; 
	
	public static String sheetName = "data";
	public static String browser = "";
	public static ExtentReports extent;
	public static ExtentTest extentTest;
	public static Map<String, String> data;
	public static Home home;
	
	
	public TestBase() {
		try {
			prop = new Properties();
			FileInputStream fileInputStream = new FileInputStream(Constants.CONFIG_PATH);
			prop.load(fileInputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializing the required setup for 
	 * test execution
	 */
	@BeforeMethod
	public void setup() {
		//Creating result folder
		Defs.createResultFolder();
		//Setup the logs
		Defs.setUpLog();
		//Reading test data from excel
		data = Defs.getTestData(sheetName, Defs.testCaseName);
		//Initializing extent report
		extent = new ExtentReports(Defs.resultPath + File.separator + "Extent.html", true);
		Defs.logger.info("Logger added");
		//Initializing webDriver instance
		intialization();
		home= new Home();
		extentTest = extent.startTest(Defs.testCaseName);
	}
	
	/**
	 * Initializing WebDriver instance
	 */
	public static void intialization() {
		
		commonMethods = new CommonMethods();
		String downloadFilepath = Constants.DOWNLOAD_PATH;
		commonMethods.createFolder(downloadFilepath);		
		browser = prop.getProperty("browser");
		
		if (StringUtils.equalsIgnoreCase(browser, Constants.CHROME_BROWSER)) {
			
			HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
			chromePrefs.put("profile.default_content_settings.popups", 0);
			chromePrefs.put("download.default_directory", downloadFilepath);
			chromePrefs.put("profile.content_settings.exceptions.automatic_downloads.*.setting", 1 );
			ChromeOptions options = new ChromeOptions();
			options.setExperimentalOption("prefs", chromePrefs);
			WebDriverManager.chromedriver().setup();
			webDriver = new ChromeDriver(options);
			
		} else if (StringUtils.equalsIgnoreCase(browser, Constants.FIREFOX_BROWSER)) {			
			
			WebDriverManager.firefoxdriver().version("0.24.0").setup();
			webDriver = new FirefoxDriver();
			
		}else if (StringUtils.equalsIgnoreCase(browser, Constants.IE_BROWSER)) {
			
			WebDriverManager.iedriver().setup();
			webDriver = new InternetExplorerDriver();
			
		}
		
		e_driver = new EventFiringWebDriver(webDriver);
		// Now create object of EventListerHandler to register it with EventFiringWebDriver
		eventListener = new WebEventListener();
		e_driver.register(eventListener);
		webDriver = e_driver;
		webDriver.manage().window().maximize();
		webDriver.manage().deleteAllCookies();
		//Setting page load timeout
		webDriver.manage().timeouts().pageLoadTimeout(Constants.PAGE_LOAD_TIME, TimeUnit.SECONDS);
		//Setting implicit wait timeout for the session
		webDriver.manage().timeouts().implicitlyWait(Constants.IMPLICIT_WAIT, TimeUnit.SECONDS);		
	}
	
			
	/**
	 * Closing the webdriver instance and storing the result
	 * 
	 * @param result
	 */
	@AfterMethod
	public void tearDown(ITestResult result) {
		if (Defs.resultFlag != "" && Defs.resultFlag.equalsIgnoreCase("Failed")) {
			extentTest.log(LogStatus.FAIL, "TEST CASE FAILED IS " + Defs.testCaseName);
			Defs.resultFlag = Constants.FAILED;
		} else {
			if (result.getStatus() == ITestResult.FAILURE) {
				extentTest.log(LogStatus.FAIL, "TEST CASE FAILED IS " + Defs.testCaseName);
				extentTest.log(LogStatus.FAIL, "ERROR - " + result.getThrowable());
				Defs.logger.info("ERROR - " + result.getThrowable());
				String error ="";
				try {
					error = Defs.getError(result.getThrowable().toString(), result.getThrowable().getLocalizedMessage().toString());
				}catch(Exception e) {
					Defs.logger.info("Exception occured in getError - "+e);
				}
				String screenshotPath = Defs.takeOneShot(webDriver, error);
				Defs.resultFlag = Constants.FAILED;
				extentTest.log(LogStatus.FAIL, extentTest.addScreenCapture(screenshotPath));
			} else if (result.getStatus() == ITestResult.SKIP) {
				extentTest.log(LogStatus.SKIP, "Test Case SKIPPED IS " + Defs.testCaseName);
				Defs.resultFlag = Constants.FAILED;
			} else if (result.getStatus() == ITestResult.SUCCESS) {
				extentTest.log(LogStatus.PASS, "Test Case PASSED IS " + Defs.testCaseName);
				Defs.resultFlag = Constants.PASSED;
			}
		}
		webDriver.quit();
		Defs.log("Successfully Closed Browser " + browser, extentTest);
		Defs.logger.info("Result Folder --->" + Defs.resultPath);
		extent.endTest(extentTest);
		extent.flush();
		extent.close();
		//Storing test data results
		Defs.setResultData();
	}
}
