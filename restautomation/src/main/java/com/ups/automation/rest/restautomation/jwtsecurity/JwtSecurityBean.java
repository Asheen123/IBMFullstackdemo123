package com.ups.automation.rest.restautomation.jwtsecurity;

import java.io.Serializable;

public class JwtSecurityBean implements Serializable{
	
	private Long tokenRetrievedTime;
	private String jwtToken;
	
	public Long getTokenRetrievedTime() {
		return tokenRetrievedTime;
	}
	public void setTokenRetrievedTime(Long tokenRetrievedTime) {
		this.tokenRetrievedTime = tokenRetrievedTime;
	}
	public String getJwtToken() {
		return jwtToken;
	}
	public void setJwtToken(String jwtToken) {
		this.jwtToken = jwtToken;
	}	
}
