package Stepdefinations;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import junit.framework.Assert;




public class Assignmentstepdefinations {
	
	WebDriver driver = hooks.hooks.driver;
	
	@Given("User logs in the elearning portal")
	public void user_logs_in_the_elearning_portal() {
	    // Write code here that turns the phrase above into concrete actions
	   // throw new cucumber.api.PendingException();
		
	      driver.get("http://elearningm1.upskills.in/index.php");
	}

	@When("User clicks Sign Up")
	public void user_clicks_Sign_Up() {
	    // Write code here that turns the phrase above into concrete actions
	   // throw new cucumber.api.PendingException();
		driver.findElement(By.xpath("//a[text()=' Sign up! ']")).click();
		
	}

	@When("User gives all details and clicks Register")
	public void user_gives_all_details_and_clicks_Register() {
	    // Write code here that turns the phrase above into concrete actions
	   // throw new cucumber.api.PendingException();
		
		driver.findElement(By.xpath("//input[@name = 'firstname']")).sendKeys("Ashtest123");
		driver.findElement(By.xpath("//input[@name = 'lastname']")).sendKeys("Lasttest123");
		driver.findElement(By.xpath("//input[@name = 'email']")).sendKeys("ash12@gmail.com");
		driver.findElement(By.id("username")).sendKeys("Ashdemo1234");
		driver.findElement(By.id("pass1")).sendKeys("password12");
		driver.findElement(By.id("pass2")).sendKeys("password12");
		driver.findElement(By.xpath("//button[@name = 'submit']")).click();
		
		
		
		
	}

/*	@When("User clicks next button and Homepage button")
	public void user_clicks_next_button_and_Homepage_button() {
	    // Write code here that turns the phrase above into concrete actions
	   // throw new cucumber.api.PendingException();
		
		driver.findElement(By.xpath("//input[@name = 'next']")).click();
		driver.findElement(By.xpath("//a[text()='Homepage']")).click();
		
	}  */
	
	@When("User gives Username password and clicks login")
	public void user_gives_Username_password_and_clicks_login() {
	    // Write code here that turns the phrase above into concrete actions
	  //  throw new cucumber.api.PendingException();
		
		driver.findElement(By.id("login")).sendKeys("Ashdemo1234");
		driver.findElement(By.id("password")).sendKeys("password12");
		driver.findElement(By.xpath("//button[text()='Login']")).click();
	}


	@When("User clicks compose give all details and clicks send message button")
	public void user_clicks_compose_give_all_details_and_clicks_send_message_button() throws InterruptedException {
	    // Write code here that turns the phrase above into concrete actions
	   // throw new cucumber.api.PendingException();
		
		driver.findElement(By.xpath("//li[@class = 'new-message-social']/a")).click();
		driver.findElement(By.className("select2-search__field")).sendKeys("div");
		Thread.sleep(2000);
		driver.findElement(By.xpath("//li[text()='Divya jha']")).click();
		//driver.findElement(By.className("select2-results__option select2-results__option--highlighted")).click();
		driver.findElement(By.xpath("//div[@class = 'col-sm-8']/input")).sendKeys("firstmail");
		driver.findElement(By.xpath("//*[@name = 'compose' and @type = 'submit']")).click();
		
		
		
	}

	@Then("User should get a sucessful sent message")
	public void user_should_get_a_sucessful_sent_message() {
	    // Write code here that turns the phrase above into concrete actions
	   // throw new cucumber.api.PendingException();
		String sucessmsg =driver.findElement(By.xpath("//div[@class = 'alert alert-success']")).getText();
		System.out.println("sucessmsg "+sucessmsg);
		
		
		Assert.assertEquals("The message has been sent to Divya jha", sucessmsg);
	}


	

}
