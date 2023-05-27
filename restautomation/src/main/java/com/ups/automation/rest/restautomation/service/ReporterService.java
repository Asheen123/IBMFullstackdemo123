package com.ups.automation.rest.restautomation.service;

import java.util.Map;
import java.util.Map.Entry;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ReporterService {
	ExtentReports extent;
	ExtentSparkReporter hr;
	ExtentTest logger;
	ExtentTest childlogger;

	public ReporterService(String filename) {
		extent = new ExtentReports();
		extent.setSystemInfo("Host Name", "SoftwareTestingMaterial");
		extent.setSystemInfo("Environment", "Automation Testing");
		extent.setSystemInfo("User Name", "GD Automation Team");
		hr = new ExtentSparkReporter(filename);
		extent.attachReporter(hr);
		//hr.loadXMLConfig(new File(System.getProperty("user.dir") + "\\extent-config.xml"), true);
		hr.config().setTheme(Theme.DARK);
		hr.config().setDocumentTitle("Automation Report");
		hr.config().setReportName("AUTOMATION TEST REPORT");
	}
	
	public void createChildTest(String tcname) {
		//System.out.println("tcname");
		childlogger = extent.createTest(tcname);
	}

	public void childReportTest(String status, String tcname, String reason,String sendType, String actual,String elements, String expected,String restURL, String req,
			String res, Map<String, Object> header,String produces,boolean reqKey) {
		if (status.equals("Pass")) {
			childlogger.log(Status.PASS, MarkupHelper.createLabel(sendType + " ---- " +tcname, ExtentColor.GREEN));
			//logger.log(Status.PASS, "Test Case => " + tcname);
		} else {
			childlogger.log(Status.FAIL, MarkupHelper.createLabel(sendType + " ---- " +tcname, ExtentColor.RED));
			//logger.log(Status.FAIL, "Test Case : " + tcname);
		}
		childlogger.info(MarkupHelper.createTable(resultsDetails(elements, actual, expected, reason,produces,reqKey)));
		
		
		ExtentTest responseDetails = childlogger.createNode("REST URL -> " + restURL );
		
		if(sendType.equals("GET")){
			//responseDetails.createNode("Headers").info(MarkupHelper.createTable(header));
			if(null != header){
			responseDetails.createNode("Headers").info(MarkupHelper.createCodeBlock(header.toString()));
			}
			responseDetails.createNode("Response").info(MarkupHelper.createCodeBlock(res, CodeLanguage.JSON));
		}else{
			if(null != header){
			responseDetails.createNode("Headers").info(MarkupHelper.createCodeBlock(header.toString()));
			}
			responseDetails.createNode("Request").info(MarkupHelper.createCodeBlock(req, CodeLanguage.JSON));
			responseDetails.createNode("Response").info(MarkupHelper.createCodeBlock(res, CodeLanguage.JSON));
		}
		
		extent.flush();
	}

	public void createTest(String tcname) {
		//System.out.println("tcname");
		logger = extent.createTest(tcname);
	}

	public void reportTest(String status, String tcname, String reason,String sendType, String actual,String elements, String expected,String restURL, String req,
			String res, Map<String, Object> header,String produces,boolean reqKey) {

		ExtentTest responseDetails = null;
		
		if (status.equals("Pass")) {
			responseDetails=logger.createNode("TestCase : "+tcname);
			responseDetails.log(Status.PASS,MarkupHelper.createLabel("REQUEST TYPE :: "+sendType,ExtentColor.GREEN));
		} else {
			responseDetails=logger.createNode("TestCase : "+tcname);
			responseDetails.log(Status.FAIL,MarkupHelper.createLabel("REQUEST TYPE :: "+sendType,ExtentColor.RED));
		}
		responseDetails.createNode("REST URL -> " + restURL);
		if(sendType.equals("GET")){
			//responseDetails.createNode("Headers").info(MarkupHelper.createTable(header));
			if(null != header){
			responseDetails.createNode("Headers").info(MarkupHelper.createCodeBlock(header.toString()));
			}
			if(produces.equalsIgnoreCase("text/plain"))
				responseDetails.createNode("Response").info(MarkupHelper.createCodeBlock(res.toString()));	
			else
			responseDetails.createNode("Response").info(MarkupHelper.createCodeBlock(res, CodeLanguage.JSON));
		}else{
			if(null != header){
			responseDetails.createNode("Headers").info(MarkupHelper.createCodeBlock(header.toString()));
			}
			responseDetails.createNode("Request").info(MarkupHelper.createCodeBlock(req, CodeLanguage.JSON));
			if(produces.equalsIgnoreCase("text/plain"))
				responseDetails.createNode("Response").info(MarkupHelper.createCodeBlock(res.toString()));	
			else
			responseDetails.createNode("Response").info(MarkupHelper.createCodeBlock(res, CodeLanguage.JSON));
		}
		responseDetails.createNode("Element Validation").info(MarkupHelper.createTable(resultsDetails(elements, actual, expected, reason,produces,reqKey)));
		
		//extent.flush();
	}
	
	public void flush() {
		extent.flush();
	}

	
public String[][] resultsDetails(String elements, String actual, String expected, String details,String produces,boolean reqkey){
		
		String[] tmparr = null;
		String[][] data = null;
		if(produces.equalsIgnoreCase("text/plain")  || reqkey){
		tmparr=expected.split(";");	
		data=new String[tmparr.length+1][4];
		}else{
		tmparr=elements.split(";");
		data=new String[tmparr.length+1][4];
	    }
		
		//ArrayList<String[][]> arrList = new ArrayList<String[][]>();
		
		data[0][0]="Elements";
		data[0][1]="Expected";
		data[0][2]="Actual";
		data[0][3]="Details";
		
		if(elements!=null && elements!=""){			
			tmparr = elements.split(";");
			for(int i = 1;i<=tmparr.length;i++){
				if(tmparr[i-1].trim()!=""){
					data[i][0]=tmparr[i-1];
				}				
		}
		}else{
			if(produces.equalsIgnoreCase("text/plain") || reqkey){
				tmparr = expected.trim().split(";");
				for(int i = 1;i<=tmparr.length;i++){
					if(tmparr[i-1].trim()!=""){
						try{		
						data[i][0]="";
					}
					catch(Exception e){
						
					}
					}	
				}
			}
		}
		if(expected!=null && expected!=""){
			tmparr = expected.trim().split(";");
			for(int i = 1;i<=tmparr.length;i++){
				if(tmparr[i-1].trim()!=""){
					try{
					data[i][1]=tmparr[i-1];
					}
					catch(Exception e){
						
					}
				}	
			}
		}
		
		if(actual!=null && actual!=""){
			if(produces.equalsIgnoreCase("text/plain")  || reqkey){
				tmparr = expected.trim().split(";");
				for(int i = 1;i<=tmparr.length;i++){
					if(tmparr[i-1].trim()!=""){
						try{		
						data[i][2]=actual;
					}
					catch(Exception e){
						
					}
					}	
				}
			}else{
			tmparr = actual.trim().split(";");
			for(int i = 1;i<=tmparr.length;i++){
				if(tmparr[i-1].trim()!=""){
					try{		
					data[i][2]=tmparr[i-1];
				}
				catch(Exception e){
					
				}
				}	
			}
			}
		}
		
		if(details!=null && details!=""){
			tmparr = details.trim().split(";");
			for(int i = 1;i<=tmparr.length;i++){
				if(tmparr[i-1].trim()!=""){
					try{
					if(tmparr[i-1].contains("Passed")){
						data[i][3]="PASSED";
					}else{
						data[i][3]="FAILED";
					}
				}
				catch(Exception e){
				
				}
				}	
			}
		}
		
		
		
		return data;
		
	}

public void reportTestDiffJSON(String tcname,String sendType, Map<String, Object> header, String targetRestURL, String baseRESTURL,String request,String responseTargetEnv ,String responseBaseEnv, Map<String, Map<String,Object>> enteriesDiffBothJson)

{
	if ((enteriesDiffBothJson.get("EnteriesOnTarget").size() <1) && enteriesDiffBothJson.get("EnteriesOnBase").size() <1 && enteriesDiffBothJson.get("EnteriesJSONDiff").size() <1) {
		logger.log(Status.PASS, MarkupHelper.createLabel(sendType + "  -  " +tcname, ExtentColor.GREEN));
		//logger.log(Status.PASS, "Test Case => " + tcname);
	} else {
		logger.log(Status.FAIL, MarkupHelper.createLabel(sendType + "  -  " +tcname, ExtentColor.RED));
		//logger.log(Status.FAIL, "Test Case : " + tcname);
	}

	
	logger.info(MarkupHelper.createTable(fetchJsonDiffinTable(enteriesDiffBothJson)));
	
	ExtentTest responseDetails = logger.createNode("TARGET REST URL -> " + targetRestURL );
	
	if(sendType.equals("GET")){
		//responseDetails.createNode("Headers").info(MarkupHelper.createTable(header));
		responseDetails.createNode("Headers").info(MarkupHelper.createCodeBlock(header.toString()));
		if(responseTargetEnv.length()<60509){
			responseDetails.createNode("Response").info(MarkupHelper.createCodeBlock(responseTargetEnv, CodeLanguage.JSON));
		}else{
			responseDetails.createNode("Response").info(MarkupHelper.createCodeBlock("Large Response, check JSON respones from local directory", CodeLanguage.XML));
		}
	}else{
		responseDetails.createNode("Headers").info(MarkupHelper.createCodeBlock(header.toString()));
		responseDetails.createNode("Request").info(MarkupHelper.createCodeBlock(request, CodeLanguage.JSON));
		if(responseTargetEnv.length()<60509){
			responseDetails.createNode("Response").info(MarkupHelper.createCodeBlock(responseTargetEnv, CodeLanguage.JSON));
		}else{
			responseDetails.createNode("Response").info(MarkupHelper.createCodeBlock("Large Response, check JSON respones from local directory", CodeLanguage.XML));
		}
	}
	
	responseDetails = logger.createNode("BASE REST URL -> " + baseRESTURL );
	if(sendType.equals("GET")){
		//responseDetails.createNode("Headers").info(MarkupHelper.createTable(header));
		responseDetails.createNode("Headers").info(MarkupHelper.createCodeBlock(header.toString()));
		if(responseBaseEnv.length()<60509){
			responseDetails.createNode("Response").info(MarkupHelper.createCodeBlock(responseBaseEnv, CodeLanguage.JSON));
		}else{
			responseDetails.createNode("Response").info(MarkupHelper.createCodeBlock("Large Response, check JSON respones from local directory", CodeLanguage.XML));
		}
	}else{
		responseDetails.createNode("Headers").info(MarkupHelper.createCodeBlock(header.toString()));
		responseDetails.createNode("Request").info(MarkupHelper.createCodeBlock(request, CodeLanguage.JSON));
		if(responseBaseEnv.length()<60509){
			responseDetails.createNode("Response").info(MarkupHelper.createCodeBlock(responseBaseEnv, CodeLanguage.JSON));
		}else{
			responseDetails.createNode("Response").info(MarkupHelper.createCodeBlock("Large Response, check JSON respones from local directory", CodeLanguage.XML));
		}
	}
	
	extent.flush();
}

@SuppressWarnings("rawtypes")	
public String[][] fetchJsonDiffinTable(Map<String, Map<String,Object>> enteriesDiffBothJson){
	String[][] data;
	int arrayRowIndex = 1;
	
	//finding the number of differences in all maps
	if(enteriesDiffBothJson!=null){
		if(enteriesDiffBothJson.get("EnteriesOnTarget")!=null){
			arrayRowIndex = arrayRowIndex+enteriesDiffBothJson.get("EnteriesOnTarget").size();
		}
		if(enteriesDiffBothJson.get("EnteriesOnBase")!=null){
			arrayRowIndex = arrayRowIndex+enteriesDiffBothJson.get("EnteriesOnBase").size();
		}
		if(enteriesDiffBothJson.get("EnteriesJSONDiff")!=null){
			arrayRowIndex = arrayRowIndex+enteriesDiffBothJson.get("EnteriesJSONDiff").size();
		}
	}
	
	if(arrayRowIndex<300){
		data = new String[arrayRowIndex][3];
		
		data[0][0]="Element";
		data[0][1]="Target Environment";
		data[0][2]="Base Environment";
		
		arrayRowIndex = 1;
		if(enteriesDiffBothJson!=null){
			if(enteriesDiffBothJson.get("EnteriesOnTarget")!=null){
				for (Entry e : enteriesDiffBothJson.get("EnteriesOnTarget").entrySet()){
					data[arrayRowIndex][0]=e.getKey().toString();
					if(e.getValue()!=null){
						data[arrayRowIndex][1]=e.getValue().toString();
						data[arrayRowIndex][2]="~element missing~";
					}
					arrayRowIndex++;
				}
			}
			if(enteriesDiffBothJson.get("EnteriesOnBase")!=null){
				for (Entry e : enteriesDiffBothJson.get("EnteriesOnBase").entrySet()){
					data[arrayRowIndex][0]=e.getKey().toString();
					if(e.getValue()!=null){
						data[arrayRowIndex][1]="~element missing~";
						data[arrayRowIndex][2]=e.getValue().toString();
					}						
					arrayRowIndex++;
				}
			}
			if(enteriesDiffBothJson.get("EnteriesJSONDiff")!=null){
				for ( Entry e : enteriesDiffBothJson.get("EnteriesJSONDiff").entrySet()){
					data[arrayRowIndex][0]=e.getKey().toString();
					if(e.getValue()!=null) {
						String[] tempArr =null;
						if(e.getValue().toString().contains(">>:"))
							tempArr=e.getValue().toString().split(">>:");
						else
							tempArr=e.getValue().toString().split(",");
						data[arrayRowIndex][1]=tempArr[0].substring(1, tempArr[0].length());
						data[arrayRowIndex][2]=tempArr[1].substring(0, tempArr[1].length()-1);
					}

					arrayRowIndex++;
				}
			}
	}

	}else{
		data = new String[1][1];
		data[0][0]="Large number of differences, compare the JSON responses manually.......";
		//data[1][1]="Check JSON Responses for more details";
		//data[1][2]="Base Environment";
	}		
	return data;
	
}
}
