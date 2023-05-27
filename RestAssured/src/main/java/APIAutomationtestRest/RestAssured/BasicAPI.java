package APIAutomationtestRest.RestAssured;

import java.io.File;
import java.util.HashMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class BasicAPI {

	
	@Test(enabled = false)
	public void GET() {
        // First fetch the response in the response container
        Response resp = RestAssured.get("http://localhost:3000/workers/1001");
        
        
        // change the response in string and print it .
        System.out.println(resp.asString());
    }
	
	@Test(enabled = false)
	public void POST() {
		
		
		RestAssured.baseURI = "http://localhost:3000";
		
		String body = "{\r\n"
				+ "        \r\n"
				+ "        \"name\": \"Automationtest1\",\r\n"
				+ "        \"age\": 26\r\n"
				+ "    }";
		
		RestAssured.given().log().all().body(body).header("Content-Type","application/json")
		.when().post("workers").then().statusCode(201);
		
		// First fetch the response in the response container
        Response resp = RestAssured.get("http://localhost:3000/workers");
        
        
        // change the response in string and print it .
        System.out.println(resp.asString());
	}
	
//DELETE
	
	@Test(enabled = false)
    public void Delete() {
        // First fetch the response in the response container
        Response resp = RestAssured.when()
                .delete("http://localhost:3000/workers/1008");
        // Extract the response code and response message and print it
        System.out.println(resp.statusCode());
        System.out.println(resp.statusLine());
    }
	
// PATCH
	
	@Test(enabled = false)
	public void PATCH() {
		
		RestAssured.baseURI = "http://localhost:3000";
		
		String body = "{\r\n"
				+ "        \r\n"
				+ "        \"name\": \"Automationpatched1\"\r\n"
				+ "       \r\n"
				+ "    }";
		
		RestAssured.given().log().all().body(body).header("Content-Type","application/json").when().patch("workers/1005")
		.then().log().all().statusCode(200);
		
		Response rsp = RestAssured.get("http://localhost:3000/workers/1001");
		
		System.out.println(rsp.asString());
	}
	
//PUT
	
	@Test(enabled = false)
    public void PUT() {
        // For PUT request , we always have to provide the ID fr which you wants to do
        // the changes
        String Body = "{\r\n"
        		+ "        \r\n"
        		+ "        \"name\": \"Autoamtion\",\r\n"
        		+ "        \"age\": 24\r\n"
        		+ "    }";
        // First fetch the response in the response container
        Response resp = RestAssured.given().header("Content-Type", "application/json").body(Body).when()
                .put("http://localhost:3000/workers/1005");
        // Extract the response code and response message and print it
        System.out.println("********************************************************************");
        System.out.println("The status Code is :" + resp.statusCode());
        System.out.println("The status response line s :" + resp.statusLine());
        System.out.println("********************************************************************");
        Response resp2 = RestAssured.get("http://localhost:3000/workers/1005");
        System.out.println(resp2.asString());
    }
	
	// Data provider method
    @DataProvider(name = "DP1")
    public Object[][] dataProviderMethod() {
        return new Object[][] { { "1005" }, { "1006" }, { "1007" } };
    }
    
    
 // Passing the ID from dataProvider table using TestNG
    @Test(enabled = false, dataProvider = "DP1")
    public void PATCHUsingDataProvider(String ID) {
        // For PUT request , we always have to provide the ID for which you wants to do
        // the changes
        String Body = "{\r\n"
        		+ "\"name\": \"Automationpatched"+ID+"\"\r\n"
        		+ "}";
        // First fetch the response in the response container
        Response resp = RestAssured.given().header("Content-Type", "application/json").body(Body).when()
                .patch("http://localhost:3000/workers/" + ID);
        // Extract the response code and response message and print it
        System.out.println("********************************************************************");
        System.out.println("The status Code is :" + resp.statusCode());
        System.out.println("The status response line s :" + resp.statusLine());
        System.out.println("********************************************************************");
		
		Response resp2 = RestAssured.get("http://localhost:3000/workers/" + ID);
        System.out.println(resp2.asString());
    }
    
    //hashmapread  
    @Test(enabled = false)
    public void POSTwithHashMap() {
        HashMap<String, String> hm = new HashMap<String, String>();
        hm.put("name", "Automationtest12");
        hm.put("age", "27");
        hm.put("id", "14");
        RestAssured.baseURI = "http://localhost:3000";
        RestAssured.given().log().all().body(hm)
                // Giving header details are important
                .header("Content-Type", "application/json").when().post("/workers");
        Response resp2 = RestAssured.get("http://localhost:3000/workers/14");
        System.out.println(resp2.asString());
    }
    
    //thru Jason file
    
    @Test(enabled = false)
    public void POSTwithjsonfile() {
       
        RestAssured.baseURI = "http://localhost:3000";
        RestAssured.given().log().all().body(new File("./data1.json"))
                // Giving header details are important
                .header("Content-Type", "application/json").when().post("/workers").then().log().all();
        Response resp2 = RestAssured.get("http://localhost:3000/workers/17");
        System.out.println(resp2.asString());
    }
    
   // Serilization
    
    @Test(enabled = false)
    public void POSTPOJOCheck() throws JsonProcessingException {
        RestAssured.baseURI = "http://localhost:3000";
        
        // Reading the data from POJO Class for the payload details
        
        WorkerPOJO objPojo = new WorkerPOJO();
        objPojo.setName("VIP3pojotest");
        objPojo.setAge(30);
       // objPojo.setId(19);
                
        // Object Mapper
        
       // ObjectMapper mapper = new ObjectMapper();
       // String jsonBodyData = mapper.writeValueAsString(objPojo);
        
        RestAssured.given().log().all().body(objPojo)
                // Giving header details are important
                .header("Content-Type", "application/json").when().post("/workers");
        Response resp2 = RestAssured.get("http://localhost:3000/workers/0");
        System.out.println(resp2.asString());
        
     // Deserilization code
    	
    	
       /* WorkerPOJO objPojo2 =RestAssured.given().get("http://localhost:3000/workers")
                     .as(WorkerPOJO.class);
             
        WorkerPOJO.ToString(objPojo2); */
    }
    
    // jasonPath
    
    @Test(enabled = true)
    public void Jasonpathtest() {
       
        RestAssured.baseURI = "http://localhost:3000";
        
        String resp2 = RestAssured.get("http://localhost:3000/workers").asString();
        System.out.println(resp2);
        
        JsonPath js = new JsonPath(resp2);
        
        System.out.println(js.getList("id"));
        
        System.out.println(js.getString("age[6]"));
        
        
    }
 
}
