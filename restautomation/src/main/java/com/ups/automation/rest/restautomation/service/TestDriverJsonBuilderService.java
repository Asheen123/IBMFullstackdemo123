package com.ups.automation.rest.restautomation.service;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ups.automation.rest.restautomation.bean.APISheetBean;
import com.ups.automation.rest.restautomation.bean.GlobalSheetBean;

public class TestDriverJsonBuilderService {

  private static Map<String,String> operationToApiMap = new HashMap<>();
  private static Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
  private static String jsonPath = "D:\\TestDriverJson\\";
  
  static {
	  operationToApiMap.put("retrieveAccessPointsByAddress","AccessPointSearchByAddress");
	  operationToApiMap.put("retrieveAccessPointAvailabilities","AccessPointAvailability");
	  operationToApiMap.put("retrieveAccesspointDetailsByGeocode","AccessPointSearch");
	  operationToApiMap.put("retrieveDrivingDirections","DrivingDirections");
	  operationToApiMap.put("retrieveGeocodeUsingAddress","Geocode");
	  operationToApiMap.put("retrieveRetailLocations","RetailLocationType");
	  
	  operationToApiMap.put("retrieveAvailablePrograms", "ProgramType");
	  //operationToApiMap.put("", "FindLocationDetail"); //No Client IMpl
	  operationToApiMap.put("retrieveAvailablePrograms", "ProgramType");
  }
  
  public void createJsonForTestDriver(Map<String,Object> excelApiData) {
	  
	 try {
		 List<GlobalSheetBean> globalSheetBeanList = (List<GlobalSheetBean>)excelApiData.get("globalSheetList");
		 for(GlobalSheetBean globalSheetBean : globalSheetBeanList) {
			 if(globalSheetBean!=null && globalSheetBean.getOperationName()!=null && !globalSheetBean.getOperationName().isEmpty()) {
				createTestJson(globalSheetBean);
			 }
		 }
	 } catch (Exception ex) {
		 ex.printStackTrace();
	 }
  }
  
  private void createTestJson( GlobalSheetBean globalSheetBean) throws Exception {
	  String testCaseFileName = globalSheetBean.getOperationName().concat("_TestCase");
	 
	  JsonObject sclTestJsonObject = new JsonObject();
	  JsonArray scltestsJsonArray = new JsonArray();
	  
	  List<APISheetBean> apiSheetBeanList = globalSheetBean.getApiSheetList();
	  for(APISheetBean apiSheetBean : apiSheetBeanList) {
		 if(apiSheetBean.getTcid()!=null && !apiSheetBean.getTcid().isEmpty()) {
			 JsonObject sclTCJsonObject = new JsonObject(); 
			 sclTCJsonObject.addProperty("api", globalSheetBean.getOperationName());
			 sclTCJsonObject.addProperty("test", apiSheetBean.getTcid());
			 sclTCJsonObject.addProperty("scenario", apiSheetBean.getTcName());
			 sclTCJsonObject.addProperty("status", "200");
			 sclTCJsonObject.addProperty("parameters", createParamToValueString(apiSheetBean));
			 
			 scltestsJsonArray.add(sclTCJsonObject);
		 }
	  }
	  if(scltestsJsonArray.size() > 0) {
		  sclTestJsonObject.add("scltests", scltestsJsonArray);
		  String tcFileName  = testCaseFileName.concat(".json");
				  
		  try(FileWriter out = new FileWriter(tcFileName)) {
			 String jsonOutput = gson.toJson(sclTestJsonObject);	
			 out.write(jsonOutput);
		  } catch (Exception ex) {
			ex.printStackTrace();
		  }
	  }
  }
  
  private String createParamToValueString(APISheetBean apiSheetBean) throws Exception{
	  StringBuilder parameters = new StringBuilder();

	  Map<String,Object> headerParams = apiSheetBean.getHeaderParam();
	  if(headerParams!=null) {
	   Set<String> headerParamsKeySet = headerParams.keySet();
	   for(String key : headerParamsKeySet) {
		   if(headerParams.get(key)!=null) {
			  String keyValuePair = key.concat("=").concat(String.valueOf(headerParams.get(key)));
			  parameters.append(keyValuePair);
			  parameters.append("&");
		   }
	   }
	  }
	  
	  Map<String,Object> pathParams = apiSheetBean.getPathParam();
	  if(pathParams!=null) {
	   Set<String> pathParamsKeySet = pathParams.keySet();
		   for(String key : pathParamsKeySet) {
			   if(pathParams.get(key)!=null) {
				  String keyValuePair = key.concat("=").concat(String.valueOf(pathParams.get(key)));
				  parameters.append(keyValuePair);
				  parameters.append("&");
		       }
	     }
	  }
	  
	  
	  Map<String,Object> queryParams = apiSheetBean.getQueryParam();
	  if(queryParams!=null) {
		  Set<String> queryParamsKeySet = queryParams.keySet();
		  for(String key : queryParamsKeySet) {
			  if(queryParams.get(key)!=null) {
				  String keyValuePair = key.concat("=").concat(String.valueOf(queryParams.get(key)));
				  parameters.append(keyValuePair);
				  parameters.append("&");
			  }
		  }
	  }
	  
	  Map<String,Object> reqParams = apiSheetBean.getRequestBodyParam();
	  if(reqParams!=null) {
		  Set<String> reqParamsKeySet = reqParams.keySet();
		  for(String key : reqParamsKeySet) {
			  if(reqParams.get(key)!=null) {
				  String keyValuePair = key.concat("=").concat(String.valueOf(reqParams.get(key)));
				  parameters.append(keyValuePair);
				  parameters.append("&");
			  }
		  }
	  }
	  
	  String parametersStringValue = parameters.toString();
	  System.out.println(parametersStringValue);
	  if(parametersStringValue!=null && parametersStringValue.length() > 0 && parametersStringValue.lastIndexOf("&") == (parametersStringValue.length()-1)) {
		  parametersStringValue = parametersStringValue.substring(0, (parametersStringValue.length()-1));
	  }
	  
	  System.out.println(parametersStringValue);
	  return parametersStringValue;
  }
}
