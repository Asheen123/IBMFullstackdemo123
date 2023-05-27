package com.ups.automation.rest.restautomation.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ups.automation.rest.restautomation.bean.APISheetBean;
import com.ups.automation.rest.restautomation.bean.GlobalSheetBean;

public class ExcelReadService {

	// added for chaining
	// Map<String,String> varElementsMap =
	// com.ups.automation.rest.restautomation.service.RestAPIResponseValidator.varElementsMap;
	// end
	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelReadService.class);
	private static ResourceBundle configProp = ResourceBundle.getBundle("application");

	/**
	 * 
	 */
	public Map<String, Object> readExcel(String templatePath) {

		Map<String, Object> excelApiData = new HashMap<>();
		try (FileInputStream in = new FileInputStream(templatePath)) {
			Workbook workbook = WorkbookFactory.create(in);
			Map<String, Object> globalParametersMap = readGlobalParameters(workbook);
			List<GlobalSheetBean> globalSheetList = readGlobalSheet(workbook, globalParametersMap);
			excelApiData.put("globalSheetList", globalSheetList);
			excelApiData.put("globalParametersMap", globalParametersMap);
			excelApiData.put("templatePath", templatePath);
		} catch (Exception ex) {
			LOGGER.error("Exception during readExcel :: {}", ex.getMessage());
		}
		return excelApiData;
	}

	/**
	 * 
	 * @param workbook
	 */
	private List<GlobalSheetBean> readGlobalSheet(Workbook workbook, Map<String, Object> globalParametersMap)
			throws Exception {
		Sheet globalSheet = workbook.getSheet(Constants.SHEETNAME_GLOBAL);
		Integer totalNumberOfAPIs = globalSheet.getPhysicalNumberOfRows();

		Map<String, Integer> map = getColumnNameToIndexMap(globalSheet);

		List<GlobalSheetBean> globalSheetList = new ArrayList<>();
		for (int x = 1; x <= totalNumberOfAPIs; x++) {
			try {
				Row dataRow = globalSheet.getRow(x);
				if (dataRow != null) {
					GlobalSheetBean globalSheetBean = new GlobalSheetBean();
					Cell executeCell = dataRow.getCell(map.get(Constants.COLUMN_EXECUTE));
					if (executeCell != null) {
						Object execute = getCellValue(executeCell, globalParametersMap);
						if (execute != null) {
							globalSheetBean.setExecute(String.valueOf(execute).trim());
						}
					}
					if (!"Y".equalsIgnoreCase(globalSheetBean.getExecute())) {
						continue;
					}
					/*
					 * if(dataRow.getCell(map.get(COLUMN_RUN_DATE))!=null) {
					 * globalSheetBean.setRunDate(dataRow.getCell(map.get(
					 * COLUMN_RUN_DATE)).getDateCellValue()); }
					 */
					Cell iterationCell = dataRow.getCell(map.get(Constants.COLUMN_ITERATION));
					if (iterationCell != null) {
						Object iteration = getCellValue(iterationCell, globalParametersMap);
						if (iteration != null) {
							globalSheetBean.setIteration((Double) iteration);
						}
					}
					Cell operationNameCell = dataRow.getCell(map.get(Constants.COLUMN_OPERATION_NAME));
					if (operationNameCell != null) {
						Object operationName = getCellValue(operationNameCell, globalParametersMap);
						if (operationName != null) {
							globalSheetBean.setOperationName(String.valueOf(operationName).trim());
						}
					}
					Cell summaryCell = dataRow.getCell(map.get(Constants.COLUMN_SUMMARY));
					if (summaryCell != null) {
						Object summary = getCellValue(summaryCell, globalParametersMap);
						if (summary != null) {
							globalSheetBean.setSummary(String.valueOf(summary).trim());
						}
					}
					Cell pathsCell = dataRow.getCell(map.get(Constants.COLUMN_PATHS));
					if (pathsCell != null) {
						Object paths = getCellValue(pathsCell, globalParametersMap);
						if (paths != null) {
							globalSheetBean.setPath(String.valueOf(paths).trim());
						}
					}
					Cell workSheetCell = dataRow.getCell(map.get(Constants.COLUMN_WORK_SHEET));
					if (workSheetCell != null) {
						Object workSheet = getCellValue(workSheetCell, globalParametersMap);
						if (workSheet != null) {
							globalSheetBean.setWorkSheet(String.valueOf(workSheet).trim());
						}
					}
					Cell toolTypeCell = dataRow.getCell(map.get(Constants.COLUMN_TOOL_TYPE));
					if (toolTypeCell != null) {
						Object toolType = getCellValue(toolTypeCell, globalParametersMap);
						if (toolType != null) {
							globalSheetBean.setToolType(String.valueOf(toolType).trim());
						}
					}
					Cell genDirCell = dataRow.getCell(map.get(Constants.COLUMN_GENERAL_DIR));
					if (genDirCell != null) {
						Object genDir = getCellValue(genDirCell, globalParametersMap);
						if (genDir != null) {
							globalSheetBean.setGeneralDir(String.valueOf(genDir).trim());
						}
					}
					Cell genJsonLocCell = dataRow.getCell(map.get(Constants.COLUMN_GENERAL_JSON_LOCATION));
					if (genJsonLocCell != null) {
						Object genJsonLoc = getCellValue(genJsonLocCell, globalParametersMap);
						if (genJsonLoc != null) {
							globalSheetBean.setGeneralJsonLocation(String.valueOf(genJsonLoc).trim());
						}
					}
					Cell reqObjCell = dataRow.getCell(map.get(Constants.COLUMN_REQUEST_OBJECT));
					if (reqObjCell != null) {
						Object reqObj = getCellValue(reqObjCell, globalParametersMap);
						if (reqObj != null) {
							globalSheetBean.setRequestObj(String.valueOf(reqObj).trim());
						}
					}
					Cell envCell = dataRow.getCell(map.get(Constants.COLUMN_ENVIRONMENT));
					if (envCell != null) {
						Object env = getCellValue(envCell, globalParametersMap);
						if (env != null) {
							globalSheetBean.setEnvironment(String.valueOf(env).trim());
						}
					}
					
					//JSON Diff
					if(null!=map.get(Constants.COLUMN_BASE_ENVIRONMENT)){
						Cell baseEnvCell  = dataRow.getCell(map.get(Constants.COLUMN_BASE_ENVIRONMENT));
						if(baseEnvCell!=null) {
							Object baseEnv = getCellValue(baseEnvCell,globalParametersMap);
							if(baseEnv!=null) {
								globalSheetBean.setBaseEnvironment(String.valueOf(baseEnv).trim());
							}
						}
					}
					
					// end
					
					Cell consumesCell = dataRow.getCell(map.get(Constants.COLUMN_CONSUMES));
					if (consumesCell != null) {
						Object consumes = getCellValue(consumesCell, globalParametersMap);
						if (consumes != null) {
							globalSheetBean.setConsumes(String.valueOf(consumes).trim());
						}
					}
					Cell producesCell = dataRow.getCell(map.get(Constants.COLUMN_PRODUCES));
					if (producesCell != null) {
						Object produces = getCellValue(producesCell, globalParametersMap);
						if (produces != null) {
							globalSheetBean.setProduces(String.valueOf(produces).trim());
						}
					}
					Cell isJwtRequiredCell = dataRow.getCell(map.get(Constants.COLUMN_IS_JWT_REQUIRED));
					if (isJwtRequiredCell != null) {
						Object isJwtRequired = getCellValue(isJwtRequiredCell, globalParametersMap);
						if (isJwtRequired != null) {
							globalSheetBean.setIsJwtRequired((Boolean) isJwtRequired);
						}
					}

					// Added for JWT Token
					Cell generateJWTTokenCell = dataRow.getCell(map.get(Constants.COLUMN_GENERATE_JWT_TOKEN));
					if (generateJWTTokenCell != null) {
						Object generateJWTToken = getCellValue(generateJWTTokenCell, globalParametersMap);
						if (generateJWTToken != null) {
							globalSheetBean.setGenerateJWTToken((Boolean) generateJWTToken);
						}
					}
					Cell authorizationCell = dataRow.getCell(map.get(Constants.COLUMN_AUTHORIZATION));
					if (authorizationCell != null) {
						Object authorization = getCellValue(authorizationCell, globalParametersMap);
						if (authorization != null) {
							//Vivek: Added logic to provide empty JWT
							if (authorization.equals(Constants.STRINGVAL)) {
								globalSheetBean.setAuthorization("");
							}
							//end
							else {
								globalSheetBean.setAuthorization(String.valueOf(authorization).trim());
							}
								
								
							
						}
					}
					// END

					Cell jwtClientIdCell = dataRow.getCell(map.get(Constants.COLUMN_JWT_CLIENT_ID));
					if (jwtClientIdCell != null) {
						Object jwtClientId = getCellValue(jwtClientIdCell, globalParametersMap);
						if (jwtClientId != null) {
							globalSheetBean.setJwtClientId(String.valueOf(jwtClientId).trim());
						}
					}
					Cell jwtClientSecretCell = dataRow.getCell(map.get(Constants.COLUMN_JWT_CLIENT_SECRET));
					if (jwtClientSecretCell != null) {
						Object jwtClientSecret = getCellValue(jwtClientSecretCell, globalParametersMap);
						if (jwtClientSecret != null) {
							globalSheetBean.setJwtClientSecret(String.valueOf(jwtClientSecret).trim());
						}
					}
					Cell jwtAudClaimCell = dataRow.getCell(map.get(Constants.COLUMN_JWT_AUD_CLAIM));
					if (jwtAudClaimCell != null) {
						Object jwtAudClaim = getCellValue(jwtAudClaimCell, globalParametersMap);
						if (jwtAudClaim != null) {
							globalSheetBean.setJwtAudclaim(String.valueOf(jwtAudClaim).trim());
						}
					}
					Cell jwtUrlCell = dataRow.getCell(map.get(Constants.COLUMN_JWT_URL));
					if (jwtUrlCell != null) {
						Object jwtUrl = getCellValue(jwtUrlCell, globalParametersMap);
						if (jwtUrl != null) {
							globalSheetBean.setJwtUrl(String.valueOf(jwtUrl).trim());
						}
					}
					
					if(null!=map.get(Constants.COLUMN_BASE_JWT_CLIENT_ID)){
						Cell baseJWTClientIDCell  = dataRow.getCell(map.get(Constants.COLUMN_BASE_JWT_CLIENT_ID));
						if(baseJWTClientIDCell!=null) {
							Object baseJWTClientID = getCellValue(baseJWTClientIDCell,globalParametersMap);
							if(baseJWTClientID!=null) {
								globalSheetBean.setBaseJwtClientId(String.valueOf(baseJWTClientID).trim());
							}
						}
					}
					if(null!=map.get(Constants.COLUMN_BASE_JWT_CLIENT_SECRET)){
						Cell baseJWTClientSecretCell  = dataRow.getCell(map.get(Constants.COLUMN_BASE_JWT_CLIENT_SECRET));
						if(baseJWTClientSecretCell!=null) {
							Object baseJWTClientSecret = getCellValue(baseJWTClientSecretCell,globalParametersMap);
							if(baseJWTClientSecret!=null) {
								globalSheetBean.setBaseJwtClientSecret(String.valueOf(baseJWTClientSecret).trim());
							}
						}
					}
					if(null!=map.get(Constants.COLUMN_BASE_JWT_AUD_CLAIM)){
						Cell baseJWTAudClaimCell  = dataRow.getCell(map.get(Constants.COLUMN_BASE_JWT_AUD_CLAIM));
						if(baseJWTAudClaimCell!=null) {
							Object baseJWTAudClaim = getCellValue(baseJWTAudClaimCell,globalParametersMap);
							if(baseJWTAudClaim!=null) {
								globalSheetBean.setBaseJwtAudclaim(String.valueOf(baseJWTAudClaim).trim());
							}
						}
					}
					if(null!=map.get(Constants.COLUMN_BASE_JWT_URL)){
						Cell baseJWTURLCell  = dataRow.getCell(map.get(Constants.COLUMN_BASE_JWT_URL));
						if(baseJWTURLCell!=null) {
							Object baseJWTURL = getCellValue(baseJWTURLCell,globalParametersMap);
							if(baseJWTURL!=null) {
								globalSheetBean.setBaseJwtUrl(String.valueOf(baseJWTURL).trim());
							}
						}
					}
					if(null!=map.get(Constants.COLUMN_COMPARE_EXISTING_TEST_RESULT)){
						Cell CompareexistingTestResultCell  = dataRow.getCell(map.get(Constants.COLUMN_COMPARE_EXISTING_TEST_RESULT));
						if(CompareexistingTestResultCell!=null) {
							Object CompareexistingTestResult = getCellValue(CompareexistingTestResultCell,globalParametersMap);
							if(CompareexistingTestResult!=null) {
								globalSheetBean.setCompareExistingTestResult(String.valueOf(CompareexistingTestResult).trim());
							}
						}
					}
					if(null!=map.get(Constants.COLUMN_EXISTING_TESTRESULT_FOLDER)){
						Cell ExistingTestResultFolderCell  = dataRow.getCell(map.get(Constants.COLUMN_EXISTING_TESTRESULT_FOLDER));
						if(ExistingTestResultFolderCell!=null) {
							Object ExistingTestResultFolder = getCellValue(ExistingTestResultFolderCell,globalParametersMap);
							if(ExistingTestResultFolder!=null) {
								globalSheetBean.setExistingTestResultFolder(String.valueOf(ExistingTestResultFolder).trim());
							}
						}
					}
					if(null!=map.get(Constants.COLUMN_CREATE_BUG)){
					Cell CreateBugCell = dataRow.getCell(map.get(Constants.COLUMN_CREATE_BUG));
					if (CreateBugCell != null) {
						Object CreateBug = getCellValue(CreateBugCell, globalParametersMap);
						if (CreateBug != null) {
							globalSheetBean.setCreateBug(String.valueOf(CreateBug));
						}
					}
					Cell AssignedToBugCell = dataRow.getCell(map.get(Constants.COLUMN_ASSIGNEDTO_BUG));
					if (AssignedToBugCell != null) {
						Object AssignedToBug = getCellValue(AssignedToBugCell, globalParametersMap);
						if (AssignedToBug != null) {
							globalSheetBean.setBugAssignedTo(String.valueOf(AssignedToBug));
						}
					}
					Cell WorkItembugCell = dataRow.getCell(map.get(Constants.COLUMN_WORKITEM_BUG));
					if (WorkItembugCell != null) {
						Object WorkItembug = getCellValue(WorkItembugCell, globalParametersMap);
						if (WorkItembug != null) {
							globalSheetBean.setBugWorItem(String.valueOf(WorkItembug));
						}
					}
					Cell StatusbugCell = dataRow.getCell(map.get(Constants.COLUMN_STATUS_BUG));
					if (StatusbugCell != null) {
						Object statusbug = getCellValue(StatusbugCell, globalParametersMap);
						if (statusbug != null) {
							globalSheetBean.setBugStatus(String.valueOf(statusbug));
						}
					}
					Cell idbugCell = dataRow.getCell(map.get(Constants.COLUMN_ID_BUG));
					if (idbugCell != null) {
						Object idbug = getCellValue(idbugCell, globalParametersMap);
						if (idbug != null) {
							globalSheetBean.setBugId(String.valueOf(idbug));
						}
					}
					Cell titlebugCell = dataRow.getCell(map.get(Constants.COLUMN_TITLE_BUG));
					if (titlebugCell != null) {
						Object titlebug = getCellValue(titlebugCell, globalParametersMap);
						if (titlebug != null) {
							globalSheetBean.setBugTitle(String.valueOf(titlebug));
						}
					}
					Cell reproStepsCell = dataRow.getCell(map.get(Constants.COLUMN_REPRO_STEPS));
					if (reproStepsCell != null) {
						Object reprosteps = getCellValue(reproStepsCell, globalParametersMap);
						if (reprosteps != null) {
							globalSheetBean.setReproSteps(String.valueOf(reprosteps));
						}
					}
					}
					List<APISheetBean> apiDataList = readAPISheet(workbook.getSheet(globalSheetBean.getWorkSheet()),
							globalParametersMap);
					globalSheetBean.setApiSheetList(apiDataList);
					globalSheetList.add(globalSheetBean);
				}
			} catch (Exception ex) {
				LOGGER.error("Exception during readGlobalSheet :: {}", ex.getMessage());
			}
		}
		return globalSheetList;
	}

	/**
	 * 
	 * @param workbook
	 * @return
	 */
	private Map<String, Object> readGlobalParameters(Workbook workbook) throws Exception {
		Sheet globalParametersSheet = workbook.getSheet(Constants.SHEETNAME_GLOBAL_PARAMETERS);
		Integer totalNumberOfGlobalParameters = globalParametersSheet.getPhysicalNumberOfRows();

		// System.out.println(varElementsMap.get("29583_serviceCenterLocations[0].locationID"));

		Map<String, Object> globalParametersMap = new HashMap<>();
		Map<String, Integer> map = getColumnNameToIndexMap(globalParametersSheet);
		for (int x = 1; x <= totalNumberOfGlobalParameters; x++) {
			try {
				Row dataRow = globalParametersSheet.getRow(x);
				String globalParameterCategory = null;
				String globalParameterName = null;
				Object globalParameterValue = null;
				if (dataRow != null) {

					if (dataRow.getCell(map.get(Constants.COLUMN_CATEGORY)) != null) {
						globalParameterCategory = dataRow.getCell(map.get(Constants.COLUMN_CATEGORY))
								.getStringCellValue().trim();
					}

					if (dataRow.getCell(map.get(Constants.COLUMN_PARAMETER_NAME)) != null) {
						globalParameterName = dataRow.getCell(map.get(Constants.COLUMN_PARAMETER_NAME))
								.getStringCellValue().trim();
					}

					if (dataRow.getCell(map.get(Constants.COLUMN_PARAMETER_VALUE)) != null) {
						Cell globalParameterValueCell = dataRow.getCell(map.get(Constants.COLUMN_PARAMETER_VALUE));
						if (CellType.STRING.equals(globalParameterValueCell.getCellType())) {
							globalParameterValue = globalParameterValueCell.getStringCellValue().trim();
						} else if (CellType.NUMERIC.equals(globalParameterValueCell.getCellType())) {
							// globalParameterValue =
							// globalParameterValueCell.getNumericCellValue();
							Double value = globalParameterValueCell.getNumericCellValue();
							if ((value % 1) == 0) {
								globalParameterValue = NumberToTextConverter
										.toText(Long.valueOf(NumberToTextConverter.toText(value)));
							} else {
								globalParameterValue = NumberToTextConverter.toText(value);
							}
						}
					}

					String globalParamKey = null;
					if (globalParameterCategory != null && !globalParameterCategory.isEmpty()
							&& globalParameterName != null && !globalParameterName.isEmpty()) {
						globalParamKey = "GP_".concat(globalParameterCategory).concat("_").concat(globalParameterName);
					} else if (globalParameterName != null && !globalParameterName.isEmpty()) {
						globalParamKey = "GP_".concat(globalParameterName);
					}
					if (globalParamKey != null && globalParameterValue != null) {
						globalParametersMap.put(globalParamKey.toUpperCase(), globalParameterValue);
					}
				}
			} catch (Exception ex) {
				LOGGER.error("Exception during readGlobalParameters :: {}", ex.getMessage());
			}
		}
		return globalParametersMap;
	}

	/**
	 * 
	 * @param apiSheet
	 * @return
	 */
	private List<APISheetBean> readAPISheet(Sheet apiSheet, Map<String, Object> globalParametersMap) throws Exception {

		List<String> nonParameterColumnNameList = getListOfNonParameterColumnName();
		Integer totalNumberOfTC = apiSheet.getPhysicalNumberOfRows();
		Map<String, Integer> map = getColumnNameToIndexMap(apiSheet);

		List<APISheetBean> apiDataList = new ArrayList<>();
		for (int x = 1; x <= totalNumberOfTC; x++) {
			try {
				Row dataRow = apiSheet.getRow(x);
				if (dataRow != null) {
					APISheetBean apiData = new APISheetBean();
					Map<String, Object> headerParamMap = null;
					Map<String, Object> pathParamMap = null;
					Map<String, Object> queryParamMap = null;
					Map<String, Object> requestBodyParamMap = null;

					Set<String> headerNameKeySet = map.keySet();

					for (String headerName : headerNameKeySet) {
						// System.out.println(headerName);
						if (!nonParameterColumnNameList.contains(headerName)) {
							if (headerName.contains("_")) {
								int lastIndex = headerName.lastIndexOf("_");
								String paramName = headerName.substring(0, lastIndex);
								String paramType = headerName.substring((lastIndex + 1), headerName.length());
								if ("H".equals(paramType)) {
									if (headerParamMap == null) {
										headerParamMap = new HashMap<>();
									}
									Cell paramCell = dataRow.getCell(map.get(headerName));
									if (paramCell != null) {
											getParamCellValue(paramName, paramCell, headerParamMap, globalParametersMap,
													true);
									}
								} else if ("P".equals(paramType)) {
									if (pathParamMap == null) {
										pathParamMap = new HashMap<>();
									}
									Cell paramCell = dataRow.getCell(map.get(headerName));
									if (paramCell != null) {
											getParamCellValue(paramName, paramCell, pathParamMap, globalParametersMap,
													false);
									}
								} else if ("Q".equals(paramType)) {
									if (queryParamMap == null) {
										queryParamMap = new HashMap<>();
									}
									Cell paramCell = dataRow.getCell(map.get(headerName));
									if (paramCell != null) {
											getParamCellValue(paramName, paramCell, queryParamMap, globalParametersMap,
													false);
									}
								} else {
									if (requestBodyParamMap == null) {
										requestBodyParamMap = new HashMap<>();
									}
									Cell paramCell = dataRow.getCell(map.get(headerName));
									if (paramCell != null) {
										getParamCellValue(headerName, paramCell, requestBodyParamMap,
												globalParametersMap, false);
									}
								}
							} else {
								if (requestBodyParamMap == null) {
									requestBodyParamMap = new HashMap<>();
								}
								Cell paramCell = dataRow.getCell(map.get(headerName));
								if (paramCell != null) {
									getParamCellValue(headerName, paramCell, requestBodyParamMap, globalParametersMap,
											false);
								}
							}
						} else {

							Cell apiDataCell = dataRow.getCell(map.get(headerName));
							//System.out.println(headerName);
							if (Constants.COLUMN_EXECUTE.equals(headerName) && apiDataCell != null) {
								Object execute = getCellValue(apiDataCell, globalParametersMap);
								if (execute != null) {
									apiData.setExecute(String.valueOf(execute).trim());
								}
							} else if (Constants.COLUMN_COMPARE.equals(headerName) && apiDataCell != null) {
								Object compare = getCellValue(apiDataCell, globalParametersMap);
								if (compare != null) {									
									apiData.setCompare(String.valueOf(compare).trim());
								}
							}else if (Constants.COLUMN_COMPARETYPE.equals(headerName) && apiDataCell != null) {
								Object comparetype = getCellValue(apiDataCell, globalParametersMap);
								if (comparetype != null) {									
									apiData.setCompareType(String.valueOf(comparetype).trim());
								}
							}else if (Constants.COLUMN_DATE.equals(headerName) && apiDataCell != null) {
								// apiData.setDate(apiDataCell.getDateCellValue());
							} else if (Constants.COLUMN_TCID.equals(headerName) && apiDataCell != null) {
								Object tcId = getCellValue(apiDataCell, globalParametersMap);
								if (tcId != null) {
									apiData.setTcid(String.valueOf(tcId).trim());
								}
							} else if (Constants.COLUMN_TC_NAME.equals(headerName) && apiDataCell != null) {
								Object tcName = getCellValue(apiDataCell, globalParametersMap);
								if (tcName != null) {
									apiData.setTcName(String.valueOf(tcName).trim());
								}
							} else if (Constants.COLUMN_QC_UPDATE.equals(headerName) && apiDataCell != null) {
								Object qcUpdate = getCellValue(apiDataCell, globalParametersMap);
								if (qcUpdate != null) {
									apiData.setQcUpdate(String.valueOf(qcUpdate).trim());
								}
							} else if (Constants.COLUMN_SEND_TYPE.equals(headerName) && apiDataCell != null) {
								Object sendType = getCellValue(apiDataCell, globalParametersMap);
								if (sendType != null) {
									apiData.setSendType(String.valueOf(sendType).trim());
								}

							}
							else if (Constants.COLUMN_SCRIPTEXEC_REQ.equals(headerName) && apiDataCell != null) {
								Object scriptexecreq = getCellValue(apiDataCell, globalParametersMap);
								if (scriptexecreq != null) {
									apiData.setPreRequestReq(String.valueOf(scriptexecreq).trim());
								}
							}
							else if (Constants.COLUMN_SCRIPT_NAME.equals(headerName) && apiDataCell != null) {
								Object scriptexecname = getCellValue(apiDataCell, globalParametersMap);
								if (scriptexecname != null) {
									apiData.setRequestScript(String.valueOf(scriptexecname).trim());
								}
							}
							else if (Constants.COLUMN_TFS_UPDATE.equals(headerName) && apiDataCell != null) {
								Object tfsUpdate = getCellValue(apiDataCell, globalParametersMap);
								if (tfsUpdate != null) {
									apiData.setTfsUpdate(String.valueOf(tfsUpdate).trim());
								}
							}
							// added for chaining
							else if (Constants.COLUMN_VAR_ELEMENTS.equals(headerName)) {
								if (null != apiDataCell) {
									Object varelements = getCellValue(apiDataCell, globalParametersMap);
									if (varelements != null) {
										apiData.setVarElements(String.valueOf(varelements).trim());
									}
								}
							}
							else if (Constants.COLUMN_VAR_HEADERS.equals(headerName)) {
								if (null != apiDataCell) {
									Object varelements = getCellValue(apiDataCell, globalParametersMap);
									if (varelements != null) {
										apiData.setVarHeaders(String.valueOf(varelements).trim());
									}
								}
							}
							// end
							 //JSON diff
							 else if(Constants.COLUMN_REMOVE_Elements.equals(headerName) ) {
								 if(null!=apiDataCell){
									 Object removeElements = getCellValue(apiDataCell,globalParametersMap);
									 if(removeElements!=null) {
										 apiData.setRemElementJSONDiff(String.valueOf(removeElements).trim());
									 } 
								 }
							 }
							 //end
							else if (Constants.COLUMN_ELEMENTS.equals(headerName) && apiDataCell != null) {
								Object elements = getCellValue(apiDataCell, globalParametersMap);
								if (elements != null) {
									apiData.setElements(String.valueOf(elements).trim());
								}
							} else if (Constants.COLUMN_EXPECTED.equals(headerName) && apiDataCell != null) {
								Object expected = getCellValue(apiDataCell, globalParametersMap);
								if (expected != null) {
									apiData.setExpected(String.valueOf(expected).trim());
								}
							}
							else if (Constants.COLUMN_REQUEST.equals(headerName) && apiDataCell != null) {
								Object expected = getCellValue(apiDataCell, globalParametersMap);
								if (expected != null) {
									apiData.setInputRequest(String.valueOf(expected).trim());
								}
							}
							else if (Constants.COLUMN_REQUEST_PATH.equals(headerName) && apiDataCell != null) {
								Object expected = getCellValue(apiDataCell, globalParametersMap);
								if (expected != null) {
									apiData.setInputRequestPath(String.valueOf(expected).trim());
								}
							}

							// Bheem-- These columns are updating during run
							// time, So dont need to read these columns
							/*
							 * else
							 * if(Constants.COLUMN_ACTUAL.equals(headerName) &&
							 * apiDataCell!=null) { Object actual =
							 * getCellValue(apiDataCell,globalParametersMap);
							 * if(actual!=null) {
							 * //apiData.setActual(String.valueOf(actual).trim()
							 * ); } } else
							 * if(Constants.COLUMN_STATUS.equals(headerName) &&
							 * apiDataCell!=null) { Object status =
							 * getCellValue(apiDataCell,globalParametersMap);
							 * if(status!=null) {
							 * apiData.setStatus(String.valueOf(status).trim());
							 * } } else
							 * if(Constants.COLUMN_STATUS_DETAIL.equals(
							 * headerName) && apiDataCell!=null) { Object
							 * statusDetail =
							 * getCellValue(apiDataCell,globalParametersMap);
							 * if(statusDetail!=null) {
							 * apiData.setStatusDetail(String.valueOf(
							 * statusDetail).trim()); } }
							 */
						}
					}

					apiData.setHeaderParam(headerParamMap);
					apiData.setPathParam(pathParamMap);
					apiData.setQueryParam(queryParamMap);

					// Sorting requestBody Param map in ascending order

					if (null != requestBodyParamMap) {
						Map<String, Object> requestBodyParamMap_Sorted = requestBodyParamMap.entrySet().stream()
								.sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey,
										Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

						apiData.setRequestBodyParam(requestBodyParamMap_Sorted);
					} else {
						apiData.setRequestBodyParam(requestBodyParamMap);
					}

					apiDataList.add(apiData);
				}
			} catch (Exception ex) {
				LOGGER.error("Exception during readAPISheet :: {}", ex.getMessage());
			}
		}
		return apiDataList;
	}

	/**
	 * 
	 * @param sheet
	 * @return
	 */
	private Map<String, Integer> getColumnNameToIndexMap(Sheet sheet) throws Exception {

		Map<String, Integer> map = new HashMap<String, Integer>(); // Create map
		Row headerRow = sheet.getRow(0); // Get first row
		// following is boilerplate from the java doc
		short minColIx = headerRow.getFirstCellNum(); // get the first column
														// index for a row
		short maxColIx = headerRow.getLastCellNum(); // get the last column
														// index for a row
		for (short colIx = minColIx; colIx < maxColIx; colIx++) { // loop from
																	// first to
																	// last
																	// index
			Cell cell = headerRow.getCell(colIx); // get the cell
			//System.out.println(cell.getStringCellValue().trim());
			map.put(cell.getStringCellValue().trim(), cell.getColumnIndex()); // add
																				// the
																				// cell
																				// contents
																				// (name
																				// of
																				// column)
																				// and
																				// cell
																				// index
																				// to
																				// the
																				// map
		}
		return map;
	}

	/**
	 * 
	 * @param paramName
	 * @param paramCell
	 * @param paramMap
	 */
	private void getParamCellValue(String paramName, Cell paramCell, Map<String, Object> paramMap,
			Map<String, Object> globalParametersMap, Boolean isHeader) throws Exception {
		String[] temp = null;
		Object cellValue = getCellValue(paramCell, globalParametersMap);
		// if(isHeader || cellValue!=null) { // Bheem nov 12- for excel issue
		// where it is passing blank value in header
		if (cellValue != null) {
			// paramMap.put(paramName,
			// getCellValue(paramCell,globalParametersMap));
			// STRINGVAL constant has been added to validate the blank cell -
			// NOV 6
			if (cellValue.equals(Constants.STRINGVAL)) {
				paramMap.put(paramName, "");
			} else {
				paramMap.put(paramName, getCellValue(paramCell, globalParametersMap));
			}
		}
	}

	/**
	 * This is the method which will fetch the value dynamically from any XLS
	 * where the cell value needs to be defined in the format -
	 * DV_<keep/delete>_<excelName>_<Sheetname>_<columnname>.
	 * 
	 * @author vbc9wqy
	 * @param excelVal-excel,sheet,field
	 *            name defined as a combined field with the separator _.
	 * @return dynamicNumber - fetched from XLS.
	 * @exception Exception-On
	 *                fetching value from XLS.
	 */
	public static String fetchDynamicVal(String excelVal) {
		String dynamicNumber = "";
		String[] excelProps = excelVal.split("_");
		try (FileInputStream in = new FileInputStream(
				configProp.getString("DYNAMICVAL_FILEPATH") + excelProps[1] + ".xls")) {
			Workbook workbook = WorkbookFactory.create(in);
			Sheet globalSheet = workbook.getSheet(excelProps[2]);
			ExcelReadService exRead = new ExcelReadService();
			Map<String, Integer> map = exRead.getColumnNameToIndexMap(globalSheet);
			Integer noOfEntries = globalSheet.getPhysicalNumberOfRows();
			try {
				if (noOfEntries > 1) {
					Row dataRow = globalSheet.getRow(1);
					if (dataRow != null) {
						if (dataRow.getCell(map.get(excelProps[3])) != null) {
							dynamicNumber = dataRow.getCell(map.get(excelProps[3])).getStringCellValue().trim();
							if (excelProps[0].equalsIgnoreCase("Delete"))
								shiftDynamicRows(configProp.getString("DYNAMICVAL_FILEPATH") + excelProps[1] + ".xls",
										excelProps[2], globalSheet);
						}
					}
				}
			} catch (Exception ex) {
				LOGGER.error("Exception during fetching 1Z value :: {}", ex.getMessage());
			}
		} catch (Exception ex) {
			LOGGER.error("Exception during Fetching 1Z number from the xlsx :: {}", ex.getMessage());
		}
		return dynamicNumber;
	}

	/**
	 * This is the method which will delete the use dynamic number row from the
	 * xls and shift the remaining rows
	 * 
	 * @author vbc9wqy
	 * @param source
	 *            filepath, sheetname and source sheet
	 * @return none
	 * @exception Exception-On
	 *                fetching value from XLS.
	 */
	public static void shiftDynamicRows(String filepath, String sheetname, Sheet dynamicSheet) {
		try (FileOutputStream out = new FileOutputStream(filepath)) {
			HSSFWorkbook newWorkbook = new HSSFWorkbook();
			HSSFSheet newSheet = newWorkbook.createSheet(sheetname);
			Row newHeaderRow = newSheet.createRow(0);
			Integer lastRowNum = dynamicSheet.getLastRowNum();
			shiftDynamicCellRows(newHeaderRow, dynamicSheet.getRow(0), newSheet, newWorkbook);
			for (int x = 1; x <= lastRowNum; x++) {
				Row sourceRow = dynamicSheet.getRow(x + 1);
				if (sourceRow != null) {
					Row newDataRow = newSheet.createRow(x);
					shiftDynamicCellRows(newDataRow, sourceRow, newSheet, newWorkbook);
				}
			}

			newWorkbook.write(out);
			newWorkbook.close();
		} catch (

		FileNotFoundException e) {
			LOGGER.error("ShiftDynamicRows - FileNotFoundException :: ", e);
		} catch (IOException ex) {
			LOGGER.error("ShiftDynamicRows - IOException :: ", ex);
		}
	}

	private static void shiftDynamicCellRows(Row newRow, Row sourceRow, Sheet workSheet, Workbook workbook) {
		for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
			Cell oldCell = sourceRow.getCell(i);
			Cell newCell = newRow.createCell(i);
			if (oldCell == null) {
				newCell = null;
				continue;
			}
			newCell.setCellType(oldCell.getCellType());
			if (CellType.NUMERIC.equals(oldCell.getCellType())) {
				newCell.setCellValue(oldCell.getNumericCellValue());
			} else if (CellType.STRING.equals(oldCell.getCellType())) {
				newCell.setCellValue(oldCell.getRichStringCellValue());
			} else if (CellType.BOOLEAN.equals(oldCell.getCellType())) {
				newCell.setCellValue(oldCell.getBooleanCellValue());
			}
		}
	}

	private Object getCellValue(Cell cell, Map<String, Object> globalParametersMap) throws Exception {

		Object cellValue = null;
		if (CellType.NUMERIC.equals(cell.getCellType())) {
			Double value = cell.getNumericCellValue();
			if ((value % 1) == 0) {
				cellValue = NumberToTextConverter.toText(Long.valueOf(NumberToTextConverter.toText(value)));
			} else {
				cellValue = NumberToTextConverter.toText(value);
			}
		} else if (CellType.STRING.equals(cell.getCellType())) {
			String cellStringValue = cell.getStringCellValue().trim().toUpperCase();
			if (cellStringValue != null && !cellStringValue.isEmpty()
					&& (cellStringValue.contains("GP_") || cellStringValue.contains("gp_"))) {
				if (globalParametersMap.containsKey(cellStringValue)) {
					cellValue = globalParametersMap.get(cellStringValue);
				}
			} else {
				cellValue = cell.getStringCellValue().trim();
			}
		} else if (CellType.BOOLEAN.equals(cell.getCellType())) {
			cellValue = cell.getBooleanCellValue();
		}
		return cellValue;
	}

	/**
	 * 
	 * @return
	 */
	private List<String> getListOfNonParameterColumnName() throws Exception {
		List<String> nonParameterColumnNameList = new ArrayList<>();
		nonParameterColumnNameList.add(Constants.COLUMN_EXECUTE);
		nonParameterColumnNameList.add(Constants.COLUMN_COMPARE);
		nonParameterColumnNameList.add(Constants.COLUMN_COMPARETYPE);
		nonParameterColumnNameList.add(Constants.COLUMN_DATE);
		nonParameterColumnNameList.add(Constants.COLUMN_TCID);
		nonParameterColumnNameList.add(Constants.COLUMN_TC_NAME);
		nonParameterColumnNameList.add(Constants.COLUMN_QC_UPDATE);
		nonParameterColumnNameList.add(Constants.COLUMN_TFS_UPDATE);
		nonParameterColumnNameList.add(Constants.COLUMN_SEND_TYPE);
		nonParameterColumnNameList.add(Constants.COLUMN_SCRIPTEXEC_REQ);
		nonParameterColumnNameList.add(Constants.COLUMN_SCRIPT_NAME);
		nonParameterColumnNameList.add(Constants.COLUMN_ELEMENTS);
		nonParameterColumnNameList.add(Constants.COLUMN_VAR_ELEMENTS);
		nonParameterColumnNameList.add(Constants.COLUMN_REMOVE_Elements);//JSON diff
		nonParameterColumnNameList.add(Constants.COLUMN_EXPECTED);
		nonParameterColumnNameList.add(Constants.COLUMN_ACTUAL);
		nonParameterColumnNameList.add(Constants.COLUMN_STATUS);
		nonParameterColumnNameList.add(Constants.COLUMN_DATE);
		nonParameterColumnNameList.add(Constants.COLUMN_STATUS_DETAIL);
		nonParameterColumnNameList.add(Constants.COLUMN_REST_URL);
		nonParameterColumnNameList.add(Constants.COLUMN_REQUEST);
		nonParameterColumnNameList.add(Constants.COLUMN_REQUEST_PATH);
		nonParameterColumnNameList.add(Constants.COLUMN_VAR_HEADERS);
		return nonParameterColumnNameList;
	}
}
