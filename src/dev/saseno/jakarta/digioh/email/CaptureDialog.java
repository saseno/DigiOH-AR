package dev.saseno.jakarta.digioh.email;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;

public class CaptureDialog extends WindowAdapter implements ActionListener {
	
	private Frame frame;	
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	
	private int width = 483;
	private int heigh = 350;
	
	private TextField addressField = null;
	private TextArea messageText = null;
	private String attachment = null;
	
	public CaptureDialog() {
		frame = new Frame("Email Sender");
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				frame.setVisible(false);
			}
		});
		
		frame.setBackground(new Color(234, 234, 234));

		Label addressLabel = new Label("Send To:");
		addressField = new TextField(60);		

		Label messageLabel = new Label("Message:");
		messageText = new TextArea();
		messageText.setColumns(20);
		
		Button sendButton = new Button("Send Email");
		sendButton.addActionListener(this);

		frame.add(addressLabel);
		frame.add(addressField);

		frame.add(messageLabel);
		frame.add(messageText);
		
		//frame.add(new Label());
		frame.add(sendButton);

		frame.pack();
		frame.setLayout(new FlowLayout(10, 10, 5));		
		frame.setSize(width, heigh);
		frame.setLocation((screenSize.width / 5) * 2, (screenSize.height / 6) * 2);		
		frame.setVisible(false);
	}
	
	public void sendEmail(String attachment) {
		try {
			frame.setVisible(true);
			this.attachment = attachment;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		new CaptureDialog();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {						

			frame.setVisible(false);
						
			new Thread(new SendEmailAttachment(addressField.getText(), 
							messageText.getText(), attachment)).start();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
