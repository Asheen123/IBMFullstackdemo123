
public class Test1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		//System.out.println("This is First check");
		
		String str = "AmIt JaIN InTerVieW";
		
		String str2 ="";
		
		 int ln = str.length();
		 
	        // Conversion using predefined methods
	        for (int i = 0; i < ln; i++) {
	            Character c = str.charAt(i);
	            if (Character.isLowerCase(c)) {
	               // str.replace(i, i + 1,
	                  //          Character.toUpperCase(c) + "");
	            	
	            	Character d = Character.toUpperCase(c);
	            	//System.out.println(d);
	            	str2 = str2 + d +"";
	            	
	            }
	            else if (Character.isUpperCase(c))
	            {
	            	//str.replace(c, Character.toLowerCase(c));
	            	
	            	Character d = Character.toLowerCase(c);
	            //	System.out.println(d);
	            	str2 = str2 + d +"";
	            }
	            
	            else {
	            	
	            	str2 = str2 + c +"";
	            }
	        }
	        
	        
	        System.out.println(str2);
	}

}
