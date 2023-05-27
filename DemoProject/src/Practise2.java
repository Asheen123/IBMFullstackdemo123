import files.ReUsableMethods;
import files.VirtualPayload;
import io.restassured.path.json.JsonPath;

public class Practise2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		String response1 = VirtualPayload.courses();
		
		int s, sum = 0;
		
		
		//System.out.println(response1);
		
		JsonPath js2 = ReUsableMethods.rawToJson(response1);
		
		int count = js2.getInt("courses.size()");
		
		System.out.println(count);
		
		
		//System.out.println(js2.getString("courses[2].title"));
		
		for (int i =0; i<count; i++){
			
			System.out.println(js2.getString("courses["+i+"].title"));
			
			 s = js2.getInt("courses["+i+"].price")*js2.getInt("courses["+i+"].copies");
			 
			 System.out.println(s);
			 
			 sum = sum + s;
		}
		
		System.out.println(sum);
	}

}
