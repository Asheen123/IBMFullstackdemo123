//  Program to find prime numbers between 1 to 20;

//then in those prime numbers find the maximun and minimum. Also add them and print total.


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;




public class Test2 {
	
	public int checkprime(int m) {
		
		int n = m;
		
		String flag = "false";
		
		for(int i =2;i<n/2;i++){
			
			
		if	(n % i == 0) {
			
			//System.out.println("Number is not prime");
			
			flag = "true";
			
			
		}
			
			
		}
		
		if(flag == "true")
			
		{
		
			System.out.println("Number is not prime");
			
			return 0;
		
		
		}
		else
			
		{
		
		System.out.println("Number is  prime");
		
		return m;
			
		}
				
			
	
		
		
	};

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Test2 t = new Test2();

int max;

int min;
	//int m = 5;
		
		//int arr[] = new int[20];

ArrayList<Integer> al = new ArrayList<Integer>();



		
		//int count = 0;
	
	
		
	System.out.println(t.checkprime(5)); 
	
	
	for(int i =2;i<20;i++) {
		
		int ele = t.checkprime(i);
		//count++;
		
		if(ele != 0) {
			al.add(ele);
		}
	}
	
	System.out.println(al.toString());
	
 max = al.get(0);
	
	min = al.get(0);
	
	for(int i =0;i<al.size();i++) {
		
		if(al.get(i) > max) {
			
			max = al.get(i);
		}
		
		if(al.get(i) < min) {
			
			min = al.get(i);
		}
	}

	System.out.println(max);
	System.out.println(min);
	System.out.println("Sum is :"+ (max+min)); 
}
	
}
