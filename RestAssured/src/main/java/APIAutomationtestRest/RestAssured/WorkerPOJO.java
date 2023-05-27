package APIAutomationtestRest.RestAssured;

public class WorkerPOJO {

	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	private int age;
	
	private int id;
	
	
	 public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public static void ToString(WorkerPOJO obj)
     
     {
         System.out.println(obj.getAge());
         System.out.println(obj.getName());
         System.out.println(obj.getId());
         
     }
	
	
}
