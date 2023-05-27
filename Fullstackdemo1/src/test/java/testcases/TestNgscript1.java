package testcases;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class TestNgscript1 {
	
	
	 @Test
	    public void firsttest(){
	        System.out.println("First test");
	    }
	 
    @BeforeMethod
    public  void beforemethod(){
        System.out.println("before method");

    }

    @AfterMethod
    public  void aftermethod(){
        System.out.println("after method");
    }

    @BeforeClass
    public void beforeclass(){
        System.out.println("before class");
    }

}
