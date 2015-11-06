package me.xeyler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;

public class ListenerThread implements Runnable {

	public void startThread() {

		Thread thread = new Thread(this);
		thread.start();
		
	}
	
	public void run() {
		
		try {
			listenLoop();
		} catch (IOException e) {
			e.printStackTrace();
		}
			
	}
	
	//This method is to listen for incoming messages from all users and broadcast them back to all users
	private static void listenLoop() throws IOException {
		
		while(true) {

			if(MainThread.getConnections() != null) {

				final HashMap<Socket, String> connections = MainThread.getConnections();
				
				for(Socket socket : connections.keySet()) {
					
					try {

						try {
						
						socket.setSoTimeout(100);
						DataInputStream in = new DataInputStream(socket.getInputStream());
						String message = in.readUTF();
						
						if(message.startsWith("/")) {
							doCommand(message);
						} else {
							broadcast("<" + connections.get(socket) + "> " + message);
						}
						
						} catch(SocketTimeoutException error) {
							
						}
						
					} catch (SocketException | NullPointerException error) {

						System.out.println("Closed connection from: " + connections.get(socket) + " at IP: " + socket.getInetAddress());
						MainThread.removeFromConnections(socket);
						broadcast(MainThread.getConnections().get(socket) + " has disconnected.");
						break;
						
					}
					
				}
			
			}
			
		}
		
	}

	private static void doCommand(String message) {
		
		
		
	}

	private static void broadcast(String message) throws IOException {

		HashMap<Socket, String> connections = MainThread.getConnections();
		
		for(Socket output : connections.keySet()) {
			
			DataOutputStream out = new DataOutputStream(output.getOutputStream());
			out.writeUTF(message);
			
		}
		
		System.out.println(message);
		
	}
}
