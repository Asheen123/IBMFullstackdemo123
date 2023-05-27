package APIAutomationtest.Restpractise;

import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class BasicAPItest {
	
	@Test(enabled = true)
	public void Posttest() {
		
		RestAssured.baseURI = "http://localhost:3000";
		
		String rsp = RestAssured.given().log().all().when().get("Food").thenReturn().asString();
		
		System.out.println(rsp);
		
		
		
		
	}

}
