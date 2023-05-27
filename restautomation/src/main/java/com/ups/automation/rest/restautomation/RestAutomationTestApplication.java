package com.ups.automation.rest.restautomation;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import com.ups.automation.rest.restautomation.bean.APISheetBean;
import com.ups.automation.rest.restautomation.bean.GlobalSheetBean;
import com.ups.automation.rest.restautomation.jwtsecurity.JWTSecurityUtility;
import com.ups.automation.rest.restautomation.service.ExcelReadService;
import com.ups.automation.rest.restautomation.service.ExcelUpdaterService;
import com.ups.automation.rest.restautomation.service.MailService;
import com.ups.automation.rest.restautomation.service.QCService;
import com.ups.automation.rest.restautomation.service.ReporterService;
import com.ups.automation.rest.restautomation.service.RestAPIInvokerService;
import com.ups.automation.rest.restautomation.service.TFSService;
import com.ups.automation.rest.restautomation.service.TestDriverJsonBuilderService;

public class RestAutomationTestApplication {
	private static ResourceBundle configProp = ResourceBundle.getBundle("application");
	public static Map<String, String> globalDynaMap = new HashMap<>();

	public static ExcelReadService excelReadService = new ExcelReadService();
	public static ExcelUpdaterService excelUpdaterService = new ExcelUpdaterService();
	public static RestAPIInvokerService restAPIInvokerService = new RestAPIInvokerService();

	public static TestDriverJsonBuilderService testDriverJsonBuilderService;

	public static MailService mailService=new MailService();

	public static QCService qcService=new QCService();

	public static TFSService tfsService=new TFSService();

	public static String globalAutoJwtToken;
	private static Timer timer;

	private static int totalAPICount;
	private static int jwtTokenCount;
	static ProcessBuilder builder;

