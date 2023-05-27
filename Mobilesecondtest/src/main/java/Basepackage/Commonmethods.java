package Basepackage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Commonmethods extends  Basepackage.Base {
	
	public static String getpropertyvalue(String propertyname) {
		
		Properties prop = new Properties();
		
		try {
		File file = new File("C:\\Users\\002DSN744\\eclipse-workspace\\Mobilesecondtest\\src\\main\\java\\resource\\Data.properties");
		
		FileInputStream fi = new FileInputStream(file);
		
		
		
		prop.load(fi);
		
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		String value = prop.getProperty(propertyname);
		
		return value;
		
		
		
	}

}
