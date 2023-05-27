package com.ups.automation.rest.restautomation.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.ups.automation.rest.restautomation.bean.GlobalSheetBean;

public class MailService {

	private StringBuilder bodyText = new StringBuilder();
	private static ResourceBundle configProp = ResourceBundle.getBundle("application");

	public int getTotalCount(Map<String, Object> excelApiData) {
		int totalCount = 0;
		@SuppressWarnings("unchecked")
		List<GlobalSheetBean> globalSheetBeanList = (List<GlobalSheetBean>) excelApiData.get("globalSheetList");
		if(null !=globalSheetBeanList){
		for (int i = 0; i < globalSheetBeanList.size(); i++) {
			if ("Y".equals(globalSheetBeanList.get(i).getExecute())) {
				for (int j = 0; j < globalSheetBeanList.get(i).getApiSheetList().size(); j++) {
					if ("Y".equals(globalSheetBeanList.get(i).getApiSheetList().get(j).getExecute())) {
						totalCount = totalCount + 1;
					}
				}
			}
		}
		}
		return totalCount;
	}

	@SuppressWarnings("unchecked")
	public int sendEmail(Map<String, Object> excelApiData, String reportPath) {
		String from = configProp.getString("MailFrom");
		String to = configProp.getString("MailTo");
		String cc = configProp.getString("MailCC");
		String subject = configProp.getString("MailSubject");
		int totalApiCount = 0;
		Properties props = new Properties();
		props.put("mail.smtp.host", configProp.getString("MailHost"));
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.startsls.enable", "true");
		Session session = Session.getDefaultInstance(props);

		try {
			InternetAddress fromAddress = new InternetAddress(from);
			//InternetAddress toAddress = new InternetAddress(to);

			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(fromAddress);
			//msg.setRecipient(Message.RecipientType.TO, toAddress);
			msg.setRecipients(Message.RecipientType.TO,InternetAddress.parse(to));
			msg.setRecipients(Message.RecipientType.CC,InternetAddress.parse(cc));
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			String reporturl = "";
			int successcount = 0;
			int failcount = 0;
			int apisuccesscount = 0;
			int apifailcount = 0;
			String tableContents = "";
			if (configProp.getString("MailReportPath").equalsIgnoreCase(""))
				reporturl = reportPath;
			else
				reporturl = configProp.getString("MailReportPath");
			List<GlobalSheetBean> globalSheetBeanList = (List<GlobalSheetBean>) excelApiData.get("globalSheetList");
			for (int i = 0; i < globalSheetBeanList.size(); i++) {
				apisuccesscount = 0;
				apifailcount = 0;
				if ("Y".equals(globalSheetBeanList.get(i).getExecute())) {
					if (globalSheetBeanList.get(i).getStatus().equalsIgnoreCase(Constants.STATUS_PASS))
						successcount = successcount + 1;
					else if (globalSheetBeanList.get(i).getStatus().equalsIgnoreCase(Constants.STATUS_FAIL))
						failcount = failcount + 1;
					for (int j = 0; j < globalSheetBeanList.get(i).getApiSheetList().size(); j++) {
						if ("Y".equals(globalSheetBeanList.get(i).getApiSheetList().get(j).getExecute())) {
							totalApiCount = totalApiCount + 1;
							if (globalSheetBeanList.get(i).getApiSheetList().get(j).getStatus()
									.equalsIgnoreCase(Constants.STATUS_PASS))
								apisuccesscount = apisuccesscount + 1;
							else if (globalSheetBeanList.get(i).getApiSheetList().get(j).getStatus()
									.equalsIgnoreCase(Constants.STATUS_FAIL))
								apifailcount = apifailcount + 1;
						}
					}
					tableContents = tableContents.concat("<tr style='border: 1px solid black;'>");
					tableContents = tableContents.concat("<td style='border: 1px solid black;text-align:center;'>"
							+ globalSheetBeanList.get(i).getOperationName() + "</td>");

					tableContents = tableContents
							.concat("<td style='color: green;border: 1px solid black;text-align:center;'><b>"
									+ apisuccesscount + "</b></td>");

					tableContents = tableContents
							.concat("<td style='color: red;border: 1px solid black;text-align:center;'><b>"
									+ apifailcount + "</b></td>");
					tableContents = tableContents.concat("</tr>");
				}
			}
			String finalStatusStyle = "";
			String finalstatus = "";
			if (failcount > 0) {
				finalstatus = "FAIL";
				finalStatusStyle = "red";
			} else {
				finalstatus = "PASS";
				finalStatusStyle = "green";
			}
			bodyText.append(
					"<p style='font-family: calibri, sans-serif;'><b>Test Execution Status : </b><span style='color:"
							+ finalStatusStyle + "'><b>" + finalstatus + "</b></span></p>");
			bodyText.append(
					"<table style='border: 1px solid black;font-family: calibri, sans-serif;'><thead><th  style='border: 1px solid black;'>Total Pass Count </th><th  style='border: 1px solid black;'>Total Fail Count</th></thead><tbody><tr style='border: 1px solid black;'><td style='color: green;border: 1px solid black;text-align:center;'><b>"
							+ successcount
							+ "</b></td><td style='text-align:center;border: 1px solid black;color: red;'><b>"
							+ failcount + "</b></td></tr></tbody></table><br>");
			bodyText.append(
					"<table style='border: 1px solid black;font-family: calibri, sans-serif;'><thead><th style='border: 1px solid black;text-align:center;'>API Name</th><th style='border: 1px solid black;text-align:center;'>Total TestCases Passed</th><th style='border: 1px solid black;text-align:center;'>Total TestCases Failed</th></thead><tbody>");

			bodyText.append(tableContents);
			bodyText.append("</tbody></table><br>");
		//	reporturl = reporturl + "\\htmlReportsJSONDIff";
			reporturl = reporturl.replace("\\\\", "\\");
			bodyText.append(
					"<p style='font-family: calibri, sans-serif;'>Detailed Execution Report generated in the below path,<br><a href='"
							+ reporturl + "'>" + reporturl + "</a></p><br><br>");
			bodyText.append(
					"<p style='font-family: calibri, sans-serif;'><em>Note : This is an auto-generated mailer.</em></p>");
			msg.setContent(bodyText.toString(), "text/html");
			Transport.send(msg);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return totalApiCount;
	}
}
