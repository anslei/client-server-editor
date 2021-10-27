
import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class TCPClient2 {
	public static String sentence;
	public static String response;
	public static String username;
	
	public static void startListener(ClientListener listener) {
		 
	        //Then, start the thread, and go back to waiting for another TCP connection
	     listener.run();
	}
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
	
	
	//This boolean is to keep the conversation going between the client and server until it is set to false
	Boolean ConversationActive=true;
	
	BufferedReader inFromUser =new BufferedReader(new InputStreamReader(System.in));
	
	//The socket is initialized to "localhost" because the server and client are running on the same host, and the port is same as the server's
	Socket clientSocket = new Socket("localhost", 1927);
	
	
	ClientListener lis= new ClientListener(clientSocket);
	
	new Thread(lis).start();
	//print a message to know that the connection was established
	System.out.println("Connection Successful");
	
	DataOutputStream outToServer =new DataOutputStream(clientSocket.getOutputStream());
	//BufferedReader inFromServer =new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	
	System.out.println("Hello user. Please enter your username : ");
	
	username=inFromUser.readLine();
	outToServer.write((username+"\n").getBytes());
	
	//entering infinite loop for client
	while (ConversationActive) {
		try {
		
			System.out.println("type in your request");
			
		//read user input
		sentence = inFromUser.readLine();
		
		//sending user's input to server; we used write(xxx.getBytes()) so the buffered reader from the server side doesn't keep reading indefinitely and block
		outToServer.write((sentence+"\n").getBytes());
		
		//read what the server sent back
		//response = inFromServer.readLine();
		
		//print the server's message
		//System.out.println("FROM SERVER: " + response);
		}
		
		 //This exception is thrown in case server is ended or taken down, so the infinite loop is exited 
		 catch (IOException ioe) {
			 System.out.println("server was ended");
	         ConversationActive = false;
	        }

	}
	System.out.println("closing socket");
	clientSocket.close();
	}
	}