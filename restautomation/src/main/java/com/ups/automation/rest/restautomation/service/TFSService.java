package com.ups.automation.rest.restautomation.service;

import static io.restassured.RestAssured.given;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ResourceBundle;
import org.apache.http.client.ClientProtocolException;
import org.apache.xmlbeans.impl.util.Base64;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ups.automation.rest.restautomation.bean.APISheetBean;
import com.ups.automation.rest.restautomation.bean.GlobalSheetBean;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.internal.RequestSpecificationImpl;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class TFSService {
	private static ResourceBundle configProp = ResourceBundle.getBundle("application");
	private static String PAT = configProp.getString("tfsUserToken");
	private static String AuthStr = ":" + PAT;
	private static Base64 base64 = new Base64();
	private static String encodedPAT = new String(base64.encode(AuthStr.getBytes()));
	private static String defaultReproSteps = "";
	private String finalReproSteps = "";
	private String finalBugtitle = "";
	private String parentWorkItem = "";

	public void bugCreation(Map<String, Object> excelApiData, GlobalSheetBean globalSheet, String reportpath,
			String OperationName, String WorkItem, String AssignedTo, String BugTitle, String ReproSteps,
			String apiBugTitle, String elements, String expected, String actual)
			throws MalformedURLException, IOException {
		Response response = null;
		RequestSpecification requestSpecification = given();
		RequestSpecificationImpl requestSpecificationImpl = (RequestSpecificationImpl) requestSpecification;
		requestSpecificationImpl.contentType("application/json-patch+json");
		requestSpecificationImpl.headers("Authorization", "Basic " + encodedPAT);
		String defaultBugTitle = "Fix issue in API-" + OperationName + " fails to " + apiBugTitle;
		defaultReproSteps = "<div><span style=font-weight:600;>Scenerio:</span></div><div>" + apiBugTitle
				+ "</div><div><span style=><br></span></div><div><span style=font-weight:600;>Elements:</span></div><div>"
				+ elements + "</div><div><br></div><div><span style=font-weight:600;>Expected Result:</span></div><div>"
				+ expected
				+ "</div><div><span style=font-weight:600;><br></span></div><div><span style=font-weight:600;>Actual Result:</span></div><div>"
				+ actual
				+ "</div><div><br></div><div><span style=font-weight:600;>Attached the test html report which comprises all the above details</span></div>";
		JsonParser jsonArrayParser = new JsonParser();
		TFSService s = new TFSService();
		String attachmentURL = s.bugCreationWithAttachment(globalSheet, reportpath, OperationName);
		if (null != BugTitle && !BugTitle.equalsIgnoreCase(""))
			finalBugtitle = BugTitle;
		else
			finalBugtitle = defaultBugTitle;

		if (null != ReproSteps && !ReproSteps.equalsIgnoreCase(""))
			finalReproSteps = ReproSteps;
		else
			finalReproSteps = defaultReproSteps;
		if (null != WorkItem && !WorkItem.equalsIgnoreCase(""))
			parentWorkItem = "{" + "\"op\":\"add\"," + "\"path\": \"/relations/-\"," + "\"value\": {"
					+ "\"rel\":\"System.LinkTypes.Hierarchy-Reverse\"," + "\"url\": \"https://tfs.ups.com/tfs/UpsProd/"
					+ configProp.getString("tfsCollection") + "/_apis/wit/workitems/" + WorkItem + "\","
					+ "\"attributes\": {" + "\"comment\": \"Auto-Generated Bug\"" + "}" + "}" + "},";

		String jsonPayload = "[" + "{" + "\"op\":\"add\"," + "\"path\":\"/fields/System.Title\"," + "\"value\":\""
				+ finalBugtitle + "\"" + "}," + "{" + "\"op\":\"add\"," + "\"path\":\"/fields/System.AssignedTo\","
				+ "\"value\":\"" + AssignedTo + "\"" + "}," + "{" + "\"op\":\"add\","
				+ "\"path\":\"/fields/Microsoft.VSTS.TCM.ReproSteps\"," + "\"value\":\"" + finalReproSteps + "\"" + "},"
				+ "{" + "\"op\":\"add\"," + "\"path\":\"/fields/System.AreaPath\"," + "\"value\":\""
				+ configProp.getString("tfsAreaPath") + "\"" + "}," + "{" + "\"op\":\"add\","
				+ "\"path\":\"/fields/System.IterationPath\"," + "\"value\":\""
				+ configProp.getString("tfsIterationPath") + "\"" + "}," + parentWorkItem + "{" + "\"op\":\"add\","
				+ "\"path\": \"/relations/-\"," + "\"value\": {" + "\"rel\":\"AttachedFile\"," + "\"url\": \""
				+ attachmentURL + "\"," + "\"attributes\": {" + "\"comment\": \"API " + OperationName
				+ " Test HTML Report\"" + "}" + "}" + "}]";
		// System.out.println(jsonPayload);
		JsonElement isJsonElement = jsonArrayParser.parse(jsonPayload);
		JsonArray json = isJsonElement.getAsJsonArray();
		System.out.println(json);
		requestSpecificationImpl.body(json);
		// String url = "https://" + configProp.getString("tfsHost") +
		// "/tfs/UpsProd/"
		// + configProp.getString("tfsCollection") +
		// "/_apis/wit/workitems/$bug?api-version=1.0";
		String url = "https://" + configProp.getString("tfsHost") + "/tfs/UpsProd/"
				+ configProp.getString("tfsCollection") + "/_apis/wit/workitems/2280252?api-version=1.0";
		response = requestSpecificationImpl.urlEncodingEnabled(false).when().patch(url);
		System.out.println(response.statusCode());
		if (response.statusCode() == 200) {
			System.out.println(response.getBody().asString());
			if (null != response.getBody().jsonPath().getString("id")) {
				String BugID = response.getBody().jsonPath().getString("id");
				System.out.println("Bug Created::" + BugID);
				globalSheet.setBugStatus("Success");
				globalSheet.setBugId(BugID);
			} else {
				globalSheet.setBugStatus("Failed");
				globalSheet.setBugId("");
			}
		} else {
			globalSheet.setBugStatus("Failed");
			globalSheet.setBugId("");
		}
	}

	public String bugCreationWithAttachment(GlobalSheetBean globalSheet, String reportpath, String OperationName)
			throws MalformedURLException, IOException {
		String imageURL = "";
		Response response = null;
		RequestSpecification requestSpecification = given();
		RequestSpecificationImpl requestSpecificationImpl = (RequestSpecificationImpl) requestSpecification;
		requestSpecificationImpl.contentType("application/octet-stream");
		requestSpecificationImpl.headers("Authorization", "Basic " + encodedPAT);
		// System.out.println(reportpath+"\\"+OperationName+"\\"+OperationName+".html");
		String htmlPath = reportpath + "\\" + OperationName + "\\" + OperationName + ".html";
		StringBuilder contentBuilder = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new FileReader(htmlPath));
			String str;
			while ((str = in.readLine()) != null) {
				contentBuilder.append(str);
			}
			in.close();
		} catch (IOException e) {
		}
		String content = contentBuilder.toString();
		System.out.println(content);
		requestSpecificationImpl.body(content);
		requestSpecificationImpl.config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig()
				.encodeContentTypeAs("application/octet-stream", io.restassured.http.ContentType.TEXT)));
		String url = "https://" + configProp.getString("tfsHost") + "/tfs/UpsProd/"
				+ configProp.getString("tfsCollection") + "/_apis/wit/attachments?fileName=" + OperationName
				+ ".html&api-version=5.1";
		response = requestSpecificationImpl.urlEncodingEnabled(false).when().post(url);
		System.out.println(response.statusCode());
		if (response.statusCode() == 201) {
			System.out.println(response.getBody().asString());
			if (null != response.getBody().jsonPath().getString("id")) {
				imageURL = response.getBody().jsonPath().getString("url");
				System.out.println("Attachment Created::" + imageURL);
			} else {
				System.out.println("Attachment not generated for API::" + OperationName);
			}
		} else {
			System.out.println("Attachment not generated for API::" + OperationName);
		}
		return imageURL;
	}

	/**
	 * @param excelApiData
	 * @param reportpath
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@SuppressWarnings("unchecked")
	public void testDetailsUpdation(Map<String, Object> excelApiData, String reportpath)
			throws ClientProtocolException, IOException, URISyntaxException {
		try {
			System.out.println("TFS Auto Logging Functionalities starts..");
			List<GlobalSheetBean> globalSheetBeanList = (List<GlobalSheetBean>) excelApiData.get("globalSheetList");
			if (globalSheetBeanList != null && !globalSheetBeanList.isEmpty()) {
				for (GlobalSheetBean globalSheet : globalSheetBeanList) {
					try {
						if ("Y".equals(globalSheet.getExecute())) {
							List<APISheetBean> apiSheetList = globalSheet.getApiSheetList();
							if (null != globalSheet.getStatus() && null != globalSheet.getCreateBug()
									&& null != globalSheet.getExecute() && "Y".equals(globalSheet.getCreateBug())
									&& "Y".equals(globalSheet.getExecute())
									&& globalSheet.getStatus().equalsIgnoreCase("fail")) {
								// System.out.println(globalSheet.getOperationName()+">>"+globalSheet.getBugAssignedTo()+">>"+globalSheet.getBugWorItem());
								TFSService service = new TFSService();
								try {
									for (APISheetBean apiSheet : apiSheetList) {
										if (null != apiSheet.getStatus() && "Y".equals(apiSheet.getExecute())
												&& "Fail".equals(apiSheet.getStatus())) {
											service.bugCreation(excelApiData, globalSheet, reportpath,
													globalSheet.getOperationName(), globalSheet.getBugWorItem(),
													globalSheet.getBugAssignedTo(), globalSheet.getBugTitle(),
													globalSheet.getReproSteps(), apiSheet.getTcName(),
													apiSheet.getElements(), apiSheet.getExpected(),
													apiSheet.getActual());
										}
									}
								} catch (MalformedURLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							if (null != globalSheet.getStatus() && null != globalSheet.getExecute()
									&& "Y".equals(globalSheet.getExecute())) {
								TFSService service = new TFSService();
								try {
									for (APISheetBean apiSheet : apiSheetList) {
										if (null != apiSheet.getStatus() && null != apiSheet.getTfsUpdate()
												&& "Y".equals(apiSheet.getExecute())
												&& "Y".equals(apiSheet.getTfsUpdate())) {
											service.statusUpdate(globalSheet.getOperationName(), apiSheet.getTcid(),
													apiSheet.getStatus());
										}
									}
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					} catch (Exception ex) {
						System.out.println("Error in logging details in TFS");
						ex.printStackTrace();
					}
				}
			}
			System.out.println("TFS Auto Logging Functionalities ends..");
		} catch (Exception e) {
			System.out.println("Error in logging details in TFS");
			e.printStackTrace();
		}
	}

	private void statusUpdate(String OperationName, String tcid, String status) {
		String pointId = "";
		String runId = "";
		Response response = null;
		String finalStatus = "";
		if (status.equalsIgnoreCase("Pass"))
			finalStatus = "Passed";
		else if (status.equalsIgnoreCase("Fail"))
			finalStatus = "Failed";
		RequestSpecification requestSpecification = given();
		RequestSpecificationImpl requestSpecificationImpl = (RequestSpecificationImpl) requestSpecification;
		requestSpecificationImpl.contentType("application/json");
		requestSpecificationImpl.headers("Authorization", "Basic " + encodedPAT);
		String url = "https://" + configProp.getString("tfsHost") + "/tfs/UpsProd/"
				+ configProp.getString("tfsCollection") + "/_apis/test/Plans/" + configProp.getString("tfsPlanId")
				+ "/suites/" + configProp.getString("tfsSuiteId") + "/points?testCaseId=" + tcid + "&api-version=5.1";
		// System.out.println("PointId URL::"+url);
		response = requestSpecificationImpl.urlEncodingEnabled(false).when().get(url);
		// System.out.println(response.statusCode());
		if (response.statusCode() == 200) {
			// System.out.println(response.getBody().asString());
			if (null != response.getBody().jsonPath().getString("value[0].id")) {
				pointId = response.getBody().jsonPath().getString("value[0].id");
				//System.out.println("PointID::" + pointId);
				////////////
				Response response1 = null;
				RequestSpecification requestSpecification1 = given();
				RequestSpecificationImpl requestSpecificationImpl1 = (RequestSpecificationImpl) requestSpecification1;
				requestSpecificationImpl1.contentType("application/json");
				requestSpecificationImpl1.headers("Authorization", "Basic " + encodedPAT);
				String jsonPayload = "{\"name\":\"AutomationTest\",\"plan\":{\"id\":"
						+ configProp.getString("tfsPlanId") + "},\"pointIds\":[" + pointId
						+ "],\"state\":\"InProgress\"}";
				JsonParser jsonParser = new JsonParser();
				JsonElement isJsonElement = jsonParser.parse(jsonPayload);
				JsonObject json = isJsonElement.getAsJsonObject();
				// System.out.println(json);
				requestSpecificationImpl1.body(json);
				String url1 = "https://" + configProp.getString("tfsHost") + "/tfs/UpsProd/"
						+ configProp.getString("tfsCollection") + "/_apis/test/runs?api-version=5.1";
				// System.out.println("RunId URL::"+url1);
				response1 = requestSpecificationImpl1.urlEncodingEnabled(false).when().post(url1);
				// System.out.println(response1.statusCode());
				if (response1.statusCode() == 200) {
					// System.out.println(response1.getBody().asString());
					if (null != response1.getBody().jsonPath().getString("id")) {
						runId = response1.getBody().jsonPath().getString("id");
						// System.out.println("runId::" + runId);
						/////////////
						Response response2 = null;
						RequestSpecification requestSpecification2 = given();
						RequestSpecificationImpl requestSpecificationImpl2 = (RequestSpecificationImpl) requestSpecification2;
						requestSpecificationImpl2.contentType("application/json");
						requestSpecificationImpl2.headers("Authorization", "Basic " + encodedPAT);
						String jsonPayload1 = "[{\"id\":100000,\"state\":\"Completed\",\"outcome\":\"" + finalStatus
								+ "\"}]";
						JsonParser jsonParser1 = new JsonParser();
						JsonElement isJsonElement1 = jsonParser1.parse(jsonPayload1);
						JsonArray json1 = isJsonElement1.getAsJsonArray();
						// System.out.println(json1);
						requestSpecificationImpl2.body(json1);
						String url2 = "https://" + configProp.getString("tfsHost") + "/tfs/UpsProd/"
								+ configProp.getString("tfsCollection") + "/_apis/test/runs/" + runId
								+ "/results?api-version=5.1";
						// System.out.println("Status update URL::"+url2);
						response2 = requestSpecificationImpl2.urlEncodingEnabled(false).when().patch(url2);
						// System.out.println(response2.statusCode());
						if (response2.statusCode() == 200) {
							System.out.println(response2.getBody().asString());
						} else {
							System.out.println("Status not updated for tcid::" + tcid);
						}
						///////////////
					} else {
						System.out.println("RunId not generated for tcid::" + tcid);
					}
				} else {
					System.out.println("RunId not generated for tcid::" + tcid);
				}

				///////////
			} else {
				System.out.println("PointID not generated for tcid::" + tcid);
			}
		} else {
			System.out.println("PointID not generated for tcid::" + tcid);
		}
	}
}