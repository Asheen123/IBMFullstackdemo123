package MobileAutomation.Secondtest;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Test;

import Basepackage.Base;
import Basepackage.Commonmethods;
import Page.Firstpage;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;

public class Firsttest extends Base {

	
	@Test
	 public void SelectCity() throws InterruptedException
	    {
	       // System.out.println( "Hello World!" );
	    	
	    	
	    	
	    	driver.findElement(By.id("com.bt.bms:id/launcher_tv_for_skip")).click();
	    	
	    	driver.findElement(By.id("com.bt.bms:id/btn_negative")).click();
	    	
	    	//driver.findElement(By.xpath("//android.widget.TextView[@text='Pune']")).click();
	    	
	    	Firstpage.selectcity();
	    	
	    	Thread.sleep(5000);
	    	
	    	Firstpage.clicksearch();
	    	
	    	
	    }
	
	@Test (dependsOnMethods = {"SelectCity"})
	
	public void SelectMovienadDate() throws InterruptedException {
	    	
	    	
	    	//driver.findElement(By.xpath("//android.widget.LinearLayout[@resource-id = 'com.bt.bms:id/menu_item_container_header_main']//android.widget.ImageView[1]")).click();
	    	
	    	//scroll("Drishyam 2");
			//Thread.sleep(3000);
	    	
	    	
	    	driver.findElement(By.xpath("//android.widget.TextView[@text = 'Drishyam 2']")).click();
	    	//driver.findElement(By.xpath("//android.widget.TextView[@text = 'Wait']")).isDisplayed();
	    	driver.findElement(By.xpath("//android.widget.TextView[@text = 'Book tickets']")).click();
	    	
	    	String date = Commonmethods.getpropertyvalue("Date");
	    	driver.findElement(By.xpath("//android.widget.TextView[@text = '"+date+"']")).click();
	    	
	    	scroll("Vilux Talkies: Khadki");
			Thread.sleep(3000);
	    	
	    	
	    	driver.findElement(By.xpath("//android.widget.TextView[@text = '09:00 AM']")).click();
	    	
	    	try {
	    	driver.findElement(By.xpath("//android.widget.Button[@text = 'Accept']")).click();
	    	driver.findElement(By.xpath("//android.widget.Button[@text = 'Select Seats']")).click();
	    	} catch(Exception e) {
	    	driver.findElement(By.xpath("//android.widget.Button[@text = 'Select Seats']")).click();
	    	}
	    	
			/*
			 * WebElement ele =
			 * driver.findElement(By.id("com.bt.bms:id/seat_table_seat_layout")); for(int i
			 * =0; i< s.size();i++) { System.out.println(s.get(i)); }
			 */
	    	
	    //	public MobileOptions (driver) {
	    //       this.driver = driver;
	     //   }

	    	
//	    	Actions action = new Actions(driver);
//	    	action.scrollByAmount(30, 0);
//	    	action.moveToElement(ele, 20, 50).click();
//	    	action.perform();


	    	
	    	
	    	
	    	
	    	
	    	
	    }
	
	//("+Drishyam 2+")
	public void scroll(String text) {
     driver.findElementByAndroidUIAutomator("new UiScrollable(new UiSelector().scrollable(true)"
     + ".instance(0)).scrollIntoView(new UiSelector().textContains(\"Vilux Talkies: Khadki\").instance(0))");
	}
}
