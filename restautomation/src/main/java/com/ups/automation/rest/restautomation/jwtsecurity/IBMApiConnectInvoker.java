package com.ups.automation.rest.restautomation.jwtsecurity;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class IBMApiConnectInvoker {
	
	private static Logger log = LoggerFactory.getLogger(IBMApiConnectInvoker.class);
	private static Properties prop = new Properties();
	private static String APIC_SERVER_URL = "APIC_SERVER_URL";
	private static String CLIENT_ID = "x-ibm-client-id";
	private static String CLIENT_SECRET = "x-ibm-client-secret";
	private static String AUD_CLAIM= "aud-claim";
	
	public static String getJWTTokenFromAPIC(String clientId, String clientSecret, String audClaim, String jwtUrl) throws Exception {
		String jwtToken = null;
		/*try {
			InputStream input = null;
			String filename = ".//application.properties";
			input = IBMApiConnectInvoker.class.getClassLoader().getResourceAsStream(filename);
			if (input == null) {
				log.info("Sorry, unable to find " + filename);
			} else {
				prop.load(input);
			}
			input.close();
		} catch (IOException e1) {
			log.error("IOException occured at>>> ", e1);
		}*/
		
		String apicUrl = "https://apigwytest.ams1907.com/ups-ent/ests-ift/V1/jwtapi/token";
		if(jwtUrl!=null && !jwtUrl.isEmpty()) {
			apicUrl = jwtUrl;	
		}else {
		    //apicUrl  = prop.getProperty(APIC_SERVER_URL);
		}
	   /* int indexOf1stColon = apicUrl.indexOf(":");
		int indexOf2ndColon = apicUrl.indexOf(":", (indexOf1stColon+1));
		if(indexOf2ndColon == -1) {
			int indexOf3rdSlash = apicUrl.indexOf("/", (indexOf1stColon+3));
			RestAssured.baseURI = apicUrl.substring(0, (indexOf3rdSlash));
			//RestAssured.port = 80;
			RestAssured.basePath = apicUrl.substring(indexOf3rdSlash, apicUrl.length());
			
		}else {
			int indexOf3rdSlash = apicUrl.indexOf("/", indexOf2ndColon);
			RestAssured.baseURI = apicUrl.substring(0, (indexOf2ndColon));
			RestAssured.port = Integer.valueOf(apicUrl.substring((indexOf2ndColon+1), indexOf3rdSlash)).intValue();
		    RestAssured.basePath = apicUrl.substring(indexOf3rdSlash, apicUrl.length());
		}*/
	    
	    Map<String,String> headerParamMap = new HashMap<>();
	    
	    headerParamMap.put(CLIENT_ID, clientId);
	    headerParamMap.put(CLIENT_SECRET, clientSecret);
	    headerParamMap.put(AUD_CLAIM, audClaim);
	    
	    RequestSpecification requestSpecification = given();
	    requestSpecification.headers(headerParamMap);
	    
	    Response response = requestSpecification.when().get(apicUrl);       
		
		if (response != null) {
			if (response.statusCode() == 200) {
				jwtToken = response.body().asString();
				jwtToken = "Bearer " + jwtToken;
			} else {
				//throw new Exception(response.readEntity(String.class));
			}
		}
		log.info("JWT Token :: {} ::", jwtToken);
		return jwtToken;
	}
}
