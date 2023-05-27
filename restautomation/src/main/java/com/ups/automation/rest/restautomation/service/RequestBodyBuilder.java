package com.ups.automation.rest.restautomation.service;

import java.io.FileReader;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
//import com.ups.automation.rest.restautomation.service.RestAPIInvokerService;

public class RequestBodyBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(RequestBodyBuilder.class);
	// private static JsonArray arrayStrja= new JsonArray();
	// private static JsonArray childArray= new JsonArray();
	// private static JsonArray parentArray= new JsonArray();

	private static JsonArray arrayStrja;
	private static String arrayTypeKey;

	public static JsonObject buildRequestBody(String requestObjectPath, String requestObjectName,
			Map<String, Object> requestBodyParam) {
		JsonObject finaljson = null;
		String fileName = null;
		if (requestObjectPath != null && !requestObjectPath.isEmpty()) {
			fileName = requestObjectPath.concat(requestObjectName);
		}
		JsonObject requestJsonObject = null;
		if (fileName != null && !fileName.isEmpty()) {
			try (FileReader fin = new FileReader(fileName)) {
				JsonParser jsonArrayParser = new JsonParser();
				JsonElement isJsonElement = jsonArrayParser.parse(fin);
				if (isJsonElement.isJsonArray()) {
					if (requestBodyParam.containsKey("[]")) {
						JsonObject jo = new JsonObject();
						JsonArray ja = new JsonArray();
						ja.add(requestBodyParam.get("[]").toString());
						jo.add("arrayKey", ja);
						requestJsonObject = jo;
						finaljson = requestJsonObject;
					} else {
						JsonObject jo = new JsonObject();
						jo.add("arrayKey", isJsonElement);
						Map<String, Object> requestBodyParamNew = new HashedMap<>();
						Set<String> requestBodyParamKeySetNew = requestBodyParam.keySet();
						for (String paramName : requestBodyParamKeySetNew) {
							String paramNameNew = "";
							if (paramName.contains("[]"))
								paramNameNew = paramName.replace("[]", "arrayKey");
							else
								paramNameNew = paramName;
							requestBodyParamNew.put(paramNameNew, requestBodyParam.get(paramName));
						}
						Set<String> requestBodyParamKeySet = requestBodyParamNew.keySet();
						for (String paramName : requestBodyParamKeySet) {
							buildRequestBody(jo, paramName, paramName, requestBodyParamNew);
						}
						requestJsonObject = jo;
						finaljson = removeUnusedProperty(requestJsonObject);
						finaljson = removeNullProperty(finaljson);
					}
				} else {
					JsonObject json = isJsonElement.getAsJsonObject();
					Set<String> requestBodyParamKeySet = requestBodyParam.keySet();
					for (String paramName : requestBodyParamKeySet) {
						buildRequestBody(json, paramName, paramName, requestBodyParam);
					}
					requestJsonObject = json;
					System.out.println(requestJsonObject);
					finaljson = removeUnusedProperty(requestJsonObject);
					finaljson = removeNullProperty(finaljson);
				}
			} catch (Exception ex) {
				LOGGER.info("Exception During BuildRequestBody :: {}", ex.getMessage());
			}
		}
		return finaljson;
	}

	private static void buildRequestBody(JsonObject requestBody, String paramName, String tokenName,
			Map<String, Object> requestBodyParam) {
		try {
			StringTokenizer st = new StringTokenizer(tokenName, "_");
			if (st.hasMoreTokens()) {
				boolean requestflag = false;
				String jsonElementName = st.nextToken();
				if (!requestBody.has(jsonElementName)) {
					requestflag = true;
					requestBody.addProperty(jsonElementName, "");
				}
				JsonElement jsonElement = requestBody.get(jsonElementName);
				if (jsonElement.isJsonPrimitive()) {
					if (requestBodyParam.containsKey(paramName)) {
						Object value = requestBodyParam.get(paramName);
						// Bagya - Response json object chaining code - starts
						if (jsonElement.getAsString().contains("object")) {
							String stringValue = (String) value;
							JsonParser jsonParser = new JsonParser();
							JsonElement jsonEle = jsonParser.parse(stringValue);
							requestBody.add(tokenName, jsonEle.getAsJsonObject());
						} else {
							// Bagya - Response json object chaining code - else
							if (jsonElement.getAsString().contains("boolean")) {
								boolean booleanValue = Boolean.parseBoolean(value.toString());
								requestBody.addProperty(tokenName, booleanValue);
							} else if (jsonElement.getAsString().contains("integer")) {
								if (value.toString().length() < 8) {
									int integerValue = Integer.parseInt(value.toString());
									requestBody.addProperty(tokenName, integerValue);
								} else {
									double doubleValue = Double.parseDouble(value.toString());
									requestBody.addProperty(tokenName, doubleValue);
								}
							} else {
								if (value instanceof Double) {
									Double integerValue = (Double) value;
									requestBody.addProperty(tokenName, integerValue);
								} else if (value instanceof String) {
									String stringValue = (String) value;
									/*
									 * if(RestAPIInvokerService.varElementsMap.
									 * containsKey(value)){//banu stringValue =
									 * RestAPIInvokerService.varElementsMap.get(
									 * stringValue).toString(); }
									 */
									requestBody.addProperty(tokenName, stringValue);
								}
							}
						}
					}
				} else if (jsonElement.isJsonObject()) {
					String subTokenName = tokenName.substring((tokenName.indexOf("_") + 1), tokenName.length());
					JsonObject jsonObj = jsonElement.getAsJsonObject();
					buildRequestBody(jsonObj, paramName, subTokenName, requestBodyParam);
				} else if (jsonElement.isJsonArray()) {
					int indexOfFirstDot = tokenName.indexOf("_");
					int indexOfSecondDot = tokenName.indexOf("_", (indexOfFirstDot + 1));
					int arrayIndex = 0;// banu
					String subTokenName = "";
					if (indexOfSecondDot > -1) {// banu
						arrayIndex = Integer.valueOf(tokenName.substring((indexOfFirstDot + 1), indexOfSecondDot))
								.intValue();
						subTokenName = tokenName.substring((indexOfSecondDot + 1), tokenName.length());
					} else {
						arrayIndex = Integer.valueOf(tokenName.substring((indexOfFirstDot + 1), tokenName.length()))
								.intValue();
						subTokenName = tokenName.substring(0, (indexOfFirstDot));// BANU
					}
					// int arrayIndex =
					// Integer.valueOf(tokenName.substring((indexOfFirstDot+1),
					// indexOfSecondDot)).intValue();
					JsonArray jsonArray = jsonElement.getAsJsonArray();
					// String subTokenName =
					// tokenName.substring((indexOfSecondDot+1),
					// tokenName.length());
					// JSON Array of string - starts
					if (!jsonArray.get(0).isJsonObject() && !jsonArray.get(0).isJsonArray()) {
						if (requestBodyParam.containsKey(paramName)) {
							Object value = requestBodyParam.get(paramName);
							if (value != null) {
								String[] keyName = paramName.split("_");
								// String finalKey= keyName.length > 3 ?
								// keyName[1] :keyName[0];
								String finalKey = keyName[keyName.length - 3];
								if (requestBody.get(finalKey) != null) {
									if (requestBody.get(finalKey).getAsJsonArray().toString().contains("string")
											|| requestBody.get(finalKey).getAsJsonArray().toString()
													.contains("integer")) {
										arrayStrja = new JsonArray();
										if (requestBody.get(finalKey).getAsJsonArray().toString().contains("string")) {
											arrayTypeKey = "string";
										} else if (requestBody.get(finalKey).getAsJsonArray().toString()
												.contains("integer")) {
											arrayTypeKey = "integer";
										} else if (requestBody.get(finalKey).getAsJsonArray().toString()
												.contains("double")) {
											arrayTypeKey = "double";
										}
									}
									if (arrayTypeKey.contains("string"))
										arrayStrja.add((String) value);
									else if (arrayTypeKey.contains("integer")) {
										if (value.toString().length() < 8) {
											arrayStrja.add(Integer.parseInt((String) value));
										} else {
											arrayStrja.add(Double.parseDouble(value.toString()));
										}
									}
									requestBody.add(finalKey, arrayStrja);
								}
							}
						}
					}

					// JSON array of string ends
					else {
						if (jsonArray.size() > arrayIndex) {
							JsonObject jsonObj = jsonArray.get(arrayIndex).getAsJsonObject();
							buildRequestBody(jsonObj, paramName, subTokenName, requestBodyParam);
						} else {
							JsonObject jsonObj = new JsonObject();
							buildRequestBody(jsonObj, paramName, subTokenName, requestBodyParam);
							// verifying weather jsonArray has key present for
							// the JSONObject instance.
							if (jsonArray.size() > 0 && jsonArray.toString().contains(jsonObj.keySet().toString()
									.substring(1, jsonObj.keySet().toString().length() - 1))) {
								// jsonArray.add(arrayIndex);
								jsonArray.add(jsonObj);
							} else {
								jsonObj.keySet().forEach(key -> {
									jsonArray.add(jsonObj.get(key));
								});
							}

							/*
							 * if(jsonArray instanceof JsonArray){//banu bgn for
							 * arrays [] jsonObj.keySet().forEach(key -> {
							 * jsonArray.add(jsonObj.get(key)); }); }
							 * else{//banu end jsonArray.add(jsonObj); }
							 */
						}
					}
				}

				// Bagya- To handle key with _ starts
				if (requestflag) {
					if (requestBody.get(jsonElementName).getAsString().equals("")) {
						requestBody.remove(jsonElementName);
					}
				}
				// Bagya- To handle key with _ ends
			}
		} catch (Exception ex) {
			LOGGER.info("Exception During Recursive BuildRequestBody :: {}", ex.getMessage());
		}
	}

	private static JsonObject removeUnusedProperty(JsonObject requestJsonObj) {
		Set<Entry<String, JsonElement>> jsonObjEntrySet = requestJsonObj.entrySet();
		JsonObject copy = requestJsonObj.deepCopy();
		for (Map.Entry<String, JsonElement> entry : jsonObjEntrySet) {
			JsonElement reqElement = entry.getValue();
			if (reqElement.isJsonPrimitive()) {
				String jsonValue = reqElement.getAsString();
				if (jsonValue != null && ("string".equalsIgnoreCase(jsonValue) || "integer".equalsIgnoreCase(jsonValue)
						|| "boolean".equalsIgnoreCase(jsonValue) || "object".equalsIgnoreCase(jsonValue))) {// banu
					copy.add(entry.getKey(), JsonNull.INSTANCE);
				}
			} else if (reqElement.isJsonObject()) {
				JsonObject jsonObj = reqElement.getAsJsonObject();
				copy.add(entry.getKey(), removeUnusedProperty(jsonObj));
			} else if (reqElement.isJsonArray()) {
				JsonArray jsonArr = reqElement.getAsJsonArray();
				JsonArray newJsonArr = new JsonArray();
				// for(int i=jsonArr.size()-1;i>=0;i--) {
				if (null != jsonArr && !jsonArr.isJsonNull() && jsonArr.size() > 0) {
					if (!jsonArr.get(0).isJsonObject() && !jsonArr.get(0).isJsonArray()) {
						if (jsonArr.get(0) != null && ("string".equalsIgnoreCase(jsonArr.get(0).getAsString())
								|| "integer".equalsIgnoreCase(jsonArr.get(0).getAsString())
								|| "boolean".equalsIgnoreCase(jsonArr.get(0).getAsString()))) {
							copy.add(entry.getKey(), JsonNull.INSTANCE);
						}
					} else {
						for (int i = 0; i < jsonArr.size(); i++) {
							if (jsonArr.get(i) instanceof JsonObject) {
								JsonObject jsonObj = (JsonObject) jsonArr.get(i);
								newJsonArr.add(removeUnusedArrayProperty(jsonObj));
							}
						}
						copy.add(entry.getKey(), newJsonArr);
					}
				}
			}
		}
		return copy;
	}

	private static JsonObject removeUnusedArrayProperty(JsonObject requestJsonObj) {
		Set<Entry<String, JsonElement>> jsonObjEntrySet = requestJsonObj.entrySet();
		JsonObject copy = requestJsonObj.deepCopy();
		for (Map.Entry<String, JsonElement> entry : jsonObjEntrySet) {
			JsonElement reqElement = entry.getValue();
			if (reqElement.isJsonPrimitive()) {
				String jsonValue = reqElement.getAsString();
				if (jsonValue != null && ("string".equalsIgnoreCase(jsonValue) || "integer".equalsIgnoreCase(jsonValue)
						|| "boolean".equalsIgnoreCase(jsonValue))) {// banu
					copy.add(entry.getKey(), JsonNull.INSTANCE);
				}
			} else if (reqElement.isJsonObject()) {
				JsonObject jsonObj = reqElement.getAsJsonObject();
				copy.add(entry.getKey(), removeUnusedArrayProperty(jsonObj));
			}
			// else if (reqElement.isJsonArray()) {
			// JsonArray jsonArr = reqElement.getAsJsonArray();
			// JsonArray newJsonArr = new JsonArray();
			// // for(int i=jsonArr.size()-1;i>=0;i--) {
			// if (null !=jsonArr && !jsonArr.isJsonNull() && jsonArr.size() >
			// 0){
			// if (!jsonArr.get(0).isJsonObject() &&
			// !jsonArr.get(0).isJsonArray()) {
			// if (jsonArr.get(0) != null &&
			// ("string".equalsIgnoreCase(jsonArr.get(0).getAsString())
			// || "integer".equalsIgnoreCase(jsonArr.get(0).getAsString())
			// || "boolean".equalsIgnoreCase(jsonArr.get(0).getAsString()))) {
			// copy.add(entry.getKey(), JsonNull.INSTANCE);
			// }
			// } else {
			// for (int i = 0; i < jsonArr.size(); i++) {
			// if (jsonArr.get(i) instanceof JsonObject) {
			// JsonObject jsonObj = (JsonObject) jsonArr.get(i);
			// newJsonArr.add(removeUnusedArrayProperty(jsonObj));
			// }
			// }
			// copy.add(entry.getKey(), newJsonArr);
			// }
			// }
			// }
		}
		return copy;
	}

	private static JsonObject removeNullProperty(JsonObject requestJsonObj) {
		////System.out.println(requestJsonObj);
		Gson gson = new GsonBuilder().create();
		String s = gson.toJson(requestJsonObj);
		JsonObject jsonObject = new JsonParser().parse(s).getAsJsonObject();
		Set<Entry<String, JsonElement>> jsonObjEntrySet = jsonObject.entrySet();
		JsonObject copy = requestJsonObj.deepCopy();

		for (Map.Entry<String, JsonElement> entry : jsonObjEntrySet) {
			JsonElement reqElement = entry.getValue();
			if (reqElement.isJsonArray()) {
				JsonArray jsonArr = reqElement.getAsJsonArray();
				// ////System.out.println(jsonArr);
				for (int i = 0; i < jsonArr.size(); i++) {
					if (jsonArr.get(i).isJsonObject()) {
						for (int j = 0; j < jsonArr.get(i).getAsJsonObject().size(); j++) {
							Set<Entry<String, JsonElement>> jsonObjEntrySet1 = jsonArr.get(i).getAsJsonObject()
									.entrySet();
							for (Map.Entry<String, JsonElement> entry1 : jsonObjEntrySet1) {
								JsonElement reqElement1 = entry1.getValue();
								if (reqElement1.isJsonObject()) {
									if (reqElement1.getAsJsonObject().size() == 0)
										copy.add(entry.getKey(), JsonNull.INSTANCE);
								}

							}
						}
					}
				}
			} else if(reqElement.isJsonObject()){
				//////System.out.println(">>>"+reqElement);
				if (reqElement.getAsJsonObject().size() == 0)
					copy.add(entry.getKey(), JsonNull.INSTANCE);
			}
		}
		return copy;
	}
}
