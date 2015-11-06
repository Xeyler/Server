package me.xeyler;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalTime;

import javax.swing.JTextArea;


public class NewOutputStream extends OutputStream {
	private JTextArea textArea;
	private String prefix;
	
	public NewOutputStream(JTextArea output) {
		textArea = output;
	}
	
	@Override
	public void write(int arg0) throws IOException {
		if(prefix == null) {
			String letter = "" + ((char) arg0);
			prefix = letter;
		} else {
			prefix = prefix + (char) arg0;
		}
		if(prefix.contains(System.getProperty("line.separator"))) {
			textArea.setText(textArea.getText() + "[" + LocalTime.now().toString().substring(0, 8) + "] " + prefix.toString());
			textArea.setCaretPosition(textArea.getDocument().getLength());
			prefix = null;
		}
	}
}

