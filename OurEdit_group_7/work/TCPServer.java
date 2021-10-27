import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class TCPServer extends TCPClient {

	private int port = 1927;

	private ServerSocket serverSocket;
	  
	//This URL will be used to access the database;
	static final String dbURL = "jdbc:mysql://localhost:3306/?user=root&password=350project";	
	
	//This ArrayList keeps track of all active threads on the server right now;
	public static ArrayList<ServerThread> threads = new ArrayList<>();
	
	//This ArrayList stores all active delete listeners 
	public static ArrayList<Delete_Listener> listeners = new ArrayList<>();
	
	//This integer keeps track of the number of documents in the database, also used to increment their names;
	public static int docs = 0;

	  
	  //Constructor for TCPServer that initializes JDBC driver and throws an exception if it could not connect to it;
	  public TCPServer() throws ClassNotFoundException {
		  Class.forName("com.mysql.cj.jdbc.Driver");
	  }
	  
	  //function that listens for connection trials and accepts them. It also adds the new threads to the threads ArrayList;
	  public void acceptConnections() {

	     try {
	    	 
	    //similar to the WelcomeSocket in the PowerPoint slides
	      serverSocket = new ServerSocket(port);
	    }
	     
	    //print error and exit program in case the socket could not be instantiated
	    catch (IOException e) {
	      System.err.println("ServerSocket instantiation failure");
	      e.printStackTrace();
	      System.exit(0);
	    }

	     //Entering the infinite loop
	     while (true) {
	      try {
	    	  
	    	//wait for a TCP handshake initialization (arrival of a "SYN" packet)
	        Socket newConnection = serverSocket.accept();
	        
	        //print message to make sure connection was accepted
	        System.out.println("accepted connection");

	        //Now, pass the connection socket created above to a thread and run it in it
	  
	        //First create the thread and pass the connection socket to it
	        ServerThread st = new ServerThread(newConnection);
	        
	        //adding new thread to threads ArrayList
	        threads.add(st);

	        //Then, start the thread, and go back to waiting for another TCP connection
	        new Thread(st).start();
	      }
	      
	      //print error if server could not accept a connection
	      catch (IOException ioe) {
	        System.err.println("server accept failed");
	      }
	    }
	   }
	    
	  public static void main(String args[]) {
		  
	 //declare the server in main
	 TCPServer server = null;
	    try {
	    	
	      //try and instantiate an object of this class.
	      server = new TCPServer();
	      
	      //print message to make sure server was created
	      System.out.println("server ok");
	    }
	    //exit program in case server could not be created
	    catch (ClassNotFoundException e) {
	      System.out.println("unable to load JDBC driver");
	      e.printStackTrace();
	      System.exit(1);
	    }
	    System.out.println("accept connections ok?");
	    
	    //this function makes the server listen and accept connections from clients
	    server.acceptConnections();
	    
	    System.out.println("accept connections ok");
	  }
	  
	  //This function declares a new instance of Delete_Listener to listen to the answers to the marked up for deletion document.
	  public static void Del_count(String str, int i) throws ClassNotFoundException {
		  
		  Delete_Listener listen = new Delete_Listener(i);
		  
		  //Adding the listener to the listeners ArrayList 
		  listeners.add(listen);
		  
		  //Setting the name attribute to the document name so the listener knows which document it should listen answers to
		  listen.doc_name=str;
		  System.out.println("listener with "+i+" user was created");
	  }
	  
	  //This function increments the counter of the Delete_Listener until it reaches the required number or receives a "no"
	  public static boolean inc_del(String str) {
		  
		  //This boolean is set to true if the listener got the required number of answers from the clients.
		  Boolean del = false;
		  
		  //Increment the listener corresponding ot th document.
		  for (Delete_Listener listener : listeners) {
			  if (listener.doc_name.equals(str)) {
				  del= listener.inc_counter(listener);
			  }
		  }
		 
		  return del;
	  }
	  
	   //Internal class
	   class ServerThread implements Runnable {

	    private Socket socket;
	    public DataOutputStream dataout;
	    public BufferedReader datain;
	    public String nameOfUser;
	    
	    //this boolean keeps the "conversation" between server and client going until it is set to false
	    boolean conversationActive= true;

	    public ServerThread(Socket socket) {
	    	
	    //Inside the constructor: store the passed object in the data member  
	      this.socket = socket;
	    }

	    //Every instance of a ServerThread will handle one client (TCP connection)
	    public void run() {
	      try {
	    	  
	    	//Input and output streams, obtained from the member socket object  
	        dataout = new DataOutputStream(socket.getOutputStream());
	        datain =new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        
	        //linking username to this specific thread
	        nameOfUser=datain.readLine();
	      }
	      //catch exception in case could not get streams
	      catch (IOException e) {
	        return;
	      }
	      
	      
	      //Starting the execution of the server functions in this while loop.
	      while(conversationActive) {
	    	  
	    	  //String message is what the server receives from the client
	    	  String message;
	    	  
	    	  //String response is what the server responds to the client's request
	    	  String response;
	    	  
	        try {
	        	
	        	//read from the input stream buffer (read a message from client)
	        	System.out.println("ready to read");
	        	message = datain.readLine();
	        	System.out.println(message);
	        	System.out.print("conversation ok\n");
	        	
	        	//Calling function SQLDatabaseConnection to know what to do with the received message, and returning it to response
	        	response = SQLDatabaseConnection(message);
	        	
	        	//writing back to the client the corresponding response to his request
	        	dataout.write((response+"\n").getBytes());
	        		
	        	
	           
	        }
	        catch (IOException | ClassNotFoundException ioe) {
	          conversationActive = false;
	        }
	      }
	      try {
	        System.out.println("closing socket");
	        datain.close();
	        dataout.close();
	        
	        //When the server receives a "q", we arrive here
	        socket.close();
	      }
	      catch (IOException e) {
	      }
	    }
	    
	    //This function is called when a mark up for deletion is required. It is called from the deldoc function at line 523
	    public void sendWhenDeleted(String str1,String str2,String str3) throws ClassNotFoundException {
	    	
	    	//str3 is split to get an array of all the users that have access to the specified document.
	    	String[] names = str3.split("~");
	    	
	    	//This integer counts the number of users that were sent the mark up for deletion (all users that have access to the document except the one that called the delete)
	    	int count=0;
	    	
	    	//Looping through array of users who have access to document.
	    	for (String s :names) {
	    		
	    		//looping through currently active threads to check if users are active to ask them.
	    		for (ServerThread t:threads) {
	    			
	    			//Excluding the user who called the delete because we do not need to ask him again
	    			if (!t.nameOfUser.equals(nameOfUser)) {
	    				
	    				//If user is active
	    				if (s.equals(t.nameOfUser)) {
	    					
	    		    		try {
	    		    			//write out the corresponding response to the clients to call the mark up for admission
	    						t.dataout.write(("deldoc~"+str1+"~"+str2+"\n").getBytes());
	    						
	    						//Incrementing the counter 
	    						count+=1;
	    						
	    						}
	    					 catch (IOException e) {
	    						e.printStackTrace();
	    					}
	    		    		
	    	    			}
	    			}
	    			
	    	}
	    	}
	    	//Calling this function to initialize a listener for the mark up for deletion that was called
	    	Del_count(str1,count);
	    
	    }
	   
	   //This function is called every time the server receives a message from the client to know what to do next.
	   public String SQLDatabaseConnection(String str) throws ClassNotFoundException {
		   System.out.println("database was called");
		   
		   String answer="lol";
		   
		   //if user closes the GUI and sends a null message.
		   if (str==null) {return "null";}
		   else {
			   
		   //Splitting the user's formatted message to read the parts separately and manipulate them.
		   String[] parts = str.split("~");
		   String op=null;
		   String name=null;
		   String arg=null;
		   if (parts.length>=1) { op = parts[0];}
		  
		   if (parts.length>=2) {name = parts[1];}
		   if (parts.length==3) {arg = parts[2];}
		   
		  
		   //if statements to check the opcode of the instruction sent from the client to know which operation to perform
		   //and calling the appropriate function with the appropriate arguments.
		   
	    	if (op.equals("newdoc")) {
	    		answer =newdoc();
	       		System.out.println(answer);
	    	}
	    		
	    		
	    	else if (op.equals("user")) {
	    		answer=addUser(name,arg);
	    	}
	    	
	    	else if (op.equals("checkdocs")) {
	    		answer=checkDocs(name);
	    	}
	    	
	    	else if (op.equals("checkusers")) {
	    		answer=checkUsers(name);
	    	}
	    	
	    	else if (op.equals("deldoc")) {
	    		deldoc(name,arg);
	    	}
			
	    	else if (op.equals("text")) {
	    		answer=getText(name);
	    	}
	    	
	    	else if (op.equals("users")) {
	    		answer=checkUsers(name);
	    	}
	    	
	    	else if (op.equals("checkallusers")) {
	    		answer=checkAllUsers();
	    	}
	    	
	    	else if (op.equals("savedoc")) {
	    		answer=saveText(name,arg);
	    	}
	    	
	    	else if (op.equals("savenewdoc")) {
	    		answer=saveNewText(name);
	    	}
	    	
	    	else if (op.equals("option")) {
	    		option(name,arg);
	    	}
			
		   }
		   return answer;
	}
	   
	   public String newdoc() {
		   
		String ans;
		
		//Incrementing the counter for new documents in database
   		docs+=1;

   		//Getting the current time (time the doc was created for server)
   		java.util.Date dt = new java.util.Date();
   		java.text.SimpleDateFormat sdf = 
   		new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   		String currentTime = sdf.format(dt);
   		System.out.println(currentTime);
   		
   		//statement that should be executed by the database
   		String insertSql = "INSERT INTO eece350.docs_info (doc_name, num_users, last_modified_at, num_users_using, content) VALUES "
                   + "('doc"+docs+"', 1,'"+currentTime+"' , 1, '');";
   		
   		
   		//connecting to the database and executing the query
   		try (Connection connection = DriverManager.getConnection(dbURL);
   			PreparedStatement prepsInsertProduct = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);) {

               prepsInsertProduct.execute();
   		

   	   
          
        //calling the function once again to create a corresponding entry in the users table to assign the new document to the user who created it  
   	   	SQLDatabaseConnection("user~"+nameOfUser+"~doc"+docs);
            }
          
   		
   		 catch (Exception e) {
   	            e.printStackTrace();
   		 }
   		
   		//returning appropriate answer to client
   		ans = "newdoc~"+"doc "+docs+" was created.~"+"doc"+docs;
   		
   		return ans;
	   
	   
	   }
	   public String addUser(String str1, String str2) {
		   
		   String ans;

	   		//This is the query that will be sent to the database
	   		String insertSql = "INSERT INTO eece350.users (ID, doc_name, username) VALUES "+ "(NULL,'"+str2+"','"+str1+"' )";
	   		
	   		
	   		
	   		//connecting to the database and executing the query
	   		try (Connection connection = DriverManager.getConnection(dbURL);
	   			PreparedStatement prepsInsertProduct = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);) {

	               prepsInsertProduct.execute();
	                 
	   		}
	   		 catch (Exception e) {
	   	            e.printStackTrace();
	   		 }
	   		ans="user~User "+str1+" successfully gained access to doc "+str2;
	   		return ans;
	   		 }
	   		
	   	 public String checkDocs(String str1) {

	   		String ans="";
	   		//This is the query that will be sent to the database
	   		String checkDoc1 = "SELECT doc_name FROM eece350.users WHERE username = '"+str1+"'";
	   				
	   		System.out.println(checkDoc1);
	   		ResultSet resultset = null;
	   		
	   		//connecting to the database and executing the query
	   		try (Connection connection = DriverManager.getConnection(dbURL); Statement statement = connection.createStatement();) {

	   			String checkDoc = "SELECT doc_name FROM eece350.users WHERE username = '"+str1+"'"; 
	           resultset = statement.executeQuery(checkDoc);
	           
	           while (resultset.next()) {
	                ans+=resultset.getString("doc_name")+"~";
	           
	            }
	           
	           //removing the last ~ resulting from the loop above
	           ans=ans.substring(0, ans.length()-1);
	           
	           ans="checkdocs~"+ans;
	        }  
	          
	   		 catch (Exception e) {
	   	            e.printStackTrace();
	   		 }
	   		return ans;
	   }
	   	 
	   	public String checkUsers(String str1) {

	   		String ans="";
	   			
	   		ResultSet resultset = null;
	   		
	   		//connecting to the database and executing the query
	   		try (Connection connection = DriverManager.getConnection(dbURL); Statement statement = connection.createStatement();) {
	   			
	   			//This is the query that will be sent to the database
	   			String checkUser = "SELECT DISTINCT username FROM eece350.users WHERE doc_name = '"+str1+"'"; 
	   			
	   			System.out.println(checkUser);
	           resultset = statement.executeQuery(checkUser);
	           
	           while (resultset.next()) {
	                ans+=resultset.getString("username")+"~";
	                
	            }
	           ans=ans.substring(0, ans.length()-1);
	        }  
	       
	   		 catch (Exception e) {
	   	            e.printStackTrace();
	   		 }
	   		return ans;
	   }
	   	public String checkAllUsers() {

	   		String ans="";
	   				
	   		ResultSet resultset = null;
	   		
	   		//connecting to the database and executing the query
	   		try (Connection connection = DriverManager.getConnection(dbURL); Statement statement = connection.createStatement();) {
	   			
	   			//This is the query that will be sent to the database
	   			String checkUser = "SELECT DISTINCT username FROM eece350.users "; 
	   			
	   			System.out.println(checkUser);
	           resultset = statement.executeQuery(checkUser);
	           
	           while (resultset.next()) {
	                ans+=resultset.getString("username")+"~";
	                
	            }
	           ans=ans.substring(0, ans.length()-1);
	           ans="checkusers~"+ans;
	        }  
	          
	   		 catch (Exception e) {
	   	            e.printStackTrace();
	   		 }
	   		return ans;
	   }
		public String getText(String str1) {

	   		String ans="";
	   		
	   				
	   		ResultSet resultset = null;
	   		
	   		//connecting to the database and executing the query
	   		try (Connection connection = DriverManager.getConnection(dbURL); Statement statement = connection.createStatement();) {
	   			
	   			//This is the query that will be sent to the database
	   			String getText = "SELECT content FROM eece350.docs_info WHERE doc_name = '"+str1+"'"; 
	   			
	   			System.out.println(getText);
	   			
	           resultset = statement.executeQuery(getText);
	           
	           resultset.next();
	           
	           ans="text~"+str1+"~"+resultset.getString("content");
	         
	        }  
	          
	   		 catch (Exception e) {
	   	            e.printStackTrace();
	   		 }
	   		return ans;
	   }
		public String saveText(String str1, String str2) {

	   		String ans="";
	   		
	   		//replacing single quotes with 2 single quotes so the database does not bug 
	   		String str3 = str2.replace("'", "''");
	   		
	   		//This is the query that will be sent to the database
	   		String update = "UPDATE eece350.docs_info SET content = '"+str3+"' WHERE doc_name = '"+str1+"';";		
	   		
	   	
	   		
	   		
	   		//connecting to the database and executing the query
	   		try (Connection connection = DriverManager.getConnection(dbURL);
	   			PreparedStatement prepsInsertProduct = connection.prepareStatement(update, Statement.RETURN_GENERATED_KEYS);) {

	           prepsInsertProduct.execute();
	           ans="updated~"+str1+" was saved"+"~"+str1;
	         
	        }  
	          
	   		 catch (Exception e) {
	   	            e.printStackTrace();
	   		 }
	   		return ans;
	   }
		public String saveNewText(String str1) throws ClassNotFoundException {

	   		String ans="";
	   		
	   		//creating a new document if a user did not create a new document then saved in it, so this function does both for him
	   		String str3 =SQLDatabaseConnection("newdoc~ ~ ");
	   		
	   		String [] name=str3.split("~");
	   		
	   		//This is the query that will be sent to the database
	   		String update = "UPDATE eece350.docs_info SET content = '"+str1+"' WHERE doc_name = '"+name[name.length-1]+"';";
	   		
	   		//connecting to the database and executing the query
	   		try (Connection connection = DriverManager.getConnection(dbURL);
	   			PreparedStatement prepsInsertProduct = connection.prepareStatement(update, Statement.RETURN_GENERATED_KEYS);) {

	           prepsInsertProduct.execute();
	           ans="updated~"+name[name.length-1]+" was created and saved~"+name[name.length-1];
	         
	        }  
	          
	   		 catch (Exception e) {
	   	            e.printStackTrace();
	   		 }
	   		return ans;
	   }
		
	   	public String deldoc(String str1, String str2) throws ClassNotFoundException {
	   		
	   		
	   		String ans=null;
	   		
	   		//getting the names of all the users who have access to the document that was marked up for deletion
	   		String str3=checkUsers(str1);
	   		
	   		sendWhenDeleted(str1,str2,str3);
	   		
	   		System.out.println("decision is being taken");
			
			ans= "markedup~"+str1+" was marked up for deletion~ "  ;
			 
	   		return ans;
	   	}
	   	 
	   	public String option(String str1, String str2) {
	   		
	   		String ans=null;
	   		
	   		//This boolean is set to true by the inc_del method when the required number of yes's is received by all clients concerned
	   		Boolean deleting;
	   		
	   		//getting the user names of all the users who have access to the document
	   		String str3=checkUsers(str1);
	   		
	   		String[] names= str3.split("~");
	   		
	   		//if the user sent back yes, increment the count of the delete listener. if the listener reaches the required condition, delete the document.
	   		if (str2.equals("yes")) {
	   			deleting=inc_del(str1);
	   			System.out.println("deleting = "+deleting);
	   			if (deleting==true) {
	   				
	   				System.out.println("decision was yes"); 
	  			  String delete ="DELETE FROM eece350.docs_info WHERE doc_name = '"+str1+"'" ;
	  			  String delete2= "DELETE FROM eece350.users WHERE doc_name = '"+str1+"' ";
	  			try (Connection connection = DriverManager.getConnection(dbURL);
	  		   			PreparedStatement prepsInsertProduct = connection.prepareStatement(delete, Statement.RETURN_GENERATED_KEYS);PreparedStatement prepsInsertProduct2 = connection.prepareStatement(delete2, Statement.RETURN_GENERATED_KEYS); ) {

	  		           prepsInsertProduct.execute();
	  		         prepsInsertProduct2.execute();
	  		         
	  		        }  
	  		          
	  		   		 catch (Exception e) {
	  		   	            e.printStackTrace();
	  		   		 }
	  			//Looping again through the active threads and checking if their user names match with the query to send them each a message.
	  			for (String s :names) {
		    		System.out.println(s);
		    		for (ServerThread t:threads) {
		    			System.out.println("looping threads");
		    			System.out.println(t.nameOfUser);
		    				if (s.equals(t.nameOfUser)) {
		    		    		try {
		    		    			
		    		    			System.out.println("sending to "+s);
		    						t.dataout.write(("deleted~"+str1+" was deleted~"+str1+"\n").getBytes());
		    						
		    						
		    						}
		    					 catch (IOException e) {
		    						e.printStackTrace();
		    					}
		    		    		
		    	    			}
		    			
		    			
		    	}
		    	}
	   			}
	   		}
	   		else if (str2.equals("no")) {
	   			for (String s :names) {
		    		System.out.println(s);
		    		for (ServerThread t:threads) {
		    			System.out.println("looping threads");
		    			System.out.println(t.nameOfUser);
		    				if (s.equals(t.nameOfUser)) {
		    		    		try {
		    		    			
		    		    			System.out.println("sending to "+s);
		    						t.dataout.write(("notdeleted~"+str1+" was not deleted~ "+"\n").getBytes());
		    						
		    						
		    						}
		    					 catch (IOException e) {
		    						e.printStackTrace();
		    					}
		    		    		
		    	    			}
		    			
		    			
		    	}
		    	}
	   		}
	   		return ans;
	   	}
	}
}

	

