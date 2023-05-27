package com.ups.automation.rest.restautomation.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.ups.automation.rest.restautomation.bean.APISheetBean;
import com.ups.automation.rest.restautomation.bean.GlobalSheetBean;

import io.restassured.response.Response;
import io.restassured.response.ResponseBody;

public class RestAPIResponseValidator {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestAPIResponseValidator.class);
	static JsonObject defaultjsonObject = new JsonObject();
	private static ResourceBundle configProp = ResourceBundle.getBundle("application");
	private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private static JsonParser jsonParser = new JsonParser();
	// static Map<String, String> varElementsMap = new HashMap<>();

	@SuppressWarnings({ "resource"})
	public static void validateAPIResponse(APISheetBean apiSheet, GlobalSheetBean globalsheet,
			Map<String, String> varElementsMap, String produces,Map<String, String> varHeadersMap) {
		try {
			String elements = apiSheet.getElements();
			String expected = apiSheet.getExpected();
			Response response = apiSheet.getApiResponse();
			int responseCode = response.getStatusCode();
			String status = apiSheet.getStatus();
			// Added for chaining
			String TCID = apiSheet.getTcid();
			String varElements = apiSheet.getVarElements();
			String varHeaders = apiSheet.getVarHeaders();
			// end

			Map<String, String> elementsMap = new HashMap<>();
			Boolean testStatus = true;
			StringBuilder actualValueSB = new StringBuilder();
			StringBuilder statusDetailSB = new StringBuilder();
			StringBuilder expectedValueSB = new StringBuilder(); // BHanu

			boolean compareFlag = false;
			boolean fileFlag = false;
			if (null != globalsheet.getCompareExistingTestResult()
					&& globalsheet.getCompareExistingTestResult().equals("Y") && null != apiSheet.getCompare()
					&& apiSheet.getCompare().equalsIgnoreCase("Y")
					&& null != globalsheet.getExistingTestResultFolder()) {
				try {
					if(null != apiSheet.getCompareType() && apiSheet.getCompareType().equalsIgnoreCase("TEXT")){
						if (globalsheet.getCompareExistingTestResult().equals("Y")) {
							File tmpDir = null;
							HashMap<String, Object> baseMap = new HashMap<String, Object>();
	    		        	HashMap<String, Object> targetMap = new HashMap<String, Object>();		    		        	
	    		        	HashMap<String, Object> diffMap = new HashMap<String, Object>();
	    					Map<String, Map<String, Object>> mapDiff = new HashMap<String, Map<String, Object>>();
		    		            String sCurrentLine;
		    		            int lineNumBase = 1;
		    		            int lineNumTarget = 1;
		    		            List<String> list1 = new ArrayList<String>();
		    		            List<String> list2 = new ArrayList<String>();
		    		            BufferedReader br1 =null;		    		            
		    		            BufferedReader br2=null;
                        if(globalsheet.getProduces().equalsIgnoreCase("plain/text")){
                        	tmpDir=new File(globalsheet.getExistingTestResultFolder() + apiSheet.getTcid()
							+ "_Response_Object.json");
					boolean exists = tmpDir.exists();
					if (exists) {
						String responseObj = apiSheet.getApiResponse().getBody().asString();
						if (responseObj != null && !responseObj.isEmpty()) {
							try (FileWriter out = new FileWriter(globalsheet.getExistingTestResultFolder() + apiSheet.getTcid()
							+ "_Response_Object_temp.txt")) {
								if (responseObj != null && !responseObj.isEmpty()) {
								 out.write(responseObj);
								}
								 br1 = new BufferedReader(new FileReader(globalsheet.getExistingTestResultFolder() + apiSheet.getTcid()
									+ "_Response_Object.txt"));
                                 br2 = new BufferedReader(new FileReader(globalsheet.getExistingTestResultFolder() + apiSheet.getTcid()
									+ "_Response_Object_temp.txt"));
							}catch (Exception ex) {
								 compareFlag = true;
									LOGGER.error("Exception during writing response object :: {}", ex.getMessage());
								}
							}	
					}else {
						compareFlag = true;
						fileFlag = true;
					}
							}else{
							tmpDir=new File(globalsheet.getExistingTestResultFolder() + apiSheet.getTcid()
									+ "_Response_Object.json");
							boolean exists = tmpDir.exists();
							if (exists) {
								String responseObj = apiSheet.getApiResponse().getBody().asString();
								if (responseObj != null && !responseObj.isEmpty()) {
									try (FileWriter out = new FileWriter(globalsheet.getExistingTestResultFolder() + apiSheet.getTcid()
									+ "_Response_Object_temp.json")) {
								 String[] relaxChar=configProp.getString("SpecialCharExcp").split(",");	    		 		 
			    		 		 boolean specChar=false;	    		 		 
			    		 		for(String s:relaxChar){
			    		 			if(responseObj.contains(s))
			    		 				specChar=true;
			    		 		}
			    		 		 if(specChar)
			    		 			 out.write(responseObj);
								else {
									JsonElement responseJson = jsonParser.parse(responseObj);
									String jsonOutput = gson.toJson(responseJson);
									 out.write(jsonOutput);
								}
									 } catch (Exception ex) {
										 compareFlag = true;
											LOGGER.error("Exception during writing response object :: {}", ex.getMessage());
										}
									}							
			    		             br1 = new BufferedReader(new FileReader(globalsheet.getExistingTestResultFolder() + apiSheet.getTcid()
			    														+ "_Response_Object.json"));
			    		             br2 = new BufferedReader(new FileReader(globalsheet.getExistingTestResultFolder() + apiSheet.getTcid()
			    														+ "_Response_Object_temp.json"));
							}else {
								compareFlag = true;
								fileFlag = true;
							}
			    		            while ((sCurrentLine = br1.readLine()) != null) {			    		            	
			    		            	if(null!=sCurrentLine.trim() && !sCurrentLine.trim().equals("")){
			    		                list1.add(lineNumBase+">>:"+sCurrentLine.trim());
			    		            	}
			    		                lineNumBase=lineNumBase+1;
			    		            }	
			    		            while ((sCurrentLine = br2.readLine()) != null) {
			    		            	if(null!=sCurrentLine.trim() && !sCurrentLine.trim().equals("")){
			    		                list2.add(lineNumTarget+">>:"+sCurrentLine.trim());
			    		            	}
			    		                lineNumTarget=lineNumTarget+1;
			    		            }
			    		            List<String> tmpList = new ArrayList<String>(list1);
			    		            tmpList.removeAll(list2);
			    		            List<String> tmpList2 = new ArrayList<String>(list2);
			    		            tmpList2.removeAll(list1);
			    		            System.out.println("File Differences..Count::"+tmpList.size());
			    		            for(int i=0;i<tmpList.size();i++){
			    		            	String[] val=tmpList.get(i).split(">>:");
			    		            	String[] val2=tmpList2.get(i).split(">>:");
			    		            	if(!val[1].equals("") && !val2[1].equals(""))
			    		            	diffMap.put("LineNo:"+val[0], val2[1]+">>:"+val[1]);
			    		            }	
			    		            mapDiff.put("EnteriesOnTarget",targetMap);
									mapDiff.put("EnteriesOnBase", baseMap);
									mapDiff.put("EnteriesJSONDiff", diffMap);
									apiSheet.setJsonDifferences(mapDiff);
									if (null != mapDiff) {
										if (!(mapDiff.get("EnteriesOnTarget").size() < 1
												&& mapDiff.get("EnteriesOnBase").size() < 1
												&& mapDiff.get("EnteriesJSONDiff").size() < 1)) {
											compareFlag = true;
										}
									}
			    		 		File file = new File(globalsheet.getExistingTestResultFolder() + apiSheet.getTcid()
								+ "_Response_Object_temp.json"); 
			    		        file.deleteOnExit();		
							} 
						}
								
					}else{
					JsonObject baseJson = null;
					// Response responseBaseEnv =
					// apiSheet.getApiResponseBaseEnv();
					String removeElements = apiSheet.getRemElementJSONDiff();
					MapDifference<String, Object> md = null;
					Map<String, Map<String, Object>> mapDiff = new HashMap<String, Map<String, Object>>();
					Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

					if (globalsheet.getCompareExistingTestResult().equals("Y")) {
						File tmpDir = new File(globalsheet.getExistingTestResultFolder() + apiSheet.getTcid()
								+ "_Response_Object.json");
						boolean exists = tmpDir.exists();
						if (exists) {
							BufferedReader bsReader = new BufferedReader(
									new FileReader(globalsheet.getExistingTestResultFolder() + apiSheet.getTcid()
											+ "_Response_Object.json"));
							baseJson = new Gson().fromJson(bsReader, JsonObject.class);

							// else {
							// baseJson =
							// gson.fromJson(responseBaseEnv.asString(),
							// JsonObject.class);
							// }

							JsonObject targetJson = gson.fromJson(response.asString(), JsonObject.class);
							Type type = new TypeToken<Map<String, Object>>() {
							}.getType();
							// Bagya - JSON Array Comparison - starts
							Gson g = new Gson();
							Map<String, Object> targetMap = g.fromJson(targetJson, type);
							Map<String, Object> BaseMap = g.fromJson(baseJson, type);
							MapDifference<String, Object> difference = Maps.difference(targetMap, BaseMap);
							//// Bagya - JSON Array Comparison - ends
							md = Maps.difference(FlatMapUtil.flatten(gson.fromJson(targetJson, type)),
									FlatMapUtil.flatten(gson.fromJson(baseJson, type)));
							mapDiff.put("EnteriesOnTarget", removeElementsMapDiff("Target", removeElements, md));
							mapDiff.put("EnteriesOnBase", removeElementsMapDiff("Base", removeElements, md));
							mapDiff.put("EnteriesJSONDiff", removeElementsMapDiff("Diff", removeElements, md));
							// Bagya - JSON Array Comparison - starts
//							mapDiff.put("EnteriesOnBase", arrayComparison("Base", difference, md));
//							mapDiff.put("EnteriesOnTarget", arrayComparison("Target", difference, md));
							// Bagya - JSON Array Comparison - ends
							apiSheet.setJsonDifferences(mapDiff);
							if (null != mapDiff) {
								if (!(mapDiff.get("EnteriesOnTarget").size() < 1
										&& mapDiff.get("EnteriesOnBase").size() < 1
										&& mapDiff.get("EnteriesJSONDiff").size() < 1)) {
									compareFlag = true;
								}
							}
						} else {
							compareFlag = true;
							fileFlag = true;
						}
					}
					}
				} catch (Exception ex) {
					compareFlag = true;
					LOGGER.info("Not able to compare JSON from both environments  ", ex);
				}

			}
			if (Constants.STATUS_SUCCESS.equals(status)) {
				String[] elementsArr = null;
				// String[] tempArr = null;
				String delim = "";
				if ((!produces.isEmpty() && produces != null)
						&& (produces.equalsIgnoreCase("text/plain") || produces.equalsIgnoreCase("text/html"))
						&& (null != expected)) {
					String[] expectedArr = expected.split(";");
					String actualValue = response.getBody().prettyPrint();
					if (expectedArr.length > 0) {
						if (null != elements && !elements.isEmpty()) {
							elementsArr = elements.split(";");
							if (expectedArr.length > 1) {
								delim = "; ";
							}
							int elementCount = 0;
							for (int i = 0; i < elementsArr.length; i++) {
								String actualVal = "";
								if (elementsArr[i] != null && !elementsArr[i].isEmpty()) {
									String element = elementsArr[i];
									if (element.equalsIgnoreCase("HTTPCODE")) {
										elementCount=elementCount+1;
										actualVal = Integer.toString(responseCode);

										String expectedValue = expectedArr[i].trim();
										if (!actualVal.equals(null) && !actualVal.equals("")
												&& !expectedValue.equals(null) && !expectedValue.equals("")) {
											if (actualVal.equals(expectedValue)) {
												statusDetailSB.append(
														"Test For ".concat(element).concat(" Passed").concat(delim));
											} else {
												testStatus = false;
												statusDetailSB.append(
														"Test For ".concat(element).concat(" Failed").concat(delim));
											}
										} else {
											testStatus = false;
											statusDetailSB.append(
													"Test For ".concat(element).concat(" Failed").concat(delim));
										}
									} else if (element.equalsIgnoreCase(Constants.EMPTYRESPONSE)) {
										String expectedValue = expectedArr[i].trim();
										elementCount=elementCount+1;
										boolean responseKey = false;
										if (null != actualValue && !actualValue.equals(""))
											actualVal = "no";
										else {
											responseKey = true;
											actualVal = "yes";
										}
										if (element.equalsIgnoreCase(Constants.EMPTYRESPONSE)
												&& expectedValue.equalsIgnoreCase("Yes") && responseKey) {
											statusDetailSB.append(
													"Test For ".concat(element).concat(" Passed").concat(delim));
										} else {
											if (element.equalsIgnoreCase(Constants.EMPTYRESPONSE)
													&& expectedValue.equalsIgnoreCase("No") && !responseKey) {
												statusDetailSB.append(
														"Test For ".concat(element).concat(" Passed").concat(delim));
											}else{
											testStatus = false;
											statusDetailSB.append(
													"Test For ".concat(element).concat(" Failed").concat(delim));
											}
										}
										// ends
									}
								}
								if (actualVal != null && !actualVal.isEmpty()) {
									actualValueSB.append(actualVal.concat(delim));
								}
							}
							for (int i = elementCount; i < expectedArr.length; i++) {
								if (expectedArr.length > 1) {
									delim = "; ";
								}
								String expectedValue = expectedArr[i].trim();
								if (!actualValue.equals(null) && !actualValue.equals("") && !expectedValue.equals(null)
										&& !expectedValue.equals("")) {
									if (actualValue.contains(expectedValue)) {
										statusDetailSB.append(
												"Test For ".concat(expectedValue).concat(" Passed").concat(delim));
									} else {
										testStatus = false;
										statusDetailSB.append(
												"Test For ".concat(expectedValue).concat(" Failed").concat(delim));
									}
								} else {
									testStatus = false;
									statusDetailSB
											.append("Test For ".concat(expectedValue).concat(" Failed").concat(delim));
								}
								if (actualValue != null && !actualValue.isEmpty()) {
									actualValueSB.append(actualValue.concat(delim));
								} else {
									if (null == actualValue || "null" == actualValue || actualValue.equals("")) {
										actualValueSB.append("null");
										actualValue = "null";
									}
								}
							}
						} else {
							for (int i = 0; i < expectedArr.length; i++) {
								if (expectedArr.length > 1) {
									delim = "; ";
								}
								String expectedValue = expectedArr[i].trim();
								if (!actualValue.equals(null) && !actualValue.equals("") && !expectedValue.equals(null)
										&& !expectedValue.equals("")) {
									if (actualValue.contains(expectedValue)) {
										statusDetailSB.append(
												"Test For ".concat(expectedValue).concat(" Passed").concat(delim));
									} else {
										testStatus = false;
										statusDetailSB.append(
												"Test For ".concat(expectedValue).concat(" Failed").concat(delim));
									}
								} else {
									testStatus = false;
									statusDetailSB
											.append("Test For ".concat(expectedValue).concat(" Failed").concat(delim));
								}
								if (actualValue != null && !actualValue.isEmpty()) {
									actualValueSB.append(actualValue.concat(delim));
								} else {
									if (null == actualValue || "null" == actualValue || actualValue.equals("")) {
										actualValueSB.append("null");
										actualValue = "null";
									}
								}
							}
						}
					} else {
						testStatus = false;
						statusDetailSB.append("Expected Data missing");
					}

					if (expected != null && !expected.isEmpty()) {
						expectedValueSB.append((expected));
					}
				} /*else if ((!apiSheet.getSendType().equals("GET") && apiSheet.getApiRequest() != null)
						&& apiSheet.getApiRequest().get("arrayKey") != null && !apiSheet.getApiRequest().get("arrayKey").getAsJsonArray().get(0).isJsonObject()) {
					String actualValue = response.getBody().prettyPrint();
					String[] expectedArr = expected.split(";");
					if (expectedArr.length > 0) {
						for (int i = 0; i < expectedArr.length; i++) {

							if (expectedArr.length > 1) {
								delim = "; ";
							}
							String expectedValue = expectedArr[i].trim();
							if (!actualValue.equals(null) && !actualValue.equals("") && !expectedValue.equals(null)
									&& !expectedValue.equals("")) {
								if (actualValue.contains(expectedValue)) {
									statusDetailSB
											.append("Test For ".concat(expectedValue).concat(" Passed").concat(delim));
								} else {
									testStatus = false;
									statusDetailSB
											.append("Test For ".concat(expectedValue).concat(" Failed").concat(delim));
								}
							}
						}
					} else {
						testStatus = false;
						statusDetailSB.append("Expected Data missing");
					}
					if (actualValue != null && !actualValue.isEmpty()) {
						actualValueSB.append(actualValue);
					}
					if (expected != null && !expected.isEmpty()) {
						expectedValueSB.append((expected));
					}
				} */else {
					if (elements != null && !elements.isEmpty() && expected != null && !expected.isEmpty()) {
						/*
						 * StringTokenizer elementsSt = new
						 * StringTokenizer(elements,";"); StringTokenizer
						 * expectedSt = new StringTokenizer(expected,";");
						 * 
						 * while(elementsSt.hasMoreTokens() &&
						 * expectedSt.hasMoreTokens()) { String elementName =
						 * elementsSt.nextToken().trim(); String expectedValue =
						 * expectedSt.nextToken().trim();
						 * elementsMap.put(elementName, expectedValue); }
						 */
						elementsArr = elements.split(";");
						String[] expectedArr = expected.split(";");
						for (int i = 0; i < elementsArr.length; i++) {
							String elementName = elementsArr[i].trim();
							if (expectedArr.length > i) {
								String expectedValue = expectedArr[i].trim();
								elementsMap.put(elementName, expectedValue);
							}
						}
					} else {
						testStatus = false;
						statusDetailSB.append("Elements/Expected Data missing");
					}

					// added for chaining
					String[] varElementsArr = null;					
					String[] varHeadersArr = null;
					// String[] temp = null;
					if (varElements != null && !varElements.isEmpty() && response != null) {
						varElementsArr = varElements.split(";");
						// for(int i=0;i<varElementsArr.length;i++){//banu
						// changed i=1 to i=0
						for (String keyname : varElementsArr) {
							try {
								if (keyname != null && !keyname.isEmpty()) {
									// Bagya - Response json object chaining
									// code - starts
									String actualValue1 = response.getBody().jsonPath().getString(keyname);
									if (actualValue1.startsWith("[", 0)) {
										JsonObject jsonObject = new JsonParser().parse(response.asString())
												.getAsJsonObject();
										JsonObject newjsonObject = new JsonObject();
										newjsonObject = jsonObject;
										for (String key : keyname.split("\\.")) {
											newjsonObject = newjsonObject.get(key).getAsJsonObject();
											defaultjsonObject = newjsonObject;
										}
										if (null != defaultjsonObject) {
											varElementsMap.put(TCID + "_" + keyname, defaultjsonObject.toString());
										}
									} else {
										if (actualValue1 != null && !actualValue1.isEmpty()) {
											System.out.println(TCID + "_" + keyname);
											varElementsMap.put(TCID + "_" + keyname, actualValue1);
										}
									}
									// Bagya - Response json object chaining
									// code - ends
								}
							} catch (Exception e) {
								System.out.println("Error reading Var Elements --  " + e);
							}
						}
					}
						// Bagya - chaining with response headers - starts				
					if(varHeaders != null && !varHeaders.isEmpty() && response != null){
						varHeadersArr = varHeaders.split(";");
						for (String keyname : varHeadersArr) {
						try {
							if (keyname != null && !keyname.isEmpty()) {
								String actualValue1 = response.getHeader(keyname);						
									if (actualValue1 != null && !actualValue1.isEmpty()) {
										System.out.println(TCID + "_" + keyname);
										varHeadersMap.put(TCID + "_" + keyname, actualValue1);
									}
							}
						} catch (Exception e) {
							System.out.println("Error reading Var Headers --  " + e);
						}
					}
						//Bagya - chaining with response headers - ends
						// }
					}
					// end
					if (!elementsMap.isEmpty() && response != null && !response.getBody().asString().isEmpty()) {
						// Set<String> keySet = elementsMap.keySet();
						if (elementsArr.length > 1) {
							delim = "; ";
						}
						for (String keyName : elementsArr) {
							try {
								if (keyName != null && !keyName.isEmpty()) {
									String actualValue = "";
									if(!keyName.equalsIgnoreCase("HTTPCODE") && !keyName.equalsIgnoreCase("EMPTYRESPONSE")  && !keyName.equalsIgnoreCase("$"))
									//	System.out.println("in.."+response.getBody().asString());
							//		List<String> jsonResponse = response.jsonPath().getList("$");
									
									
									actualValue=response.getBody().jsonPath().getString(keyName);
									//System.out.println(actualValue);
									if(keyName.equalsIgnoreCase("$")){
										// if(response.getBody().asString().startsWith("\"") && response.getBody().asString().endsWith("\""))
										//     actualValue=response.getBody().asString().substring(1, response.getBody().asString().length()-1);	
										// else
											actualValue=response.getBody().asString();
									}
									String expectedValue = elementsMap.get(keyName);
//									if(expectedValue.contains("has")){
//										List<String> values =response.jsonPath()
//											    .getList("keys.use");
//			//System.out.println(values.contains("sig"));
//									}else{
//										
//									}
									if (expectedValue.indexOf("VE_") != -1) {// banu
																				// -
																				// VR
																				// Elements
																				// in
																				// Expect
																				// element
																				// column
										String expecteddyna = null;
										expecteddyna = expectedValue.split("VE_")[1];
										if (varElementsMap.containsKey(expecteddyna)) {
											// System.out.println(varElementsMap.get(expecteddyna));
											expectedValue = varElementsMap.get(expecteddyna).toString();
										}
									}
									if (expectedValue != null && !expectedValue.isEmpty()) {
										expectedValueSB.append(expectedValue.concat(delim));
									} // Bhanu end
										// Bagya - Response key present -starts
									if (expectedValue.equalsIgnoreCase(Constants.KEYPRESENT)) {// bagya
																								// -
																								// isPresent
																								// Check
										if (!actualValue.equals(null) && !actualValue.equals("")) {
											actualValue = "[value is present]";
										} else {
											actualValue = "[value is not present]";
										}
									}
									if (expectedValue.equalsIgnoreCase(Constants.KEYNOTPRESENT)) {// bagya
																									// -
																									// isNotPresent
																									// Check
										if (actualValue.equals(null) || actualValue.equals("")) {
											actualValue = "[value is not present]";
										} else {
											actualValue = "[value is present]";
										}
									}
									// ends
									// Bagya - Response present -starts
									if (keyName.equalsIgnoreCase(Constants.EMPTYRESPONSE)) {
										actualValue = "no";
									}
									if (keyName.equalsIgnoreCase(Constants.EMPTYJSONARRAY)) {
                                       if(response.getBody().asString().equals("[]"))
                                    	   actualValue = "yes";
                                       else
                                    	   actualValue = "no";
									}
									if (keyName.equalsIgnoreCase(Constants.EMPTYJSONOBJECT)) {
										  if(response.getBody().asString().equals("{}"))
	                                    	   actualValue = "yes";
	                                       else
	                                    	   actualValue = "no";
									}
									// ends
									// HTTP STATUS CODE - starts
									if (keyName.equalsIgnoreCase("HTTPCODE")) {
										actualValue = Integer.toString(responseCode);
									}
									// ends
									boolean stringFlag = false;
									// Bagya - String Operations -starts
									if (actualValue != null && expectedValue != null) {
										if (expectedValue.startsWith("[") && expectedValue.endsWith("]")
												&& expectedValue.contains(":")) {
											String tempExpectedValue = between(expectedValue, ":", "]");
											if (expectedValue.startsWith("[contains:")) {
												if (actualValue.contains(tempExpectedValue))
													stringFlag = true;
											} else if (expectedValue.startsWith("[starts:")) {
												if (actualValue.startsWith(tempExpectedValue))
													stringFlag = true;
											} else if (expectedValue.startsWith("[ends:")) {
												if (actualValue.endsWith(tempExpectedValue))
													stringFlag = true;
											}
										}
									}
									// Bagya - String Operations -ends
									if (actualValue != null && !actualValue.isEmpty()) {
										actualValueSB.append(actualValue.concat(delim));
									} else {
										if (null == actualValue || "null" == actualValue) {
											actualValueSB.append("null;");
											actualValue = "null";
										}
									}
									if (actualValue != null && expectedValue != null
											&& expectedValue.trim().equalsIgnoreCase(actualValue.trim())) {
										statusDetailSB
												.append("Test For ".concat(keyName).concat(" Passed").concat(delim));
									} else {
										if (actualValue != null && expectedValue != null
												&& (expectedValue.equalsIgnoreCase(Constants.KEYPRESENT)
														&& actualValue.trim().equalsIgnoreCase("[value is present]"))
												|| (expectedValue.equalsIgnoreCase(Constants.KEYNOTPRESENT)
														&& actualValue.trim()
																.equalsIgnoreCase("[value is not present]"))) {
											statusDetailSB.append(
													"Test For ".concat(keyName).concat(" Passed").concat(delim));
										} else {
											if ("null" == actualValue
													&& (null == expectedValue || "null" == expectedValue)) {
												statusDetailSB.append(
														"Test For ".concat(keyName).concat(" Passed").concat(delim));
											} else {
												if (expectedValue.startsWith("[") && expectedValue.endsWith("]")
														&& expectedValue.contains(":") && stringFlag) {
													statusDetailSB.append("Test For ".concat(keyName).concat(" Passed")
															.concat(delim));
												} else {
													if (keyName.equalsIgnoreCase(Constants.EMPTYRESPONSE)
															&& expectedValue.equalsIgnoreCase("No")) {
														statusDetailSB.append("Test For ".concat(keyName)
																.concat(" Passed").concat(delim));
													} else {
														testStatus = false;
														statusDetailSB.append("Test For ".concat(keyName)
																.concat(" Failed").concat(delim));
													}
												}
											}
										}
									}
								}
							} catch (Exception ex) {
								System.out.println(ex);
								testStatus = false;
								statusDetailSB.append(keyName.concat(" missing in response").concat(delim));
								LOGGER.info("Missing Element In response for TCID - {} :: ELEMENT NAME :: {}",
										apiSheet.getTcid(), keyName);
							}
						}
						if (null != globalsheet.getCompareExistingTestResult()
								&& globalsheet.getCompareExistingTestResult().equals("Y")
								&& null != apiSheet.getCompare() && apiSheet.getCompare().equalsIgnoreCase("Y")) {
							if (compareFlag) {
								testStatus = false;
								if (fileFlag)
									statusDetailSB.append("Base Response file is missing".concat(delim));
								else
									statusDetailSB.append("API Response Base Comparison Failed".concat(delim));
							} else
								statusDetailSB.append("API Response Base Comparison Passed".concat(delim));
						}
					} else if (null == response || response.getBody().asString().isEmpty()) {
						// Bagya - Response present -starts
						for (String keyName : elementsArr) {
							delim = "; ";
							String expectedValue = elementsMap.get(keyName);
							expectedValueSB.append(expectedValue.concat(delim));
							String actualValue = "";
							
							
							if (keyName.equalsIgnoreCase(Constants.EMPTYRESPONSE)) {
								actualValue = "yes";
								actualValueSB.append(actualValue.concat(delim));
							}
							if (keyName.equalsIgnoreCase(Constants.EMPTYRESPONSE)
									&& expectedValue.equalsIgnoreCase("Yes")) {
								statusDetailSB.append("Test For ".concat(keyName).concat(" Passed").concat(delim));
							}
							else if (keyName.equalsIgnoreCase("HTTPCODE")) {
								actualValue = Integer.toString(responseCode).trim();
								actualValueSB.append(actualValue.concat(delim));

								expectedValue = expectedValue.trim();
								if (!actualValue.equals(null) && !actualValue.equals("")
										&& !expectedValue.equals(null) && !expectedValue.equals("")) {
									if (actualValue.equals(expectedValue)) {
										statusDetailSB.append(
												"Test For ".concat(keyName).concat(" Passed").concat(delim));
									} else {
										testStatus = false;
										statusDetailSB.append(
												"Test For ".concat(keyName).concat(" Failed").concat(delim));
									}
								}
							}else {
								testStatus = false;
								statusDetailSB.append("Test For ".concat(keyName).concat(" Failed").concat(delim));
							}
						}
						// ends
					}
				}
			}
			if (testStatus) {
				apiSheet.setStatus(Constants.STATUS_PASS);
			} else {
				apiSheet.setStatus(Constants.STATUS_FAIL);
			}

			apiSheet.setActual(actualValueSB.toString());
			apiSheet.setStatusDetail(statusDetailSB.toString());
			apiSheet.setExpected(expectedValueSB.toString());// Bhanu
		} catch (Exception ex) {
			LOGGER.error("Exception during response validation for TCID - {} :: {}", apiSheet.getTcid(),
					ex.getMessage());
			ex.printStackTrace();
		}
	}

	// Bagya - JSON Array Comparison - starts

	public static HashMap<String, Object> arrayComparison(String key, MapDifference<String, Object> difference,
			MapDifference<String, Object> flatdifference) {
		HashMap<String, Object> tempMap = new HashMap<String, Object>();
		if (key.equalsIgnoreCase("Base")) {
			for (@SuppressWarnings("rawtypes")
			Entry e : difference.entriesOnlyOnRight().entrySet()) {
				if (e.getValue().toString().contains("[")) {
					tempMap.put(e.getKey().toString() + "[]", e.getValue().toString());
				}
			}
			for (@SuppressWarnings("rawtypes")
			Entry e : difference.entriesDiffering().entrySet()) {
				if (e.getValue() != null) {
					if (e.getValue().toString().contains("([") && !e.getValue().toString().contains("([{")) {
						String removeVal = e.getValue().toString();
						String[] arrayVal = StringUtils.splitPreserveAllTokens(removeVal, "[/]");
						String[] baseArray = Arrays.stream(arrayVal[1].toString().split(",")).map(String::trim)
								.toArray(String[]::new);
						String[] targetArray = Arrays.stream(arrayVal[3].toString().split(",")).map(String::trim)
								.toArray(String[]::new);
						List<String> tmpArray = new ArrayList<String>();
						for (String str : targetArray) {
							if (!Arrays.asList(baseArray).contains(str)) {
								tmpArray.add(str);
								System.out.println("Missing in Base JSON Array:>>:" + e.getKey() + "::" + str);
							}
						}
						if (tmpArray.size() > 0)
							tempMap.put(e.getKey().toString() + "[]", tmpArray);
					}

				}
			}
		} else if (key.equalsIgnoreCase("Target")) {
			for (@SuppressWarnings("rawtypes")
			Entry e : difference.entriesOnlyOnLeft().entrySet()) {
				if (e.getValue().toString().contains("[")) {
					tempMap.put(e.getKey().toString() + "[]", e.getValue().toString());
				}
			}
			for (@SuppressWarnings("rawtypes")
			Entry e : difference.entriesDiffering().entrySet()) {
				if (e.getValue() != null) {
					if (e.getValue().toString().contains("([") && !e.getValue().toString().contains("([{")) {
						String removeVal = e.getValue().toString();
						String[] arrayVal = StringUtils.splitPreserveAllTokens(removeVal, "[/]");
						String[] baseArray = Arrays.stream(arrayVal[1].toString().split(",")).map(String::trim)
								.toArray(String[]::new);
						String[] targetArray = Arrays.stream(arrayVal[3].toString().split(",")).map(String::trim)
								.toArray(String[]::new);
						List<String> tmpArray = new ArrayList<String>();
						for (String str : baseArray) {
							if (!Arrays.asList(targetArray).contains(str)) {
								tmpArray.add(str);
								System.out.println("Missing in Target JSON Array:>>:" + e.getKey() + "::" + str);
							}
						}
						if (tmpArray.size() > 0)
							tempMap.put(e.getKey().toString() + "[]", tmpArray);
					}

				}
			}
		}
		return tempMap;
	}

	@SuppressWarnings("unused")
	public static boolean isNumeric(String strNum) {
		if (strNum == null) {
			return false;
		}
		try {
			double d = Double.parseDouble(strNum);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	// Bagya - JSON Array Comparison - ends

	@SuppressWarnings({ "rawtypes" })
	public static Map<String, Object> removeElementsMapDiff(String typeOfDifference, String removeElements,
			MapDifference<String, Object> mapDiff) {
		HashMap<String, Object> tempMap = new HashMap<String, Object>();
		boolean arrayFlag = false;
		if (mapDiff != null) {
			if (typeOfDifference == "Target" && mapDiff.entriesOnlyOnLeft() != null) {
				for (Entry e : mapDiff.entriesOnlyOnLeft().entrySet()) {
					if (e.getKey().toString().contains(".")) {
						String[] keyVal = e.getKey().toString().split("[.]");
//						String lastVal = keyVal[keyVal.length - 1];
//						if (isNumeric(lastVal))
//							arrayFlag = true;
					}
//					if (!arrayFlag) {
						if (e.getValue() != null)
							tempMap.put(e.getKey().toString(), e.getValue());
						else
							tempMap.put(e.getKey().toString(), "");
//					}
				}
				if (removeElements != null && removeElements != "") {
					String[] removeElementsArr = removeElements.split(";");
					for (String keyName : removeElementsArr) {
						if (keyName != null || keyName != "") {
							if (tempMap.containsKey(keyName.trim())) {
								tempMap.remove(keyName.trim());
							}
						}
					}
				}

			}
			if (typeOfDifference == "Base" && mapDiff.entriesOnlyOnRight() != null) {
				for (Entry e : mapDiff.entriesOnlyOnRight().entrySet()) {
					if (e.getKey().toString().contains(".")) {
						String[] keyVal = e.getKey().toString().split("[.]");
//						String lastVal = keyVal[keyVal.length - 1];
//						if (isNumeric(lastVal))
//							arrayFlag = true;
					}
//					if (!arrayFlag) {
						if (e.getValue() != null)
							tempMap.put(e.getKey().toString(), e.getValue());
						else
							tempMap.put(e.getKey().toString(), "");
			//		}
				}
				if (removeElements != null && removeElements != "") {
					String[] removeElementsArr = removeElements.split(";");
					for (String keyName : removeElementsArr) {
						if (keyName != null || keyName != "") {
							if (tempMap.containsKey(keyName.trim())) {
								tempMap.remove(keyName.trim());
							}
						}
					}
				}
			}
			if (typeOfDifference == "Diff" && mapDiff.entriesDiffering() != null) {
				for (Entry e : mapDiff.entriesDiffering().entrySet()) {
					if (e.getKey().toString().contains(".")) {
						String[] keyVal = e.getKey().toString().split("[.]");
//						String lastVal = keyVal[keyVal.length - 1];
//						if (isNumeric(lastVal))
//							arrayFlag = true;
					}
//					if (!arrayFlag) {
						if (e.getValue() != null)
							tempMap.put(e.getKey().toString(), e.getValue());
						else
							tempMap.put(e.getKey().toString(), "");
//					}
				}
				if (removeElements != null && removeElements != "") {
					String[] removeElementsArr = removeElements.split(";");
					for (String keyName : removeElementsArr) {
						if (keyName != null || keyName != "") {
							if (tempMap.containsKey(keyName.trim())) {
								tempMap.remove(keyName.trim());
							}
						}
					}
				}
			}
		}
		// remove transID, because it is always going to different.
		if (tempMap.containsKey("transactionReference.transId")) {
			tempMap.remove("transactionReference.transId");
		}

		return tempMap;
	}

	static String between(String value, String a, String b) {
		int posA = value.indexOf(a);
		if (posA == -1) {
			return "";
		}
		int posB = value.lastIndexOf(b);
		if (posB == -1) {
			return "";
		}
		int adjustedPosA = posA + a.length();
		if (adjustedPosA >= posB) {
			return "";
		}
		return value.substring(adjustedPosA, posB);
	}
}
