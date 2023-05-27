package Stepdefinations;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class Stepdefination {
	
	WebDriver driver = hooks.hooks.driver;
	
	@Given("Login the Facebook using the URL")
	public void login_the_Facebook_using_the_URL() {
	    // Write code here that turns the phrase above into concrete actions
	  //  throw new cucumber.api.PendingException();
		
		//System.setProperty("webdriver.chrome.driver","C:\\Users\\002DSN744\\Desktop\\IBM\\Fullstacktester\\chromedriver_win32\\chromedriver.exe");

	     //  driver = new ChromeDriver();

	        driver.get("https://www.facebook.com/login/");
	        

	}

	@When("User gives his login credentails")
	public void user_gives_his_login_credentails() {
	    // Write code here that turns the phrase above into concrete actions
	   // throw new cucumber.api.PendingException();
		
		 driver.findElement(By.id("email")).sendKeys("ash123@gmail.com");
	        driver.findElement(By.id("pass")).sendKeys("pass@123");
	}

	@Then("User should be able to login")
	public void user_should_be_able_to_login() {
	    // Write code here that turns the phrase above into concrete actions
	   // throw new cucumber.api.PendingException();
		
		driver.findElement(By.id("loginbutton")).click();
	}

	@When("User gives his {string} username and {string} password")
	public void user_gives_his_username_and_password(String username, String password) {
	    // Write code here that turns the phrase above into concrete actions
	    //throw new cucumber.api.PendingException();
		
		 driver.findElement(By.id("email")).sendKeys(username);
	        driver.findElement(By.id("pass")).sendKeys(password);
	}



}
