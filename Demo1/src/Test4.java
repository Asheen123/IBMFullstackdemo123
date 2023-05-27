import java.util.Arrays;

//String s = "Sonu monu chintu pintu"

//o/p = pintu utinch monu unoS


public class Test4 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int j = 0;
		
		String arr2[] = new String[4];

		String s = "Sonu monu chintu pintu";
		
		String arr[] = s.split(" ");
		
		for(int i = arr.length-1; i>= 0;i--) {
			
			if(i%2 == 0) {
				
				arr2[j] = "";
				
				for(int k = arr[i].length()-1; k>= 0;k--) {
					
					
					arr2[j] = arr2[j] + arr[i].charAt(k);
				}
				
			}
			
			else {
			arr2[j] = arr[i];
			}
			j++;
		}
		
		System.out.println(Arrays.toString(arr2));
	}

}
