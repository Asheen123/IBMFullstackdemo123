package APIAutomationtestRest.RestAssured;

import java.util.HashMap;

public class Hashmaptest {
	
	
	
	
		public static void main(String[] args) {
	        
	        HashMap<String, String> hm = new HashMap<String, String>();
	        hm.put("id", "10");
	        hm.put("name", "TEN");
	        hm.put("Designation", "CEO");
	        
	        System.out.println(hm);
	        System.out.println(hm.get("id"));
	        
	        
	    }

}
