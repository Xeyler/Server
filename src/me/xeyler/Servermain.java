package me.xeyler;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.*;
import java.net.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Servermain {

	static MainThread threads = new MainThread();

	// This is the official main method where variables are set up and Objects are defined
	public static void main(String[] args) throws Exception {
		
		JTextArea output = new JTextArea();
		JPanel panel = new JPanel(new BorderLayout());
		JFrame frame = new JFrame("server");
		PrintStream stream = new PrintStream(new NewOutputStream(output));
		System.setOut(stream);
		System.setErr(stream);

		output.setEditable(false);
		output.setBackground(Color.BLACK);
		output.setForeground(Color.WHITE);
		output.setFont(Font.createFont(Font.TRUETYPE_FONT, new File("res/pixelfont.ttf")).deriveFont(28f));
		panel.add(output);
		frame.add(panel);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600); 
		frame.setVisible(true);
		
		System.out.println("Starting server...");
		
		try{
			
		int port = threads.startServer();
		System.out.println("Server started on port " + port + "!");
		
		threads.startThread();
		
		} catch (BindException error) {
			return;
		}
		
	}
	
}
