import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;

import static io.restassured.RestAssured.*;


import static org.hamcrest.Matchers.*;

import files.VirtualPayload;
import files.payload;

public class Practise1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		RestAssured.baseURI = "https://rahulshettyacademy.com";

		String response = given().log().all().body("{\r\n" + 
				"  \"location\": {\r\n" + 
				"    \"lat\": -38.383494,\r\n" + 
				"    \"lng\": 33.427362\r\n" + 
				"  },\r\n" + 
				"  \"accuracy\": 50,\r\n" + 
				"  \"name\": \"Frontline house\",\r\n" + 
				"  \"phone_number\": \"(+91) 983 893 3937\",\r\n" + 
				"  \"address\": \"29, side layout, cohen 09\",\r\n" + 
				"  \"types\": [\r\n" + 
				"    \"shoe park\",\r\n" + 
				"    \"shop\"\r\n" + 
				"  ],\r\n" + 
				"  \"website\": \"http://google.com\",\r\n" + 
				"  \"language\": \"French-IN\"\r\n" + 
				"}").queryParam("key", "qaclick123").
		header("Content-Type", "text/plain").when().post("/maps/api/place/add/json").
		then().log().all().assertThat().statusCode(200).body("status", equalTo("OK")).extract().asString();
		
		System.out.println(response);
		
		JsonPath js = new JsonPath(response);
		
		String placeid = js.getString("place_id");
		
		System.out.println(placeid);
		
		
		
	}

}
