import java.util.Scanner;

public class Test3 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		
		String a = "AsheeN123J@$%#@";
		String b = a.replaceAll("[^\\w]", "");
		
		
		
		System.out.println(b);
		
		
		Scanner sc = new Scanner(System.in);
		
		System.out.println("enter the String :");
		
		String s = sc.next();
		
		b = s.replaceAll("[^\\w]", "");
		
		System.out.println(b);
		
		
	}

}
