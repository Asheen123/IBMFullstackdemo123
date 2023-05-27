package com.ups.automation.rest.restautomation.service;

import static io.restassured.RestAssured.given;

import java.io.FileReader;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ups.automation.rest.restautomation.RestAutomationTestApplication;
import com.ups.automation.rest.restautomation.bean.APISheetBean;
import com.ups.automation.rest.restautomation.bean.GlobalSheetBean;
import com.ups.automation.rest.restautomation.jwtsecurity.JWTSecurityUtility;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.internal.RequestSpecificationImpl;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class RestAPIInvokerService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestAPIInvokerService.class);
	private static ResourceBundle configProp = ResourceBundle.getBundle("application");
	public static Map<String, String> varElementsMap = new HashMap<>();
	public static Map<String, String> varHeadersMap = new HashMap<>();
	public static Map<String, String> dynamicElementsMap = new HashMap<>();
	public static String[] relaxHttp = configProp.getString("RELAX_HTTPSVALIDATION").split(",");

	public void invokeRestApi(Map<String, Object> excelApiData) {
		List<GlobalSheetBean> globalSheetBeanList = (List<GlobalSheetBean>) excelApiData.get("globalSheetList");
		if (globalSheetBeanList != null && !globalSheetBeanList.isEmpty()) {
			for (GlobalSheetBean globalSheet : globalSheetBeanList) {
				try {
					if ("Y".equals(globalSheet.getExecute())) {
						invokeRestApiTestCase(globalSheet, excelApiData);
					}
				} catch (Exception ex) {
					LOGGER.error("Exception during invokeRestApi :: {}", ex.getMessage());
				}
			}
		}
	}

	private void invokeRestApiTestCase(GlobalSheetBean globalSheet, Map<String, Object> excelApiData) {
		List<APISheetBean> apiSheetList = globalSheet.getApiSheetList();
		String apiPath = globalSheet.getEnvironment().concat(globalSheet.getPath());
		Boolean testStatus = true;
		globalSheet.setRunDate(new Date());
		for (APISheetBean apiSheet : apiSheetList) {
			try {
				if ("Y".equals(apiSheet.getExecute())) {
					// added code for chaining
					replaceDynamicRequestElements(globalSheet, apiSheet, varElementsMap, varHeadersMap);

					RequestSpecificationImpl requestSpecificationImpl = setCommonParameters(globalSheet, apiSheet,
							globalSheet.getIsJwtRequired(), globalSheet.getGenerateJWTToken(),
							globalSheet.getAuthorization(), globalSheet.getJwtClientId(),
							globalSheet.getJwtClientSecret(), globalSheet.getJwtAudclaim(), globalSheet.getJwtUrl(),
							globalSheet.getConsumes(), globalSheet.getProduces());

					// End

					if ("GET".equals(apiSheet.getSendType().toUpperCase())) {
						invokeGetAPI(requestSpecificationImpl, apiSheet, globalSheet, apiPath,
								globalSheet.getConsumes(), globalSheet.getProduces());
					} else if ("POST".equals(apiSheet.getSendType().toUpperCase())) {
						invokePostAPI(requestSpecificationImpl, apiSheet, globalSheet, apiPath,
								globalSheet.getRequestObj(), globalSheet.getGeneralJsonLocation(),
								globalSheet.getConsumes(), globalSheet.getProduces());
					} else if ("PATCH".equals(apiSheet.getSendType().toUpperCase())) {
						invokePatchAPI(requestSpecificationImpl, apiSheet, globalSheet, apiPath,
								globalSheet.getRequestObj(), globalSheet.getGeneralJsonLocation(),
								globalSheet.getConsumes(), globalSheet.getProduces());
					} else if ("DELETE".equals(apiSheet.getSendType().toUpperCase())) {
						invokeDeletetAPI(requestSpecificationImpl, apiSheet, globalSheet, apiPath,
								globalSheet.getRequestObj(), globalSheet.getGeneralJsonLocation(),
								globalSheet.getConsumes(), globalSheet.getProduces());
					} else if ("PUT".equals(apiSheet.getSendType().toUpperCase())) {
						invokePutAPI(requestSpecificationImpl, apiSheet, globalSheet, apiPath,
								globalSheet.getRequestObj(), globalSheet.getGeneralJsonLocation(),
								globalSheet.getConsumes(), globalSheet.getProduces());
					}

					if (!Constants.STATUS_PASS.equals(apiSheet.getStatus())) {
						testStatus = false;
					}
				}
			} catch (Exception ex) {
				testStatus = false;
				apiSheet.setStatus(Constants.STATUS_FAIL);
				apiSheet.setStatusDetail(ex.getMessage());
				LOGGER.error("Exception during Rest API trigger :: {}", ex.getMessage());
			}
		}
		if (testStatus)

		{
			globalSheet.setStatus(Constants.STATUS_PASS);
		} else {
			globalSheet.setStatus(Constants.STATUS_FAIL);
		}
	}

	// added for chaining
	private void replaceDynamicRequestElements(GlobalSheetBean globalSheet, APISheetBean apiSheet,
			Map<String, String> varElementsMap, Map<String, String> varHeadersMap) {
		Map<String, Object> headerParamMap = apiSheet.getHeaderParam();
		Map<String, Object> pathParamMap = apiSheet.getPathParam();
		Map<String, Object> queryParamMap = apiSheet.getQueryParam();
		Map<String, Object> requestBodyParam = apiSheet.getRequestBodyParam();
		try {
			// Getting pre-script request execution values - starts
			if (null != apiSheet.getPreRequestReq() && apiSheet.getPreRequestReq().equalsIgnoreCase("Y")) {
				Response response = null;
				RequestSpecification requestSpecification = given();
				try {
					response = requestSpecification.urlEncodingEnabled(true).when()
							.get("http://127.0.0.1:5010/" + apiSheet.getRequestScript());
					System.out.println("Pre-Request Script value for " + apiSheet.getRequestScript() + " is "
							+ response.getBody().asString());
					varElementsMap.put(apiSheet.getTcid() + "_" + apiSheet.getRequestScript(),
							response.getBody().asString());
				} catch (Exception e) {
					System.out.println(e);
				}
			}
			// Getting pre-script request execution values - ends
			// Header Param - Dynamic Values changes -starts
			if (headerParamMap != null) {
				for (Map.Entry<String, Object> map : headerParamMap.entrySet()) {
					// System.out.println("Key = " + map.getKey() + ", Value = "
					// + map.getValue());
					String[] temp = null;
					if (map.getValue() != null) {
						if (map.getValue().toString().contains("VE_")) {
							if (map.getKey().toString().equalsIgnoreCase("Authorization")) {
								String val, authVal = "";
								val = headerParamMap.get("Authorization").toString().split("VE_")[1];
								if (varElementsMap.containsKey(val)) {
									authVal = varElementsMap.get(val).toString();
								}
								// headerParamMap.put(configProp.getString("AuthorizationKey"),
								// "Bearer "+authVal);
								map.setValue("Bearer " + authVal);
							} else {
								temp = map.getValue().toString().split("VE_");
								map.setValue(varElementsMap.get(temp[1]));
							}
						}
						// Bagya- Chaning headers -starts
						else if (map.getValue().toString().contains("VH_")) {
							if (map.getValue().toString().startsWith("VH_")) {
								temp = map.getValue().toString().split("VH_");
								map.setValue(varHeadersMap.get(temp[1]));
							} else {
								for (Map.Entry<String, String> map1 : varHeadersMap.entrySet()) {
									if (map.getValue().toString().contains(map1.getKey())) {
										temp = map.getValue().toString().split("VH_");
										String tempVal = map.getValue().toString().replace("VH_" + map1.getKey(),
												varHeadersMap.get(map1.getKey()));
										System.out.println(tempVal);
										map.setValue(tempVal);
									}
								}
							}
						}
						// Bagya- Chaning headers -ends
						// Check for dynamic value - starts
						// Format - DV_<excelName>_<Sheetname>_<columnname>
						else if (map.getValue().toString().contains("DV_")) {
							temp = map.getValue().toString().split("DV_");
							String dynamicVal = ExcelReadService.fetchDynamicVal(temp[1]);
							map.setValue(dynamicVal);
							dynamicElementsMap.put(
									globalSheet.getWorkSheet() + "_" + apiSheet.getTcid() + "_" + map.getKey() + "_H",
									dynamicVal);
						}

						// Check for dynamic value - ends
					}
				}
			}
			// Header Param - Dynamic Values changes -ends
			if (pathParamMap != null) {// banu
				for (Map.Entry<String, Object> map : pathParamMap.entrySet()) {
					// System.out.println("Key = " + map.getKey() + ", Value = "
					// + map.getValue());
					String[] temp = null;
					if (map.getValue() != null) {
						if (map.getValue().toString().contains("VE_")) {
							temp = map.getValue().toString().split("VE_");
							map.setValue(varElementsMap.get(temp[1]));
						}
						// Bagya- Chaning headers -starts
						else if (map.getValue().toString().contains("VH_")) {
							temp = map.getValue().toString().split("VH_");
							map.setValue(varHeadersMap.get(temp[1]));
						}
						// Bagya- Chaning headers -ends
						// Check for dynamic value - starts
						// Format - DV_<excelName>_<Sheetname>_<columnname>
						else if (map.getValue().toString().contains("DV_")) {
							temp = map.getValue().toString().split("DV_");
							String dynamicVal = ExcelReadService.fetchDynamicVal(temp[1]);
							map.setValue(dynamicVal);
							dynamicElementsMap.put(
									globalSheet.getWorkSheet() + "_" + apiSheet.getTcid() + "_" + map.getKey() + "_P",
									dynamicVal);
						}
						// Check for dynamic value - ends
					}
				}
			}
			if (queryParamMap != null) {// banu
				for (Map.Entry<String, Object> map : queryParamMap.entrySet()) {
					// System.out.println("Key = " + map.getKey() + ", Value = "
					// + map.getValue());
					String[] temp = null;
					if (map.getValue() != null) {
						if (map.getValue().toString().contains("VE_")) {
							temp = map.getValue().toString().split("VE_");
							map.setValue(varElementsMap.get(temp[1]));
						}

						// Bagya- Chaning headers -starts
						else if (map.getValue().toString().contains("VH_")) {
							temp = map.getValue().toString().split("VH_");
							map.setValue(varHeadersMap.get(temp[1]));
						}
						// Bagya- Chaning headers -ends
						// Check for dynamic value - starts
						// Format - DV_<excelName>_<Sheetname>_<columnname>
						else if (map.getValue().toString().contains("DV_")) {
							temp = map.getValue().toString().split("DV_");
							String dynamicVal = ExcelReadService.fetchDynamicVal(temp[1]);
							map.setValue(dynamicVal);
							dynamicElementsMap.put(
									globalSheet.getWorkSheet() + "_" + apiSheet.getTcid() + "_" + map.getKey() + "_Q",
									dynamicVal);
						}
						// Check for dynamic value - ends
					}
					// System.out.println("Key = " + map.getKey() + ", Value = "
					// + map.getValue());
				}
			}
			if (requestBodyParam != null) {// banu
				for (Map.Entry<String, Object> map : requestBodyParam.entrySet()) {
					// System.out.println("Key = " + map.getKey() + ", Value = "
					// + map.getValue());
					String[] temp = null;
					if (map.getValue() != null) {
						// System.out.println("***"+map.getValue().toString());
						if (map.getValue().toString().contains("VE_")) {
							// System.out.println("policyid.."+varElementsMap.get(temp[1]));
							// To replace the Var elements inside JSON type of
							// values - starts
							if (map.getValue().toString().startsWith("{")) {
								for (Map.Entry<String, String> map1 : varElementsMap.entrySet()) {
									if (map.getValue().toString().contains(map1.getKey())) {
										temp = map.getValue().toString().split("VE_");
										// JsonParser jsonParser = new
										// JsonParser();
										// JsonElement jsonEle =
										// jsonParser.parse(map.getValue().toString().replace("VE_"+map1.getKey(),
										// varElementsMap.get(map1.getKey())));
										String tempVal = map.getValue().toString().replace("VE_" + map1.getKey(),
												varElementsMap.get(map1.getKey()));
										map.setValue(tempVal);
									}
								}
							}
							// To replace the Var elements inside JSON type of
							// values - ends
							else {
								temp = map.getValue().toString().split("VE_");
								map.setValue(varElementsMap.get(temp[1]));
							}
						}
						// Bagya- Chaning headers -starts
						else if (map.getValue().toString().contains("VH_")) {
							temp = map.getValue().toString().split("VH_");
							map.setValue(varHeadersMap.get(temp[1]));
						}
						// Bagya- Chaning headers -ends
						// Check for dynamic value - starts
						// Format - DV_<excelName>_<Sheetname>_<columnname>
						else if (map.getValue().toString().contains("DV_")) {
							temp = map.getValue().toString().split("DV_");
							String dynamicVal = ExcelReadService.fetchDynamicVal(temp[1]);
							map.setValue(dynamicVal);
							dynamicElementsMap.put(
									globalSheet.getWorkSheet() + "_" + apiSheet.getTcid() + "_" + map.getKey(),
									dynamicVal);
						}
						// Check for dynamic value - ends
					}
				}
			}
		} catch (Exception ex) {
			System.out.println("Exception Occur while replace dynamic request object  -- " + ex.getMessage());
			LOGGER.error("Exception Occur while replace dynamic request object", ex.getMessage());
		}
		// headerParamMap.get("")

	}

	// end

	private RequestSpecificationImpl setCommonParameters(GlobalSheetBean globalSheet, APISheetBean apiSheet,
			Boolean isJwtRequired, Boolean generateJWTToken, String authorization, String clientId, String clientSecret,
			String audClaim, String jwtUrl, String consumes, String produces) throws Exception {
		Map<String, Object> headerParamMap = apiSheet.getHeaderParam();
		Map<String, Object> pathParamMap = apiSheet.getPathParam();
		Map<String, Object> queryParamMap = apiSheet.getQueryParam();

		// Added JWT Token Condition
		if (isJwtRequired && headerParamMap != null) {
			// Bagya - Added Global Jwt Token changes - starts
			if (configProp.getString("GlobalJwtTokenRequired").equalsIgnoreCase("TRUE")) {
				// System.out.println("Using Global JWT Token for " + apiname);
				headerParamMap.put(configProp.getString("AuthorizationKey"),
						RestAutomationTestApplication.globalAutoJwtToken);
			} else {
				// Bagya - Added Global Jwt Token changes - ends
				if (generateJWTToken) {
					String jwtToken = JWTSecurityUtility.getJwtToken(clientId, clientSecret, audClaim, jwtUrl);
					// Vivek - added logic to remove keyword Bearer from JWT if
					// API does not support Bearer
					if (configProp.getString("RemoveBEARERFromJWT").equalsIgnoreCase("yes")) {
						jwtToken = jwtToken.substring(7);
					}
					//// Vivek - added logic to remove keyword Bearer from JWT
					//// if API does not support Bearer -end
					// System.out.println("Generated Default JWT Token for " +
					//// apiname);
					// jwtToken = "Bearer "+ jwtToken;
					headerParamMap.put(configProp.getString("AuthorizationKey"), jwtToken);
				} else {
					if (authorization != null)// vivek: Added logic to not set
												// JWT as NULL when user dont
												// give anything
						headerParamMap.put(configProp.getString("AuthorizationKey"), authorization); // Vivek:
																										// Removed
																										// Bearer
																										// Keyword
																										// when
																										// providing
																										// Static
																										// JWT
				}
			}
		} else if (headerParamMap != null && headerParamMap.containsKey("Authorization")) {
			if (!headerParamMap.get("Authorization").toString().contains("Bearer"))
				headerParamMap.remove("Authorization");

		}

		// if(isJwtRequired && headerParamMap!= null &&
		// headerParamMap.containsKey("Authorization")) {
		// String jwtToken = JWTSecurityUtility.getJwtToken(clientId,
		// clientSecret, audClaim,jwtUrl);
		// //jwtToken = "Bearer "+ jwtToken;
		// headerParamMap.put("Authorization", jwtToken);
		// } else {
		// headerParamMap.remove("Authorization");
		// }

		RequestSpecification requestSpecification = given();
		RequestSpecificationImpl requestSpecificationImpl = (RequestSpecificationImpl) requestSpecification;
		requestSpecificationImpl.config(
				RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().defaultContentCharset("UTF-8")));
		requestSpecificationImpl.config(RestAssured.config().encoderConfig(
				EncoderConfig.encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)));
		if ((consumes != null) && consumes.contains("form-urlencoded")) {
			requestSpecificationImpl.contentType(ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
		} else if ((consumes != null) && consumes.contains("application/json")) {
			requestSpecificationImpl.contentType(ContentType.APPLICATION_JSON.getMimeType());
		} else if ((consumes != null) && consumes.contains("application/text")) {
			requestSpecificationImpl.contentType("application/text");
			requestSpecificationImpl
		.config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs("application/text", io.restassured.http.ContentType.TEXT)));
		}
		if ((produces != null) && produces.contains("text/plain")) {
			requestSpecificationImpl.accept(ContentType.TEXT_PLAIN.getMimeType());
		} else {
			requestSpecificationImpl.accept(ContentType.APPLICATION_JSON.getMimeType());
		}

		if (headerParamMap != null && !headerParamMap.isEmpty()) {
			requestSpecificationImpl.headers(headerParamMap);
		}

		if (pathParamMap != null && !pathParamMap.isEmpty()) {
			requestSpecificationImpl.pathParams(pathParamMap);
		}

		if (queryParamMap != null && !queryParamMap.isEmpty()) {
			requestSpecificationImpl.queryParams(queryParamMap);
		}
		// vbc9wqy - environment which needs to relax the HTTPS SSL
		// Certification validation - starts
		String environment = globalSheet.getEnvironment();
		for (String s : relaxHttp) {
			if (environment.equalsIgnoreCase("https://" + s)) {
				requestSpecificationImpl.relaxedHTTPSValidation();
			}
		}
		// vbc9wqy - domains which needs to relax the HTTPS SSL Certification
		// validation - ends
		return requestSpecificationImpl;
	}

	private void invokeGetAPI(RequestSpecificationImpl requestSpecification, APISheetBean apiSheet,
			GlobalSheetBean globalSheet, String apiPath, String consumes, String produces) throws Exception {

		apiSheet.setDate(new Date());
		Response response = null;
		try {
			if (configProp.getString("DisableURLEncoding").equalsIgnoreCase("yes"))
				response = requestSpecification.urlEncodingEnabled(false).when().get(apiPath);
			else
				response = requestSpecification.urlEncodingEnabled(true).when().get(apiPath);
			String restURL = requestSpecification.getURI();
			// System.out.println(apiSheet.getActual());
			apiSheet.setRestUrl(restURL);
			LOGGER.info("REST URL for TCID {} :: {}", apiSheet.getTcid(), restURL);
		} catch (IllegalArgumentException ex) {
			LOGGER.error("Exception during invokeGetAPI :: {}", ex.getMessage());
			String restURL = requestSpecification.getURI();
			apiSheet.setRestUrl(restURL);
			apiSheet.setStatusDetail(ex.getMessage());
			apiSheet.setStatus(Constants.STATUS_ERROR);
		}
		if (response != null && (response.statusCode() == 200 || response.statusCode() == 403
				|| response.statusCode() == 400 || response.statusCode() == 417 || response.statusCode() == 500
				|| response.statusCode() == 401 || response.statusCode() == 404 || response.statusCode() == 405 || response.statusCode() == 503
				|| response.statusCode() == 422 || response.statusCode() == 302)) {
			//LOGGER.info("Status for TCID {} :: {}", apiSheet.getTcid(), response.statusCode());
			LOGGER.info("Response Body for TCID {} :: {}", apiSheet.getTcid(), response.getBody().asString());
			apiSheet.setStatus(Constants.STATUS_SUCCESS);
			apiSheet.setApiResponse(response);
			RestAPIResponseValidator.validateAPIResponse(apiSheet, globalSheet, varElementsMap, produces,
					varHeadersMap);
		} else if (response != null) {
			apiSheet.setApiResponse(response);
			apiSheet.setStatus(Constants.STATUS_FAIL);

			StringBuilder statusDetail = new StringBuilder();
			statusDetail.append("HttpStatus :: ");
			statusDetail.append(response.statusLine());
			if (response.getBody() != null) {
				statusDetail.append(":: ErrorCode :: ");
				statusDetail.append(response.getBody().jsonPath().getString("response.errors[0].code"));
				statusDetail.append(":: ErrorMessage :: ");
				statusDetail.append(response.getBody().jsonPath().getString("response.errors[0].message"));
			}
			apiSheet.setStatusDetail(statusDetail.toString());
			//LOGGER.info("Status for TCID {} :: {} :: {} :: {}", apiSheet.getTcid(), response.statusCode(),
					//response.getStatusLine(), statusDetail.toString());
		}
	}

	private void invokePostAPI(RequestSpecificationImpl requestSpecification, APISheetBean apiSheet,
			GlobalSheetBean globalSheet, String apiPath, String requestObjName, String requestObjPath, String consumes,
			String produces) throws Exception {

		apiSheet.setDate(new Date());
		if (requestObjName != null && !requestObjName.isEmpty()) {
			JsonObject requestJsonObject = null;
			if (null != apiSheet.getInputRequest() && null != apiSheet.getInputRequestPath()
					&& apiSheet.getInputRequest().equalsIgnoreCase("Y")
					&& !apiSheet.getInputRequestPath().equalsIgnoreCase("")) {
				try (FileReader obj = new FileReader(apiSheet.getInputRequestPath())) {
					JsonParser jsonArrayParser = new JsonParser();
					JsonElement isJsonElement = jsonArrayParser.parse(obj);
					if (isJsonElement.isJsonArray()) {
						requestSpecification.body(isJsonElement.getAsJsonArray());
						JsonObject jo = new JsonObject();
						jo.add("arrayKey", isJsonElement.getAsJsonArray());
						apiSheet.setApiRequest(jo);
					} else {
						JsonObject json = isJsonElement.getAsJsonObject();
						requestSpecification.body(json);
						apiSheet.setApiRequest(json);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				requestJsonObject = RequestBodyBuilder.buildRequestBody(requestObjPath, requestObjName,
						apiSheet.getRequestBodyParam());
				if (requestJsonObject != null) {
					if (requestJsonObject.get("arrayKey") != null) {
						requestSpecification.body(requestJsonObject.get("arrayKey"));
						apiSheet.setApiRequest(requestJsonObject);
					} else {
						requestSpecification.body(requestJsonObject);
						apiSheet.setApiRequest(requestJsonObject);
					}
				}
			}
		} else {
			if (consumes != null && consumes != "" && consumes.contains("x-www-form-urlencoded")) {
				// added for POST request form-urlencoding - Currently not added
				// condition to check whether consume type = form-urlencoded, it
				// can be added later.
				Set<String> requestBodyParamKeySet = apiSheet.getRequestBodyParam().keySet();
				for (String paramName : requestBodyParamKeySet) {
					requestSpecification.formParam(paramName, apiSheet.getRequestBodyParam().get(paramName));
				}
				// requestSpecification.formParam("payload",
				// apiSheet.getRequestBodyParam().get(paramName));
			}else if(consumes != null && consumes != "" && consumes.contains("application/text")) {
				//System.out.println("I am processing encoded text request::"+apiSheet.getRequestBodyParam().keySet());
				String originalInput = apiSheet.getRequestBodyParam().get("request").toString();
				String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
				System.out.println("Encoded String::"+encodedString);
				requestSpecification.body(encodedString);
				if(!encodedString.contains("=")){
				JsonParser jsonArrayParser = new JsonParser();
				JsonElement isJsonElement = jsonArrayParser.parse(encodedString);
				JsonObject jo = new JsonObject();
				jo.add("request", isJsonElement);
				apiSheet.setApiRequest(jo);
				}
			}
		}

		Response response = null;
		try {
			if (configProp.getString("DisableURLEncoding").equalsIgnoreCase("yes"))
				response = requestSpecification.urlEncodingEnabled(false).when().post(apiPath);
			else
				response = requestSpecification.urlEncodingEnabled(true).when().post(apiPath);
			String restURL = requestSpecification.getURI();
			apiSheet.setRestUrl(restURL);
			LOGGER.info("REST URL for TCID {} :: {}", apiSheet.getTcid(), restURL);
		} catch (IllegalArgumentException ex) {
			LOGGER.error("Exception during invokeGetAPI :: {}", ex.getMessage());
			String restURL = requestSpecification.getURI();
			apiSheet.setRestUrl(restURL);
			apiSheet.setStatusDetail(ex.getMessage());
			apiSheet.setStatus(Constants.STATUS_ERROR);
		}
//		System.out.println(response.statusCode() );
//		System.out.println(response.getBody().asString());
		if (response.statusCode() == 200 || response.statusCode() == 201 || response.statusCode() == 403
				|| response.statusCode() == 400 || response.statusCode() == 417 || response.statusCode() == 500
				|| response.statusCode() == 401 || response.statusCode() == 406 || response.statusCode() == 404
				|| response.statusCode() == 405 || response.statusCode() == 503 || response.statusCode() == 409 || response.statusCode() == 415
				|| response.statusCode() == 422 || response.statusCode() == 302) {
			//LOGGER.info("Status for TCID {} :: {}", apiSheet.getTcid(), response.statusCode());
			LOGGER.info("Response Body for TCID {} :: {}", apiSheet.getTcid(), response.getBody().asString());
			apiSheet.setApiResponse(response);
			apiSheet.setStatus(Constants.STATUS_SUCCESS);
			RestAPIResponseValidator.validateAPIResponse(apiSheet, globalSheet, varElementsMap, produces,
					varHeadersMap);
		} else if (response != null) {
			apiSheet.setApiResponse(response);
			apiSheet.setStatus(Constants.STATUS_FAIL);

			StringBuilder statusDetail = new StringBuilder();
			statusDetail.append("HttpStatus :: ");
			statusDetail.append(response.statusLine());
			if (response.getBody() != null) {
				statusDetail.append(":: ErrorCode :: ");
				statusDetail.append(response.getBody().jsonPath().getString("response.errors[0].code"));
				// statusDetail.append(response.getBody().jsonPath().getString("response.errors[0].errorCode"));
				statusDetail.append(":: ErrorMessage :: ");
				statusDetail.append(response.getBody().jsonPath().getString("response.errors[0].message"));
				// statusDetail.append(response.getBody().jsonPath().getString("response.errors[0].errorMessage"));
			}
			apiSheet.setStatusDetail(statusDetail.toString());
			//LOGGER.info("Status for TCID {} :: {} :: {} :: {}", apiSheet.getTcid(), response.statusCode(),
					//response.getStatusLine(), statusDetail.toString());
		}
	}

	private void invokeDeletetAPI(RequestSpecificationImpl requestSpecification, APISheetBean apiSheet,
			GlobalSheetBean globalSheet, String apiPath, String requestObjName, String requestObjPath,
			String consumeType, String produces) throws Exception {
		apiSheet.setDate(new Date());
		Response response = null;
		if (requestObjName != null && !requestObjName.isEmpty()) {
			JsonObject requestJsonObject = null;
			if (null != apiSheet.getInputRequest() && null != apiSheet.getInputRequestPath()
					&& apiSheet.getInputRequest().equalsIgnoreCase("Y")
					&& !apiSheet.getInputRequestPath().equalsIgnoreCase("")) {
				try (FileReader obj = new FileReader(apiSheet.getInputRequestPath())) {
					JsonParser jsonArrayParser = new JsonParser();
					JsonElement isJsonElement = jsonArrayParser.parse(obj);
					if (isJsonElement.isJsonArray()) {
						requestSpecification.body(isJsonElement.getAsJsonArray());
						JsonObject jo = new JsonObject();
						jo.add("arrayKey", isJsonElement.getAsJsonArray());
						apiSheet.setApiRequest(jo);
					} else {
						JsonObject json = isJsonElement.getAsJsonObject();
						requestSpecification.body(json);
						apiSheet.setApiRequest(json);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				requestJsonObject = RequestBodyBuilder.buildRequestBody(requestObjPath, requestObjName,
						apiSheet.getRequestBodyParam());
				if (requestJsonObject != null) {
					if (requestJsonObject.get("arrayKey") != null) {
						requestSpecification.body(requestJsonObject.get("arrayKey"));
						apiSheet.setApiRequest(requestJsonObject);
					} else {
						requestSpecification.body(requestJsonObject);
						apiSheet.setApiRequest(requestJsonObject);
					}
				}
			}
		} else { // vbc9wqy: added for Delete request form-urlencoding - Added
					// condition to check whether consume type = form-urlencoded
					// in delete only, it can be added later for other request.
					// added parameter "globalSheet.getConsumes()" check here in
					// delete request only as few PDR delete request are without
					// payload and in that case it was giving null pointer
					// before.
			if (consumeType != null && consumeType != "" && consumeType.contains("x-www-form-urlencoded")) {
				Set<String> requestBodyParamKeySet = apiSheet.getRequestBodyParam().keySet();
				for (String paramName : requestBodyParamKeySet) {
					requestSpecification.formParam(paramName, apiSheet.getRequestBodyParam().get(paramName));
				}
			}else if(consumeType != null && consumeType != "" && consumeType.contains("text")) {
				//System.out.println("I am processing encoded text request::"+apiSheet.getRequestBodyParam().keySet());
				String originalInput = apiSheet.getRequestBodyParam().get("request").toString();
				String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
				System.out.println("Encoded String::"+encodedString);
				requestSpecification.body(encodedString);
				if(!encodedString.contains("=")){
				JsonParser jsonArrayParser = new JsonParser();
				JsonElement isJsonElement = jsonArrayParser.parse(encodedString);
				JsonObject jo = new JsonObject();
				jo.add("request", isJsonElement);
				apiSheet.setApiRequest(jo);
				}
			}
		}

		try {
			if (configProp.getString("DisableURLEncoding").equalsIgnoreCase("yes"))
				response = requestSpecification.urlEncodingEnabled(false).when().delete(apiPath);
			else
				response = requestSpecification.urlEncodingEnabled(true).when().delete(apiPath);
			String restURL = requestSpecification.getURI();
			apiSheet.setRestUrl(restURL);
			LOGGER.info("REST URL for TCID {} :: {}", apiSheet.getTcid(), restURL);
		} catch (IllegalArgumentException ex) {
			LOGGER.error("Exception during invokeDeleteAPI :: {}", ex.getMessage());
			String restURL = requestSpecification.getURI();
			apiSheet.setRestUrl(restURL);
			apiSheet.setStatusDetail(ex.getMessage());
			apiSheet.setStatus(Constants.STATUS_ERROR);
		}
		if (response != null && (response.statusCode() == 200 || response.statusCode() == 403
				|| response.statusCode() == 400 || response.statusCode() == 417 || response.statusCode() == 500
				|| response.statusCode() == 401 || response.statusCode() == 404 || response.statusCode() == 405 || response.statusCode() == 503
				|| response.statusCode() == 422 || response.statusCode() == 410 || response.statusCode() == 302)) {
			//LOGGER.info("Status for TCID {} :: {}", apiSheet.getTcid(), response.statusCode());
			LOGGER.info("Response Body for TCID {} :: {}", apiSheet.getTcid(), response.getBody().asString());
			apiSheet.setStatus(Constants.STATUS_SUCCESS);
			apiSheet.setApiResponse(response);
			RestAPIResponseValidator.validateAPIResponse(apiSheet, globalSheet, varElementsMap, produces,
					varHeadersMap);
		} else if (response != null) {
			apiSheet.setApiResponse(response);
			apiSheet.setStatus(Constants.STATUS_FAIL);

			StringBuilder statusDetail = new StringBuilder();
			statusDetail.append("HttpStatus :: ");
			statusDetail.append(response.statusLine());
			if (response.getBody() != null) {
				statusDetail.append(":: ErrorCode :: ");
				statusDetail.append(response.getBody().jsonPath().getString("response.errors[0].code"));
				statusDetail.append(":: ErrorMessage :: ");
				statusDetail.append(response.getBody().jsonPath().getString("response.errors[0].message"));
			}
			apiSheet.setStatusDetail(statusDetail.toString());
			//LOGGER.info("Status for TCID {} :: {} :: {} :: {}", apiSheet.getTcid(), response.statusCode(),
					//response.getStatusLine(), statusDetail.toString());
		}
	}

	private void invokePatchAPI(RequestSpecificationImpl requestSpecification, APISheetBean apiSheet,
			GlobalSheetBean globalSheet, String apiPath, String requestObjName, String requestObjPath, String consumes,
			String produces) throws Exception {

		apiSheet.setDate(new Date());
		Response response = null;
		if (requestObjName != null && !requestObjName.isEmpty()) {
			JsonObject requestJsonObject = null;
			if (null != apiSheet.getInputRequest() && null != apiSheet.getInputRequestPath()
					&& apiSheet.getInputRequest().equalsIgnoreCase("Y")
					&& !apiSheet.getInputRequestPath().equalsIgnoreCase("")) {
				try (FileReader obj = new FileReader(apiSheet.getInputRequestPath())) {
					JsonParser jsonArrayParser = new JsonParser();
					JsonElement isJsonElement = jsonArrayParser.parse(obj);
					if (isJsonElement.isJsonArray()) {
						requestSpecification.body(isJsonElement.getAsJsonArray());
						JsonObject jo = new JsonObject();
						jo.add("arrayKey", isJsonElement.getAsJsonArray());
						apiSheet.setApiRequest(jo);
					} else {
						JsonObject json = isJsonElement.getAsJsonObject();
						requestSpecification.body(json);
						apiSheet.setApiRequest(json);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				requestJsonObject = RequestBodyBuilder.buildRequestBody(requestObjPath, requestObjName,
						apiSheet.getRequestBodyParam());
				if (requestJsonObject != null) {
					if (requestJsonObject.get("arrayKey") != null) {
						requestSpecification.body(requestJsonObject.get("arrayKey"));
						apiSheet.setApiRequest(requestJsonObject);
					} else {
						requestSpecification.body(requestJsonObject);
						apiSheet.setApiRequest(requestJsonObject);
					}
				}
			}
		} else { // added for POST request form-urlencoding - Currently not
					// added condition to check whether consume type =
					// form-urlencoded, it can be added later.
			if (consumes != null && consumes != "" && consumes.contains("x-www-form-urlencoded")) {
				Set<String> requestBodyParamKeySet = apiSheet.getRequestBodyParam().keySet();
				for (String paramName : requestBodyParamKeySet) {
					requestSpecification.formParam(paramName, apiSheet.getRequestBodyParam().get(paramName));
				}
			}else if(consumes != null && consumes != "" && consumes.contains("application/text")) {
				//System.out.println("I am processing encoded text request::"+apiSheet.getRequestBodyParam().keySet());
				String originalInput = apiSheet.getRequestBodyParam().get("request").toString();
				String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
				System.out.println("Encoded String::"+encodedString);
				requestSpecification.body(encodedString);
				if(!encodedString.contains("=")){
				JsonParser jsonArrayParser = new JsonParser();
				JsonElement isJsonElement = jsonArrayParser.parse(encodedString);
				JsonObject jo = new JsonObject();
				jo.add("request", isJsonElement);
				apiSheet.setApiRequest(jo);
				}
			}
		}
		try {
			if (configProp.getString("DisableURLEncoding").equalsIgnoreCase("yes"))
				response = requestSpecification.urlEncodingEnabled(false).when().patch(apiPath);
			else
				response = requestSpecification.urlEncodingEnabled(true).when().patch(apiPath);
			String restURL = requestSpecification.getURI();
			apiSheet.setRestUrl(restURL);
			LOGGER.info("REST URL for TCID {} :: {}", apiSheet.getTcid(), restURL);
		} catch (IllegalArgumentException ex) {
			LOGGER.error("Exception during invokePatchAPI :: {}", ex.getMessage());
			String restURL = requestSpecification.getURI();
			apiSheet.setRestUrl(restURL);
			apiSheet.setStatusDetail(ex.getMessage());
			apiSheet.setStatus(Constants.STATUS_ERROR);
		}
		if (response != null && (response.statusCode() == 200 || response.statusCode() == 403
				|| response.statusCode() == 400 || response.statusCode() == 417 || response.statusCode() == 500
				|| response.statusCode() == 401 || response.statusCode() == 404 || response.statusCode() == 405 || response.statusCode() == 503)) {
			//LOGGER.info("Status for TCID {} :: {}", apiSheet.getTcid(), response.statusCode());
			LOGGER.info("Response Body for TCID {} :: {}", apiSheet.getTcid(), response.getBody().asString());
			apiSheet.setStatus(Constants.STATUS_SUCCESS);
			apiSheet.setApiResponse(response);
			RestAPIResponseValidator.validateAPIResponse(apiSheet, globalSheet, varElementsMap, produces,
					varHeadersMap);
		} else if (response != null) {
			apiSheet.setApiResponse(response);
			apiSheet.setStatus(Constants.STATUS_FAIL);

			StringBuilder statusDetail = new StringBuilder();
			statusDetail.append("HttpStatus :: ");
			statusDetail.append(response.statusLine());
			if (response.getBody() != null) {
				statusDetail.append(":: ErrorCode :: ");
				statusDetail.append(response.getBody().jsonPath().getString("response.errors[0].code"));
				statusDetail.append(":: ErrorMessage :: ");
				statusDetail.append(response.getBody().jsonPath().getString("response.errors[0].message"));
			}
			apiSheet.setStatusDetail(statusDetail.toString());
			//LOGGER.info("Status for TCID {} :: {} :: {} :: {}", apiSheet.getTcid(), response.statusCode(),
					//response.getStatusLine(), statusDetail.toString());
		}
	}

	private void invokePutAPI(RequestSpecificationImpl requestSpecification, APISheetBean apiSheet,
			GlobalSheetBean globalSheet, String apiPath, String requestObjName, String requestObjPath, String consumes,
			String produces) throws Exception {

		apiSheet.setDate(new Date());
		Response response = null;
		if (requestObjName != null && !requestObjName.isEmpty()) {
			JsonObject requestJsonObject = null;
			if (null != apiSheet.getInputRequest() && null != apiSheet.getInputRequestPath()
					&& apiSheet.getInputRequest().equalsIgnoreCase("Y")
					&& !apiSheet.getInputRequestPath().equalsIgnoreCase("")) {
				try (FileReader obj = new FileReader(apiSheet.getInputRequestPath())) {
					JsonParser jsonArrayParser = new JsonParser();
					JsonElement isJsonElement = jsonArrayParser.parse(obj);
					if (isJsonElement.isJsonArray()) {
						requestSpecification.body(isJsonElement.getAsJsonArray());
						JsonObject jo = new JsonObject();
						jo.add("arrayKey", isJsonElement.getAsJsonArray());
						apiSheet.setApiRequest(jo);
					} else {
						JsonObject json = isJsonElement.getAsJsonObject();
						requestSpecification.body(json);
						apiSheet.setApiRequest(json);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				requestJsonObject = RequestBodyBuilder.buildRequestBody(requestObjPath, requestObjName,
						apiSheet.getRequestBodyParam());
				if (requestJsonObject != null) {
					if (requestJsonObject.get("arrayKey") != null) {
						requestSpecification.body(requestJsonObject.get("arrayKey"));
						apiSheet.setApiRequest(requestJsonObject);
					} else {
						requestSpecification.body(requestJsonObject);
						apiSheet.setApiRequest(requestJsonObject);
					}
				}
			}
		} else { // added for POST request form-urlencoding - Currently not
					// added condition to check whether consume type =
					// form-urlencoded, it can be added later.
			if (consumes != null && consumes != "" && consumes.contains("x-www-form-urlencoded")) {
				Set<String> requestBodyParamKeySet = apiSheet.getRequestBodyParam().keySet();
				for (String paramName : requestBodyParamKeySet) {
					requestSpecification.formParam(paramName, apiSheet.getRequestBodyParam().get(paramName));
				}
			}else if(consumes != null && consumes != "" && consumes.contains("application/text")) {
				//System.out.println("I am processing encoded text request::"+apiSheet.getRequestBodyParam().keySet());
				String originalInput = apiSheet.getRequestBodyParam().get("request").toString();
				String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
				System.out.println("Encoded String::"+encodedString);
				requestSpecification.body(encodedString);
				if(!encodedString.contains("=")){
				JsonParser jsonArrayParser = new JsonParser();
				JsonElement isJsonElement = jsonArrayParser.parse(encodedString);
				JsonObject jo = new JsonObject();
				jo.add("request", isJsonElement);
				apiSheet.setApiRequest(jo);
				}
			}
		}
		try {
			if (configProp.getString("DisableURLEncoding").equalsIgnoreCase("yes"))
				response = requestSpecification.urlEncodingEnabled(false).when().put(apiPath);
			else
				response = requestSpecification.urlEncodingEnabled(true).when().put(apiPath);
			String restURL = requestSpecification.getURI();
			apiSheet.setRestUrl(restURL);
			LOGGER.info("REST URL for TCID {} :: {}", apiSheet.getTcid(), restURL);
		} catch (IllegalArgumentException ex) {
			LOGGER.error("Exception during invokePutAPI :: {}", ex.getMessage());
			String restURL = requestSpecification.getURI();
			apiSheet.setRestUrl(restURL);
			apiSheet.setStatusDetail(ex.getMessage());
			apiSheet.setStatus(Constants.STATUS_ERROR);
		}
		if (response != null && (response.statusCode() == 200 || response.statusCode() == 403
				|| response.statusCode() == 400 || response.statusCode() == 417 || response.statusCode() == 500
				|| response.statusCode() == 401 || response.statusCode() == 404 || response.statusCode() == 405 || response.statusCode() == 503)) {
			//LOGGER.info("Status for TCID {} :: {}", apiSheet.getTcid(), response.statusCode());
			LOGGER.info("Response Body for TCID {} :: {}", apiSheet.getTcid(), response.getBody().asString());
			apiSheet.setStatus(Constants.STATUS_SUCCESS);
			apiSheet.setApiResponse(response);
			RestAPIResponseValidator.validateAPIResponse(apiSheet, globalSheet, varElementsMap, produces,
					varHeadersMap);
		} else if (response != null) {
			apiSheet.setApiResponse(response);
			apiSheet.setStatus(Constants.STATUS_FAIL);

			StringBuilder statusDetail = new StringBuilder();
			statusDetail.append("HttpStatus :: ");
			statusDetail.append(response.statusLine());
			if (response.getBody() != null) {
				statusDetail.append(":: ErrorCode :: ");
				statusDetail.append(response.getBody().jsonPath().getString("response.errors[0].code"));
				statusDetail.append(":: ErrorMessage :: ");
				statusDetail.append(response.getBody().jsonPath().getString("response.errors[0].message"));
			}
			apiSheet.setStatusDetail(statusDetail.toString());
			//LOGGER.info("Status for TCID {} :: {} :: {} :: {}", apiSheet.getTcid(), response.statusCode(),
					//response.getStatusLine(), statusDetail.toString());
		}
	}

}
