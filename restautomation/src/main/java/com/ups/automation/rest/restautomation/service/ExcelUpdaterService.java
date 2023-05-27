package com.ups.automation.rest.restautomation.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ups.automation.rest.restautomation.bean.APISheetBean;
import com.ups.automation.rest.restautomation.bean.GlobalSheetBean;


public class ExcelUpdaterService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUpdaterService.class);
	private static ResourceBundle configProp = ResourceBundle.getBundle("application");
	private static CellStyle bgRedStyle;
	private static CellStyle bgGreenStyle;
	private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private static JsonParser jsonParser = new JsonParser();
	private static SimpleDateFormat sdt = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss");

	private static CellStyle headerCellStyle;
	private static CellStyle requiredHeaderCellStyle;
	private static CellStyle newCellBgRedStyle;
	private static CellStyle newCellBgGreenStyle;
	private static Font boldFont;

	public String reportPathGeneration(Map<String, Object> excelApiData) {
		@SuppressWarnings("unchecked")
		List<GlobalSheetBean> globalSheetBeanList = (List<GlobalSheetBean>) excelApiData.get("globalSheetList");
		String templatePath = (String) excelApiData.get("templatePath");
		String reportsPath = null;
		try (FileInputStream in = new FileInputStream(templatePath)) {
			Map<String, List<GlobalSheetBean>> envToGlobalSheetListMap = groupGlobalSheetByEnvironment(
					globalSheetBeanList);
			Date executionTimeStamp = new Date();
			for (String environment : envToGlobalSheetListMap.keySet()) {
				List<GlobalSheetBean> envGlobalSheetBeanList = envToGlobalSheetListMap.get(environment);
				String generalDir = null;
				for (GlobalSheetBean globalSheetBean : envGlobalSheetBeanList) {
					if ("Y".equals(globalSheetBean.getExecute())) {
						generalDir = globalSheetBean.getGeneralDir();
						String basePath = "";
						if (!configProp.getString("ParentReportName").trim().equalsIgnoreCase(""))
							basePath = configProp.getString("ParentReportName").trim().concat("_");
						else {
							basePath = globalSheetBean.getPath();
							if (basePath != null && !basePath.isEmpty()) {
								int firstIndexOfSlash = basePath.indexOf("/");
								int secondIndexOfSlash = basePath.indexOf("/", (firstIndexOfSlash + 1));
								basePath = basePath.substring(1, secondIndexOfSlash);
							}
						}

						if (generalDir == null || (generalDir != null && generalDir.isEmpty())) {
							generalDir = templatePath
									.substring(templatePath.lastIndexOf("\\") + 1, templatePath.length())
									.concat("\\Result");
						}

						if (basePath != null && !basePath.isEmpty()) {
							String newdate = sdt.format(executionTimeStamp);
							if (!configProp.getString("ParentReportName").trim().equalsIgnoreCase("")) {
								if (generalDir.substring(generalDir.length() - 1, generalDir.length()).equals("\\")) {
									generalDir = generalDir.concat(basePath).concat(newdate);
								} else {
									generalDir = generalDir.concat("\\").concat(basePath).concat(newdate);
								}
							} else
								generalDir = generalDir.concat("\\").concat(basePath).concat("_API_").concat(newdate);

						}
						reportsPath = generalDir;
					}
				}
			}
		} catch (Exception ex) {
			LOGGER.error("Exception during update xls sheet :: {}", ex.getMessage());
		}
		return reportsPath;
	}

	public void updateExcel(Map<String, Object> excelApiData, String generalDir) {

		@SuppressWarnings("unchecked")
		List<GlobalSheetBean> globalSheetBeanList = (List<GlobalSheetBean>) excelApiData.get("globalSheetList");
		Workbook workbook = null;
		String templatePath = (String) excelApiData.get("templatePath");
		// String reportsPath = null;
		try (FileInputStream in = new FileInputStream(templatePath)) {
			workbook = WorkbookFactory.create(in);

			if (bgRedStyle == null) {
				bgRedStyle = createRedBGCell(workbook);
			}
			if (bgGreenStyle == null) {
				bgGreenStyle = createGreenBGCell(workbook);
			}

			Map<String, List<GlobalSheetBean>> envToGlobalSheetListMap = groupGlobalSheetByEnvironment(
					globalSheetBeanList);
			Sheet globalSheet = workbook.getSheet(Constants.SHEETNAME_GLOBAL);
			Map<String, Integer> headerMap = getColumnNameToIndexMap(globalSheet);
			Date executionTimeStamp = new Date();
			for (String environment : envToGlobalSheetListMap.keySet()) {
				List<GlobalSheetBean> envGlobalSheetBeanList = envToGlobalSheetListMap.get(environment);
				// String generalDir = null;
				for (GlobalSheetBean globalSheetBean : envGlobalSheetBeanList) {
					if ("Y".equals(globalSheetBean.getExecute())) {
						// generalDir = globalSheetBean.getGeneralDir();
						// String basePath = "";
						// if
						// (!configProp.getString("ParentReportName").trim().equalsIgnoreCase(""))
						// basePath =
						// configProp.getString("ParentReportName").trim().concat("_");
						// else {
						// basePath = globalSheetBean.getPath();
						// if (basePath != null && !basePath.isEmpty()) {
						// int firstIndexOfSlash = basePath.indexOf("/");
						// int secondIndexOfSlash = basePath.indexOf("/",
						// (firstIndexOfSlash + 1));
						// basePath = basePath.substring(1, secondIndexOfSlash);
						// }
						// }
						//
						// if (generalDir == null || (generalDir != null &&
						// generalDir.isEmpty())) {
						// generalDir = templatePath
						// .substring(templatePath.lastIndexOf("\\") + 1,
						// templatePath.length())
						// .concat("\\Result");
						// }
						//
						// if (basePath != null && !basePath.isEmpty()) {
						// String newdate = sdt.format(executionTimeStamp);
						// if
						// (!configProp.getString("ParentReportName").trim().equalsIgnoreCase(""))
						// {
						// if (generalDir.substring(generalDir.length() - 1,
						// generalDir.length()).equals("\\")) {
						// generalDir =
						// generalDir.concat(basePath).concat(newdate);
						// } else {
						// generalDir =
						// generalDir.concat("\\").concat(basePath).concat(newdate);
						// }
						// } else
						// generalDir =
						// generalDir.concat("\\").concat(basePath).concat("_API_").concat(newdate);
						//
						// }
						// reportsPath = generalDir;
						updateGlobalSheet(globalSheetBean, workbook, headerMap, templatePath, executionTimeStamp,
								generalDir);
					}
				}
				if (generalDir != null && !generalDir.isEmpty()) {
					createMasterTemplateByEnvironment(templatePath, generalDir, envGlobalSheetBeanList, workbook,
							headerMap);
				}
				// return generalDir;
			}
		} catch (Exception ex) {
			LOGGER.error("Exception during update xls sheet :: {}", ex.getMessage());
		}
		/*
		 * try(FileOutputStream out = new FileOutputStream(templatePath)){
		 * if(workbook!=null) { workbook.write(out); workbook.close(); } }
		 * catch(Exception ex) {
		 * LOGGER.error("Exception during update workbook :: {}" ,
		 * ex.getMessage()); }
		 */
		// return reportsPath;
	}

	private Map<String, List<GlobalSheetBean>> groupGlobalSheetByEnvironment(List<GlobalSheetBean> globalSheetBeanList)
			throws Exception {
		Set<String> environmentSet = findUniqueEnvironment(globalSheetBeanList);
		Map<String, List<GlobalSheetBean>> envToGlobalSheetListMap = new HashMap<>();
		for (String environment : environmentSet) {
			if (environment != null && !environment.isEmpty()) {
				List<GlobalSheetBean> envGlobalSheetList = new ArrayList<>();
				for (GlobalSheetBean globalSheetBean : globalSheetBeanList) {
					if (environment.equalsIgnoreCase(globalSheetBean.getEnvironment())) {
						envGlobalSheetList.add(globalSheetBean);
					}
				}
				envToGlobalSheetListMap.put(environment, envGlobalSheetList);
			}
		}
		return envToGlobalSheetListMap;
	}

	private Set<String> findUniqueEnvironment(List<GlobalSheetBean> globalSheetBeanList) throws Exception {
		Map<String, GlobalSheetBean> map = new HashMap<>();
		for (GlobalSheetBean globalSheetBean : globalSheetBeanList) {
			map.put(globalSheetBean.getEnvironment(), globalSheetBean);
		}
		return map.keySet();
	}

	private void updateGlobalSheet(GlobalSheetBean globalSheetBean, Workbook workbook, Map<String, Integer> headerMap,
			String templatePath, Date executionTimeStamp, String generalDir) {
		try {
			Sheet globalSheet = workbook.getSheet(Constants.SHEETNAME_GLOBAL);
			Integer rowIndex = findRowNumByOperationNameAndEnvironment(globalSheet, globalSheetBean.getOperationName(),
					globalSheetBean.getEnvironment(), headerMap);
			Row dataRow = globalSheet.getRow(rowIndex);
			if (dataRow != null) {
				Cell runDateCell = dataRow.getCell(headerMap.get(Constants.COLUMN_RUN_DATE));
				if (runDateCell != null) {
					runDateCell.setCellValue(globalSheetBean.getRunDate().toString());
				} else if (globalSheetBean.getRunDate() != null) {
					runDateCell = dataRow.createCell(headerMap.get(Constants.COLUMN_RUN_DATE));
					runDateCell.setCellType(CellType.STRING);
					runDateCell.setCellValue(globalSheetBean.getRunDate().toString());
				}
				// globalSheet.autoSizeColumn(headerMap.get(Constants.COLUMN_RUN_DATE));

				Cell statusCell = dataRow.getCell(headerMap.get(Constants.COLUMN_STATUS));
				String status = globalSheetBean.getStatus();
				if (statusCell != null) {
					statusCell.setCellValue(globalSheetBean.getStatus());
				} else {
					statusCell = dataRow.createCell(headerMap.get(Constants.COLUMN_STATUS));
					statusCell.setCellType(CellType.STRING);
					statusCell.setCellValue(globalSheetBean.getStatus());
				}
				if (Constants.STATUS_PASS.equals(status)) {
					statusCell.setCellStyle(bgGreenStyle);
				} else if (Constants.STATUS_FAIL.equals(status)) {
					statusCell.setCellStyle(bgRedStyle);
				}
				if (null != headerMap.get(Constants.COLUMN_CREATE_BUG)) {
					Cell bugstatusCell = dataRow.getCell(headerMap.get(Constants.COLUMN_STATUS_BUG));
					if (bugstatusCell != null) {
						bugstatusCell.setCellValue(globalSheetBean.getBugStatus().toString());
					} else if (globalSheetBean.getRunDate() != null) {
						bugstatusCell = dataRow.createCell(headerMap.get(Constants.COLUMN_STATUS_BUG));
						bugstatusCell.setCellType(CellType.STRING);
						bugstatusCell.setCellValue(globalSheetBean.getBugStatus().toString());
					}

					Cell bugIdCell = dataRow.getCell(headerMap.get(Constants.COLUMN_ID_BUG));
					if (bugIdCell != null) {
						bugIdCell.setCellValue(globalSheetBean.getBugId().toString());
					} else if (globalSheetBean.getRunDate() != null) {
						bugIdCell = dataRow.createCell(headerMap.get(Constants.COLUMN_ID_BUG));
						bugIdCell.setCellType(CellType.STRING);
						bugIdCell.setCellValue(globalSheetBean.getBugId().toString());
					}
				}
				// globalSheet.autoSizeColumn(headerMap.get(Constants.COLUMN_STATUS));
			}

			List<APISheetBean> apiSheetList = globalSheetBean.getApiSheetList();
			Sheet apiDetailsSheet = workbook.getSheet(globalSheetBean.getWorkSheet());
			Map<String, Integer> map = getColumnNameToIndexMap(apiDetailsSheet);
			Map<String, Integer> tcidmap = getRowNumByTCID(apiDetailsSheet, map);
			String operationName = globalSheetBean.getOperationName();
			String produces = globalSheetBean.getProduces();
			String consumes = globalSheetBean.getConsumes();
			for (APISheetBean apiSheet : apiSheetList) {
				if ("Y".equals(apiSheet.getExecute())) {
					updateApiSheet(apiSheet, apiDetailsSheet, map, tcidmap);
					// if(!configProp.getString("HandleBulkData").equalsIgnoreCase("yes")){
					createRequestResponseJson(generalDir, apiSheet, operationName, produces, consumes);
					// }
				}
			}
			// if(!configProp.getString("HandleBulkData").equalsIgnoreCase("yes")){
			createApiXls(templatePath, generalDir, operationName, globalSheet.getRow(0), dataRow,
					workbook.getSheet(Constants.SHEETNAME_GLOBAL_PARAMETERS), apiDetailsSheet);
			// }
		} catch (Exception ex) {
			LOGGER.error("Exception during updateGlobalSheet :: {}", ex.getMessage());
		}
	}

	private void updateApiSheet(APISheetBean apiSheet, Sheet apiDetailsSheet, Map<String, Integer> map,
			Map<String, Integer> tcidmap) {
		try {
			// Integer rowIndex = findRowNumByTCID(apiDetailsSheet,
			// apiSheet.getTcid(), map);
			Integer rowIndex = tcidmap.get(apiSheet.getTcid());
			if (rowIndex == 0) {
				return;
			}
			Row dataRow = apiDetailsSheet.getRow(rowIndex);
			if (dataRow != null) {

				Cell dateDataCell = dataRow.getCell(map.get(Constants.COLUMN_DATE));
				if (dateDataCell != null) {
					dateDataCell.setCellValue(apiSheet.getDate().toString());
				} else {
					dateDataCell = dataRow.createCell(map.get(Constants.COLUMN_DATE));
					dateDataCell.setCellType(CellType.STRING);
					dateDataCell.setCellValue(apiSheet.getDate().toString());
				}
				// apiDetailsSheet.autoSizeColumn(map.get(Constants.COLUMN_DATE));

				Cell actualDataCell = dataRow.getCell(map.get(Constants.COLUMN_ACTUAL));
				if (actualDataCell != null) {
					actualDataCell.setCellValue(apiSheet.getActual());
				} else {
					actualDataCell = dataRow.createCell(map.get(Constants.COLUMN_ACTUAL));
					actualDataCell.setCellType(CellType.STRING);
					actualDataCell.setCellValue(apiSheet.getActual());
				}
				// apiDetailsSheet.autoSizeColumn(map.get(Constants.COLUMN_ACTUAL));

				Cell statusCell = dataRow.getCell(map.get(Constants.COLUMN_STATUS));
				String status = apiSheet.getStatus();
				if (statusCell != null) {
					statusCell.setCellValue(apiSheet.getStatus());
				} else {
					statusCell = dataRow.createCell(map.get(Constants.COLUMN_STATUS));
					statusCell.setCellType(CellType.STRING);
					statusCell.setCellValue(apiSheet.getStatus());
				}
				if (Constants.STATUS_PASS.equals(status)) {
					statusCell.setCellStyle(bgGreenStyle);
				} else if (Constants.STATUS_FAIL.equals(status) || Constants.STATUS_ERROR.equals(status)) {
					statusCell.setCellStyle(bgRedStyle);
				}
				// apiDetailsSheet.autoSizeColumn(map.get(Constants.COLUMN_STATUS));

				Cell statusDetailCell = dataRow.getCell(map.get(Constants.COLUMN_STATUS_DETAIL));
				if (statusDetailCell != null) {
					statusDetailCell.setCellValue(apiSheet.getStatusDetail());
				} else {
					statusDetailCell = dataRow.createCell(map.get(Constants.COLUMN_STATUS_DETAIL));
					statusDetailCell.setCellType(CellType.STRING);
					statusDetailCell.setCellValue(apiSheet.getStatusDetail());
				}
				// apiDetailsSheet.autoSizeColumn(map.get(Constants.COLUMN_STATUS_DETAIL));

				// Bhanu - Var Elements in expect element column
				Cell expectCell = dataRow.getCell(map.get(Constants.COLUMN_EXPECTED)); // banu
				if (expectCell != null) {
					expectCell.setCellValue(apiSheet.getExpected());
				} else {
					expectCell = dataRow.createCell(map.get(Constants.COLUMN_EXPECTED));
					expectCell.setCellType(CellType.STRING);
					expectCell.setCellValue(apiSheet.getExpected());
				}
				// apiDetailsSheet.autoSizeColumn(map.get(Constants.COLUMN_EXPECTED));
				// end Bhanu
				if (null != apiSheet.getRestUrl()) {
					Cell restUrlCell = dataRow.getCell(map.get(Constants.COLUMN_REST_URL));
					if (restUrlCell != null) {
						restUrlCell.setCellValue(apiSheet.getRestUrl());
					} else {
						restUrlCell = dataRow.createCell(map.get(Constants.COLUMN_REST_URL));
						restUrlCell.setCellType(CellType.STRING);
						restUrlCell.setCellValue(apiSheet.getRestUrl());
					}
					// apiDetailsSheet.autoSizeColumn(map.get(Constants.COLUMN_REST_URL));
				}

				if (null != apiSheet.getInputRequest()) {
					Cell inputrequest = dataRow.getCell(map.get(Constants.COLUMN_REQUEST));
					if (inputrequest != null) {
						inputrequest.setCellValue(apiSheet.getInputRequest());
					} else {
						inputrequest = dataRow.createCell(map.get(Constants.COLUMN_REQUEST));
						inputrequest.setCellType(CellType.STRING);
						inputrequest.setCellValue(apiSheet.getInputRequest());
					}
					// apiDetailsSheet.autoSizeColumn(map.get(Constants.COLUMN_REQUEST));
				}
				if (null != apiSheet.getInputRequestPath()) {
					Cell inputrequestpath = dataRow.getCell(map.get(Constants.COLUMN_REQUEST_PATH));
					if (inputrequestpath != null) {
						inputrequestpath.setCellValue(apiSheet.getInputRequestPath());
					} else {
						inputrequestpath = dataRow.createCell(map.get(Constants.COLUMN_REQUEST_PATH));
						inputrequestpath.setCellType(CellType.STRING);
						inputrequestpath.setCellValue(apiSheet.getInputRequestPath());
					}
					// apiDetailsSheet.autoSizeColumn(map.get(Constants.COLUMN_REQUEST_PATH));
				}
			}
		} catch (Exception ex) {
			LOGGER.error("Exception during updateApiSheet :: {}", ex.getMessage());
		}
	}

	private Integer findRowNumByOperationNameAndEnvironment(Sheet globalSheet, String operationName, String environment,
			Map<String, Integer> map) throws Exception {

		int totalNoOfRows = globalSheet.getPhysicalNumberOfRows();
		Integer rowIndex = 0;
		for (int x = 1; x <= totalNoOfRows; x++) {
			Row dataRow = globalSheet.getRow(x);
			if (dataRow != null) {
				Cell dataCellOpName = dataRow.getCell(map.get(Constants.COLUMN_OPERATION_NAME));
				Cell dataCellEnv = dataRow.getCell(map.get(Constants.COLUMN_ENVIRONMENT));
				if (dataCellOpName != null && dataCellEnv != null) {
					String operationNameValue = dataCellOpName.getStringCellValue();
					String environmentValue = dataCellEnv.getStringCellValue();
					if ((operationNameValue != null && operationNameValue.equalsIgnoreCase(operationName))
							&& (environmentValue != null && environmentValue.equalsIgnoreCase(environment))) {
						rowIndex = Integer.valueOf(x);
						break;
					}
				}
			}
		}
		return rowIndex;
	}

	private Integer findRowNumByTCID(Sheet apiDetailsSheet, String tcid, Map<String, Integer> map) throws Exception {

		int totalNoOfRows = apiDetailsSheet.getPhysicalNumberOfRows();
		Integer rowIndex = 0;
		for (int x = 1; x <= totalNoOfRows; x++) {
			Row dataRow = apiDetailsSheet.getRow(x);
			if (dataRow != null) {
				Cell dataCell = dataRow.getCell(map.get(Constants.COLUMN_TCID));
				if (dataCell != null) {

					String tcidValue = (String) getCellValue(dataCell);
					if (tcidValue != null && tcidValue.equalsIgnoreCase(tcid)) {
						rowIndex = Integer.valueOf(x);
						break;
					}
				}
			}
		}
		return rowIndex;
	}

	private Map<String, Integer> getRowNumByTCID(Sheet apiDetailsSheet, Map<String, Integer> map) throws Exception {
		Map<String, Integer> finalmap = new HashMap<String, Integer>();
		int totalNoOfRows = apiDetailsSheet.getPhysicalNumberOfRows();
		Integer rowIndex = 0;
		for (int x = 1; x <= totalNoOfRows; x++) {
			Row dataRow = apiDetailsSheet.getRow(x);
			if (dataRow != null) {
				Cell dataCell = dataRow.getCell(map.get(Constants.COLUMN_TCID));
				if (dataCell != null) {
					String tcidValue = (String) getCellValue(dataCell);
					if (tcidValue != null) {
						rowIndex = Integer.valueOf(x);
						finalmap.put(tcidValue, rowIndex);
					}
				}
			}
		}
		return finalmap;
	}

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

	private CellStyle createRedBGCell(Workbook workbook) throws Exception {

		CellStyle bgRedStyle = workbook.createCellStyle();
		bgRedStyle.setFillForegroundColor(IndexedColors.RED1.getIndex());
		bgRedStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		bgRedStyle.setBorderBottom(BorderStyle.THIN);
		bgRedStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		bgRedStyle.setBorderLeft(BorderStyle.THIN);
		bgRedStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		bgRedStyle.setBorderRight(BorderStyle.THIN);
		bgRedStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
		bgRedStyle.setBorderTop(BorderStyle.THIN);
		bgRedStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
		return bgRedStyle;
	}

	private CellStyle createGreenBGCell(Workbook workbook) throws Exception {

		CellStyle bgGreenStyle = workbook.createCellStyle();
		bgGreenStyle.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.getIndex());
		bgGreenStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		bgGreenStyle.setBorderBottom(BorderStyle.THIN);
		bgGreenStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		bgGreenStyle.setBorderLeft(BorderStyle.THIN);
		bgGreenStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		bgGreenStyle.setBorderRight(BorderStyle.THIN);
		bgGreenStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
		bgGreenStyle.setBorderTop(BorderStyle.THIN);
		bgGreenStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
		return bgGreenStyle;
	}

	private void createHeaderStyle(Workbook workbook) throws Exception {
		// if(boldFont == null) {
		createBoldFont(workbook);
		// }
		headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(boldFont);
	}

	private void createHeaderRequiredStyle(Workbook workbook) throws Exception {
		requiredHeaderCellStyle = workbook.createCellStyle();
		requiredHeaderCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
		requiredHeaderCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		requiredHeaderCellStyle.setBorderBottom(BorderStyle.THIN);
		requiredHeaderCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		requiredHeaderCellStyle.setBorderLeft(BorderStyle.THIN);
		requiredHeaderCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		requiredHeaderCellStyle.setBorderRight(BorderStyle.THIN);
		requiredHeaderCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
		requiredHeaderCellStyle.setBorderTop(BorderStyle.THIN);
		requiredHeaderCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
		requiredHeaderCellStyle.setFont(boldFont);
	}

	private Font createBoldFont(Workbook workbook) throws Exception {

		boldFont = workbook.createFont();
		boldFont.setFontHeightInPoints((short) 11);
		boldFont.setFontName("Arial");
		boldFont.setColor(IndexedColors.BLACK.getIndex());
		boldFont.setBold(true);
		boldFont.setItalic(false);
		return boldFont;
	}

	private void createRequestResponseJson(String generalDir, APISheetBean apiSheet, String operationName,
			String produces, String consumes) {

		try {
			if (generalDir != null && !generalDir.isEmpty()) {
				File generalDirectory = new File(generalDir);
				if (!generalDirectory.exists()) {
					generalDirectory.mkdir();
				}

				String reqRespJsonDirPath = generalDir.concat("\\").concat(operationName);

				File reqRespJsonDirectory = new File(reqRespJsonDirPath);
				if (!reqRespJsonDirectory.exists()) {
					reqRespJsonDirectory.mkdir();
				}
				String requestObjectFileName = reqRespJsonDirPath.concat("\\");
				String responseObjectFileName = reqRespJsonDirPath.concat("\\");
				String headerObjectFileName = reqRespJsonDirPath.concat("\\");

				if (apiSheet.getTcid() != null && !apiSheet.getTcid().isEmpty()) {
					if ((consumes != null) && consumes.contains("text")) {
						requestObjectFileName = requestObjectFileName.concat(apiSheet.getTcid())
								.concat("_Request_Object.txt");
					} else {
						requestObjectFileName = requestObjectFileName.concat(apiSheet.getTcid())
								.concat("_Request_Object.json");
					}
					if ((produces != null) && produces.contains("text/plain")) {
						responseObjectFileName = responseObjectFileName.concat(apiSheet.getTcid())
								.concat("_Response_Object.txt");
					} else {
						responseObjectFileName = responseObjectFileName.concat(apiSheet.getTcid())
								.concat("_Response_Object.json");
					}
					headerObjectFileName = headerObjectFileName.concat(apiSheet.getTcid())
							.concat("_Header_Object.json");
				} else {
					if ((consumes != null) && consumes.contains("text")) {
						requestObjectFileName = requestObjectFileName.concat("_Request_Object.txt");
					} else {
						requestObjectFileName = requestObjectFileName.concat("_Request_Object.json");
					}
					if ((produces != null) && produces.contains("text/plain")) {
						responseObjectFileName = responseObjectFileName.concat("_Response_Object.txt");
					} else {
						responseObjectFileName = responseObjectFileName.concat("_Response_Object.json");
					}
					headerObjectFileName = headerObjectFileName.concat("_Header_Object.json");
				}
				if (!configProp.getString("HandleBulkData").equalsIgnoreCase("yes")) {
					if (apiSheet.getApiRequest() != null) {
						try (FileWriter out = new FileWriter(requestObjectFileName)) {
							JsonElement requestJson = apiSheet.getApiRequest();
							if (requestJson.getAsJsonObject().get("arrayKey") != null) {
								String jsonOutput = gson.toJson(requestJson.getAsJsonObject().get("arrayKey"));
								out.write(jsonOutput);
							} else {
								if(consumes.contains("text")){
									String textContent=apiSheet.getApiRequest().get("request").getAsString();
									out.write(textContent);
								}else{
								String jsonOutput = gson.toJson(requestJson);
								out.write(jsonOutput);
								}
							}
						} catch (Exception ex) {
							LOGGER.error("Exception during writing request object :: {}", ex.getMessage());
						}
					}
				}
				if (apiSheet.getApiResponse() != null && apiSheet.getApiResponse().getBody() != null) {
					String responseObj = apiSheet.getApiResponse().getBody().asString();
					if (responseObj != null && !responseObj.isEmpty()) {
						try (FileWriter out = new FileWriter(responseObjectFileName)) {
							if ((produces != null) && produces.contains("text/plain")) {
								out.write(responseObj);
							} else {
								// Bagya - avoiding GsonBuilder in order prevent
								// the conversion of special characters - starts
								String[] relaxChar = configProp.getString("SpecialCharExcp").split(",");
								boolean specChar = false;
								for (String s : relaxChar) {
									if (responseObj.contains(s))
										specChar = true;
								}
								if (specChar)
									out.write(responseObj);
								// Bagya - avoiding GsonBuilder in order prevent
								// the conversion of special characters - ends
								else {
									JsonElement responseJson = jsonParser.parse(responseObj);
									String jsonOutput = gson.toJson(responseJson);
									out.write(jsonOutput);
								}
							}
						} catch (Exception ex) {
							LOGGER.error("Exception during writing response object :: {}", ex.getMessage());
						}
					}
				}
				if (!configProp.getString("HandleBulkData").equalsIgnoreCase("yes")) {
					if (apiSheet.getHeaderParam() != null) {
						try (FileWriter out = new FileWriter(headerObjectFileName)) {
							out.write(apiSheet.getHeaderParam().toString());
						} catch (Exception ex) {
							LOGGER.error("Exception during writing header object :: {}", ex.getMessage());
						}
					}
				}
			}
		} catch (Exception ex) {
			LOGGER.error("Exception during creating request and response JSON object :: {}", ex.getMessage());
		}
	}

	private void createApiXls(String templatePath, String generalDir, String operationName, Row globalSheetHeaderRow,
			Row globalSheetDataRow, Sheet globalParameterSheet, Sheet apiSheet) {
		try {
			String apiXlsFilePath = null;
			if (generalDir != null && !generalDir.isEmpty()) {
				File generalDirectory = new File(generalDir);
				if (!generalDirectory.exists()) {
					generalDirectory.mkdir();
				}

				if (templatePath.endsWith("xls")) {
					apiXlsFilePath = generalDir.concat("\\").concat(operationName).concat("\\").concat(operationName)
							.concat("_Rest_API_Data.xls");

					try (FileOutputStream out = new FileOutputStream(apiXlsFilePath)) {
						HSSFWorkbook workbook = new HSSFWorkbook();
						createHeaderStyle(workbook);
						createHeaderRequiredStyle(workbook);
						newCellBgRedStyle = createRedBGCell(workbook);
						newCellBgGreenStyle = createGreenBGCell(workbook);

						HSSFSheet globalSheet = workbook.createSheet(Constants.SHEETNAME_GLOBAL);
						Row newHeaderRow = globalSheet.createRow(0);
						copyRow(newHeaderRow, globalSheetHeaderRow, globalSheet, workbook, true);
						Row newDataRow = globalSheet.createRow(1);
						copyRow(newDataRow, globalSheetDataRow, globalSheet, workbook, false);

						HSSFSheet newGlobalParameterSheet = workbook.createSheet(Constants.SHEETNAME_GLOBAL_PARAMETERS);
						Row newGPHeaderRow = newGlobalParameterSheet.createRow(0);
						copyRow(newGPHeaderRow, globalParameterSheet.getRow(0), newGlobalParameterSheet, workbook,
								true);
						for (int x = 1; x <= globalParameterSheet.getLastRowNum(); x++) {
							Row sourceRow = globalParameterSheet.getRow(x);
							if (sourceRow != null) {
								Row newGPDataRow = newGlobalParameterSheet.createRow(x);
								copyRow(newGPDataRow, globalParameterSheet.getRow(x), newGlobalParameterSheet, workbook,
										false);
							}
						}

						HSSFSheet newApiSheet = workbook.createSheet(apiSheet.getSheetName());
						Row newAPIHeaderRow = newApiSheet.createRow(0);
						copyRow(newAPIHeaderRow, apiSheet.getRow(0), newApiSheet, workbook, true);
						int rowid = 0;
						for (int x = 1; x <= apiSheet.getLastRowNum(); x++) {
							Row sourceRow = apiSheet.getRow(x);
							if (sourceRow != null) {
								if (null != sourceRow.getCell(0)
										&& sourceRow.getCell(0).getStringCellValue().equalsIgnoreCase("Y")) {
									rowid = rowid + 1;
									Row newAPIDataRow = newApiSheet.createRow(rowid);
									copyRow(newAPIDataRow, apiSheet.getRow(x), newApiSheet, workbook, false);
								}
							}
						}
						workbook.write(out);
						workbook.close();
					} catch (FileNotFoundException e) {
						LOGGER.error("ExcelUpdaterService - FileNotFoundException :: ", e);
					} catch (IOException ex) {
						LOGGER.error("ExcelUpdaterService - IOException :: ", ex);
					}
				} else {
					if (configProp.getString("HandleBulkData").equalsIgnoreCase("yes")) {

						apiXlsFilePath = generalDir.concat("\\").concat(operationName).concat("\\")
								.concat(operationName).concat("_Rest_API_Data.xlsx");

						try (FileOutputStream out = new FileOutputStream(apiXlsFilePath)) {
							SXSSFWorkbook workbook = new SXSSFWorkbook(100);
							createHeaderStyle(workbook);
							createHeaderRequiredStyle(workbook);
							newCellBgRedStyle = createRedBGCell(workbook);
							newCellBgGreenStyle = createGreenBGCell(workbook);

							SXSSFSheet globalSheet = workbook.createSheet(Constants.SHEETNAME_GLOBAL);
							Row newHeaderRow = globalSheet.createRow(0);
							copyRow(newHeaderRow, globalSheetHeaderRow, globalSheet, workbook, true);
							Row newDataRow = globalSheet.createRow(1);
							copyRow(newDataRow, globalSheetDataRow, globalSheet, workbook, false);

							SXSSFSheet newGlobalParameterSheet = workbook
									.createSheet(Constants.SHEETNAME_GLOBAL_PARAMETERS);
							Row newGPHeaderRow = newGlobalParameterSheet.createRow(0);
							copyRow(newGPHeaderRow, globalParameterSheet.getRow(0), newGlobalParameterSheet, workbook,
									true);
							for (int x = 1; x <= globalParameterSheet.getLastRowNum(); x++) {
								Row sourceRow = globalParameterSheet.getRow(x);
								if (sourceRow != null) {
									Row newGPDataRow = newGlobalParameterSheet.createRow(x);
									copyRow(newGPDataRow, globalParameterSheet.getRow(x), newGlobalParameterSheet,
											workbook, false);
								}
							}

							SXSSFSheet newApiSheet = workbook.createSheet(apiSheet.getSheetName());
							Row newAPIHeaderRow = newApiSheet.createRow(0);
							copyRow(newAPIHeaderRow, apiSheet.getRow(0), newApiSheet, workbook, true);
							int rowid = 0;
							for (int x = 1; x <= apiSheet.getLastRowNum(); x++) {
								Row sourceRow = apiSheet.getRow(x);
								if (sourceRow != null) {
									if (null != sourceRow.getCell(0)
											&& sourceRow.getCell(0).getStringCellValue().equalsIgnoreCase("Y")) {
										rowid = rowid + 1;
										Row newAPIDataRow = newApiSheet.createRow(rowid);
										copyRow(newAPIDataRow, apiSheet.getRow(x), newApiSheet, workbook, false);
									}
								}
							}
							workbook.write(out);
							workbook.close();
						} catch (FileNotFoundException e) {
							LOGGER.error("ExcelUpdaterService - FileNotFoundException :: ", e);
						} catch (IOException ex) {
							LOGGER.error("ExcelUpdaterService - IOException :: ", ex);
						}
					} else {
						apiXlsFilePath = generalDir.concat("\\").concat(operationName).concat("\\")
								.concat(operationName).concat("_Rest_API_Data.xlsx");

						try (FileOutputStream out = new FileOutputStream(apiXlsFilePath)) {
							XSSFWorkbook workbook = new XSSFWorkbook();
							createHeaderStyle(workbook);
							createHeaderRequiredStyle(workbook);
							newCellBgRedStyle = createRedBGCell(workbook);
							newCellBgGreenStyle = createGreenBGCell(workbook);

							XSSFSheet globalSheet = workbook.createSheet(Constants.SHEETNAME_GLOBAL);
							Row newHeaderRow = globalSheet.createRow(0);
							copyRow(newHeaderRow, globalSheetHeaderRow, globalSheet, workbook, true);
							Row newDataRow = globalSheet.createRow(1);
							copyRow(newDataRow, globalSheetDataRow, globalSheet, workbook, false);

							XSSFSheet newGlobalParameterSheet = workbook
									.createSheet(Constants.SHEETNAME_GLOBAL_PARAMETERS);
							Row newGPHeaderRow = newGlobalParameterSheet.createRow(0);
							copyRow(newGPHeaderRow, globalParameterSheet.getRow(0), newGlobalParameterSheet, workbook,
									true);
							for (int x = 1; x <= globalParameterSheet.getLastRowNum(); x++) {
								Row sourceRow = globalParameterSheet.getRow(x);
								if (sourceRow != null) {
									Row newGPDataRow = newGlobalParameterSheet.createRow(x);
									copyRow(newGPDataRow, globalParameterSheet.getRow(x), newGlobalParameterSheet,
											workbook, false);
								}
							}

							XSSFSheet newApiSheet = workbook.createSheet(apiSheet.getSheetName());
							Row newAPIHeaderRow = newApiSheet.createRow(0);
							copyRow(newAPIHeaderRow, apiSheet.getRow(0), newApiSheet, workbook, true);
							int rowid = 0;
							for (int x = 1; x <= apiSheet.getLastRowNum(); x++) {
								Row sourceRow = apiSheet.getRow(x);
								if (sourceRow != null) {
									if (null != sourceRow.getCell(0)
											&& sourceRow.getCell(0).getStringCellValue().equalsIgnoreCase("Y")) {
										rowid = rowid + 1;
										Row newAPIDataRow = newApiSheet.createRow(rowid);
										copyRow(newAPIDataRow, apiSheet.getRow(x), newApiSheet, workbook, false);
									}
								}
							}
							workbook.write(out);
							workbook.close();
						} catch (FileNotFoundException e) {
							LOGGER.error("ExcelUpdaterService - FileNotFoundException :: ", e);
						} catch (IOException ex) {
							LOGGER.error("ExcelUpdaterService - IOException :: ", ex);
						}
					}
				}
			}
		} catch (Exception ex) {
			LOGGER.error("Exception during creating API xls :: {}", ex);
		}
	}

	private void copyRow(Row newRow, Row sourceRow, Sheet workSheet, Workbook workbook, boolean isHeaderRow)
			throws Exception {

		// Loop through source columns to add to new row
		// CellStyle newCellStyle = workbook.createCellStyle();
		// String test;
		for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
			// Grab a copy of the old/new cell
			Cell oldCell = sourceRow.getCell(i);
			Cell newCell = newRow.createCell(i);

			// If the old cell is null jump to next cell
			if (oldCell == null) {
				newCell = null;
				continue;
			}

			// newCellStyle.cloneStyleFrom();
			CellStyle oldStyle = oldCell.getCellStyle();
			if (isHeaderRow) {
				if (oldStyle != null && oldStyle.getFillForegroundColor() == IndexedColors.RED.getIndex()) {
					newCell.setCellStyle(requiredHeaderCellStyle);
				} else {
					newCell.setCellStyle(headerCellStyle);
				}
			} else if (oldStyle != null && oldStyle.getFillForegroundColor() == IndexedColors.RED1.getIndex()) {
				newCell.setCellStyle(newCellBgRedStyle);
			} else if (oldStyle != null && oldStyle.getFillForegroundColor() == IndexedColors.BRIGHT_GREEN.getIndex()) {
				newCell.setCellStyle(newCellBgGreenStyle);
			}

			// If there is a cell comment, copy
			if (oldCell.getCellComment() != null) {
				newCell.setCellComment(oldCell.getCellComment());
			}

			// If there is a cell hyperlink, copy
			if (oldCell.getHyperlink() != null) {
				newCell.setHyperlink(oldCell.getHyperlink());
			}

			// Set the cell data type
			newCell.setCellType(oldCell.getCellType());

			if (CellType.NUMERIC.equals(oldCell.getCellType())) {
				newCell.setCellValue(oldCell.getNumericCellValue());
			} else if (CellType.STRING.equals(oldCell.getCellType())) {
				if (oldCell.getRichStringCellValue().getString().contains("DV_")) {
					String dynamicVal = replaceDynamicVal(workSheet.getSheetName(), oldCell.getStringCellValue());
					newCell.setCellValue(dynamicVal);
				} else
					newCell.setCellValue(oldCell.getRichStringCellValue());
			} else if (CellType.BOOLEAN.equals(oldCell.getCellType())) {
				newCell.setCellValue(oldCell.getBooleanCellValue());
			}
			if (!configProp.getString("HandleBulkData").equalsIgnoreCase("yes")) {
				workSheet.autoSizeColumn(i);
			}
		}
	}

	private String replaceDynamicVal(String sheetname, String cellValue) {
		String dynamicVal = "";
		String[] tempVal = cellValue.split("_", 6);
		dynamicVal = RestAPIInvokerService.dynamicElementsMap.get(sheetname + "_" + tempVal[5]);
		return dynamicVal;
	}

	private void createMasterTemplateByEnvironment(String templatePath, String generalDir,
			List<GlobalSheetBean> envGlobalSheetBeanList, Workbook workbook, Map<String, Integer> headerMap)
			throws Exception {
		if (generalDir != null && !generalDir.isEmpty()) {
			File generalDirectory = new File(generalDir);
			if (!generalDirectory.exists()) {
				generalDirectory.mkdir();
			}
			if (templatePath.endsWith(".xls")) {
				String masterApiXlsFilePath = generalDir.concat("\\").concat("Master_Result_Rest_API_Data.xls");
				try (FileOutputStream out = new FileOutputStream(masterApiXlsFilePath)) {
					HSSFWorkbook masterWorkbook = new HSSFWorkbook();
					Sheet globalSheet = workbook.getSheet(Constants.SHEETNAME_GLOBAL);
					Row headerRow = globalSheet.getRow(0);

					createHeaderStyle(masterWorkbook);
					createHeaderRequiredStyle(masterWorkbook);
					newCellBgRedStyle = createRedBGCell(masterWorkbook);
					newCellBgGreenStyle = createGreenBGCell(masterWorkbook);

					HSSFSheet masterGlobalSheet = masterWorkbook.createSheet(Constants.SHEETNAME_GLOBAL);
					Row newHeaderRow = masterGlobalSheet.createRow(0);
					copyRow(newHeaderRow, headerRow, masterGlobalSheet, workbook, true);

					Sheet globalParameterSheet = workbook.getSheet(Constants.SHEETNAME_GLOBAL_PARAMETERS);
					HSSFSheet newGlobalParameterSheet = masterWorkbook
							.createSheet(Constants.SHEETNAME_GLOBAL_PARAMETERS);
					Row newGPHeaderRow = newGlobalParameterSheet.createRow(0);
					copyRow(newGPHeaderRow, globalParameterSheet.getRow(0), newGlobalParameterSheet, workbook, true);
					for (int x = 1; x <= globalParameterSheet.getLastRowNum(); x++) {
						Row sourceRow = globalParameterSheet.getRow(x);
						if (sourceRow != null) {
							Row newGPDataRow = newGlobalParameterSheet.createRow(x);
							copyRow(newGPDataRow, globalParameterSheet.getRow(x), newGlobalParameterSheet, workbook,
									false);
						}
					}

					int rowNum = 1;

					for (GlobalSheetBean globalSheetBean : envGlobalSheetBeanList) {
						Integer rowIndex = findRowNumByOperationNameAndEnvironment(globalSheet,
								globalSheetBean.getOperationName(), globalSheetBean.getEnvironment(), headerMap);
						Row dataRow = globalSheet.getRow(rowIndex);

						Row newDataRow = masterGlobalSheet.createRow(rowNum);
						copyRow(newDataRow, dataRow, masterGlobalSheet, workbook, false);
						rowNum++;

						Sheet apiSheet = workbook.getSheet(globalSheetBean.getWorkSheet());
						HSSFSheet newApiSheet = masterWorkbook.createSheet(apiSheet.getSheetName());
						Row newAPIHeaderRow = newApiSheet.createRow(0);
						copyRow(newAPIHeaderRow, apiSheet.getRow(0), newApiSheet, workbook, true);

						int rowid = 0;
						for (int x = 1; x <= apiSheet.getLastRowNum(); x++) {
							Row sourceRow = apiSheet.getRow(x);
							if (sourceRow != null) {
								if (null != sourceRow.getCell(0)
										&& sourceRow.getCell(0).getStringCellValue().equalsIgnoreCase("Y")) {
									rowid = rowid + 1;
									Row newAPIDataRow = newApiSheet.createRow(rowid);
									copyRow(newAPIDataRow, apiSheet.getRow(x), newApiSheet, workbook, false);
								}
							}
						}
					}
					masterWorkbook.write(out);
					masterWorkbook.close();
				} catch (FileNotFoundException e) {
					LOGGER.error("ExcelUpdaterService - FileNotFoundException :: ", e);
				} catch (IOException ex) {
					LOGGER.error("ExcelUpdaterService - IOException :: ", ex);
				}
			} else {
				if (configProp.getString("HandleBulkData").equalsIgnoreCase("yes")) {
					String masterApiXlsFilePath = generalDir.concat("\\").concat("Master_Result_Rest_API_Data.xlsx");
					try (FileOutputStream out = new FileOutputStream(masterApiXlsFilePath)) {
						SXSSFWorkbook masterWorkbook = new SXSSFWorkbook(100);
						Sheet globalSheet = workbook.getSheet(Constants.SHEETNAME_GLOBAL);
						Row headerRow = globalSheet.getRow(0);

						createHeaderStyle(masterWorkbook);
						createHeaderRequiredStyle(masterWorkbook);
						newCellBgRedStyle = createRedBGCell(masterWorkbook);
						newCellBgGreenStyle = createGreenBGCell(masterWorkbook);

						SXSSFSheet masterGlobalSheet = masterWorkbook.createSheet(Constants.SHEETNAME_GLOBAL);
						Row newHeaderRow = masterGlobalSheet.createRow(0);
						copyRow(newHeaderRow, headerRow, masterGlobalSheet, workbook, true);

						Sheet globalParameterSheet = workbook.getSheet(Constants.SHEETNAME_GLOBAL_PARAMETERS);
						SXSSFSheet newGlobalParameterSheet = masterWorkbook
								.createSheet(Constants.SHEETNAME_GLOBAL_PARAMETERS);
						Row newGPHeaderRow = newGlobalParameterSheet.createRow(0);
						copyRow(newGPHeaderRow, globalParameterSheet.getRow(0), newGlobalParameterSheet, workbook,
								true);
						for (int x = 1; x <= globalParameterSheet.getLastRowNum(); x++) {
							Row sourceRow = globalParameterSheet.getRow(x);
							if (sourceRow != null) {
								Row newGPDataRow = newGlobalParameterSheet.createRow(x);
								copyRow(newGPDataRow, globalParameterSheet.getRow(x), newGlobalParameterSheet, workbook,
										false);
							}
						}

						int rowNum = 1;

						for (GlobalSheetBean globalSheetBean : envGlobalSheetBeanList) {
							Integer rowIndex = findRowNumByOperationNameAndEnvironment(globalSheet,
									globalSheetBean.getOperationName(), globalSheetBean.getEnvironment(), headerMap);
							Row dataRow = globalSheet.getRow(rowIndex);

							Row newDataRow = masterGlobalSheet.createRow(rowNum);
							copyRow(newDataRow, dataRow, masterGlobalSheet, workbook, false);
							rowNum++;

							Sheet apiSheet = workbook.getSheet(globalSheetBean.getWorkSheet());
							SXSSFSheet newApiSheet = masterWorkbook.createSheet(apiSheet.getSheetName());
							Row newAPIHeaderRow = newApiSheet.createRow(0);
							copyRow(newAPIHeaderRow, apiSheet.getRow(0), newApiSheet, workbook, true);

							int rowid = 0;
							for (int x = 1; x <= apiSheet.getLastRowNum(); x++) {
								Row sourceRow = apiSheet.getRow(x);
								if (sourceRow != null) {
									if (null != sourceRow.getCell(0)
											&& sourceRow.getCell(0).getStringCellValue().equalsIgnoreCase("Y")) {
										rowid = rowid + 1;
										Row newAPIDataRow = newApiSheet.createRow(rowid);
										copyRow(newAPIDataRow, apiSheet.getRow(x), newApiSheet, workbook, false);
									}
								}
							}
						}
						masterWorkbook.write(out);
						masterWorkbook.close();
					} catch (FileNotFoundException e) {
						LOGGER.error("ExcelUpdaterService - FileNotFoundException :: ", e);
					} catch (IOException ex) {
						LOGGER.error("ExcelUpdaterService - IOException :: ", ex);
					}
				} else {
					String masterApiXlsFilePath = generalDir.concat("\\").concat("Master_Result_Rest_API_Data.xlsx");
					try (FileOutputStream out = new FileOutputStream(masterApiXlsFilePath)) {
						XSSFWorkbook masterWorkbook = new XSSFWorkbook();
						Sheet globalSheet = workbook.getSheet(Constants.SHEETNAME_GLOBAL);
						Row headerRow = globalSheet.getRow(0);

						createHeaderStyle(masterWorkbook);
						createHeaderRequiredStyle(masterWorkbook);
						newCellBgRedStyle = createRedBGCell(masterWorkbook);
						newCellBgGreenStyle = createGreenBGCell(masterWorkbook);

						XSSFSheet masterGlobalSheet = masterWorkbook.createSheet(Constants.SHEETNAME_GLOBAL);
						Row newHeaderRow = masterGlobalSheet.createRow(0);
						copyRow(newHeaderRow, headerRow, masterGlobalSheet, workbook, true);

						Sheet globalParameterSheet = workbook.getSheet(Constants.SHEETNAME_GLOBAL_PARAMETERS);
						XSSFSheet newGlobalParameterSheet = masterWorkbook
								.createSheet(Constants.SHEETNAME_GLOBAL_PARAMETERS);
						Row newGPHeaderRow = newGlobalParameterSheet.createRow(0);
						copyRow(newGPHeaderRow, globalParameterSheet.getRow(0), newGlobalParameterSheet, workbook,
								true);
						for (int x = 1; x <= globalParameterSheet.getLastRowNum(); x++) {
							Row sourceRow = globalParameterSheet.getRow(x);
							if (sourceRow != null) {
								Row newGPDataRow = newGlobalParameterSheet.createRow(x);
								copyRow(newGPDataRow, globalParameterSheet.getRow(x), newGlobalParameterSheet, workbook,
										false);
							}
						}

						int rowNum = 1;

						for (GlobalSheetBean globalSheetBean : envGlobalSheetBeanList) {
							Integer rowIndex = findRowNumByOperationNameAndEnvironment(globalSheet,
									globalSheetBean.getOperationName(), globalSheetBean.getEnvironment(), headerMap);
							Row dataRow = globalSheet.getRow(rowIndex);

							Row newDataRow = masterGlobalSheet.createRow(rowNum);
							copyRow(newDataRow, dataRow, masterGlobalSheet, workbook, false);
							rowNum++;

							Sheet apiSheet = workbook.getSheet(globalSheetBean.getWorkSheet());
							XSSFSheet newApiSheet = masterWorkbook.createSheet(apiSheet.getSheetName());
							Row newAPIHeaderRow = newApiSheet.createRow(0);
							copyRow(newAPIHeaderRow, apiSheet.getRow(0), newApiSheet, workbook, true);
							int rowid = 0;
							for (int x = 1; x <= apiSheet.getLastRowNum(); x++) {
								Row sourceRow = apiSheet.getRow(x);
								if (sourceRow != null) {
									if (null != sourceRow.getCell(0)
											&& sourceRow.getCell(0).getStringCellValue().equalsIgnoreCase("Y")) {
										rowid = rowid + 1;
										Row newAPIDataRow = newApiSheet.createRow(rowid);
										copyRow(newAPIDataRow, apiSheet.getRow(x), newApiSheet, workbook, false);
									}
								}
							}
						}
						masterWorkbook.write(out);
						masterWorkbook.close();
					} catch (FileNotFoundException e) {
						LOGGER.error("ExcelUpdaterService - FileNotFoundException :: ", e);
					} catch (IOException ex) {
						LOGGER.error("ExcelUpdaterService - IOException :: ", ex);
					}
				}
			}
		}
	}

	private Object getCellValue(Cell cell) throws Exception {

		Object cellValue = null;
		if (CellType.NUMERIC.equals(cell.getCellType())) {
			cellValue = NumberToTextConverter.toText(cell.getNumericCellValue());
		} else if (CellType.STRING.equals(cell.getCellType())) {
			cellValue = cell.getStringCellValue().trim();
		} else if (CellType.BOOLEAN.equals(cell.getCellType())) {
			cellValue = cell.getBooleanCellValue();
		}
		return cellValue;
	}

}
