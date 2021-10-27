import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;




public class TCPClient {
	
	//this is the string the client will send to the server
	static String sentence=null;
	
	//Assign a user name to the TCPClient
	static String username;
	
	static Socket clientSocket ;
	
	//This client listener listens to everything the server sends the client and interprets it using the operation fucntion
	static class ClientListener implements Runnable {
	Socket socket;
	BufferedReader in;
	String response;
	public ClientListener (Socket socket) throws IOException {
		this.socket = socket;
		this.in     = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	
	
	public void run() {
	  try {
		  while (true) {
		    response = in.readLine();
		    System.out.println("response");
		    System.out.println("FROM SERVER: " + response);
		    operation(response);
		  }
		  
	  } catch (IOException e) {
		  System.err.println("IO Exception in client listener");
		  System.err.println(e.getStackTrace());
	  }
	  try {
		  System.out.println("client listener closing");
		  in.close();
		  socket.close();
	  } catch(IOException e) {
		  e.printStackTrace();
	  }
			
		}}
	
	public static void main(String argv[]) throws Exception
	{
	initialize();

	}
	
	public static void initialize() {
		

		try {
			//The socket is initialized to "localhost" because the server and client are running on the same host, and the port is same as the server's
			clientSocket = new Socket("localhost", 1927);
			
			//creating and starting a new client listener for this client
			ClientListener lis;
			
			lis = new ClientListener(clientSocket);
			
			new Thread(lis).start();
			
		} catch (UnknownHostException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		
		
		//print a message to know that the connection was established
		System.out.println("Connection Successful");
	}
	
	//This function writes the GUI's response to the server
	public static void writeToServer(String str, String str2, String str3) {
		
		//The client sends the server the formatted request it has
		sentence = formatResponse(str, str2, str3);
		
		try {
			
			DataOutputStream outToServer =new DataOutputStream(clientSocket.getOutputStream());
			
			outToServer.write((sentence+"\n").getBytes());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	//This function just writes out the user name to the server at the start of the connection
	public static void writeUsername(String str) {
		
		username=str;
		
		System.out.println(username);
		try {
			DataOutputStream outToServer =new DataOutputStream(clientSocket.getOutputStream());
			
			outToServer.write((str+"\n").getBytes());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	//This function simply formats the request of the user in the format used in both client and server (strings divided by ~)
	public static String formatResponse (String str , String str2, String str3) {
		
		String ans;
		
		ans=str+"~"+str2+"~"+str3;
		
		return ans;
		
		
	}
	
	//This function performs the necessary operation depending on what the server sent back to the client
	public static void operation(String str) {
		
		String[] parts=str.split("~");
		
		if (parts[0].equals("newdoc")){
			GUI.updateLog(parts[1]);
			GUI.docs.addItem(parts[2]);
			GUI.docs.setSelectedItem(parts[2]);
		}
		
		if (parts[0].equals("user")) {
			GUI.updateLog(parts[1]);
		}
		
		if (parts[0].equals("checkdocs")) {
			int i = 1;
			GUI.docs.removeAllItems();
			while (i < parts.length) {
				
				GUI.docs.addItem(parts[i]);
				i+=1;
			}
			GUI.docs.setSelectedItem(parts[1]);
			writeToServer("text",parts[1]," ");
		}
		
		if (parts[0].equals("text")) {
			GUI.docs.setSelectedItem(parts[1]);
			if (parts.length==3) {
				GUI.updateText(parts[2]);
			}
			else {
				GUI.updateText(null);
			}
			
		}
		
		if (parts[0].equals("checkusers")) {
			int i = 1;
			while (i < parts.length) {
				if (!parts[i].equals(username)) {
					GUI.users.addItem(parts[i]);
				}
				i+=1;
			}
			GUI.docs.setSelectedItem(parts[1]);
			writeToServer("text",parts[1]," ");
		}
		
		if (parts[0].equals("updated")) {
			GUI.updateLog(parts[1]);
			if (parts[1].contains("created")) {
				GUI.docs.addItem(parts[2]);
				GUI.docs.setSelectedItem(parts[2]);
			}
			
		}
		
		if (parts[0].equals("deleted")) {
			GUI.updateLog(parts[1]);
			GUI.docs.removeItem(parts[2]);
		}
		
		if (parts[0].equals("notdeleted")) {
			GUI.updateLog(parts[1]);
		}
		
		if (parts[0].equals("deldoc")) {
			GUI.markupDeletion(parts[1], parts[2]);
		}
		
		if(parts[0].equals("markedup")) {
			GUI.updateLog(parts[1]);
		}
	}
	}
	