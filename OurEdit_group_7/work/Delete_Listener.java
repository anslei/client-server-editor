
public class Delete_Listener {
	
	public int counter;
	
	public int users;
	
	public String doc_name;
	
	public Delete_Listener(int i) throws ClassNotFoundException {
		  users=i;
		  System.out.println("listener initialized with "+users+" users");
	  }
	public Boolean inc_counter(Delete_Listener listener){
		Boolean del = false;
		listener.counter+=1;
		System.out.println("There are "+listener.users+" users");
		System.out.println("Count is "+listener.counter);
		if (listener.counter==listener.users) {
			del = true;
		}
		return del;
	}
}
