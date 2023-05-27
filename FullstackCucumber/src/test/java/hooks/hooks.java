package hooks;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import cucumber.api.java.After;
import cucumber.api.java.Before;

public class hooks {

	
	public static WebDriver driver;
	
	
	@Before
		
public void init() {
		System.setProperty("webdriver.chrome.driver","C:\\Users\\002DSN744\\Desktop\\IBM\\Fullstacktester\\chromedriver_win32\\chromedriver.exe");
		   driver = new ChromeDriver();
	}
	
	@After
	
	public void quit() {
		
		driver.quit();
		System.out.println("Closing driver");
	}
	
	

}
