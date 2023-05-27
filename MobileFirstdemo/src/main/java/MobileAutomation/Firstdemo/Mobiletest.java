package MobileAutomation.Firstdemo;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.remote.MobileOptions;

/**
 * Hello world!
 *
 */
public class Mobiletest 
{
	
	
    public static void main( String[] args ) throws MalformedURLException, InterruptedException
    {
       // System.out.println( "Hello World!" );
    	
    	//File appDir = new File("C:\\Users\\002DSN744\\Desktop\\IBM\\Fullstacktester\\apps\\com.bt.bms_11.5.1-11510_minAPI23(arm64-v8a,armeabi-v7a,x86,x86_64)(nodpi)_apkmirror.com.apk");
    	
    	DesiredCapabilities cap = new DesiredCapabilities();
    	
    	cap.setCapability(MobileCapabilityType.DEVICE_NAME, "Firstdevice");
    	//cap.setCapability(MobileCapabilityType.BROWSER_NAME, "Chrome");
    	cap.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
    	
    	//cap.setCapability("app", appDir.getAbsolutePath());
    	
    	
    	
    	cap.setCapability("appPackage", "com.bt.bms");
    	
    	cap.setCapability("appActivity", "com.movie.bms.splashscreen.SplashScreenActivity");
    	
    	cap.setCapability("autoGrantPermissions", true);
    	
    //	cap.setCapability("appPackage", "com.android.vending");
    //	cap.setCapability("appActivity","com.aranoah.healthkart.plus.doctors.doctorratingactivity"); 
    	
    	AndroidDriver driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"),cap);
    	
    	driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
    	//Thread.sleep(2000);
    	driver.findElement(By.id("com.bt.bms:id/launcher_tv_for_skip")).click();
    	//Thread.sleep(2000);
    	driver.findElement(By.id("com.bt.bms:id/btn_negative")).click();
    	//Thread.sleep(2000);
    	driver.findElement(By.xpath("//android.widget.TextView[@text='Pune']")).click();
    	//driver.findElement(By.xpath("//android.widget.Button[@text='Allow']")).click();
    	driver.findElement(By.xpath("//android.widget.LinearLayout[@resource-id = 'com.bt.bms:id/menu_item_container_header_main']//android.widget.ImageView[1]")).click();
    	driver.findElement(By.xpath("//android.widget.TextView[@text = 'Drishyam 2']")).click();
    	//driver.findElement(By.xpath("//android.widget.TextView[@text = 'Wait']")).isDisplayed();
    	driver.findElement(By.xpath("//android.widget.TextView[@text = 'Book tickets']")).click();
    	driver.findElement(By.xpath("//android.widget.TextView[@text = '22']")).click();
    	
    	
    	
    	driver.findElement(By.xpath("//android.widget.TextView[@text = '12:30 PM']")).click();
    	driver.findElement(By.xpath("//android.widget.Button[@text = 'Accept']")).click();
    	driver.findElement(By.xpath("//android.widget.Button[@text = 'Select Seats']")).click();
    	WebElement ele = driver.findElement(By.id("com.bt.bms:id/seat_table_seat_layout"));
    	//for(int i =0; i< s.size();i++) {
    	//	System.out.println(s.get(i));
    	//}
    	
    //	public MobileOptions (driver) {
    //       this.driver = driver;
     //   }

    	
    //	Actions action = new Actions(driver);
    //	action.scrollByAmount(30, 0);
    //	action.moveToElement(ele, 20, 50).click();
    //	action.perform();


    	
    	// driver.findElementByAndriodUIAutomator("");
    	
    	
    	
    	
    }
}
