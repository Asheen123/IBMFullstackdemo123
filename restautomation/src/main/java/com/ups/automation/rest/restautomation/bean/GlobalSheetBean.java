package com.ups.automation.rest.restautomation.bean;

import java.util.Date;
import java.util.List;

/**
 * 
 * @author BNF6MDG
 *
 */
public class GlobalSheetBean {

	private String execute;
	private Date runDate;
	private Double iteration;
	private String status;
	private String operationName;
	private String summary;
	private String path;
	private String workSheet;
	private String toolType;
	private String generalDir;
	private String generalJsonLocation;
	private String requestObj;
	private String environment;
	private String consumes;
	private String produces;
	private Boolean isJwtRequired;
	// Added for JWT Token
	private Boolean generateJWTToken;
	private String authorization;
	// End
	private String baseEnvironment;
	private String jwtClientId;
	private String jwtClientSecret;
	private String jwtAudclaim;
	private String jwtUrl;
	List<APISheetBean> apiSheetList;
	private String baseJwtClientId;
	private String baseJwtClientSecret;
	private String baseJwtAudclaim;
	private String baseJwtUrl;
	private String baseAuthorization;
	private String existingTestResultFolder;
	private String compareExistingTestResult;
	private String createBug;
	private String bugAssignedTo;
	private String bugWorItem;
	private String bugStatus;
	private String bugId;
	private String bugTitle;
	private String reproSteps;

	public String getExecute() {
		return execute;
	}

	public void setExecute(String execute) {
		this.execute = execute;
	}

	public Date getRunDate() {
		return runDate;
	}

	public void setRunDate(Date runDate) {
		this.runDate = runDate;
	}

	public Double getIteration() {
		return iteration;
	}

	public void setIteration(Double iteration) {
		this.iteration = iteration;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getWorkSheet() {
		return workSheet;
	}

	public void setWorkSheet(String workSheet) {
		this.workSheet = workSheet;
	}

	public String getToolType() {
		return toolType;
	}

	public void setToolType(String toolType) {
		this.toolType = toolType;
	}

	public String getGeneralDir() {
		return generalDir;
	}

	public void setGeneralDir(String generalDir) {
		this.generalDir = generalDir;
	}

	public String getGeneralJsonLocation() {
		return generalJsonLocation;
	}

	public void setGeneralJsonLocation(String generalJsonLocation) {
		this.generalJsonLocation = generalJsonLocation;
	}

	public String getRequestObj() {
		return requestObj;
	}

	public void setRequestObj(String requestObj) {
		this.requestObj = requestObj;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getConsumes() {
		return consumes;
	}

	public void setConsumes(String consumes) {
		this.consumes = consumes;
	}

	public String getProduces() {
		return produces;
	}

	public void setProduces(String produces) {
		this.produces = produces;
	}

	public Boolean getIsJwtRequired() {
		return isJwtRequired;
	}

	public void setIsJwtRequired(Boolean isJwtRequired) {
		this.isJwtRequired = isJwtRequired;
	}

	//
	public Boolean getGenerateJWTToken() {
		return generateJWTToken;
	}

	public void setGenerateJWTToken(Boolean generateJWTToken) {
		this.generateJWTToken = generateJWTToken;
	}

	public String getAuthorization() {
		return authorization;
	}

	public void setAuthorization(String authorization) {
		this.authorization = authorization;
	}

	//

	public String getJwtClientId() {
		return jwtClientId;
	}

	public void setJwtClientId(String jwtClientId) {
		this.jwtClientId = jwtClientId;
	}

	public String getJwtClientSecret() {
		return jwtClientSecret;
	}

	public void setJwtClientSecret(String jwtClientSecret) {
		this.jwtClientSecret = jwtClientSecret;
	}

	public String getJwtAudclaim() {
		return jwtAudclaim;
	}

	public void setJwtAudclaim(String jwtAudclaim) {
		this.jwtAudclaim = jwtAudclaim;
	}

	public String getJwtUrl() {
		return jwtUrl;
	}

	public void setJwtUrl(String jwtUrl) {
		this.jwtUrl = jwtUrl;
	}

	public List<APISheetBean> getApiSheetList() {
		return apiSheetList;
	}

	public void setApiSheetList(List<APISheetBean> apiSheetList) {
		this.apiSheetList = apiSheetList;
	}

	public String getBaseJwtClientId() {
		return baseJwtClientId;
	}

	public void setBaseJwtClientId(String baseJwtClientId) {
		this.baseJwtClientId = baseJwtClientId;
	}

	public String getBaseJwtClientSecret() {
		return baseJwtClientSecret;
	}

	public void setBaseJwtClientSecret(String baseJwtClientSecret) {
		this.baseJwtClientSecret = baseJwtClientSecret;
	}

	public String getBaseJwtAudclaim() {
		return baseJwtAudclaim;
	}

	public void setBaseJwtAudclaim(String baseJwtAudclaim) {
		this.baseJwtAudclaim = baseJwtAudclaim;
	}

	public String getBaseJwtUrl() {
		return baseJwtUrl;
	}

	public void setBaseJwtUrl(String baseJwtUrl) {
		this.baseJwtUrl = baseJwtUrl;
	}

	public String getBaseAuthorization() {
		return baseAuthorization;
	}

	public void setBaseAuthorization(String baseAuthorization) {
		this.baseAuthorization = baseAuthorization;
	}

	public String getExistingTestResultFolder() {
		return existingTestResultFolder;
	}

	public void setExistingTestResultFolder(String existingTestResultFolder) {
		this.existingTestResultFolder = existingTestResultFolder;
	}

	public String getCompareExistingTestResult() {
		return compareExistingTestResult;
	}

	public void setCompareExistingTestResult(String compareExistingTestResult) {
		this.compareExistingTestResult = compareExistingTestResult;
	}

	public String getBaseEnvironment() {
		return baseEnvironment;
	}

	public void setBaseEnvironment(String baseEnvironment) {
		this.baseEnvironment = baseEnvironment;
	}

	public String getCreateBug() {
		return createBug;
	}

	public void setCreateBug(String createBug) {
		this.createBug = createBug;
	}

	public String getBugAssignedTo() {
		return bugAssignedTo;
	}

	public void setBugAssignedTo(String bugAssignedTo) {
		this.bugAssignedTo = bugAssignedTo;
	}

	public String getBugWorItem() {
		return bugWorItem;
	}

	public void setBugWorItem(String bugWorItem) {
		this.bugWorItem = bugWorItem;
	}

	public String getBugStatus() {
		return bugStatus;
	}

	public void setBugStatus(String bugStatus) {
		this.bugStatus = bugStatus;
	}

	public String getBugId() {
		return bugId;
	}

	public void setBugId(String bugId) {
		this.bugId = bugId;
	}

	public String getBugTitle() {
		return bugTitle;
	}

	public void setBugTitle(String bugTitle) {
		this.bugTitle = bugTitle;
	}

	public String getReproSteps() {
		return reproSteps;
	}

	public void setReproSteps(String reproSteps) {
		this.reproSteps = reproSteps;
	}

}
