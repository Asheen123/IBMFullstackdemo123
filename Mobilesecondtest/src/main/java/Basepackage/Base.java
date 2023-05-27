package Basepackage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;

public class Base {

	public static AndroidDriver driver;
	public static ExtentReports report;
	public static ExtentTest extentTestVar;

	@BeforeTest
	public void setExtent() {
		report = new ExtentReports(System.getProperty("user.dir") + "/Reports/ExtentReport.html", true);
		report.addSystemInfo("Host Name", "Testing Machine");
		report.addSystemInfo("User Name", "Asheen test");
		report.addSystemInfo("Environment", "QA");

	}

	@BeforeTest
	public void Basemethods() throws IOException {

		File appDir = new File(
				"C:\\Users\\002DSN744\\Desktop\\IBM\\Fullstacktester\\apps\\com.bt.bms_11.5.1-11510_minAPI23(arm64-v8a,armeabi-v7a,x86,x86_64)(nodpi)_apkmirror.com.apk");

		DesiredCapabilities cap = new DesiredCapabilities();

		cap.setCapability(MobileCapabilityType.DEVICE_NAME, "Firstdevice");
		// cap.setCapability(MobileCapabilityType.BROWSER_NAME, "Chrome");
		cap.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");

		cap.setCapability("app", appDir.getAbsolutePath());

		cap.setCapability("autoGrantPermissions", true);

		// cap.setCapability("appPackage", "com.android.vending");
		// cap.setCapability("appActivity","com.aranoah.healthkart.plus.doctors.doctorratingactivity");
		
		String URL = Base.GetExcelvalue();

		driver = new AndroidDriver(new URL(URL), cap);

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	@AfterMethod
	public void tearDown(ITestResult result) throws IOException {

		extentTestVar = report.startTest(result.getName());

		if (result.getStatus() == ITestResult.FAILURE) {
			extentTestVar.log(LogStatus.FAIL, "TEST CASE FAILED IS " + result.getName()); // to add name in extent
																							// report
			extentTestVar.log(LogStatus.FAIL, "TEST CASE FAILED IS " + result.getThrowable()); // to add error/exception
																								// in extent report

			String screenshotPath = Base.getScreenshot(driver, result.getName());
			extentTestVar.log(LogStatus.FAIL, extentTestVar.addScreenCapture(screenshotPath)); // to add screenshot in
																								// extent report
			// extentTest.log(LogStatus.FAIL, extentTest.addScreencast(screenshotPath));
			// //to add screencast/video in extent report
		} else if (result.getStatus() == ITestResult.SKIP) {
			extentTestVar.log(LogStatus.SKIP, "Test Case SKIPPED IS " + result.getName());
		} else if (result.getStatus() == ITestResult.SUCCESS) {
			extentTestVar.log(LogStatus.PASS, "Test Case PASSED IS " + result.getName());

		}

		report.endTest(extentTestVar); // ending test and ends the current test and prepare to create html report
		// driver.quit();
	}

	@AfterTest
	public void endReport() {
		report.flush();
		// driver.quit();
	}

	// Take screenshot code

	public static String getScreenshot(AndroidDriver driver, String screenshotName) throws IOException {

		// Get current date
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
		LocalDateTime now = LocalDateTime.now();
		// System.out.println(dtf.format(now));

		String dateName = dtf.format(now);
		TakesScreenshot ts = (TakesScreenshot) driver;
		File source = ts.getScreenshotAs(OutputType.FILE);
		// after execution, you could see a folder "FailedTestsScreenshots"
		// under src folder
		String destination = System.getProperty("user.dir") + "/Reports/Screenshots/" + screenshotName + dateName
				+ ".jpeg";
		System.out.println("destination path " + destination);
		File finalDestination = new File(destination);
		FileUtils.copyFile(source, finalDestination);
		return destination;
	}

// Read Excel sheet

	public static String GetExcelvalue() throws IOException {
		String pathofExcelSheet = "C:\\Users\\002DSN744\\Desktop\\IBM\\Fullstacktester\\Data\\Appium-Input.xlsx";
		File file = new File(pathofExcelSheet);
		FileInputStream fis = new FileInputStream(file);

		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet sheetOfInterest = wb.getSheet("Sheet1");
		XSSFRow rowOfInterest = sheetOfInterest.getRow(1);
		String value = rowOfInterest.getCell(1).getStringCellValue();
		System.out.println("value :" + value);
		return value;
	}

}
