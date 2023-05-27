package testcases;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;


public class Test1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		System.setProperty("webdriver.chrome.driver","C:\\Users\\002DSN744\\Desktop\\IBM\\Fullstacktester\\chromedriver_win32\\chromedriver.exe");
		
		WebDriver driver = new ChromeDriver();
		
		//driver.
		
		driver.get("https://demo.guru99.com/test/radio.html");

        driver.findElement(By.id("vfb-7-1")).click();

        driver.findElement(By.id("vfb-6-0")).click();
        
        
		
	
		
		
		
		
	}

}
