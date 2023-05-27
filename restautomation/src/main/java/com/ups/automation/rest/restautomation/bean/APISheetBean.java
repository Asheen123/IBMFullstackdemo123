package com.ups.automation.rest.restautomation.bean;

import java.util.Date;
import java.util.Map;

import com.google.gson.JsonObject;

import io.restassured.response.Response;

public class APISheetBean {
 
	private String execute;
	private Date date;
	private String tcid;
	private String tcName;
	private String qcUpdate;
	private String tfsUpdate;
	private String sendType;
	private String elements;
	private String varElements;
	private String expected;
	private String actual;
	private String status;
	private String statusDetail;
	private Map<String,Object> headerParam;
	private Map<String,Object> pathParam;
	private Map<String,Object> queryParam;
	private Map<String,Object> requestBodyParam;
	private Response apiResponse;
	private JsonObject apiRequest;
	private String restUrl;
	private String inputRequest;
	private String inputRequestPath;
	private Response apiResponseBaseEnv;// JSON diff
	private String restURLBaseEnv; // JSON diff
	private Map<String,Map<String,Object>> difference; // JSON diff
	private String removeElementsJSONDiff;
	private String compare;	
	private String compareType;
	private String VarHeaders;
	private String preRequestReq;
	private String RequestScript;
	
	public String getExecute() {
		return execute;
	}
	public void setExecute(String execute) {
		this.execute = execute;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getTcid() {
		return tcid;
	}
	public void setTcid(String tcid) {
		this.tcid = tcid;
	}
	public String getTcName() {
		return tcName;
	}
	public void setTcName(String tcName) {
		this.tcName = tcName;
	}
	public String getQcUpdate() {
		return qcUpdate;
	}
	public void setQcUpdate(String qcUpdate) {
		this.qcUpdate = qcUpdate;
	}
	public String getSendType() {
		return sendType;
	}
	public void setSendType(String sendType) {
		this.sendType = sendType;
	}
	public String getElements() {
		return elements;
	}
	public void setElements(String elements) {
		this.elements = elements;
	}
	// added for chaining
	public String getVarElements() {
		return varElements;
	}
	public void setVarElements(String varElements) {
		this.varElements = varElements;
	}
	// end
	
	public String getExpected() {
		return expected;
	}
	public void setExpected(String expected) {
		this.expected = expected;
	}
	public String getActual() {
		return actual;
	}
	public void setActual(String actual) {
		this.actual = actual;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatusDetail() {
		return statusDetail;
	}
	public void setStatusDetail(String statusDetail) {
		this.statusDetail = statusDetail;
	}
	public Map<String, Object> getHeaderParam() {
		return headerParam;
	}
	public void setHeaderParam(Map<String, Object> headerParam) {
		this.headerParam = headerParam;
	}
	public Map<String, Object> getPathParam() {
		return pathParam;
	}
	public void setPathParam(Map<String, Object> pathParam) {
		this.pathParam = pathParam;
	}
	public Map<String, Object> getQueryParam() {
		return queryParam;
	}
	public void setQueryParam(Map<String, Object> queryParam) {
		this.queryParam = queryParam;
	}
	public Map<String, Object> getRequestBodyParam() {
		return requestBodyParam;
	}
	public void setRequestBodyParam(Map<String, Object> requestBodyParam) {
		this.requestBodyParam = requestBodyParam;
	}
	public Response getApiResponse() {
		return apiResponse;
	}
	public void setApiResponse(Response apiResponse) {
		this.apiResponse = apiResponse;
	}
	public JsonObject getApiRequest() {
		return apiRequest;
	}
	public void setApiRequest(JsonObject apiRequest) {
		this.apiRequest = apiRequest;
	}
	public String getRestUrl() {
		return restUrl;
	}
	public void setRestUrl(String restUrl) {
		this.restUrl = restUrl;
	}
	public String getInputRequest() {
		return inputRequest;
	}
	public void setInputRequest(String inputRequest) {
		this.inputRequest = inputRequest;
	}
	public String getInputRequestPath() {
		return inputRequestPath;
	}
	public void setInputRequestPath(String inputRequestPath) {
		this.inputRequestPath = inputRequestPath;
	}
	public Response getApiResponseBaseEnv() {
		return apiResponseBaseEnv;
	}
	public void setApiResponseBaseEnv(Response apiResponseBaseEnv) {
		this.apiResponseBaseEnv = apiResponseBaseEnv;
	}
	public String getRestURLBaseEnv() {
		return restURLBaseEnv;
	}
	public void setRestURLBaseEnv(String restURLBaseEnv) {
		this.restURLBaseEnv = restURLBaseEnv;
	}
	public Map<String, Map<String, Object>> getJsonDifferences() {
		return difference;
	}
	public void setJsonDifferences(Map<String, Map<String, Object>> difference) {
		this.difference = difference;
	}
	public String getRemElementJSONDiff() { // JSON diff
		return removeElementsJSONDiff;
	}
	public void setRemElementJSONDiff(String removeElementsJSONDiff) {
		this.removeElementsJSONDiff = removeElementsJSONDiff;
	} // end
	public String getCompare() {
		return compare;
	}
	public void setCompare(String compare) {
		this.compare = compare;
	}
	public String getCompareType() {
		return compareType;
	}
	public void setCompareType(String compareType) {
		this.compareType = compareType;
	}
	public String getVarHeaders() {
		return VarHeaders;
	}
	public void setVarHeaders(String varHeaders) {
		VarHeaders = varHeaders;
	}
	public String getPreRequestReq() {
		return preRequestReq;
	}
	public void setPreRequestReq(String preRequestReq) {
		this.preRequestReq = preRequestReq;
	}
	public String getRequestScript() {
		return RequestScript;
	}
	public void setRequestScript(String requestScript) {
		RequestScript = requestScript;
	}
	public String getTfsUpdate() {
		return tfsUpdate;
	}
	public void setTfsUpdate(String tfsUpdate) {
		this.tfsUpdate = tfsUpdate;
	}
}

