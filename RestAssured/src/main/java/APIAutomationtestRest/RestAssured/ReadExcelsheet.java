package APIAutomationtestRest.RestAssured;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadExcelsheet {
	
	public static void main(String[] args) throws IOException {
        String pathofExcelSheet = "C:\\Users\\002DSN744\\Desktop\\IBM\\Fullstacktester\\Data\\Input.xlsx";
        File file = new File(pathofExcelSheet);
        FileInputStream fis = new FileInputStream(file);
        
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        XSSFSheet sheetOfInterest = wb.getSheet("Sheet1");
        XSSFRow rowOfInterest = sheetOfInterest.getRow(1);
        System.out.println(rowOfInterest.getCell(0).getNumericCellValue());
    }

}
