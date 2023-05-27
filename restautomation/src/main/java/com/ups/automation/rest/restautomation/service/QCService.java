package com.ups.automation.rest.restautomation.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ups.automation.rest.restautomation.bean.APISheetBean;
import com.ups.automation.rest.restautomation.bean.GlobalSheetBean;

public class QCService {

	public static String base64encodedString = null;
	public static CloseableHttpClient client = null;
	public static HashMap<String, String> headersMap = new HashMap<String, String>();
	public static String tSetID = "";
	public static String tCaseID = "";
	public static String tStatus = "";
	private static ResourceBundle configProp = ResourceBundle.getBundle("application");


	public static void connectQC() throws ClientProtocolException, IOException {

		client = HttpClients.createDefault();
		HttpPost myPost = new HttpPost();
		URI uri;
		try {
			uri = new URI(configProp.getString("QC_Path").trim() + "/qcbin/api/authentication/sign-in");
			myPost.setURI(uri);
			base64encodedString = Base64.getEncoder().encodeToString(
					(configProp.getString("QC_Uname").trim() + ":" + configProp.getString("QC_Pwd").trim())
							.getBytes("utf-8"));
			myPost.addHeader("Authorization", "Basic " + base64encodedString);
			HttpResponse response = client.execute(myPost);
			Header[] headers = response.getAllHeaders();

			for (Header hdr : headers) {
				if (hdr.getName().equalsIgnoreCase("set-cookie")) {
					String val = hdr.getValue();
					String tokens[] = val.split("=");
					headersMap.put(tokens[0], tokens[1]);
				}
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public static void appendAttachments(String filepath, String testcaseIds) throws IOException {
		try {
			Path resourceDirectory = Paths.get("src", "main", "resources");
			//System.out.println(resourceDirectory);
			String vbScriptPath = "C:\\Windows\\SysWOW64\\wscript.exe " + resourceDirectory
					+ "\\vbScript_uploadAttachment.vbs" + " "+configProp.getString("QC_TestSetId").trim() + " "
					+ testcaseIds + " \"" + filepath + "\" " + configProp.getString("QC_Path").trim() + "/qcbin "
					+ configProp.getString("QC_Uname").trim() +" "+ configProp.getString("QC_Pwd").trim() + " "
					+ configProp.getString("QC_Domain").trim() + " " + configProp.getString("QC_Project").trim() + " "
					+ configProp.getString("QC_AttachToTestRun").trim();
			System.out.println(vbScriptPath);
			 Process qcUpdateProcess =Runtime.getRuntime().exec(vbScriptPath);
			 try {
				qcUpdateProcess.waitFor();
				System.out.println("QC Update for provided API is completed..");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			System.out.println(e);
		}
		// HttpPost myPost = new HttpPost();
		// URI uri;
		// try {
		// uri = new URI(configProp.getString("QC_Path").trim() +
		// "/qcbin/rest/domains/"
		// + configProp.getString("QC_Domain").trim() + "/projects/"
		// + configProp.getString("QC_Project").trim()
		// + "/test-sets/23587/attachments");
		// System.out.println(uri);
		// myPost.setURI(uri);
		// for (String key : headersMap.keySet()) {

		// myPost.addHeader(key, headersMap.get(key));
		// }
		// myPost.addHeader("Authorization", "Basic " + base64encodedString);
		// myPost.addHeader("Content-Type", "multipart/form-data;
		// boundary=upload");
		// myPost.addHeader("Accept","application/json");
		// File file = new File(filepath);
		// MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		// builder.setBoundary("upload");
		// builder.addBinaryBody(
		// "file",
		// new FileInputStream(file),
		// ContentType.MULTIPART_FORM_DATA,
		// file.getName()
		// );
		//
		// HttpEntity multipart = builder.build();
		// System.out.println(multipart.getContentType());
		// myPost.setEntity(multipart);
		// HttpResponse getResponse = client.execute(myPost);
		// BufferedReader br = new BufferedReader(new
		// InputStreamReader(getResponse.getEntity().getContent()));
		// String respStr = "";
		// String line = "";
		// while ((line = br.readLine()) != null) {
		// respStr += line;
		// }
		// System.out.println("Response" + respStr);
		// }
		// catch (UnsupportedEncodingException | URISyntaxException e) {
		// e.printStackTrace();
		// }
	}

	public static String getTestInstanceID(String testSetID_QC, String testCaseID)
			throws ClientProtocolException, IOException {
		String instanceID = null;
		String baseURI = configProp.getString("QC_Path").trim() + "/qcbin/rest/domains/"
				+ configProp.getString("QC_Domain").trim() + "/projects/" + configProp.getString("QC_Project").trim()
				+ "/test-instances?query=%7Bcycle-id[" + testSetID_QC + "];test-id[" + testCaseID
				+ "]%7D&fields=id,name,test-config-id";
		// System.out.println("Base URI::" + baseURI);
		HttpGet myGet = new HttpGet(baseURI);
		for (String key : headersMap.keySet()) {
			myGet.addHeader(key, headersMap.get(key));
		}
		myGet.addHeader("Accept", "application/json");
		myGet.addHeader("Authorization", "Basic " + base64encodedString);
		HttpResponse getResponse = client.execute(myGet);

		BufferedReader br = new BufferedReader(new InputStreamReader(getResponse.getEntity().getContent()));
		String respStr = "";
		String line = "";
		while ((line = br.readLine()) != null) {
			respStr += line;
		}
		// System.out.println("Response" + respStr);

		JsonParser jsonArrayParser = new JsonParser();
		JsonElement jsonElement = jsonArrayParser.parse(respStr);
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		if (null != ((JsonObject) ((JsonObject) ((JsonObject) jsonObject.get("entities").getAsJsonArray().get(0))
				.get("Fields").getAsJsonArray().get(2)).get("values").getAsJsonArray().get(0)).get("value"))
			instanceID = ((JsonObject) ((JsonObject) ((JsonObject) jsonObject.get("entities").getAsJsonArray().get(0))
					.get("Fields").getAsJsonArray().get(2)).get("values").getAsJsonArray().get(0)).get("value")
							.getAsString();
		return instanceID;
	}

	public static void updateTestStatus(String instanceID, String status, String testcaseID)
			throws ClientProtocolException, IOException, URISyntaxException {

		String putURL = configProp.getString("QC_Path").trim() + "/qcbin/rest/domains/"
				+ configProp.getString("QC_Domain").trim() + "/projects/" + configProp.getString("QC_Project").trim()
				+ "/test-instances/" + instanceID;
		// System.out.println(putURL);
		String payload = "{\"Fields\":[{\"Name\":\"status\",\"values\":[{\"value\":\"" + status
				+ "\"}]},{\"Name\":\"subtype-id\",\"values\":[{\"value\":\"hp.qc.test-instance.MANUAL\"}]}]}";
		//System.out.println(payload);

		HttpPut myPut = new HttpPut();
		myPut.setURI(new URI(putURL));
		HttpEntity myEntity = new StringEntity(payload);
		myPut.setEntity(myEntity);
		myPut.setHeader("Content-Type", "application/json");
		myPut.setHeader("Accept", "application/json");
		myPut.addHeader("Authorization", "Basic " + base64encodedString);
		for (String key : headersMap.keySet()) {
			myPut.addHeader(key, headersMap.get(key));
		}
		HttpResponse myPutResponse = client.execute(myPut);
		BufferedReader br1 = new BufferedReader(new InputStreamReader(myPutResponse.getEntity().getContent()));
		String ln;
		while ((ln = br1.readLine()) != null) {
			System.out.println(ln);
		}
		System.out.println("Updation of testcase ID::" + testcaseID + " Status code "
				+ myPutResponse.getStatusLine().getStatusCode());

	}

	@SuppressWarnings("unchecked")
	public void updateQCTestStatus(Map<String, Object> excelApiData, String reportpath)
			throws ClientProtocolException, IOException, URISyntaxException {
		try {
			System.out.println("QC Updation starts..");
			List<GlobalSheetBean> globalSheetBeanList = (List<GlobalSheetBean>) excelApiData.get("globalSheetList");
			if (globalSheetBeanList != null && !globalSheetBeanList.isEmpty()) {
				for (GlobalSheetBean globalSheet : globalSheetBeanList) {
					try {
						if ("Y".equals(globalSheet.getExecute())) {
							List<APISheetBean> apiSheetList = globalSheet.getApiSheetList();
							List<String> testcaseDetails = new ArrayList<>();
							for (APISheetBean apiSheet : apiSheetList) {
								if (null != apiSheet.getStatus() && null != apiSheet.getQcUpdate()
										&& "Y".equals(apiSheet.getExecute()) && "Y".equals(apiSheet.getQcUpdate())) {
									testcaseDetails.add(apiSheet.getTcid().concat(":").concat(qcTestStatus(apiSheet.getStatus())));
								}
							}
							if (configProp.getString("QC_UpdateWithAttachment").trim().equalsIgnoreCase("yes")) {
								String tclist = Arrays.toString(testcaseDetails.toArray()).replace("[", "").replace("]", "").replace(" ", "");
								System.out.println("Updating the status along with Attachment for API "+globalSheet.getOperationName()+" with Testcase Results::"+tclist);
								appendAttachments(reportpath + "\\" + globalSheet.getOperationName(),
										tclist);
							} else {
								for (int i = 0; i < testcaseDetails.size(); i++) {
									connectQC();
									String[] currentTcId = testcaseDetails.get(i).toString().split(":");
									tCaseID = currentTcId[0];
									tStatus = currentTcId[1];
									String instanceID = getTestInstanceID(configProp.getString("QC_TestSetId").trim(),
											tCaseID);
									if (null != instanceID) {
										System.out.println("API "+globalSheet.getOperationName()+" TestCaseID: " + tCaseID + " InstanceID : " + instanceID
												+ " ExecutionStatus: " + tStatus);
										updateTestStatus(instanceID, tStatus, tCaseID);
									} else {
										System.out.println("Error in getting Test Instance");
									}
								}
							}
						}
					} catch (Exception ex) {
						System.out.println("Error in updating the test status in QC");
						ex.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Error in updating the test status in QC");
			e.printStackTrace();
		}
	}
	
	public static String qcTestStatus(String testStatus) {

		String testQCStatus = null;
		switch (testStatus.toLowerCase()) {
		case "fail":
			testQCStatus = "Failed";
			break;
		case "pass":
			testQCStatus = "Passed";
			break;
		}
		return testQCStatus;
	}

}