	public static void main(String[] args) {
		try {
			// String templatePath = "D:\\PDRjsonfile\\PDR.xls";
			if (args.length == 0 || (args.length > 0 && args[0] == null) || (args[0] != null && args[0].isEmpty())) {
				System.out.println("Please enter a valid XLS Test Template path.");
				return;
			}
			System.out.println("Test Case Execution In Progress........");
			String templatePath = args[0];
			String reportsPath = null;
			// Code to start the node server in port 5010 for script execution - starts
			if (configProp.getString("RequestScriptExecution").equalsIgnoreCase("Yes")) {
				Path resourceDirectory = Paths. get("src","main","resources");
				String scriptDir=resourceDirectory+"//requestScriptExec";
				builder = new ProcessBuilder(
			            "cmd.exe", "/c", "cd "+scriptDir+" && npm start");
			        builder.redirectErrorStream(true);
			        builder.start();
			        System.out.println("Pre-RequestScriptExecution started");
			}
			//Code to start the node server in port 5010 for script execution - ends
			Map<String, Object> excelApiData = excelReadService.readExcel(templatePath);
			// Bagya - For generating global Jwt Token changes -starts
			if (configProp.getString("GlobalJwtTokenRequired").equalsIgnoreCase("TRUE")) {
				jwtTokenCount = jwtTokenCount + 1;
				globalAutoJwtToken = JWTSecurityUtility.getJwtToken(configProp.getString("JwtClientId"),
						configProp.getString("JwtClientSecret"), configProp.getString("JwtAudClaim"),
						configProp.getString("JwtUrl"));

				// System.out.println("Generated Global JWT Token");

				TimerTask task = new TimerTask() {
					@Override
					public void run() {
						jwtTokenCount = jwtTokenCount + 1;
						String globalJwtTokenNew = JWTSecurityUtility.getJwtToken(configProp.getString("JwtClientId"),
								configProp.getString("JwtClientSecret"), configProp.getString("JwtAudClaim"),
								configProp.getString("JwtUrl"));
						globalAutoJwtToken = globalJwtTokenNew;
						// System.out.println("Regenerated Global JWT Token");
					}
				};
				timer = new Timer();
				long delay = Integer.parseInt(configProp.getString("JwtTokenTimer")) * 1000;
				long intervalPeriod = Integer.parseInt(configProp.getString("JwtTokenTimer")) * 1000;
				timer.scheduleAtFixedRate(task, delay, intervalPeriod);
			}

			// Bagya - For generating global Jwt Token changes -ends
			restAPIInvokerService.invokeRestApi(excelApiData);
			if (timer != null) {
				timer.cancel();
				timer.purge();
			}
			
			reportsPath = excelUpdaterService.reportPathGeneration(excelApiData);
			// if(!configProp.getString("HandleBulkData").equalsIgnoreCase("yes")){
			new RestAutomationTestApplication().updateHTMLReport(reportsPath, excelApiData);
			// }
			if (configProp.getString("tfsBugLogging").equalsIgnoreCase("Yes") || configProp.getString("tfsStatusUpdate").equalsIgnoreCase("Yes")) {
				tfsService.testDetailsUpdation(excelApiData, reportsPath);				
			}
			excelUpdaterService.updateExcel(excelApiData,reportsPath);
			// QC Updation - starts- Bagya
			if (configProp.getString("QC_Update").equalsIgnoreCase("Yes")) {
				qcService.updateQCTestStatus(excelApiData, reportsPath);
			}
			// QC Updation - ends - Bagya
			// Bagya- Added mail service to send the summary report
			if (configProp.getString("MailReportRequired").equalsIgnoreCase("Yes")) {
				totalAPICount = mailService.sendEmail(excelApiData, reportsPath);
			}
			if (configProp.getString("GlobalJwtTokenRequired").equalsIgnoreCase("TRUE")) {
				totalAPICount = mailService.getTotalCount(excelApiData);
				System.out.println(
						"Jwt Token Generated::" + jwtTokenCount + " times to execute " + totalAPICount + " APIs");
			}
			//clearing the jwt-token file contents
			File file = new File("jwt-token.ser");
			try {
				PrintWriter writer = new PrintWriter(file);
				writer.print("");
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (configProp.getString("RequestScriptExecution").equalsIgnoreCase("Yes")) {			
				builder = new ProcessBuilder(
			            "cmd.exe", "/c", "taskkill /f /im node.exe");
			        builder.redirectErrorStream(true);
			        builder.start();
			        System.out.println("Pre-RequestScriptExecution stopped");
			}
			System.out.println("Test Case Execution Completed. Please check log file for more details.");
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
		return;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void updateHTMLReport(String reportsPath, Map<String, Object> excelApiData) {
		ReporterService r = new ReporterService(reportsPath + "\\finalReport.html");
		for (Entry e : excelApiData.entrySet()) {
			String g = e.getKey().toString();
			// System.out.println(g);
			if (g == "globalSheetList") {
				List l = (List) e.getValue();
				for (int j = 0; j < l.size(); j++) {
					ReporterService rsJsonDiff = null;
					String apiName = ((GlobalSheetBean) l.get(j)).getOperationName().toString();
					String produces = ((GlobalSheetBean) l.get(j)).getProduces().toString();
					boolean reqKey = false;
					// System.out.println("API Under Test " + apiName);
					ReporterService childreport = new ReporterService(
							reportsPath + "\\" + apiName + "\\" + apiName + ".html");
					if (null != ((GlobalSheetBean) l.get(j)).getCompareExistingTestResult()
							&& ((GlobalSheetBean) l.get(j)).getCompareExistingTestResult().equalsIgnoreCase("Y"))
						rsJsonDiff = new ReporterService(reportsPath + "\\htmlReportsJSONDIff\\" + apiName + ".html");
					List globals = ((GlobalSheetBean) l.get(j)).getApiSheetList();
					String exists = ((GlobalSheetBean) l.get(j)).getCompareExistingTestResult();
					List<APISheetBean> as = (List<APISheetBean>) globals;
					// System.out.println("No of Testcases : " + as.size());
					r.createTest(apiName);
					for (int i = 0; i <= as.size() - 1; i++) {
						String status;
						String actual;
						String elements;
						String expected;
						String detail;
						String tcname;
						String response;
						// String execute;
						String sendType;
						String apiRequest;
						String restUrl;

						Map<String, Object> header;

						{
							if (as.get(i).getExecute() != null && as.get(i).getExecute().equals("Y")) {
								if (as.get(i).getStatus() != null)
									status = as.get(i).getStatus().toString();
								else
									status = "";
								if (as.get(i).getActual() != null)
									actual = as.get(i).getActual().toString();
								else
									actual = "";
								// System.out.println("***"+i+"&&&&&"+actual);
								if (as.get(i).getElements() != null)
									elements = as.get(i).getElements().toString();
								else
									elements = "";
								if (as.get(i).getExpected() != null)
									expected = as.get(i).getExpected().toString();
								else
									expected = "";
								if (as.get(i).getStatusDetail() != null)
									detail = as.get(i).getStatusDetail().toString();
								else
									detail = "";
								if (as.get(i).getApiResponse() != null)
									response = as.get(i).getApiResponse().asString();
								else
									response = "";

								if (as.get(i).getTcid() != null)
									tcname = as.get(i).getTcid().toString();
								else
									tcname = "";
								// ~~~Bheem~~~~ addtional details in the html
								// reports
								if (as.get(i).getSendType() != null)
									sendType = as.get(i).getSendType().toString();
								else
									sendType = "";
								if (as.get(i).getApiRequest() != null) {
									if (as.get(i).getApiRequest().getAsJsonObject().get("arrayKey") != null) {
										apiRequest = as.get(i).getApiRequest().getAsJsonObject().get("arrayKey")
												.toString();
										reqKey = true;
									} else {
										apiRequest = as.get(i).getApiRequest().toString();
									}
								} else
									apiRequest = "N/A";
								if (as.get(i).getRestUrl() != null)
									restUrl = as.get(i).getRestUrl().toString();
								else
									restUrl = "";
								if (as.get(i).getHeaderParam() != null)
									header = as.get(i).getHeaderParam();
								else
									header = null;

								childreport.createChildTest(as.get(i).getTcid().toString());
								childreport.childReportTest(status, tcname, detail, sendType, actual, elements,
										expected, restUrl, apiRequest, response, header, produces, reqKey);

								String testcname = "";
								if (as.get(i).getTcName() != null)
									testcname = as.get(i).getTcid().toString() + "_" + as.get(i).getTcName().toString();
								else
									testcname = as.get(i).getTcid().toString();

								r.reportTest(status, testcname, detail, sendType, actual, elements, expected, restUrl,
										apiRequest, response, header, produces, reqKey);

								/*
								 * r.createTest(as.get(i).getTcid().toString());
								 * r.reportTest(as.get(i).getStatus().toString()
								 * , as.get(i).getTcid().toString(),
								 * as.get(i).getStatusDetail().toString(),
								 * as.get(i).getActual().toString(),
								 * as.get(i).getExpected().toString(), "NA",
								 * as.get(i).getApiResponse().asString());
								 */
								// HTML reports for JSON Diff
								if (rsJsonDiff != null) {
									if ((((as.get(i).getExecute() != null && as.get(i).getExecute().equals("Y")
											&& as.get(i).getRestURLBaseEnv() != null))
											|| ((as.get(i).getExecute() != null && as.get(i).getExecute().equals("Y")
													&& globals.get(i).getClass() != null)))
											|| ((as.get(i).getExecute() != null && as.get(i).getExecute().equals("Y")
													&& exists.equals("Y"))
													|| ((as.get(i).getExecute() != null
															&& as.get(i).getExecute().equals("Y"))
															&& (globals.get(i).getClass() != null)))) {
										String targetRestURL;
										String baseRestURL;
										String responseBaseEnv;
										Map<String, Map<String, Object>> enteriesDiffBothJson;

										if (as.get(i).getRestUrl() != null)
											targetRestURL = as.get(i).getRestUrl().toString();
										else
											targetRestURL = "";
										if (as.get(i).getRestURLBaseEnv() != null)
											baseRestURL = as.get(i).getRestURLBaseEnv().toString();
										else
											baseRestURL = "";
										if (as.get(i).getApiResponseBaseEnv() != null)
											responseBaseEnv = as.get(i).getApiResponseBaseEnv().asString();
										else
											responseBaseEnv = "";
										if (as.get(i).getJsonDifferences() != null)
											enteriesDiffBothJson = as.get(i).getJsonDifferences();
										else
											enteriesDiffBothJson = null;
										if (null != enteriesDiffBothJson) {
											if (!(enteriesDiffBothJson.get("EnteriesOnTarget").size() < 1
													&& enteriesDiffBothJson.get("EnteriesOnBase").size() < 1
													&& enteriesDiffBothJson.get("EnteriesJSONDiff").size() < 1)) {
												rsJsonDiff.createTest(as.get(i).getTcid().toString());
												rsJsonDiff.reportTestDiffJSON(tcname, sendType, header, targetRestURL,
														baseRestURL, apiRequest, response, responseBaseEnv,
														enteriesDiffBothJson);
											}
										}
									}
								} // end
							}
						}
					}
					r.flush();
				}
			}
		}
		if (configProp.getString("REPORT_POPUP").equalsIgnoreCase("yes")) {
			File htmlFile = new File(reportsPath + "\\htmlReports\\finalReport.html");
			try {
				if (htmlFile.isFile())
					Desktop.getDesktop().browse(htmlFile.toURI());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

}
