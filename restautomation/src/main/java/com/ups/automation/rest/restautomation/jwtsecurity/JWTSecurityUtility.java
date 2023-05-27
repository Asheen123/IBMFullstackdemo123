package com.ups.automation.rest.restautomation.jwtsecurity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JWTSecurityUtility {
  
	private static Logger log = LoggerFactory.getLogger(JWTSecurityUtility.class);
	private static String apicDatafilename = "jwt-token.ser";
	private static long min_30 = 1000 * 60 * 25;
	private static Map<String, JwtSecurityBean> newJwtTokenDetailsMap = new HashMap<>();
	
	public static synchronized String getJwtToken(String clientId, String clientSecret, String audClaim, String jwtUrl) {
		String jwtToken = null;
		try {
			
			if(newJwtTokenDetailsMap!=null && newJwtTokenDetailsMap.isEmpty()) {
				try(FileInputStream fileIn = new FileInputStream(apicDatafilename)){
					if(fileIn.available() > 0) {
						ObjectInputStream in = new ObjectInputStream(fileIn);
						newJwtTokenDetailsMap = (HashMap<String,JwtSecurityBean>) in.readObject();
					}
				}catch(FileNotFoundException ex) {
					log.error(ex.getMessage());
				} catch(IOException ex) {
					log.error(ex.getMessage());
				}
			}

            if(newJwtTokenDetailsMap != null && !newJwtTokenDetailsMap.isEmpty()) {
            	JwtSecurityBean jwtSecurityBean = (JwtSecurityBean)newJwtTokenDetailsMap.get(audClaim);
            	if(jwtSecurityBean!=null && jwtSecurityBean.getJwtToken()!=null && !jwtSecurityBean.getJwtToken().isEmpty()) {
            	Long currentTime = System.currentTimeMillis();
            	Long tokenRetrievedTime = jwtSecurityBean.getTokenRetrievedTime();
	            	if((currentTime - tokenRetrievedTime) > min_30){
	            		jwtToken = getAndSaveJWTToken(clientId, clientSecret, audClaim, jwtUrl);
	            	} else {
	            		jwtToken = jwtSecurityBean.getJwtToken();
	            	}
            	} else {
            		jwtToken = getAndSaveJWTToken(clientId, clientSecret, audClaim, jwtUrl);
            	}
            } else {
            	jwtToken = getAndSaveJWTToken(clientId, clientSecret, audClaim, jwtUrl);
            }
            
		} catch(FileNotFoundException ex) {
			log.error(ex.getMessage());
		} catch(IOException ex) {
			log.error(ex.getMessage());
		} catch(ClassNotFoundException ex){
			log.error(ex.getMessage());
		} catch(Exception ex) {
			log.error(ex.getMessage());
		}
		return jwtToken;
	}
	
	private static String getAndSaveJWTToken(String clientId, String clientSecret, String audClaim,String jwtUrl) throws Exception {
		
		log.info("Triggering APIC to get new JWT-TOKEN");
		String jwtToken = IBMApiConnectInvoker.getJWTTokenFromAPIC(clientId, clientSecret, audClaim, jwtUrl);
		Long tokenRetrievedTime = System.currentTimeMillis();
		JwtSecurityBean jwtSecurityBean = newJwtTokenDetailsMap.get(audClaim);
		if(jwtSecurityBean == null) {
			jwtSecurityBean = new JwtSecurityBean();
		}
		
		if(jwtToken!=null) {
			jwtSecurityBean.setTokenRetrievedTime(tokenRetrievedTime);
			jwtSecurityBean.setJwtToken(jwtToken);
			newJwtTokenDetailsMap.put(audClaim, jwtSecurityBean);
		}
		
		FileOutputStream fileOut = new FileOutputStream(apicDatafilename);
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(newJwtTokenDetailsMap);
		out.flush();
		out.close();
		fileOut.close();
		
		return jwtToken;
	}
}
