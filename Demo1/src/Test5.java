import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Test5 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		   LocalDateTime now = LocalDateTime.now();  
		   System.out.println(dtf.format(now));  

	}

}
