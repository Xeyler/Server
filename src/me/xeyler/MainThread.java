package me.xeyler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainThread implements Runnable {
	static ServerSocket serverSocket;
	static Socket socket;

	static ListenerThread threads = new ListenerThread();
	
	static NewOutputStream stream;
	static HashMap<Socket, String> connections = new HashMap<Socket, String>();
	static DataOutputStream out;
	static DataInputStream in;
	
	public int startServer() throws IOException {

		try{
			serverSocket = new ServerSocket(24353);
		} catch (BindException error) {

			System.out.println("ERROR BINDING SERVER!");
			System.out.println(error.getLocalizedMessage().toUpperCase());
			System.out.println("IS THE SERVER ALREADY RUNNING?");
			
			throw new BindException();
			
		}
		
		return serverSocket.getLocalPort();
		
	}

	public void startThread() {

		Thread thread = new Thread(this);
		thread.start();
		
	}
	
	public void run() {

		try {
			System.out.println("Started listening for new connections!");
			threads.startThread();
			mainLoop();
		} catch (IOException e) {
			e.printStackTrace();
		}
			
	}
	
	// This is the main method that allows and manages new connections
			private static void mainLoop() throws IOException {

				serverSocket.setSoTimeout(6000);
				
				while(true){
					
					try {
					socket = serverSocket.accept();
					System.out.println("New connection recieved");
					in = new DataInputStream(socket.getInputStream());
					out = new DataOutputStream(socket.getOutputStream());
					Boolean register = in.readBoolean();
					
					String login = in.readUTF();
					
					System.out.println("Recieved credentials: " + login);
					
					try {
						
						new String(login.split(":")[0]);
						new String(login.split(":")[1]);
						
					} catch (IndexOutOfBoundsException error) {
						
						System.out.println("Denied connection at IP: " + socket.getInetAddress());
						System.out.println("Client submitted blank fields.");
						return;
						
					}

					if(register == true) {
						
						FileReader reader = new FileReader("res/accounts");
						BufferedReader textReader = new BufferedReader(reader);
						String AccountReader;
						ArrayList<String> accounts = new ArrayList<String>();

						while((AccountReader = textReader.readLine()) != null) {
						
							accounts.add(AccountReader);
							
						}

						textReader.close();
						
						//Check through all registered users and passwords to see if the credentials match any of them
						for(String account : accounts) {
							if(login.split(":")[0].equals(account.split(":")[0])) {
								
								System.out.println("Username is already in use, denied new account addition.");
								out.writeInt(3);
								
							} else {

								PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("res/accounts")));
								writer.append(login);
								writer.close();
								System.out.println("New account added: " + login);
								
							}
						}
						
					} else {
					
						int valid;
						try {
							valid = validate(login);
						} catch(NullPointerException error) {
							System.out.println("All validations were skipped!");
							break;
						}
						if(valid != 0) {

							out.writeInt(valid);
							socket.close();
							
						} else {							

							out.writeInt(valid);
							
							for(Socket output : MainThread.getConnections().keySet()) {
								
								DataOutputStream out = new DataOutputStream(output.getOutputStream());
								if(output.equals(socket)) {
									out.writeUTF("Welcome to the chat, " + login.split(":")[0]);
								} else {
									out.writeUTF(login.split(":")[0] + " joined the chat!");
								}
								
							}
							
						}
					}
					
					} catch (SocketTimeoutException error) {
						
					}
					
				}
				
			}

			// This method is used to validate the users' credentials
			private static int validate(String login) throws IOException {

				FileReader reader = new FileReader("res/accounts");
				BufferedReader textReader = new BufferedReader(reader);
				String AccountReader;
				ArrayList<String> accounts = new ArrayList<String>();
				Boolean valid = false;
				
				while((AccountReader = textReader.readLine()) != null) {
				
					accounts.add(AccountReader);
					
				}

				//Closing the BufferedReader to prevent a memory leak
				textReader.close();
				
				//Check through all registered users and passwords to see if the credentials match any of them
				for(String account : accounts) {
					if(login.equals(account)) {
						valid = true;
					}
				}
				
				//Check if valid was left false (is there a better way to do this?)
				if(valid == false) {
					System.out.println("Denied connection from: " + login.split(":")[0] + " at IP: " + socket.getInetAddress() + " with password: " + login.split(":")[1]);
					System.out.println("Client used wrong credentials!");
					return 1;
				}
				
				//Check through the users ArrayList to see if the credentials match someone who is already logged in
				if(connections != null) {
					for(String user : connections.values()) {
						if(user.equals(login.split(":")[0])) {
							System.out.println("Denied connection from: " + login.split(":")[0] + " at IP: " + socket.getInetAddress() + " with password: " + login.split(":")[1]);
							System.out.println("Client is already connected!");
							return 2;
						}
					}
				}
				
				//Last check to see if its actually valid (we really don't need this, but something might go horribly wrong, making this check justified)
				for(String account : accounts) {
					if(login.equals(account)) {
						connections.put(socket, login.split(":")[0]);
						System.out.println("Accepted connection from: " + login.split(":")[0] + " at IP: " + socket.getInetAddress() + " with password: " + login.split(":")[1]);
						return 0;
					}
				}
				
				//Something is very wrong if we reach this spot
				throw new NullPointerException();
		
		}

			public static HashMap<Socket, String> getConnections() {
				
				return connections;
				
			}

			public static void removeFromConnections(Socket socket) {
				
				connections.remove(socket);
				
			}

	}
